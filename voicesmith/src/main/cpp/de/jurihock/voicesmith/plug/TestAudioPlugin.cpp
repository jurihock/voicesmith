#include <voicesmith/plug/TestAudioPlugin.h>

#include <voicesmith/Source.h>

#include <voicesmith/io/AudioSink.h>
#include <voicesmith/io/AudioSource.h>

#include <voicesmith/fx/BypassEffect.h>
#include <voicesmith/fx/NoiseEffect.h>
#include <voicesmith/fx/NullEffect.h>
#include <voicesmith/fx/SineEffect.h>
#include <voicesmith/fx/SweepEffect.h>

TestAudioPlugin::TestAudioPlugin(jna_callback* callback) :
  callback(callback) {
}

TestAudioPlugin::~TestAudioPlugin() {
  stop();
}

void TestAudioPlugin::setup(const std::optional<int> input,
                            const std::optional<int> output,
                            const std::optional<float> samplerate,
                            const std::optional<size_t> buffersize) {
  config.input = input;
  config.output = output;
  config.samplerate = samplerate;
  config.buffersize = buffersize;
}

void TestAudioPlugin::start() {
  if (state.pipeline != nullptr) {
    return;
  }

  auto bypass = std::make_shared<BypassEffect>();
  auto noise = std::make_shared<NoiseEffect>(1.f);
  auto null = std::make_shared<NullEffect>();
  auto sine = std::make_shared<SineEffect>(1.f, 440.f);
  auto sweep = std::make_shared<SweepEffect>(1.f, std::make_pair(440.f, 2*440.f), 2.f);

  auto source = std::make_shared<AudioSource>(config.input, config.samplerate, config.buffersize, sweep);
  auto sink = std::make_shared<AudioSink>(config.output, config.samplerate, config.buffersize);

  callback(!AudioPluginCallcode::Info, "START PIPE");

  state.pipeline = std::make_shared<AudioPipeline>(source, sink, bypass);
  state.pipeline->open();
  state.pipeline->start();
}

void TestAudioPlugin::stop() {
  if (state.pipeline == nullptr) {
    return;
  }

  callback(!AudioPluginCallcode::Info, "STOP PIPE");

  state.pipeline->stop();
  state.pipeline->close();
  state.pipeline = nullptr;
}
