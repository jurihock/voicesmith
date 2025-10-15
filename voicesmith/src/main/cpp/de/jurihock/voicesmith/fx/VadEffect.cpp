#include <voicesmith/fx/VadEffect.h>

#include <voicesmith/Source.h>

VadEffect::VadEffect(const int level, const float window) :
  vad(std::unique_ptr<VadInst, void(*)(VadInst*)>(WebRtcVad_Create(), WebRtcVad_Free)) {
  config.level = level;
  config.window = window;

  const auto ok = WebRtcVad_Init(vad.get()) == 0;

  if (!ok) {
    Log::e("Unable to initialize the VAD processor!");
    vad = nullptr;
  }

  WebRtcVad_set_mode(vad.get(), std::clamp(config.level, 0, 3));
}

void VadEffect::reset(const float samplerate, const size_t blocksize, const size_t channels) {
  config.samplerate = samplerate;
  config.blocksize = blocksize;

  state.buffer.clear();
  state.result = false;

  const auto ok = WebRtcVad_ValidRateAndFrameLength(
    static_cast<int>(samplerate),
    static_cast<size_t>(config.window * config.samplerate)) == 0;

  if (!ok) {
    Log::e("Invalid VAD configuration (samplerate={0}, window={1})!",
           config.samplerate, config.window);
    return;
  }

  const auto seconds = std::max(config.blocksize / config.samplerate, config.window);
  const auto samples = static_cast<size_t>(seconds * config.samplerate);

  state.buffer.resize(samples);
  std::fill(state.buffer.begin(), state.buffer.end(), 0);
}

void VadEffect::apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) {
  std::memcpy(output.data(), input.data(), input.size() * sizeof(float));

  if (!vad || state.buffer.empty()) {
    return;
  }

  const auto offset =
    static_cast<ptrdiff_t>(state.buffer.size()) -
    static_cast<ptrdiff_t>(input.size());

  std::copy(
    state.buffer.begin() + offset,
    state.buffer.end(),
    state.buffer.begin());

  std::transform(
    input.begin(),
    input.end(),
    state.buffer.begin() + offset,
    transform);

  const std::span<int16_t> window {
    state.buffer.end() - static_cast<size_t>(config.window * config.samplerate),
    state.buffer.end()
  };

  const auto result = WebRtcVad_Process(
    vad.get(),
    static_cast<int>(config.samplerate),
    window.data(),
    window.size());

  if (result < 0) {
    Log::e("Unable to perform VAD!");
    state.result = false;
    return;
  }

  if (state.result != (result > 0)) {
    state.result = (result > 0);
    Log::d("VAD {0}", int(state.result));
  }
}
