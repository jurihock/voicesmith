#ifndef LogCat_h
#define LogCat_h

#include <android/log.h>

#define LOGCAT_TAG "Voicesmith"
#define LOGGING 0

#if LOGGING
	#define LOG(message, args...) __android_log_print(ANDROID_LOG_DEBUG, LOGCAT_TAG, message, args)
#else
	#define LOG(message, args...) while(0){}
#endif

#endif
