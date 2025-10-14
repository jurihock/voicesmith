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
    left.template get<Type>(callback);
    right.template get<Type>(callback);
  }

  void reset(const float samplerate, const size_t blocksize) override {
    const size_t n = blocksize / 2;

    left.reset(samplerate, n);
    right.reset(samplerate, n);
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    const size_t n = input.size() / 2;

    inputs.left.resize(n);
    inputs.right.resize(n);

    outputs.left.resize(n);
    outputs.right.resize(n);

    for (size_t i = 0; i < n; ++i) {
      inputs.left[i] = input[i * 2 + 0];
      inputs.right[i] = input[i * 2 + 1];
    }

    left.apply(index, inputs.left, outputs.left);
    right.apply(index, inputs.right, outputs.right);

    for (size_t i = 0; i < n; ++i) {
      output[i * 2 + 0] = outputs.left[i];
      output[i * 2 + 1] = outputs.right[i];
    }
  }

private:

  ChainEffect<AudioEffects...> left;
  ChainEffect<AudioEffects...> right;

  struct
  {
    std::vector<float> left;
    std::vector<float> right;
  } inputs;

  struct
  {
    std::vector<float> left;
    std::vector<float> right;
  } outputs;

};
