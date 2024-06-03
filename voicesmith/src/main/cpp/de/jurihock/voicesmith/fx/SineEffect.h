#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Oscillator.h>
#include <voicesmith/fx/AudioEffect.h>

class SineEffect final : public AudioEffect {

public:

  SineEffect(const float amplitude, const float frequency) :
    amplitude(amplitude),
    frequency(frequency) {
  }

  void reset(const float samplerate, const size_t buffersize) override {
    osc = Oscillator<float>(frequency, samplerate);
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    for (size_t i = 0; i < output.size(); ++i) {
      output[i] = amplitude * osc.sin();
    }
  }

private:

  const float amplitude;
  const float frequency;

  Oscillator<float> osc;

};
