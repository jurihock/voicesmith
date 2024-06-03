#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/FIFO.h>
#include <voicesmith/io/AudioBuffer.h>

class AudioBufferQueue final : public FIFO<AudioBuffer> {

public:

  void resize(const size_t queuesize, const size_t buffersize);

};
