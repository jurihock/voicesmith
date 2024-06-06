#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/io/AudioEventCode.h>
#include <voicesmith/io/AudioEventCodeExtensions.h>

#include <eventpp/callbacklist.h>

class AudioEvent final : public eventpp::CallbackList<void(const AudioEventCode code, const std::string& text)> {

public:

  class Emitter {

  public:

    virtual ~Emitter() = default;

    virtual void subscribe(const Callback& callback) = 0;

  };

};
