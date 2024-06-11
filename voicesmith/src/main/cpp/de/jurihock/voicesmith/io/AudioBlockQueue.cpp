#include <voicesmith/io/AudioBlockQueue.h>

#include <voicesmith/Source.h>

void AudioBlockQueue::resize(const size_t queuesize, const size_t blocksize) {
  FIFO<AudioBlock>::clear();

  blocks.clear();
  blocks.reserve(queuesize);

  memory.resize(queuesize * blocksize);
  std::fill(memory.begin(), memory.end(), 0);

  for (size_t i = 0; i < queuesize; ++i) {
    std::span<float> data { memory.data() + i * blocksize, blocksize };
    blocks.push_back(std::make_shared<AudioBlock>(data));
  }

  FIFO<AudioBlock>::resize(
    queuesize,
    [&](size_t index) { return blocks.at(index).get(); },
    [](AudioBlock* block) {});
}
