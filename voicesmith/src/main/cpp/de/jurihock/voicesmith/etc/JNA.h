#pragma once

#include <voicesmith/Header.h>

#include <jni.h>

#ifdef __cplusplus
#define jna extern "C"
#else
#define jna
#endif

typedef jlong jna_pointer;
typedef void jna_callback(const int code, const char* text);

struct jna_result {

  bool okay;
  char* error;
  int max_error_length;
  int min_error_length;

  bool ok() {
    okay = true;
    min_error_length = 0;
    return okay;
  }

  bool nok(const std::string& what) {
    okay = false;
    min_error_length = std::min(max_error_length, static_cast<int>(what.size()));
    std::memcpy(error, what.data(), min_error_length);
    return okay;
  }

  bool nok(const std::exception& exception) {
    return nok(exception.what());
  }

};
