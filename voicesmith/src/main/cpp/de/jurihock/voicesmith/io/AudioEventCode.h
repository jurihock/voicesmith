#pragma once

enum class AudioEventCode {
  Info,
  Warning,
  SourceOverrun,
  SinkUnderrun,
  Error,
  SourceError,
  SinkError,
};
