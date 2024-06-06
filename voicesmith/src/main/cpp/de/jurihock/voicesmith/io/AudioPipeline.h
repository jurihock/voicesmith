#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/fx/AudioEffect.h>
#include <voicesmith/io/AudioEvent.h>
#include <voicesmith/io/AudioSink.h>
#include <voicesmith/io/AudioSource.h>

class AudioPipeline final : public AudioEvent::Emitter {

public:

  AudioPipeline(const std::shared_ptr<AudioSource> source,
                const std::shared_ptr<AudioSink> sink,
                const std::shared_ptr<AudioEffect> effect = nullptr);

  ~AudioPipeline();

  void subscribe(const AudioEvent::Callback& callback) override;

  void open();
  void close();

  void start();
  void stop();

private:

  const std::shared_ptr<AudioSource> source;
  const std::shared_ptr<AudioSink> sink;
  const std::shared_ptr<AudioEffect> effect;

  struct {

    std::shared_ptr<std::thread> thread;
    std::condition_variable signal;
    std::mutex mutex;
    bool loop;

  } state;

  AudioEvent event;
  std::mutex eventmutex;

  void onloop();
  void onevent(const AudioEventCode code, const std::string& data);

};
