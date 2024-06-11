#include <voicesmith/io/AudioBlock.h>

#include <voicesmith/Source.h>

AudioBlock::AudioBlock(const size_t size_to_allocate) :
  data(size_to_allocate), view(data) {}

AudioBlock::AudioBlock(const std::span<float> data_to_attach) :
  data(0), view(data_to_attach) {}

size_t AudioBlock::size() const {
  return view.size();
}

void AudioBlock::copyfrom(const std::span<const float> samples) {
  if (view.size() != samples.size()) {
    Log::w("Unequal block size: {0} (src), {1} (dst)",
           samples.size(), view.size());
  }

  const size_t size = std::min(view.size(), samples.size());

  std::memcpy(view.data(), samples.data(), size * sizeof(float));
}

void AudioBlock::copyfrom(const AudioBlock& other) {
  if (view.size() != other.view.size()) {
    Log::w("Unequal block size: {0} (src), {1} (dst)",
           other.view.size(), view.size());
  }

  const size_t size = std::min(view.size(), other.view.size());

  std::memcpy(view.data(), other.view.data(), size * sizeof(float));
}

void AudioBlock::copyto(const std::span<float> samples) const {
  if (view.size() != samples.size()) {
    Log::w("Unequal block size: {0} (src), {1} (dst)",
           view.size(), samples.size());
  }

  const size_t size = std::min(view.size(), samples.size());

  std::memcpy(samples.data(), view.data(), size * sizeof(float));
}

void AudioBlock::copyto(AudioBlock& other) const {
  if (view.size() != other.view.size()) {
    Log::w("Unequal block size: {0} (src), {1} (dst)",
           view.size(), other.view.size());
  }

  const size_t size = std::min(view.size(), other.view.size());

  std::memcpy(other.view.data(), view.data(), size * sizeof(float));
}

AudioBlock::operator std::span<float>() {
  return view;
}

AudioBlock::operator std::span<const float>() const {
  return view;
}
