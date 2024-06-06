#pragma once

enum class AudioEventCode {
  INFO,
  WARNING,
  SourceOverflow,
  SourceOverrun,
  SinkUnderflow,
  SinkUnderrun,
  PipeRead,
  PipeWrite,
  ERROR,
  SourceError,
  SinkError,
  PipeError,
};
