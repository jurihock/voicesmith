#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/FFT/PocketFFT.h>
#include <voicesmith/etc/FFT/PrettyFastFFT.h>

using FFT = std::conditional_t<std::is_same_v<int, double>, PocketFFT, PrettyFastFFT>;
