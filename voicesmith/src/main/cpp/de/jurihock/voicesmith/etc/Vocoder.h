#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Arctangent.h>

class Vocoder final
{

public:

  Vocoder(const float samplerate, const size_t dftsize) :
    rad2hz(samplerate / (2 * std::numbers::pi)),
    hz2rad((2 * std::numbers::pi) / samplerate) {
    buffer.analysis.resize(dftsize, 1);
    buffer.synthesis.resize(dftsize, 0);
  }

  void analyze(const std::span<const std::complex<double>> dft,
               const std::span<double> magns,
               const std::span<double> freqs) {
    for (size_t i = 0; i < dft.size(); ++i) {
      const double phase = Arctangent::atan2(dft[i] / buffer.analysis[i]); // approx. of std::arg

      magns[i] = std::abs(dft[i]);
      freqs[i] = phase * rad2hz;

      buffer.analysis[i] = dft[i];
    }
  }

  void synthesize(const std::span<const double> magns,
                  const std::span<const double> freqs,
                  const std::span<std::complex<double>> dft) {
    for (size_t i = 0; i < dft.size(); ++i) {
      const double phase = freqs[i] * hz2rad + buffer.synthesis[i];

      dft[i] = std::polar(magns[i], phase);

      buffer.synthesis[i] = phase;
    }
  }

private:

  const double rad2hz;
  const double hz2rad;

  struct {
    std::vector<std::complex<double>> analysis;
    std::vector<double> synthesis;
  }
  buffer;

};
