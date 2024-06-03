#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Event.h>

#include <oboe/Oboe.h>

class AudioStream : public oboe::AudioStreamDataCallback,
                    public oboe::AudioStreamErrorCallback,
                    public std::enable_shared_from_this<AudioStream> {

public:

  AudioStream(const oboe::Direction direction,
              const std::optional<int> device = std::nullopt,
              const std::optional<float> samplerate = std::nullopt,
              const std::optional<size_t> buffersize = std::nullopt);

  virtual ~AudioStream();

  int device() const;
  float samplerate() const;
  size_t buffersize() const;
  size_t maxbuffersize() const;
  std::chrono::milliseconds timeout() const;

  void onopen();
  void onopen(const std::function<void()> callback);

  void onclose();
  void onclose(const std::function<void()> callback);

  void onstart();
  void onstart(const std::function<void()> callback);

  void onstop();
  void onstop(const std::function<void()> callback);

  void onxrun();
  void onxrun(const std::function<void(const int32_t count)> callback);

  void onerror();
  void onerror(const std::function<bool(const oboe::Result error)> callback);

  void open();
  void close();

  void start();
  void stop();

protected:

  virtual void callback(const std::span<float> samples) = 0;

private:

  const oboe::Direction direction;

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
    } buffersize;

    std::optional<std::chrono::milliseconds> timeout;

  } config;

  struct {

    Event<void()> open = []() {};
    Event<void()> close = []() {};
    Event<void()> start = []() {};
    Event<void()> stop = []() {};
    Event<void(int32_t)> xrun = [](int32_t) {};
    Event<bool(oboe::Result)> error = [](oboe::Result) { return false; };

  } events;

  struct {

    std::shared_ptr<oboe::AudioStream> stream;
    int32_t xruns;

  } state;

  oboe::DataCallbackResult onAudioReady(oboe::AudioStream* stream, void* data, int32_t size) override;
  bool onError(oboe::AudioStream* stream, oboe::Result error) override;

};
