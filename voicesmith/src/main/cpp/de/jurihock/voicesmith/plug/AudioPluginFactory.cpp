#include <voicesmith/plug/AudioPluginFactory.h>

#include <voicesmith/Source.h>

#include <voicesmith/plug/AudioPlugin.h>
#include <voicesmith/plug/TestAudioPlugin.h>

TestAudioPlugin* make_plugin(const std::string& name, jna_callback* callback) {
  if (name == "TestAudioPlugin") {
    return new TestAudioPlugin(callback);
  }

  throw std::runtime_error("Invalid plugin name " + name + "!");
}

jna bool voicesmith_plugin_open(const char* name, jna_callback* callback, jna_pointer* pointer, jna_result* result) {
  if (*pointer != jna_nullptr) {
    return result->ok();
  }

  try {
    auto plugin = make_plugin(name, callback);
    *pointer = reinterpret_cast<jna_pointer>(plugin);
    return result->ok();
  }
  catch (const std::exception& exception) {
    *pointer = jna_nullptr;
    return result->nok(exception);
  }
}

jna bool voicesmith_plugin_setup(int input, int output, int samplerate, int blocksize, int channels, jna_pointer* pointer, jna_result* result) {
  if (*pointer == jna_nullptr) {
    return result->nok("Invalid plugin pointer!");
  }

  const auto optional = []<typename T>(auto value) -> std::optional<T> {
    return (value > 0) ? std::optional<T>(static_cast<T>(value)) : std::nullopt;
  };

  try {
    auto plugin = reinterpret_cast<AudioPlugin*>(*pointer);
    plugin->setup(
      optional.template operator()<int>(input),
      optional.template operator()<int>(output),
      optional.template operator()<float>(samplerate),
      optional.template operator()<size_t>(blocksize),
      optional.template operator()<size_t>(channels));
    return result->ok();
  }
  catch (const std::exception& exception) {
    return result->nok(exception);
  }
}

jna bool voicesmith_plugin_set(const char* param, const char* value, jna_pointer* pointer, jna_result* result) {
  if (*pointer == jna_nullptr) {
    return result->nok("Invalid plugin pointer!");
  }

  try {
    auto plugin = reinterpret_cast<AudioPlugin*>(*pointer);
    plugin->set(param, value);
    return result->ok();
  }
  catch (const std::exception& exception) {
    return result->nok(exception);
  }
}

jna bool voicesmith_plugin_start(jna_pointer* pointer, jna_result* result) {
  if (*pointer == jna_nullptr) {
    return result->ok();
  }

  try {
    auto plugin = reinterpret_cast<AudioPlugin*>(*pointer);
    plugin->start();
    return result->ok();
  }
  catch (const std::exception& exception) {
    return result->nok(exception);
  }
}

jna bool voicesmith_plugin_stop(jna_pointer* pointer, jna_result* result) {
  if (*pointer == jna_nullptr) {
    return result->ok();
  }

  try {
    auto plugin = reinterpret_cast<AudioPlugin*>(*pointer);
    plugin->stop();
    return result->ok();
  }
  catch (const std::exception& exception) {
    return result->nok(exception);
  }
}

jna bool voicesmith_plugin_close(jna_pointer* pointer, jna_result* result) {
  if (*pointer == jna_nullptr) {
    return result->ok();
  }

  try {
    auto plugin = reinterpret_cast<AudioPlugin*>(*pointer);
    delete plugin;
    *pointer = jna_nullptr;
    return result->ok();
  }
  catch (const std::exception& exception) {
    *pointer = jna_nullptr;
    return result->nok(exception);
  }
}
