#include <voicesmith/io/AudioSink.h>

#include <voicesmith/Source.h>

AudioSink::AudioSink(const std::optional<int> device,
                     const std::optional<float> samplerate,
                     const std::optional<size_t> blocksize,
                     const std::shared_ptr<AudioEffect> effect,
                     const std::shared_ptr<AudioBlockQueue> queue) :
  AudioStream(oboe::Direction::Output, device, samplerate, blocksize),
  effect(effect),
  queue((queue != nullptr) ? queue : std::make_shared<AudioBlockQueue>()) {}

std::shared_ptr<AudioEffect> AudioSink::fx() const {
  return effect;
}

std::shared_ptr<AudioBlockQueue> AudioSink::fifo() const {
  return queue;
}

void AudioSink::callback(const std::span<float> samples) {
  const bool ok = queue->read([&](AudioBlock& block) {
    if (effect) {
      effect->apply(index.inner, block, samples);
    } else {
      block.copyto(samples);
    }
    ++index.inner;
  });

  if (ok) {
    if (underflows.accumulate) {
      LOG(INFO) << $("Audio sink {0} passed callbacks", underflows.count);
      underflows = {false, 0};
    }
  } else {
    if (underflows.accumulate) {
      ++underflows.count;
    } else if (!index.outer) {
      underflows = {true, 1};
    } else {
      LOG(WARNING) << $("Audio sink fifo underflow! #{0}", index.outer);
    }
  }

  ++index.outer;
}

void AudioSink::onopen() {
  if (effect) {
    effect->reset(samplerate(), blocksize());
  }
}

void AudioSink::onstart() {
  index = {0, 0};
  underflows = {false, 0};
}
