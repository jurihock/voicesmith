#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Oscillator.h>

template<typename T>
class Wobbulator final {

public:

  Wobbulator() :
    slope(0),
    intercept(0),
    lfo(),
    hfo() {
  }

  Wobbulator(const std::pair<T, T> frequencies, const T period, const T samplerate) :
    slope((frequencies.first - frequencies.second) / T(2)),
    intercept((frequencies.first + frequencies.second) / T(2)),
    lfo(T(1) / period, samplerate),
    hfo(frequencies.first, samplerate) {
  }

  Wobbulator(const Wobbulator<T>& other) :
    slope(other.slope),
    intercept(other.intercept),
    lfo(other.lfo),
    hfo(other.hfo) {
  }

  Wobbulator<T>& operator=(const Wobbulator<T>& other) {
    if (this != &other) {
      slope = other.slope;
      intercept = other.intercept;
      lfo = other.lfo;
      hfo = other.hfo;
    }
    return *this;
  }

  std::complex<T> operator()() {
    return hfo(lfo.cos() * slope + intercept);
  }

  T cos() {
    return (*this)().real();
  }

  T sin() {
    return (*this)().imag();
  }

private:

  T slope;
  T intercept;

  Oscillator<T> lfo;
  Oscillator<T> hfo;

};
