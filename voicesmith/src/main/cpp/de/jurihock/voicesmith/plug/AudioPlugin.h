#pragma once

#include <voicesmith/Header.h>

class AudioPlugin {

public:

  virtual ~AudioPlugin() = default;

  virtual void setup(const std::optional<int> input,
                     const std::optional<int> output,
                     const std::optional<float> samplerate,
                     const std::optional<size_t> buffersize) = 0;

  virtual void start() = 0;
  virtual void stop() = 0;

};
