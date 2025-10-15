#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Wobbulator.h>
#include <voicesmith/fx/AudioEffect.h>

class SweepEffect final : public AudioEffect {

public:

  SweepEffect(const float amplitude, const std::pair<float, float> frequencies, const float period) :
    amplitude(amplitude),
    frequencies(frequencies),
    period(period) {
  }

  void reset(const float samplerate, const size_t blocksize, const size_t channels) override {
    osc = Wobbulator<float>(frequencies, period, samplerate);
  }

  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override {
    for (size_t i = 0; i < output.size(); ++i) {
      output[i] = amplitude * osc.sin();
    }
  }

private:

  const float amplitude;

  std::pair<float, float> frequencies;
  float period;

  Wobbulator<float> osc;

};
