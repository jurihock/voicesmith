#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/QDFT.h>
#include <voicesmith/etc/Vocoder.h>
#include <voicesmith/fx/AudioEffect.h>

class PitchShiftEffect final : public AudioEffect {

public:

  PitchShiftEffect() = default;

  void pitch(const std::string& value);

  void reset(const float samplerate, const size_t blocksize, const size_t channels) override;
  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override;

private:

  struct {
    float samplerate;
    std::pair<double, double> bandwidth;
    double resolution;
  } config;

  struct {
    double pitch = 1;
  } params;

  struct {
    std::shared_ptr<QDFT> qdft;
    std::shared_ptr<Vocoder> vocoder;
  } state;

  struct {
    std::vector<std::complex<double>> dft;
    std::vector<double> magns;
    std::vector<double> freqs;
  } buffer;

  std::mutex mutex;

};
