#pragma once

#include <voicesmith/Header.h>

/**
 * Arctangent approximation according to [1].
 *
 * [1] Xavier Girones and Carme Julia and Domenec Puig
 *     Full Quadrant Approximations for the Arctangent Function
 *     IEEE Signal Processing Magazine (2013)
 *     https://ieeexplore.ieee.org/document/6375931
 **/
namespace Arctangent
{
  template<std::floating_point T>
  inline T atan2(const T y, const T x)
  {
    if (y == 0 && x == 0)
    {
      // skip approximation and return
      // zero instead of NaN in this case
      return T(0);
    }

    // extract the sign bits
    const int ys = std::signbit(y);
    const int xs = std::signbit(x);

    // determine the quadrant offset and sign
    const int q = (ys & ~xs) * 4 + xs * 2;
    const int s = (ys ^ xs) ? -1 : +1;

    // calculate the arctangent in the first quadrant
    const T a = T(0.596227);
    const T b = std::abs(a * y * x);
    const T c = b + y * y;
    const T d = b + x * x;
    const T e = c / (c + d);

    // translate it to the proper quadrant
    const T phi = q + std::copysign(e, s);

    // translate the result from [0, 4) to [0, 2pi)
    return phi * T(std::numbers::pi / 2);
  }

  template<std::floating_point T>
  inline T atan2(const std::complex<T>& z)
  {
    return Arctangent::atan2(z.imag(), z.real());
  }
}
