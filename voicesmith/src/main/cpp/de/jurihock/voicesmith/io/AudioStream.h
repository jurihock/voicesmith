#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Debouncer.h>
#include <voicesmith/io/AudioEvent.h>

#include <oboe/Oboe.h>

class AudioStream : public AudioEvent::Emitter,
                    public oboe::AudioStreamDataCallback,
                    public oboe::AudioStreamErrorCallback,
                    public std::enable_shared_from_this<AudioStream> {

public:

  AudioStream(const oboe::Direction direction,
              const std::optional<int> device = std::nullopt,
              const std::optional<float> samplerate = std::nullopt,
              const std::optional<size_t> blocksize = std::nullopt);

  virtual ~AudioStream();

  void subscribe(const AudioEvent::Callback& callback) override;

  int device() const;
  float samplerate() const;
  size_t blocksize() const;
  size_t maxblocksize() const;
  std::chrono::milliseconds timeout() const;

  void open();
  void close();

  void start();
  void stop();

protected:

  const oboe::Direction direction;

  AudioEvent event;

  virtual void callback(const std::span<float> samples) = 0;

  virtual void onopen() {}
  virtual void onclose() {}

  virtual void onstart() {}
  virtual void onstop() {}

private:

  struct {

    struct {
      std::optional<int> set;
      std::optional<int> get;
    } device;

    struct {
      std::optional<float> set;
      std::optional<float> get;
    } samplerate;

    struct {
      std::optional<size_t> set;
      std::optional<size_t> get;
      std::optional<size_t> max;
    } blocksize;

    std::optional<std::chrono::milliseconds> timeout;

  } config;

  struct {

    std::shared_ptr<oboe::AudioStream> stream;
    Debouncer xrun;
    int32_t xruns;

  } state;

  oboe::DataCallbackResult onAudioReady(oboe::AudioStream* stream, void* data, int32_t size) override;
  bool onError(oboe::AudioStream* stream, oboe::Result error) override;

};
