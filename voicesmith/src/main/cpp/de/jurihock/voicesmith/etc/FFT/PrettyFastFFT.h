#pragma once

#include <StftPitchShift/FFT.h>

#include <pffft.h>

class FFT final : public stftpitchshift::FFT {

public:

  FFT(const size_t blocksize) :
    plan(pffft_new_setup(blocksize, PFFFT_REAL), pffft_destroy_setup),
    data(pffft_aligned_malloc_float(blocksize), pffft_aligned_free_float) {}

  void fft(const std::span<const float> frame,
           const std::span<std::complex<float>> dft) override {
    const auto src = frame.data();
    auto const dst = reinterpret_cast<float* const>(dft.data());

    pffft_transform_ordered(
      plan.get(),
      src,
      dst,
      data.get(),
      PFFFT_FORWARD);

    const auto factor = float(1) / frame.size();
    const auto scale = [factor](float value){ return value * factor; };
    std::transform(dst, dst + dft.size() * 2, dst, scale);
  }

  void fft(const std::span<const double> frame,
           const std::span<std::complex<double>> dft) override {
    throw std::runtime_error("PFFFT doesn't support double precision!");
  }

  void ifft(const std::span<const std::complex<float>> dft,
            const std::span<float> frame) override {
    const auto src = reinterpret_cast<const float*>(dft.data());
    auto const dst = frame.data();

    pffft_transform_ordered(
      plan.get(),
      src,
      dst,
      data.get(),
      PFFFT_BACKWARD);
  }

  void ifft(const std::span<const std::complex<double>> dft,
            const std::span<double> frame) override {
    throw std::runtime_error("PFFFT doesn't support double precision!");
  }

private:

  std::unique_ptr<PFFFT_Setup, void(*)(PFFFT_Setup*)> plan;
  std::unique_ptr<float, void(*)(float*)> data;

  static float* pffft_aligned_malloc_float(size_t size) {
    return static_cast<float*>(
      pffft_aligned_malloc(size * sizeof(float)));
  }

  static void pffft_aligned_free_float(float* data) {
    pffft_aligned_free(data);
  }

};
