#include <voicesmith/io/AudioStream.h>

#include <voicesmith/Source.h>

AudioStream::AudioStream(const oboe::Direction direction,
                         const std::optional<int> device,
                         const std::optional<float> samplerate,
                         const std::optional<size_t> blocksize) :
  direction(direction) {
  config.device.set = device;
  config.device.get = std::nullopt;
  config.samplerate.set = samplerate;
  config.samplerate.get = std::nullopt;
  config.blocksize.set = blocksize;
  config.blocksize.get = std::nullopt;
  config.blocksize.max = std::nullopt;
  config.timeout = std::nullopt;
}

AudioStream::~AudioStream() {
  close();
}

void AudioStream::subscribe(const AudioEvent::Callback& callback) {
  event.append(callback);
}

int AudioStream::device() const {
  return config.device.get.value();
}

float AudioStream::samplerate() const {
  return config.samplerate.get.value();
}

size_t AudioStream::blocksize() const {
  return config.blocksize.get.value();
}

size_t AudioStream::maxblocksize() const {
  return config.blocksize.max.value();
}

std::chrono::milliseconds AudioStream::timeout() const {
  return config.timeout.value();
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
  builder.setFramesPerDataCallback(static_cast<int32_t>(config.blocksize.set.value_or(oboe::Unspecified)));

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
  config.blocksize.get = state.stream->getFramesPerDataCallback();
  config.blocksize.max = state.stream->getBufferSizeInFrames();

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
  LOG(DEBUG) << "PerformanceHintEnabled " << (state.stream->isPerformanceHintEnabled() ? "true" : "false");
  LOG(DEBUG) << "XRunCountSupported " << (state.stream->isXRunCountSupported() ? "true" : "false");

  onopen();
}

void AudioStream::close() {
  if (state.stream == nullptr) {
    return;
  }

  onclose();

  const auto x = state.stream->getState();
  const auto y = {
    oboe::StreamState::Closing,
    oboe::StreamState::Closed,
    oboe::StreamState::Disconnected
  };

  if (std::none_of(y.begin(), y.end(), [x](auto y){ return x == y; })) {
    state.stream->stop();
    state.stream->close();
  }

  state.stream = nullptr;
}

void AudioStream::start() {
  if (state.stream == nullptr) {
    return;
  }

  onstart();

  state.xruns = state.stream->getXRunCount().value();
  state.stream->start();
}

void AudioStream::stop() {
  if (state.stream == nullptr) {
    return;
  }

  onstop();

  state.stream->stop();
}

oboe::DataCallbackResult AudioStream::onAudioReady(oboe::AudioStream* stream, void* data, int32_t size) {
  const std::span<float> samples(
    static_cast<float*>(data),
    static_cast<size_t>(size));

  callback(samples);

  const auto xruns = stream->getXRunCount();

  if (!xruns) {
    LOG(ERROR) << oboe::convertToText(xruns.error());
  } else if (state.xruns != xruns.value()) {
    state.xruns = xruns.value();

    if (direction == oboe::Direction::Input) {
      event(AudioEventCode::SourceOverrun, $("xruns={0}", state.xruns));
    } else if (direction == oboe::Direction::Output) {
      event(AudioEventCode::SinkUnderrun, $("xruns={0}", state.xruns));
    }
  }

  return oboe::DataCallbackResult::Continue;
}

bool AudioStream::onError(oboe::AudioStream* stream, oboe::Result error) {
  close();

  if (direction == oboe::Direction::Input) {
    event(AudioEventCode::SourceError, oboe::convertToText(error));
  } else if (direction == oboe::Direction::Output) {
    event(AudioEventCode::SinkError, oboe::convertToText(error));
  }

  return true;
}
