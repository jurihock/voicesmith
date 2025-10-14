#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/ChainEffect.h>

template<typename... AudioEffects>
class StereoChainEffect : public AudioEffect
{

public:

  template<typename Type>
  inline void get(const std::function<void(std::shared_ptr<Type> effect)> callback) const
  {
    effects[0].template get<Type>(callback);
    effects[1].template get<Type>(callback);
  }

  void reset(const float samplerate, const size_t blocksize) override {
    const size_t n = blocksize / 2;

    effects[0].reset(samplerate, n);
    effects[1].reset(samplerate, n);

    inputs[0].resize(n);
    inputs[1].resize(n);

    outputs[0].resize(n);
    outputs[1].resize(n);
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    const size_t n = input.size() / 2;

    for (size_t i = 0; i < n; ++i) {
      inputs[0][i] = input[i * 2 + 0];
      inputs[1][i] = input[i * 2 + 1];
    }

    effects[0].apply(index, inputs[0], outputs[0]);
    effects[1].apply(index, inputs[1], outputs[1]);

    for (size_t i = 0; i < n; ++i) {
      output[i * 2 + 0] = outputs[0][i];
      output[i * 2 + 1] = outputs[1][i];
    }
  }

private:

  std::array<ChainEffect<AudioEffects...>, 2> effects;
  std::array<std::vector<float>, 2> inputs;
  std::array<std::vector<float>, 2> outputs;

};
