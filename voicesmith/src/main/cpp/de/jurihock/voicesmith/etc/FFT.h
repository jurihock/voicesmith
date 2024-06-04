#pragma once

#include <StftPitchShift/FFT.h>

#include <pocketfft_hdronly.h>

class FFT final : public stftpitchshift::FFT {

public:

  void fft(const std::span<const float> frame,
           const std::span<std::complex<float>> dft) override {
    pocketfft::r2c(
      {frame.size()},
      {sizeof(float)},
      {sizeof(std::complex<float>)},
      0,
      true,
      frame.data(),
      dft.data(),
      float(1) / frame.size());
  }

  void fft(const std::span<const double> frame,
           const std::span<std::complex<double>> dft) override {
    pocketfft::r2c(
      {frame.size()},
      {sizeof(double)},
      {sizeof(std::complex<double>)},
      0,
      true,
      frame.data(),
      dft.data(),
      double(1) / frame.size());
  }

  void ifft(const std::span<const std::complex<float>> dft,
            const std::span<float> frame) override {
    pocketfft::c2r(
      {frame.size()},
      {sizeof(std::complex<float>)},
      {sizeof(float)},
      0,
      false,
      dft.data(),
      frame.data(),
      float(1));
  }

  void ifft(const std::span<const std::complex<double>> dft,
            const std::span<double> frame) override {
    pocketfft::c2r(
      {frame.size()},
      {sizeof(std::complex<double>)},
      {sizeof(double)},
      0,
      false,
      dft.data(),
      frame.data(),
      double(1));
  }

};
