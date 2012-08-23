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

#include "NativeResampleProcessor.h"
#include "../Math/MathDefs.h"
#include <stdlib.h>

struct Resample
{
	int frameSizeIn;
	int frameSizeOut;

	int* ix;
	int* ix1;
	float* dx;
	float* dx1;
};

JNIEXPORT jlong JNICALL Java_de_jurihock_voicesmith_dsp_processors_NativeResampleProcessor_alloc(
		JNIEnv *, jobject, jint frameSizeIn, jint frameSizeOut)
{
	Resample *rs = new Resample();

	rs->frameSizeIn = frameSizeIn;
	rs->frameSizeOut = frameSizeOut;

	rs->ix = (int*) malloc(sizeof(int) * frameSizeOut);
	rs->ix1 = (int*) malloc(sizeof(int) * frameSizeOut);
	rs->dx = (float*) malloc(sizeof(float) * frameSizeOut);
	rs->dx1 = (float*) malloc(sizeof(float) * frameSizeOut);

	for (int i = 0; i < frameSizeOut; i++)
	{
		float x = 1 + i * (float) frameSizeIn / (float) frameSizeOut;

		rs->ix[i] = floor(x);
		rs->ix1[i] = rs->ix[i] + 1;
		rs->dx[i] = x - rs->ix[i];
		rs->dx1[i] = 1 - rs->dx[i];
	}

	return (jlong) rs;
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_processors_NativeResampleProcessor_free
(JNIEnv *, jobject, jlong handle)
{
	Resample *rs = (Resample*)handle;

	free(rs->ix);
	free(rs->ix1);
	free(rs->dx);
	free(rs->dx1);

	free(rs);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_processors_NativeResampleProcessor_processFrame
(JNIEnv *env, jobject, jlong handle, jfloatArray _frameIn, jfloatArray _frameOut)
{
	Resample *rs = (Resample*)handle;
	float* frameIn = (float*)env->GetPrimitiveArrayCritical(_frameIn, 0);
	float* frameOut = (float*)env->GetPrimitiveArrayCritical(_frameOut, 0);

	int lastValue = rs->frameSizeOut - 1;

	for (int i = 0; i < lastValue; i++)
	{
		frameOut[i] =
		frameIn[rs->ix[i] - 1] * rs->dx1[i] +
		frameIn[rs->ix1[i] - 1] * rs->dx[i];
	}

	frameOut[lastValue] = frameIn[rs->ix[lastValue] - 1] * rs->dx1[lastValue]; // + 0 * rs->dx[lastValue]

	env->ReleasePrimitiveArrayCritical(_frameOut, frameOut, 0);
	env->ReleasePrimitiveArrayCritical(_frameIn, frameIn, 0);
}
