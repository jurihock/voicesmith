#pragma once

#include <voicesmith/Header.h>

template<typename T>
class Noise final {

public:

  Noise() :
    generator(),
    distribution(-1, +1) {
  }

  Noise(const Noise<T>& other) :
    generator(other.generator),
    distribution(other.distribution) {
  }

  Noise<T>& operator=(const Noise<T>& other) {
    if (this != &other) {
      generator = other.generator;
      distribution = other.distribution;
    }
    return *this;
  }

  T operator()() {
    return distribution(generator);
  }

private:

  std::mt19937 generator;
  std::uniform_real_distribution<T> distribution;

};
