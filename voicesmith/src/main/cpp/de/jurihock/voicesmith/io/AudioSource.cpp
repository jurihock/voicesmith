#include <voicesmith/io/AudioSource.h>

#include <voicesmith/Source.h>

AudioSource::AudioSource(const std::optional<int> device,
                         const std::optional<float> samplerate,
                         const std::optional<size_t> blocksize,
                         const std::optional<size_t> channels,
                         const std::shared_ptr<AudioEffect> effect,
                         const std::shared_ptr<AudioBlockQueue> queue) :
  AudioStream(oboe::Direction::Input, device, samplerate, blocksize, channels),
  effect(effect),
  queue((queue != nullptr) ? queue : std::make_shared<AudioBlockQueue>()) {
  state.overflow.onflush([&](auto overflows){
    event(
      AudioEventCode::SourceOverflow,
      $("overflows={0} inner={1} outer={2}",
        overflows, state.index.inner, state.index.outer));
  });
}

std::shared_ptr<AudioEffect> AudioSource::fx() const {
  return effect;
}

std::shared_ptr<AudioBlockQueue> AudioSource::fifo() const {
  return queue;
}

void AudioSource::callback(const std::span<float> samples) {
  const bool ok = queue->write([&](AudioBlock& block) {
    if (effect) {
      effect->apply(state.index.inner, samples, block);
    } else {
      block.copyfrom(samples);
    }
    ++state.index.inner;
  });

  state.overflow(!ok);
  ++state.index.outer;
}

void AudioSource::onopen() {
  if (effect) {
    effect->reset(samplerate(), blocksize(), channels());
  }
}

void AudioSource::onstart() {
  state.index = {0, 0};
  state.overflow.reset();
}
