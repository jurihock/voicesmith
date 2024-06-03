#include <voicesmith/io/AudioBuffer.h>

#include <voicesmith/Source.h>

AudioBuffer::AudioBuffer(const size_t capacity) {
  buffer.resize(capacity);
}

size_t AudioBuffer::size() const {
  return buffer.size();
}

void AudioBuffer::copyfrom(const std::span<const float> samples) {
  if (buffer.size() != samples.size()) {
    LOG(WARNING) << $("Unequal buffer size: {0} (src), {1} (dst)",
                      samples.size(), buffer.size());
  }

  const size_t size = std::min(buffer.size(), samples.size());

  std::memcpy(buffer.data(), samples.data(), size * sizeof(float));
}

void AudioBuffer::copyfrom(const AudioBuffer& other) {
  if (buffer.size() != other.buffer.size()) {
    LOG(WARNING) << $("Unequal buffer size: {0} (src), {1} (dst)",
                      other.buffer.size(), buffer.size());
  }

  const size_t size = std::min(buffer.size(), other.buffer.size());

  std::memcpy(buffer.data(), other.buffer.data(), size * sizeof(float));
}

void AudioBuffer::copyto(const std::span<float> samples) const {
  if (buffer.size() != samples.size()) {
    LOG(WARNING) << $("Unequal buffer size: {0} (src), {1} (dst)",
                      buffer.size(), samples.size());
  }

  const size_t size = std::min(buffer.size(), samples.size());

  std::memcpy(samples.data(), buffer.data(), size * sizeof(float));
}

void AudioBuffer::copyto(AudioBuffer& other) const {
  if (buffer.size() != other.buffer.size()) {
    LOG(WARNING) << $("Unequal buffer size: {0} (src), {1} (dst)",
                      buffer.size(), other.buffer.size());
  }

  const size_t size = std::min(buffer.size(), other.buffer.size());

  std::memcpy(other.buffer.data(), buffer.data(), size * sizeof(float));
}

AudioBuffer::operator std::span<float>() {
  return std::span<float>(buffer.data(), buffer.size());
}

AudioBuffer::operator std::span<const float>() const {
  return std::span<const float>(buffer.data(), buffer.size());
}
