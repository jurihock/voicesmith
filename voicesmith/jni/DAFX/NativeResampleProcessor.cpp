/*******************************************************************************
 * jni/DAFX/NativeResampleProcessor.cpp
 * is part of the Voicesmith project
 * <http://voicesmith.jurihock.de>
 * 
 * Copyright (C) 2011-2012 Juergen Hock
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

#include "NativeResampleProcessor.h"
#include "../SecretRabbitCode/src/samplerate.h"
#include "../LogCat.h"
#include <stdlib.h>

struct SRC
{
	SRC_STATE *state;
	SRC_DATA data;
	double ratio;
	int error;
};

JNIEXPORT jlong JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeResampleProcessor_alloc
  (JNIEnv *, jobject, jint frameSizeIn, jint frameSizeOut)
{
	SRC *src = new SRC();

	src->state = src_new(SRC_LINEAR, 1, &src->error);
	src->data.end_of_input = 0;
	src->data.input_frames = frameSizeIn;
	src->data.output_frames = frameSizeOut;
	src->data.src_ratio = (double)frameSizeOut / (double)frameSizeIn;

	return (jlong)src;
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeResampleProcessor_free
  (JNIEnv *, jobject, jlong handle)
{
	SRC *src = (SRC*)handle;

	src_delete(src->state);
	free(src);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeResampleProcessor_processFrame
  (JNIEnv *env, jobject, jlong handle, jfloatArray _frameIn, jfloatArray _frameOut)
{
	SRC *src = (SRC*)handle;
	float* frameIn = (float*)env->GetPrimitiveArrayCritical(_frameIn, 0);
	float* frameOut = (float*)env->GetPrimitiveArrayCritical(_frameOut, 0);

	src->data.data_in = frameIn;
	src->data.data_out = frameOut;

	int result = src_process(src->state, &src->data);
//	LOG("SRC RESULT %i", result);
//	LOG("SRC ERROR %i", src->error);
//	LOG("SRC input_frames_used %i", src->data.input_frames_used);
//	LOG("SRC output_frames_gen %i", src->data.output_frames_gen);

	env->ReleasePrimitiveArrayCritical(_frameOut, frameOut, 0);
	env->ReleasePrimitiveArrayCritical(_frameIn, frameIn, 0);
}
