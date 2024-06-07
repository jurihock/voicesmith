#pragma once

#include <voicesmith/Header.h>

class Debouncer final {

public:

  Debouncer(const uint64_t threshold = 1000) :
    threshold(threshold) {}

  void operator() (const bool countup) {
    if (countup) {
      if (++counter % threshold == 0) {
        flush();
      }
    } else {
      if (counter > 0) {
        flush();
        reset();
      }
    }
  }

  void reset() {
    counter = 0;
  }

  void flush() {
    callback(counter);
  }

  void onflush(const std::function<void(const uint64_t counter)>& newcallback) {
    callback = newcallback;
  }

private:

  const uint64_t threshold;

  uint64_t counter = 0;
  std::function<void(const uint64_t counter)> callback = [](uint64_t){};

};
