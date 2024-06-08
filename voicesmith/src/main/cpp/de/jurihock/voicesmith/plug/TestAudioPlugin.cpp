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
  state.effects = std::make_shared<MultiEffect>(
    std::initializer_list<std::shared_ptr<AudioEffect>>({
      std::make_shared<DelayEffect>(),
      std::make_shared<PitchTimbreShiftEffect>()
    }));
}

TestAudioPlugin::~TestAudioPlugin() {
  stop();
}

void TestAudioPlugin::setup(const std::optional<int> input,
                            const std::optional<int> output,
                            const std::optional<float> samplerate,
                            const std::optional<size_t> blocksize) {
  config.input = input;
  config.output = output;
  config.samplerate = samplerate;
  config.blocksize = blocksize;
}

void TestAudioPlugin::set(const std::string& param,
                          const std::string& value) {
  if (param == "delay") {
    state.effects->fx<DelayEffect>(0)->delay(value);
  }
  if (param == "pitch") {
    state.effects->fx<PitchTimbreShiftEffect>(1)->pitch(value);
  }
  if (param == "timbre") {
    state.effects->fx<PitchTimbreShiftEffect>(1)->timbre(value);
  }
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

  auto source = std::make_shared<AudioSource>(config.input, config.samplerate, config.blocksize);
  auto sink = std::make_shared<AudioSink>(config.output, config.samplerate, config.blocksize);
  auto pipe = std::make_shared<AudioPipeline>(source, sink, state.effects);

  pipe->subscribe([&](const AudioEventCode code, const std::string& data){
    callback(!code, data.c_str());
  });

  state.pipeline = pipe;
  state.pipeline->open();
  state.pipeline->start();
}

void TestAudioPlugin::stop() {
  if (state.pipeline == nullptr) {
    return;
  }

  state.pipeline->stop();
  state.pipeline->close();
  state.pipeline = nullptr;
}
