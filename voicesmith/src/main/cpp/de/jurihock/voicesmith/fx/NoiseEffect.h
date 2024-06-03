#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Noise.h>
#include <voicesmith/fx/AudioEffect.h>

class NoiseEffect final : public AudioEffect {

public:

  NoiseEffect(const float amplitude) :
    amplitude(amplitude) {
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    for (size_t i = 0; i < output.size(); ++i) {
      output[i] = amplitude * noise();
    }
  }

private:

  const float amplitude;

  Noise<float> noise;

};
