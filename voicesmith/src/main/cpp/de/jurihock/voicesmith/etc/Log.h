#pragma once

#include <voicesmith/Header.h>

#include <android/log.h>
#include <fmt/format.h>

class Log final {

public:

  static constexpr auto TAG = "voicesmith.cpp";
  static constexpr auto DEBUG = ANDROID_LOG_DEBUG;
  static constexpr auto INFO = ANDROID_LOG_INFO;
  static constexpr auto WARN = ANDROID_LOG_WARN;
  static constexpr auto ERROR = ANDROID_LOG_ERROR;

  template<typename... Args>
  static void d(fmt::format_string<Args...> msg, Args&&... args) {
    const auto str = fmt::format(msg, std::forward<Args>(args)...);
    __android_log_print(DEBUG, TAG, "%s", str.c_str());
  }

  template<typename... Args>
  static void i(fmt::format_string<Args...> msg, Args&&... args) {
    const auto str = fmt::format(msg, std::forward<Args>(args)...);
    __android_log_print(INFO, TAG, "%s", str.c_str());
  }

  template<typename... Args>
  static void w(fmt::format_string<Args...> msg, Args&&... args) {
    const auto str = fmt::format(msg, std::forward<Args>(args)...);
    __android_log_print(WARN, TAG, "%s", str.c_str());
  }

  template<typename... Args>
  static void e(fmt::format_string<Args...> msg, Args&&... args) {
    const auto str = fmt::format(msg, std::forward<Args>(args)...);
    __android_log_print(ERROR, TAG, "%s", str.c_str());
  }

};
