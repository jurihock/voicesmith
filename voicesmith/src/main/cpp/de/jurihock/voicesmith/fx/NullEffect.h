#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

class NullEffect final : public AudioEffect {

public:

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    std::memset(output.data(), 0, output.size() * sizeof(float));
  }

};
