#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

class MultiEffect final : public AudioEffect {

public:

  MultiEffect(const std::initializer_list<std::shared_ptr<AudioEffect>>& effects) :
    effects(effects) {}

  template<class T, class = std::enable_if_t<std::is_base_of_v<AudioEffect, T>>>
  inline std::shared_ptr<T> fx(const size_t index) const {
    return std::static_pointer_cast<T>(effects.at(index));
  }

  void reset(const float samplerate, const size_t blocksize) override {
    for (auto& effect : effects) {
      effect->reset(samplerate, blocksize);
    }
    for (auto& buffer : buffers) {
      buffer.resize(blocksize);
      std::fill(buffer.begin(), buffer.end(), 0);
    }
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    for (size_t i = 0, x = 1, y = 0; i < effects.size(); ++i, x ^= 1, y ^= 1) {
      auto src = (i > 0) ? buffers[x] : input;
      auto dst = (i < effects.size() - 1) ? buffers[y] : output;
      effects[i]->apply(index, src, dst);
    }
  }

private:

  std::vector<std::shared_ptr<AudioEffect>> effects;
  std::array<std::vector<float>, 2> buffers;

};
