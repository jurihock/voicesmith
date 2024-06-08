#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/JNA.h>
#include <voicesmith/io/AudioPipeline.h>
#include <voicesmith/plug/AudioPlugin.h>

#include <voicesmith/fx/DelayEffect.h>
#include <voicesmith/fx/MultiEffect.h>
#include <voicesmith/fx/PitchTimbreShiftEffect.h>

class TestAudioPlugin final : public AudioPlugin {

public:

  TestAudioPlugin(jna_callback* callback);
  ~TestAudioPlugin();

  void setup(const std::optional<int> input,
             const std::optional<int> output,
             const std::optional<float> samplerate,
             const std::optional<size_t> blocksize) override;

  void set(const std::string& param,
           const std::string& value) override;

  void start() override;
  void stop() override;

private:

  jna_callback* const callback;

  struct {

    std::optional<int> input;
    std::optional<int> output;
    std::optional<float> samplerate;
    std::optional<size_t> blocksize;

  } config;

  struct {
    std::shared_ptr<AudioPipeline> pipeline;
    std::shared_ptr<MultiEffect> effects;
  } state;

};
