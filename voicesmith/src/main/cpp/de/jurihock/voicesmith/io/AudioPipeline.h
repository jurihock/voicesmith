#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/Event.h>
#include <voicesmith/fx/AudioEffect.h>
#include <voicesmith/io/AudioSink.h>
#include <voicesmith/io/AudioSource.h>

class AudioPipeline final {

public:

  AudioPipeline(const std::shared_ptr<AudioSource> source,
                const std::shared_ptr<AudioSink> sink,
                const std::shared_ptr<AudioEffect> effect = nullptr);

  ~AudioPipeline();

  void onerror();
  void onerror(std::function<void()> callback);

  void open();
  void close();

  void start();
  void stop();

private:

  const std::shared_ptr<AudioSource> source;
  const std::shared_ptr<AudioSink> sink;
  const std::shared_ptr<AudioEffect> effect;

  struct {

    Event<void()> error = []() {};

  } events;

  struct {

    std::shared_ptr<std::thread> loopthread;
    bool doloop;

  } state;

  void loop();
  void onxrun(const oboe::Direction direction, const int32_t count);

  std::mutex onerrormutex;
  bool onerror(const oboe::Direction direction, const oboe::Result error);

};
