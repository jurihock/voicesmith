#pragma once

#include <voicesmith/Header.h>

class AudioPlugin {

public:

  virtual ~AudioPlugin() = default;

  virtual void setup(const std::optional<int> input,
                     const std::optional<int> output,
                     const std::optional<float> samplerate,
                     const std::optional<size_t> blocksize) = 0;

  virtual void set(const std::string& param,
                   const std::string& value) = 0;

  virtual void start() = 0;
  virtual void stop() = 0;

};
