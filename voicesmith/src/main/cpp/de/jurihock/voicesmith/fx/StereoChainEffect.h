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

  void reset(const float samplerate, const size_t blocksize, const size_t channels) override {
    mono = channels != 2;

    if (channels == 1) {
      effects[0].reset(samplerate, blocksize, channels);
    }
    else if (channels == 2) {
      const size_t halfsize = blocksize / 2;

      effects[0].reset(samplerate, halfsize, channels);
      effects[1].reset(samplerate, halfsize, channels);

      inputs[0].resize(halfsize);
      inputs[1].resize(halfsize);

      outputs[0].resize(halfsize);
      outputs[1].resize(halfsize);
    }
    else {
      throw std::invalid_argument(
        "Invalid number of channels!");
    }
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    if (mono) {
      effects[0].apply(index, input, output);
      return;
    }

    const size_t halfsize = input.size() / 2;

    for (size_t i = 0; i < halfsize; ++i) {
      inputs[0][i] = input[i * 2 + 0];
      inputs[1][i] = input[i * 2 + 1];
    }

    effects[0].apply(index, inputs[0], outputs[0]);
    effects[1].apply(index, inputs[1], outputs[1]);

    for (size_t i = 0; i < halfsize; ++i) {
      output[i * 2 + 0] = outputs[0][i];
      output[i * 2 + 1] = outputs[1][i];
    }
  }

private:

  bool mono {false};

  std::array<ChainEffect<AudioEffects...>, 2> effects;
  std::array<std::vector<float>, 2> inputs;
  std::array<std::vector<float>, 2> outputs;

};
