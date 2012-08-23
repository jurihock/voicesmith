/*******************************************************************************
 * Voicesmith <http://voicesmith.jurihock.de/>
 * Copyright (c) 2011-2012 Juergen Hock
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
