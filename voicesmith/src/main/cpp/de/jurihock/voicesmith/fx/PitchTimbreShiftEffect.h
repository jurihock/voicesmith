#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/FFT.h>
#include <voicesmith/fx/AudioEffect.h>

#include <StftPitchShift/STFT.h>
#include <StftPitchShift/StftPitchShiftCore.h>

class PitchTimbreShiftEffect final : public AudioEffect {

public:

  PitchTimbreShiftEffect(const size_t dftsize, const size_t overlap);

  void reset(const float samplerate, const size_t blocksize) override;
  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override;

private:

  typedef float stft_t;

  struct {
    float samplerate;
    size_t blocksize;
    size_t dftsize;
    size_t overlap;
    size_t analysis_window_size;
    size_t synthesis_window_size;
  } config;

  struct {
    std::vector<stft_t> input;
    std::vector<stft_t> output;
  } buffer;

  std::shared_ptr<FFT> fft;
  std::unique_ptr<stftpitchshift::STFT<stft_t>> stft;
  std::unique_ptr<stftpitchshift::StftPitchShiftCore<stft_t>> core;

  template<typename X, typename Y>
  inline static Y transform(const X x) { return static_cast<Y>(x); }

};
