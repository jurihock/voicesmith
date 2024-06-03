#pragma once

#include <voicesmith/Header.h>

template<typename T>
class Noise final {

public:

  Noise() :
    seed(),
    generator(seed()),
    distribution(-1, +1) {
  }

  Noise(const Noise<T>& other) :
    seed(other.seed),
    generator(other.generator),
    distribution(other.distribution) {
  }

  Noise<T>& operator=(const Noise<T>& other) {
    if (this != &other) {
      seed = other.seed;
      generator = other.generator;
      distribution = other.distribution;
    }
    return *this;
  }

  T operator()() {
    return distribution(generator);
  }

private:

  std::random_device seed;
  std::mt19937 generator;
  std::uniform_real_distribution<T> distribution;

};
