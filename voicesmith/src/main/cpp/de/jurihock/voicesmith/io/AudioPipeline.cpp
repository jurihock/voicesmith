#include <voicesmith/io/AudioPipeline.h>

#include <voicesmith/Source.h>

#include <voicesmith/etc/Timer.h>

AudioPipeline::AudioPipeline(const std::shared_ptr<AudioSource> source,
                             const std::shared_ptr<AudioSink> sink,
                             const std::shared_ptr<AudioEffect> effect) :
  source(source),
  sink(sink),
  effect(effect) {
}

AudioPipeline::~AudioPipeline() {
  close();
}

void AudioPipeline::subscribe(const AudioEvent::Callback& callback) {
  event.append(callback);
}

void AudioPipeline::open() {
  source->open();
  sink->open();

  if (source->samplerate() != sink->samplerate()) {
    LOG(ERROR) << $("Unequal audio stream sample rate: {0} (source), {1} (sink)!",
                    source->samplerate(), sink->samplerate());
  }

  if (source->samplerate() == 0 || sink->samplerate() == 0) {
    LOG(ERROR) << $("Invalid audio stream sample rate: {0} (source), {1} (sink)!",
                    source->samplerate(), sink->samplerate());
  }

  if (source->blocksize() != sink->blocksize()) {
    LOG(ERROR) << $("Unequal audio stream block size: {0} (source), {1} (sink)!",
                    source->blocksize(), sink->blocksize());
  }

  if (source->blocksize() == 0 || sink->blocksize() == 0) {
    LOG(ERROR) << $("Invalid audio stream block size: {0} (source), {1} (sink)!",
                    source->blocksize(), sink->blocksize());
  }

  if (source->maxblocksize() == 0 || sink->maxblocksize() == 0) {
    LOG(ERROR) << $("Invalid audio stream max. block size: {0} (source), {1} (sink)!",
                    source->maxblocksize(), sink->maxblocksize());
  }

  if (effect) {
    effect->reset(source->samplerate(), source->blocksize());
  }

  const size_t fifosize = 10 *
    std::max(source->maxblocksize(), sink->maxblocksize()) /
    std::min(source->blocksize(), sink->blocksize());

  source->fifo()->resize(fifosize, source->blocksize());
  sink->fifo()->resize(fifosize, sink->blocksize());

  source->subscribe([&](const AudioEventCode code, const std::string& text) {
    onevent(code, text);
  });

  sink->subscribe([&](const AudioEventCode code, const std::string& text) {
    onevent(code, text);
  });
}

void AudioPipeline::close() {
  stop();

  source->close();
  sink->close();
}

void AudioPipeline::start() {
  state.doloop = true;

  source->start();
  sink->start();

  state.loopthread = std::make_shared<std::thread>(
    [&]() { onloop(); });
}

void AudioPipeline::stop() {
  state.doloop = false;

  if (state.loopthread != nullptr) {
    if (state.loopthread->joinable()) {
      state.loopthread->join();
    }
    state.loopthread = nullptr;
  }

  sink->stop();
  source->stop();

  sink->fifo()->flush();
  source->fifo()->flush();
}

void AudioPipeline::onloop() {
  struct timers_t {
    Timer<std::chrono::milliseconds> outer;
    Timer<std::chrono::milliseconds> inner;
  } timers;

  const auto dowork = [this](uint64_t& index, timers_t& timers, const std::chrono::milliseconds timeout) {
    const bool ok = source->fifo()->read(timeout, [&](AudioBlock& input) {
      timers.outer.toc();
      timers.outer.tic();

      const bool ok = sink->fifo()->write([&](AudioBlock& output) {
        timers.inner.tic();

        if (effect) {
          effect->apply(index, input, output);
        } else {
          input.copyto(output);
        }

        timers.inner.toc();

        ++index;
      });

      if (!ok) {
        LOG(WARNING) << $("Audio pipe fifo overflow!");
      }
    });

    if (!ok) {
      LOG(WARNING) << $("Audio pipe fifo underflow!");
    }
  };

  auto millis = [](const std::chrono::steady_clock::duration& duration) {
    return std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
  };

  auto now = []() {
    return std::chrono::steady_clock::now();
  };

  auto index = uint64_t(0);
  auto timestamp = now();

  timers.outer.tic();

  if (state.doloop) {
    dowork(index, timers, source->timeout() * 2);
  }

  while (state.doloop) {
    dowork(index, timers, source->timeout());

    if (millis(now() - timestamp) > 10000) {
      LOG(DEBUG)
        << "Timing: "
        << "inner " << timers.inner.str() << " / "
        << "outer " << timers.outer.str();

      timers.outer.cls();
      timers.inner.cls();

      timestamp = now();
    }
  }
}

void AudioPipeline::onevent(const AudioEventCode code, const std::string& text) {
  if (code >= AudioEventCode::Error) {
    LOG(WARNING) << $("Aborting audio pipeline due to error: {0}!", text);
    std::unique_lock lock(eventmutex);
    close();
  }

  event(code, text);
}
