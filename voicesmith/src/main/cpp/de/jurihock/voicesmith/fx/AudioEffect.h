#pragma once

#include <voicesmith/Header.h>

class AudioEffect {

public:

  virtual ~AudioEffect() = default;

  virtual void reset(const float samplerate, const size_t blocksize, const size_t channels) {};

  virtual void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) = 0;

};
