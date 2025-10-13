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

  if (direction == oboe::Direction::Input) {
    state.xrun.onflush([&](auto){
      event(AudioEventCode::SourceOverrun, $("xruns={0}", state.xruns));
    });
  } else if (direction == oboe::Direction::Output) {
    state.xrun.onflush([&](auto){
      event(AudioEventCode::SinkUnderrun, $("xruns={0}", state.xruns));
    });
  }
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
  // https://source.android.com/docs/core/audio/latency/app

  oboe::AudioStreamBuilder builder;

  builder.setDirection(direction);

  builder.setDeviceId(static_cast<int32_t>(config.device.set.value_or(oboe::Unspecified)));
  builder.setSampleRate(static_cast<int32_t>(config.samplerate.set.value_or(oboe::Unspecified)));
  builder.setFramesPerDataCallback(static_cast<int32_t>(config.blocksize.set.value_or(oboe::Unspecified)));

  builder.setChannelCount(oboe::ChannelCount::Stereo);
  builder.setChannelConversionAllowed(true);
  builder.setFormat(oboe::AudioFormat::Float);
  builder.setFormatConversionAllowed(true);
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
      $("Unable to open the {0} stream: {1}",
        oboe::convertToText(direction),
        oboe::convertToText(result)));
  }

  config.device.get = state.stream->getDeviceId();
  config.samplerate.get = state.stream->getSampleRate();
  config.blocksize.get = state.stream->getFramesPerDataCallback() * 2; // TODO stereo
  config.blocksize.max = state.stream->getBufferSizeInFrames() * 2; // TODO stereo

  const double seconds = 1.0 * state.stream->getBufferSizeInFrames() / state.stream->getSampleRate();
  const double milliseconds = std::max(1.0, seconds * 1e+3);

  config.timeout = std::chrono::milliseconds(static_cast<int>(milliseconds));

  dump();

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

  const auto xruns = state.stream->getXRunCount();

  state.xrun.reset();
  state.xruns = xruns ? xruns.value() : 0;

  const oboe::Result result = state.stream->start();

  if (result != oboe::Result::OK) {
    stop();
    throw std::runtime_error(
      $("Unable to start the {0} stream: {1}",
        oboe::convertToText(direction),
        oboe::convertToText(result)));
  }
}

void AudioStream::stop() {
  if (state.stream == nullptr) {
    return;
  }

  onstop();

  state.stream->stop();
}

void AudioStream::dump() const {
  Log::d("~ {0} ~", oboe::convertToText(state.stream->getDirection()));
  Log::d("DeviceId {0}", state.stream->getDeviceId());
  Log::d("AudioApi {0}", oboe::convertToText(state.stream->getAudioApi()));
  Log::d("SharingMode {0}", oboe::convertToText(state.stream->getSharingMode()));
  Log::d("PerformanceMode {0}", oboe::convertToText(state.stream->getPerformanceMode()));
  if (direction == oboe::Direction::Input) {
    Log::d("InputPreset {0}", oboe::convertToText(state.stream->getInputPreset()));
  }
  else if (direction == oboe::Direction::Output) {
    Log::d("Usage {0}", oboe::convertToText(state.stream->getUsage()));
  }
  Log::d("PerformanceHintEnabled {0}", state.stream->isPerformanceHintEnabled() ? "true" : "false");
  Log::d("XRunCountSupported {0}", state.stream->isXRunCountSupported() ? "true" : "false");
  Log::d("SampleRate {0}", state.stream->getSampleRate());
  Log::d("HardwareSampleRate {0}", state.stream->getHardwareSampleRate());
  Log::d("ChannelCount {0}", state.stream->getChannelCount());
  Log::d("HardwareChannelCount {0}", state.stream->getHardwareChannelCount());
  Log::d("Format {0}", oboe::convertToText(state.stream->getFormat()));
  Log::d("HardwareFormat {0}", oboe::convertToText(state.stream->getHardwareFormat()));
  Log::d("BufferCapacityInFrames {0}", state.stream->getBufferCapacityInFrames());
  Log::d("BufferSizeInFrames {0}", state.stream->getBufferSizeInFrames());
  Log::d("FramesPerBurst {0}", state.stream->getFramesPerBurst());
  Log::d("FramesPerDataCallback {0}", state.stream->getFramesPerDataCallback());
  Log::d("Timeout {0}ms", config.timeout.value().count());
}

oboe::DataCallbackResult AudioStream::onAudioReady(oboe::AudioStream* stream, void* data, int32_t size) {
  const std::span<float> samples(
    static_cast<float*>(data),
    static_cast<size_t>(size * 2)); // TODO stereo

  callback(samples);

  const auto xruns = stream->getXRunCount();

  if (xruns) {
    state.xrun(xruns.value() > state.xruns);
    state.xruns = xruns.value();
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
