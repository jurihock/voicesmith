#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>

#include <webrtc_vad.h>

class VadEffect final : public AudioEffect {

public:

  VadEffect(const int level = 0, const float window = 10e-3);

  void reset(const float samplerate, const size_t blocksize, const size_t channels) override;
  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override;

private:

  struct {
    int level;
    float window;
    float samplerate;
    size_t blocksize;
  } config;

  struct {
    std::vector<int16_t> buffer;
    bool result;
  } state;

  std::unique_ptr<VadInst, void(*)(VadInst*)> vad;

  inline static int16_t transform(const float x) {
    // https://www.cs.cmu.edu/~rbd/papers/cmj-float-to-int.html
    const int32_t y = static_cast<int32_t>(x * 32767 + 32768.5f) - 32768;
    return static_cast<int16_t>(std::clamp(y, -32768, +32767));
  }

};
