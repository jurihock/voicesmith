#include <voicesmith/io/AudioPipeline.h>

#include <voicesmith/Source.h>

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
    throw std::runtime_error(
      $("Unequal audio stream sample rate: {0} (source), {1} (sink)!",
        source->samplerate(), sink->samplerate()));
  }

  if (source->blocksize() != sink->blocksize()) {
    throw std::runtime_error(
      $("Unequal audio stream block size: {0} (source), {1} (sink)!",
        source->blocksize(), sink->blocksize()));
  }

  if (source->channels() != sink->channels()) {
    throw std::runtime_error(
      $("Unequal audio stream channel count: {0} (source), {1} (sink)!",
        source->channels(), sink->channels()));
  }

  const auto samplerate = source->samplerate();
  const auto blocksize = source->blocksize();
  const auto channels = source->channels();

  const auto fifosize = static_cast<size_t>(
    std::ceil(1 /* seconds */ * samplerate / blocksize));

  Log::i("Using samplerate={0} blocksize={1} fifosize={2} channels={3}",
         samplerate, blocksize, fifosize, channels);

  source->fifo()->resize(fifosize, blocksize);
  sink->fifo()->resize(fifosize, blocksize);

  source->subscribe([&](const AudioEventCode code, const std::string& data) {
    onevent(code, data);
  });

  sink->subscribe([&](const AudioEventCode code, const std::string& data) {
    onevent(code, data);
  });

  if (effect) {
    effect->reset(samplerate, blocksize, channels);
  }
}

void AudioPipeline::close() {
  stop();

  source->close();
  sink->close();
}

void AudioPipeline::start() {
  stop();

  state.thread = std::make_shared<std::thread>(
    [&]() { onloop(); });

  sink->start();
  source->start();

  state.loop = true;
  state.signal.notify_all();
}

void AudioPipeline::stop() {
  state.loop = false;

  if (state.thread != nullptr) {
    if (state.thread->joinable()) {
      state.thread->join();
    }
    state.thread = nullptr;
  }

  sink->stop();
  source->stop();

  sink->fifo()->flush();
  source->fifo()->flush();
}

void AudioPipeline::onloop() {
  const auto millis = [](const std::chrono::steady_clock::duration& duration) {
    return std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
  };

  const auto now = []() {
    return std::chrono::steady_clock::now();
  };

  auto ok = true;
  auto index = uint64_t(0);
  auto timeout = source->timeout();
  auto timestamp = now();

  timers_t timers;
  debouncers_t debouncers;

  debouncers.read.onflush([&](auto count){
    event(
      AudioEventCode::PipeRead,
      $("index={0} count={1} timeout={2}ms",
        index, count, timeout.count()));
  });

  debouncers.write.onflush([&](auto count){
    event(
      AudioEventCode::PipeWrite,
      $("index={0} count={1}",
        index, count));
  });

  if (!state.loop) {
    std::unique_lock lock(state.mutex);
    state.signal.wait_for(lock, std::chrono::seconds(1));
  }

  if (!state.loop) {
    return;
  }

  while (state.loop && ok && (millis(now() - timestamp) < 1'000)) {
    ok = oncycle(timers, debouncers, index, timeout * 3);
  }

  while (state.loop && ok) {
    Log::d("Timing: inner {0} / outer {1}",
           timers.inner.str(), timers.outer.str());

    timers.outer.cls();
    timers.inner.cls();
    timestamp = now();

    while (state.loop && ok && (millis(now() - timestamp) < 10'000)) {
      ok = oncycle(timers, debouncers, index, timeout);
    }
  }

  if (!ok) {
    Log::e("Aborting pipe loop due to an error!");
  }
}

bool AudioPipeline::oncycle(timers_t& timers, debouncers_t& debouncers, uint64_t& index, const std::chrono::milliseconds& timeout) const {
  bool okcycle = true;

  if (!index) {
    timers.outer.tic();
  }

  const bool okread = source->fifo()->read(timeout, [&](AudioBlock& input) {
    timers.outer.toc();
    timers.outer.tic();

    const bool okwrite = sink->fifo()->write([&](AudioBlock& output) {
      timers.inner.tic();

      try {
        if (effect) {
          effect->apply(index, input, output);
        } else {
          input.copyto(output);
        }
      }
      catch (const std::exception& exception) {
        event(AudioEventCode::PipeError, exception.what());
        okcycle = false;
      }

      timers.inner.toc();
      ++index;
    });

    debouncers.write(!okwrite);
  });

  debouncers.read(!okread);

  return okcycle;
}

void AudioPipeline::onevent(const AudioEventCode code, const std::string& data) {
  if (code >= AudioEventCode::ERROR) {
    Log::e("Aborting audio pipeline due to error: {0}!", data);
    std::unique_lock lock(eventmutex);
    close();
  }

  event(code, data);
}
