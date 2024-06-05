#include <voicesmith/fx/PitchTimbreShiftEffect.h>

#include <voicesmith/Source.h>

PitchTimbreShiftEffect::PitchTimbreShiftEffect(const size_t dftsize, const size_t overlap) {
  config.dftsize = dftsize;
  config.overlap = overlap;
}

void PitchTimbreShiftEffect::reset(const float samplerate, const size_t blocksize) {
  config.samplerate = samplerate;
  config.blocksize = blocksize;
  config.analysis_window_size = config.dftsize + config.dftsize;
  config.synthesis_window_size = config.blocksize;

  const auto winsize = std::make_tuple(config.analysis_window_size, config.synthesis_window_size);
  const auto hopsize = config.synthesis_window_size / config.overlap;
  const auto bufsize = config.analysis_window_size + config.synthesis_window_size;

  buffer.input.resize(bufsize);
  buffer.output.resize(bufsize);

  std::fill(buffer.input.begin(), buffer.input.end(), 0);
  std::fill(buffer.output.begin(), buffer.output.end(), 0);

  fft = std::make_shared<FFT>(std::get<0>(winsize));
  stft = std::make_unique<stftpitchshift::STFT<stft_t>>(fft, winsize, hopsize);
  core = std::make_unique<stftpitchshift::StftPitchShiftCore<stft_t>>(fft, winsize, hopsize, samplerate);

  core->normalization(true);
  core->quefrency(0e-3);
  core->distortion(1);
  core->factors({ 2 });
}

void PitchTimbreShiftEffect::apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) {
  const auto analysis_window_size = config.analysis_window_size;
  const auto synthesis_window_size = config.synthesis_window_size;

  // shift input buffer
  std::copy(
    buffer.input.begin() + synthesis_window_size,
    buffer.input.end(),
    buffer.input.begin());

  // copy new input samples
  std::transform(
    input.begin(),
    input.end(),
    buffer.input.begin() + analysis_window_size,
    transform<float, stft_t>);

  // perform pitch shifting
  (*stft)(buffer.input, buffer.output, [&](auto dft) {
    core->shiftpitch(dft);
  });

  // copy new output samples back
  std::transform(
    (buffer.output.begin() + analysis_window_size) - synthesis_window_size,
    buffer.output.end() - synthesis_window_size,
    output.begin(),
    transform<stft_t, float>);

  // shift output buffer
  std::copy(
    buffer.output.begin() + synthesis_window_size,
    buffer.output.end(),
    buffer.output.begin());

  // prepare for the next callback
  std::fill(
    buffer.output.begin() + analysis_window_size,
    buffer.output.end(),
    0);
}
