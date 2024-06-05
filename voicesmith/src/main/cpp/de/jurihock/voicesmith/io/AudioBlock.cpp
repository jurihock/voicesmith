#include <voicesmith/io/AudioBlock.h>

#include <voicesmith/Source.h>

AudioBlock::AudioBlock(const size_t capacity) {
  block.resize(capacity);
}

size_t AudioBlock::size() const {
  return block.size();
}

void AudioBlock::copyfrom(const std::span<const float> samples) {
  if (block.size() != samples.size()) {
    LOG(WARNING) << $("Unequal block size: {0} (src), {1} (dst)",
                      samples.size(), block.size());
  }

  const size_t size = std::min(block.size(), samples.size());

  std::memcpy(block.data(), samples.data(), size * sizeof(float));
}

void AudioBlock::copyfrom(const AudioBlock& other) {
  if (block.size() != other.block.size()) {
    LOG(WARNING) << $("Unequal block size: {0} (src), {1} (dst)",
                      other.block.size(), block.size());
  }

  const size_t size = std::min(block.size(), other.block.size());

  std::memcpy(block.data(), other.block.data(), size * sizeof(float));
}

void AudioBlock::copyto(const std::span<float> samples) const {
  if (block.size() != samples.size()) {
    LOG(WARNING) << $("Unequal block size: {0} (src), {1} (dst)",
                      block.size(), samples.size());
  }

  const size_t size = std::min(block.size(), samples.size());

  std::memcpy(samples.data(), block.data(), size * sizeof(float));
}

void AudioBlock::copyto(AudioBlock& other) const {
  if (block.size() != other.block.size()) {
    LOG(WARNING) << $("Unequal block size: {0} (src), {1} (dst)",
                      block.size(), other.block.size());
  }

  const size_t size = std::min(block.size(), other.block.size());

  std::memcpy(other.block.data(), block.data(), size * sizeof(float));
}

AudioBlock::operator std::span<float>() {
  return std::span<float>(block.data(), block.size());
}

AudioBlock::operator std::span<const float>() const {
  return std::span<const float>(block.data(), block.size());
}
