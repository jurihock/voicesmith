#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/io/AudioEventCode.h>

constexpr int operator!(AudioEventCode code) noexcept {
  return static_cast<int>(code);
}
