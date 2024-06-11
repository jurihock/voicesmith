#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/FIFO.h>
#include <voicesmith/io/AudioBlock.h>

class AudioBlockQueue final : public FIFO<AudioBlock> {

public:

  void resize(const size_t queuesize, const size_t blocksize);

private:

  std::vector<float> memory;
  std::vector<std::shared_ptr<AudioBlock>> blocks;

};
