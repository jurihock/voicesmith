#include <voicesmith/io/AudioStream.h>

#include <voicesmith/Source.h>

AudioStream::AudioStream(const oboe::Direction direction,
                         const std::optional<int> device,
                         const std::optional<float> samplerate,
                         const std::optional<size_t> buffersize) :
  direction(direction) {
  config.device.set = device;
  config.device.get = std::nullopt;
  config.samplerate.set = samplerate;
  config.samplerate.get = std::nullopt;
  config.buffersize.set = buffersize;
  config.buffersize.get = std::nullopt;
  config.buffersize.max = std::nullopt;
  config.timeout = std::nullopt;
}

AudioStream::~AudioStream() {
  close();
}

int AudioStream::device() const {
  return config.device.get.value();
}

float AudioStream::samplerate() const {
  return config.samplerate.get.value();
}

size_t AudioStream::buffersize() const {
  return config.buffersize.get.value();
}

size_t AudioStream::maxbuffersize() const {
  return config.buffersize.max.value();
}

std::chrono::milliseconds AudioStream::timeout() const {
  return config.timeout.value();
}

void AudioStream::onopen() {
  events.open.reset();
}

void AudioStream::onopen(const std::function<void()> callback) {
  events.open.set(callback);
}

void AudioStream::onclose() {
  events.close.reset();
}

void AudioStream::onclose(const std::function<void()> callback) {
  events.close.set(callback);
}

void AudioStream::onstart() {
  events.start.reset();
}

void AudioStream::onstart(const std::function<void()> callback) {
  events.start.set(callback);
}

void AudioStream::onstop() {
  events.stop.reset();
}

void AudioStream::onstop(const std::function<void()> callback) {
  events.stop.set(callback);
}

void AudioStream::onxrun() {
  events.xrun.reset();
}

void AudioStream::onxrun(const std::function<void(const int32_t count)> callback) {
  events.xrun.set(callback);
}

void AudioStream::onerror() {
  events.error.reset();
}

void AudioStream::onerror(const std::function<bool(const oboe::Result error)> callback) {
  events.error.set(callback);
}

void AudioStream::open() {
  if (state.stream != nullptr) {
    return;
  }

  // https://github.com/google/oboe/blob/main/docs/FullGuide.md
  // https://developer.android.com/games/sdk/oboe/low-latency-audio
  // https://developer.android.com/ndk/guides/audio/audio-latency

  oboe::AudioStreamBuilder builder;

  builder.setDirection(direction);

  builder.setDeviceId(static_cast<int32_t>(config.device.set.value_or(oboe::Unspecified)));
  builder.setSampleRate(static_cast<int32_t>(config.samplerate.set.value_or(oboe::Unspecified)));
  builder.setFramesPerDataCallback(static_cast<int32_t>(config.buffersize.set.value_or(oboe::Unspecified)));

  builder.setChannelCount(oboe::ChannelCount::Mono);
  builder.setFormat(oboe::AudioFormat::Float);
  builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
  builder.setSampleRateConversionQuality(oboe::SampleRateConversionQuality::Fastest);
  builder.setSharingMode(oboe::SharingMode::Exclusive);

  if (direction == oboe::Direction::Input) {
    builder.setInputPreset(oboe::InputPreset::VoiceRecognition);
  }
  else if (direction == oboe::Direction::Output) {
    builder.setUsage(oboe::Usage::Game);
  }

  builder.setDataCallback(shared_from_this());
  builder.setErrorCallback(shared_from_this());

  const oboe::Result result = builder.openStream(state.stream);

  if (result != oboe::Result::OK) {
    close();
    throw std::runtime_error(
      $("Unable to open audio source stream: {0}",
        oboe::convertToText(result)));
  }

  config.device.get = state.stream->getDeviceId();
  config.samplerate.get = state.stream->getSampleRate();
  config.buffersize.get = state.stream->getFramesPerDataCallback();
  config.buffersize.max = state.stream->getBufferSizeInFrames();

  const double seconds = 1.0 * state.stream->getBufferSizeInFrames() / state.stream->getSampleRate();
  const double milliseconds = std::max(1.0, seconds * 1e+3);

  config.timeout = std::chrono::milliseconds(static_cast<int>(milliseconds));

  LOG(DEBUG) << "~ " << oboe::convertToText(state.stream->getDirection()) << " ~";
  LOG(DEBUG) << "DeviceId " << state.stream->getDeviceId();
  LOG(DEBUG) << "AudioApi " << oboe::convertToText(state.stream->getAudioApi());
  LOG(DEBUG) << "SharingMode " << oboe::convertToText(state.stream->getSharingMode());
  LOG(DEBUG) << "PerformanceMode " << oboe::convertToText(state.stream->getPerformanceMode());
  if (direction == oboe::Direction::Input) {
    LOG(DEBUG) << "InputPreset " << oboe::convertToText(state.stream->getInputPreset());
  }
  else if (direction == oboe::Direction::Output) {
    LOG(DEBUG) << "Usage " << oboe::convertToText(state.stream->getUsage());
  }
  LOG(DEBUG) << "SampleRate " << state.stream->getSampleRate();
  LOG(DEBUG) << "ChannelCount " << state.stream->getChannelCount();
  LOG(DEBUG) << "Format " << oboe::convertToText(state.stream->getFormat());
  LOG(DEBUG) << "BufferCapacityInFrames " << state.stream->getBufferCapacityInFrames();
  LOG(DEBUG) << "BufferSizeInFrames " << state.stream->getBufferSizeInFrames();
  LOG(DEBUG) << "FramesPerBurst " << state.stream->getFramesPerBurst();
  LOG(DEBUG) << "FramesPerDataCallback " << state.stream->getFramesPerDataCallback();
  LOG(DEBUG) << "Timeout " << config.timeout.value().count() << " ms";

  events.open();
}

void AudioStream::close() {
  if (state.stream == nullptr) {
    return;
  }

  if (state.stream->getState() != oboe::StreamState::Closed) {
    state.stream->stop();
    state.stream->close();
  }

  state.stream = nullptr;
  events.close();
}

void AudioStream::start() {
  if (state.stream == nullptr) {
    return;
  }

  events.start();
  state.xruns = state.stream->getXRunCount().value();
  state.stream->start();
}

void AudioStream::stop() {
  if (state.stream == nullptr) {
    return;
  }

  state.stream->stop();
  events.stop();
}

oboe::DataCallbackResult AudioStream::onAudioReady(oboe::AudioStream* stream, void* data, int32_t size) {
  const std::span<float> samples(
    static_cast<float*>(data),
    static_cast<size_t>(size));

  callback(samples);

  const int32_t xruns = stream->getXRunCount().value();

  if (state.xruns != xruns) {
    state.xruns = xruns;
    events.xrun(state.xruns);
  }

  return oboe::DataCallbackResult::Continue;
}

bool AudioStream::onError(oboe::AudioStream* stream, oboe::Result error) {
  return events.error(error);
}
