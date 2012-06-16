/*******************************************************************************
 * jni/DAFX/NativeTimescaleProcessor.cpp
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

#include "NativeTimescaleProcessor.h"
#include "../MATLAB.h"
#include <stdlib.h>

struct Timescale
{
	int fftSize;
	float timescaleRatio;

	float* omegaA; // omega * analysisHopSize
	float* omegaS; // omega * synthesisHopSize
	float* prevPhaseA;
	float* prevPhaseS;
};

JNIEXPORT jlong JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeTimescaleProcessor_alloc(
		JNIEnv *, jobject, jint frameSize, jint analysisHopSize,
		jint synthesisHopSize)
{
	Timescale* ts = new Timescale();

	ts->fftSize = frameSize / 2;
	ts->timescaleRatio = (float) synthesisHopSize / (float) analysisHopSize;

	ts->omegaA = (float*) malloc(sizeof(float) * ts->fftSize);
	ts->omegaS = (float*) malloc(sizeof(float) * ts->fftSize);
	ts->prevPhaseA = (float*) malloc(sizeof(float) * ts->fftSize);
	ts->prevPhaseS = (float*) malloc(sizeof(float) * ts->fftSize);

	for (int i = 0; i < ts->fftSize; i++)
	{
		ts->omegaA[i] = 2 * PI * (i / (float) frameSize) // not fftSize!
				* (float) analysisHopSize;

		ts->omegaS[i] = 2 * PI * (i / (float) frameSize) // not fftSize!
				* (float) synthesisHopSize;

		ts->prevPhaseA[i] = 0;
		ts->prevPhaseS[i] = 0;
	}

	return (jlong) ts;
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeTimescaleProcessor_free
(JNIEnv *, jobject, jlong handle)
{
	Timescale* ts = (Timescale*)handle;

	free(ts->omegaA);
	free(ts->omegaS);
	free(ts->prevPhaseA);
	free(ts->prevPhaseS);

	free(ts);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeTimescaleProcessor_processFrame
(JNIEnv *env, jobject, jlong handle, jfloatArray _frame)
{
	Timescale* ts = (Timescale*)handle;

	if (ts->timescaleRatio == 1)
			return;

	float* frame = (float*)env->GetPrimitiveArrayCritical(_frame, 0);

	float re, im, abs;
	float nextPhaseA, nextPhaseS;
	float phaseDeltaA, phaseDeltaS;

	for (int i = 1; i < ts->fftSize; i++)
	{
		// Get source Re and Im parts
		re = frame[2 * i];
		im = frame[2 * i + 1];

		// Compute source phase
		nextPhaseA = atan2f(im, re);

		if (ts->timescaleRatio < 2)
		{
			// Compute phase deltas
			phaseDeltaA = princargf(nextPhaseA - (ts->prevPhaseA[i] + ts->omegaA[i]));
			phaseDeltaS = phaseDeltaA * ts->timescaleRatio;

			// Compute destination phase
			nextPhaseS = princargf((ts->prevPhaseS[i] + ts->omegaS[i]) + phaseDeltaS);

			// Save computed phase values
			ts->prevPhaseA[i] = nextPhaseA;
			ts->prevPhaseS[i] = nextPhaseS;
		}
		else
		{
			// Compute destination phase
			nextPhaseS = princargf(nextPhaseA * 2);
		}

		// Compute destination Re and Im parts
		abs = sqrtf(re * re + im * im);
		re = abs * cosf(nextPhaseS);
		im = abs * sinf(nextPhaseS);

		// Save new values
		frame[2 * i] = re;
		frame[2 * i + 1] = im;
	}

	env->ReleasePrimitiveArrayCritical(_frame, frame, 0);
}
