#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/FFT.h>
#include <voicesmith/fx/AudioEffect.h>

#include <StftPitchShift/STFT.h>
#include <StftPitchShift/StftPitchShiftCore.h>

class PitchTimbreShiftEffect final : public AudioEffect {

public:

  PitchTimbreShiftEffect(const size_t dftsize = 1024, const size_t overlap = 4);

  void pitch(const std::string& value);
  void timbre(const std::string& value);

  void reset(const float samplerate, const size_t blocksize, const size_t channels) override;
  void apply(const uint64_t index, const std::span<const float> input, const std::span<float> output) override;

private:

  struct {
    float samplerate;
    size_t blocksize;
    size_t dftsize;
    size_t overlap;
    size_t analysis_window_size;
    size_t synthesis_window_size;
  } config;

  struct {
    const bool normalization = true;
    const double quefrency[2] = {0, 1e-3};
    double pitch = 1;
    double timbre = 1;
  } params;

  struct {
    std::shared_ptr<FFT> fft;
    std::unique_ptr<stftpitchshift::STFT<fft_t>> stft;
    std::unique_ptr<stftpitchshift::StftPitchShiftCore<fft_t>> core;
  } state;

  struct {
    std::vector<fft_t> input;
    std::vector<fft_t> output;
  } buffer;

  std::mutex mutex;

  template<typename X, typename Y>
  inline static Y transform(const X x) { return static_cast<Y>(x); }

};
