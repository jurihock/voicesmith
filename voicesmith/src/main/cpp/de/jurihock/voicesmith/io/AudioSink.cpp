#include <voicesmith/io/AudioSink.h>

#include <voicesmith/Source.h>

AudioSink::AudioSink(const std::optional<int> device,
                     const std::optional<float> samplerate,
                     const std::optional<size_t> blocksize,
                     const std::shared_ptr<AudioEffect> effect,
                     const std::shared_ptr<AudioBlockQueue> queue) :
  AudioStream(oboe::Direction::Output, device, samplerate, blocksize),
  effect(effect),
  queue((queue != nullptr) ? queue : std::make_shared<AudioBlockQueue>()) {
  state.underflow.onflush([&](auto underflows){
    event(
      AudioEventCode::SinkUnderflow,
      $("underflows={0} inner={1} outer={2}",
        underflows, state.index.inner, state.index.outer));
  });
}

std::shared_ptr<AudioEffect> AudioSink::fx() const {
  return effect;
}

std::shared_ptr<AudioBlockQueue> AudioSink::fifo() const {
  return queue;
}

void AudioSink::callback(const std::span<float> samples) {
  const bool ok = queue->read([&](AudioBlock& block) {
    if (effect) {
      effect->apply(state.index.inner, block, samples);
    } else {
      block.copyto(samples);
    }
    ++state.index.inner;
  });

  state.underflow(!ok);
  ++state.index.outer;
}

void AudioSink::onopen() {
  if (effect) {
    effect->reset(samplerate(), blocksize());
  }
}

void AudioSink::onstart() {
  state.index = {0, 0};
  state.underflow.reset();
}
