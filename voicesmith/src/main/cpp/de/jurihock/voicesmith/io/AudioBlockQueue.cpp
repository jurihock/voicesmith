#include <voicesmith/io/AudioBlockQueue.h>

#include <voicesmith/Source.h>

void AudioBlockQueue::resize(const size_t queuesize, const size_t blocksize) {
  FIFO<AudioBlock>::resize(
    queuesize,
    [blocksize]() { return new AudioBlock(blocksize); },
    [](AudioBlock* block) { delete block; });
}
