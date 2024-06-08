#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

class DelayEffect final : public AudioEffect {

public:

  void delay(const std::string& value);

  void reset(const float samplerate, const size_t blocksize) override;
  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override;

private:

  struct {
    float samplerate;
    size_t blocksize;
  } config;

  struct {
    struct {
      const double max = 1;
      double min = 0;
    } delay;
  } params;

  std::deque<float> buffer;
  std::mutex mutex;

};
