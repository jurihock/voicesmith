#include <voicesmith/fx/DelayEffect.h>

#include <voicesmith/Source.h>

void DelayEffect::delay(const std::string& value) {
  std::unique_lock lock(mutex);
  params.delay.min = std::stod(value) * 1e-3;
}

void DelayEffect::reset(const float samplerate, const size_t blocksize, const size_t channels) {
  std::unique_lock lock(mutex);

  config.samplerate = samplerate;
  config.blocksize = blocksize;

  const auto maxdelay = static_cast<size_t>(
    params.delay.max * samplerate);

  buffer.resize(maxdelay);
  std::fill(buffer.begin(), buffer.end(), 0);
}

void DelayEffect::apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) {
  std::unique_lock lock(mutex);

  const auto delay = static_cast<size_t>(
    std::clamp(params.delay.min, 0.0, params.delay.max) * config.samplerate);

  for (size_t i = 0; i < input.size(); ++i) {
    buffer.pop_back();
    buffer.push_front(input[i]);
    output[i] = buffer[delay];
  }
}
