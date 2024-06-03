#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

class BypassEffect final : public AudioEffect {

public:

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    std::memcpy(output.data(), input.data(), input.size() * sizeof(float));
  }

};
