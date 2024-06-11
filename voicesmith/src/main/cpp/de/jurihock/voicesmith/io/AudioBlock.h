#pragma once

#include <voicesmith/Header.h>

class AudioBlock final {

public:

  explicit AudioBlock(const std::span<float> data_to_attach);
  explicit AudioBlock(const size_t size_to_allocate);

  size_t size() const;

  void copyfrom(const std::span<const float> samples);
  void copyfrom(const AudioBlock& other);

  void copyto(const std::span<float> samples) const;
  void copyto(AudioBlock& other) const;

  operator std::span<float>();
  operator std::span<const float>() const;

private:

  std::vector<float> data;
  std::span<float> view;

};
