#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Arctangent.h>

template<typename T>
class Vocoder final
{

public:

  Vocoder(const float samplerate, const size_t dftsize) :
    rad2hz(static_cast<T>(samplerate / (2 * std::numbers::pi))),
    hz2rad(static_cast<T>((2 * std::numbers::pi) / samplerate)) {
    buffer.analysis.resize(dftsize, T(1));
    buffer.synthesis.resize(dftsize, T(0));
  }

  void analyze(const std::span<const std::complex<T>> dft,
               const std::span<T> magns,
               const std::span<T> freqs) {
    for (size_t i = 0; i < dft.size(); ++i) {
      const T phase = Arctangent::atan2(dft[i] / buffer.analysis[i]); // approx. of std::arg

      magns[i] = std::abs(dft[i]);
      freqs[i] = phase * rad2hz;

      buffer.analysis[i] = dft[i];
    }
  }

  void synthesize(const std::span<const T> magns,
                  const std::span<const T> freqs,
                  const std::span<std::complex<T>> dft) {
    for (size_t i = 0; i < dft.size(); ++i) {
      const T phase = freqs[i] * hz2rad + buffer.synthesis[i];

      dft[i] = std::polar(magns[i], phase);

      buffer.synthesis[i] = phase;
    }
  }

private:

  const T rad2hz;
  const T hz2rad;

  struct {
    std::vector<std::complex<T>> analysis;
    std::vector<T> synthesis;
  }
  buffer;

};
