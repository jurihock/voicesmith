#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>
#include <voicesmith/io/AudioBufferQueue.h>
#include <voicesmith/io/AudioStream.h>

class AudioSource final : public AudioStream {

public:

  AudioSource(const std::optional<int> device = std::nullopt,
              const std::optional<float> samplerate = std::nullopt,
              const std::optional<size_t> buffersize = std::nullopt,
              const std::shared_ptr<AudioEffect> effect = nullptr,
              const std::shared_ptr<AudioBufferQueue> queue = nullptr);

  std::shared_ptr<AudioEffect> fx() const;
  std::shared_ptr<AudioBufferQueue> fifo() const;

protected:

  void callback(const std::span<float> samples) override;

private:

  const std::shared_ptr<AudioEffect> effect;
  const std::shared_ptr<AudioBufferQueue> queue;

  struct {
    uint64_t inner;
    uint64_t outer;
  } index;

};
