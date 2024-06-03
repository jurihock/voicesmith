#pragma once

enum class AudioPluginCallcode {
  Info,
  Warning,
  Error
};

constexpr int operator!(AudioPluginCallcode code) noexcept {
  return static_cast<int>(code);
}
