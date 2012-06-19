#ifndef Utils_h
#define Utils_h

#define LOGCAT_TAG "Voicesmith"
#define LOGGING 0

#if LOGGING
	#include <android/log.h>
	#define LOG(message, args...) __android_log_print(ANDROID_LOG_DEBUG, LOGCAT_TAG, message, args)
#else
	#define LOG(message, args...) while(0){}
#endif

#endif
