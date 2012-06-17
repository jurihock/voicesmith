// Be sure to include the LogCat library in Android.mk
// Log example: LOG("FFT size %i", fft->size);

#ifndef LogCat_h
#define LogCat_h

#include <android/log.h>

#define LOG(message, args...) __android_log_print(ANDROID_LOG_DEBUG, "Voicesmith", message, args)

#endif
