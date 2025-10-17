#include <voicesmith/fx/PitchShiftEffect.h>

#include <voicesmith/Source.h>

void PitchShiftEffect::pitch(const std::string& value) {
  std::unique_lock lock(mutex);
  params.pitch = std::pow(2, std::stod(value) / 12);
}

void PitchShiftEffect::reset(const float samplerate, const size_t blocksize, const size_t channels) {
  std::unique_lock lock(mutex);

  config.samplerate = samplerate;
  config.bandwidth = { 10e3, samplerate / 2 };
  config.resolution = 24;

  state.qdft = std::make_shared<QDFT<fft_t>>(config.samplerate, config.bandwidth, config.resolution);
  state.vocoder = std::make_shared<Vocoder<fft_t>>(config.samplerate, state.qdft->size());

  buffer.dft.resize(state.qdft->size());
  buffer.magns.resize(state.qdft->size());
  buffer.freqs.resize(state.qdft->size());

  std::fill(buffer.dft.begin(), buffer.dft.end(), 0);
  std::fill(buffer.magns.begin(), buffer.magns.end(), 0);
  std::fill(buffer.freqs.begin(), buffer.freqs.end(), 0);
}

void PitchShiftEffect::apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) {
  std::unique_lock lock(mutex);

  const fft_t rayleigh = 0;
  const fft_t nyquist = config.samplerate / 2;

  auto& [qdft, vocoder] = state;
  auto& [dft, magns, freqs] = buffer;
  auto& pitch = params.pitch;

  for (size_t i = 0; i < input.size(); ++i) {
    qdft->qdft(input[i], dft.data());
    vocoder->analyze(dft, magns, freqs);

    for (size_t j = 0; j < dft.size(); ++j) {
      auto magn = magns[j];
      auto freq = freqs[j] * pitch;

      if (!std::isfinite(freq)) {
        magn = 0;
        freq = 0;
      }
      else if (freq < rayleigh) {
        magn = 0;
        freq = 0;
      }
      else if (freq > nyquist) {
        magn = 0;
        freq = 0;
      }

      magns[j] = magn;
      freqs[j] = freq;
    }

    vocoder->synthesize(magns, freqs, dft);
    output[i] = qdft->iqdft(dft.data());
  }
}
