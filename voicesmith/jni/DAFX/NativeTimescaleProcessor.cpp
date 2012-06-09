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
	float* omega; // Omega_k incl. H_A
	float* prevPhaseA;
	float* prevPhaseS;
	float  phaseScaleRatio;
	float  frameSize;
};

JNIEXPORT jlong JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeTimescaleProcessor_alloc
(JNIEnv *, jobject, jint frameSize, jint analysisHopSize, jint synthesisHopSize)
{
	Timescale* ts = new Timescale();

	ts->omega = (float*) malloc(sizeof(float) * frameSize);
	ts->prevPhaseA = (float*) malloc(sizeof(float) * frameSize);
	ts->prevPhaseS = (float*) malloc(sizeof(float) * frameSize);

	for (int i = 0; i < frameSize; i++)
	{
		// TODO: 2pi k/N?
		ts->omega[i] = 2 * PI * (i / (float) frameSize)
			* (float) analysisHopSize;

		ts->prevPhaseA[i] = 0;
		ts->prevPhaseS[i] = 0;
	}

	ts->phaseScaleRatio = (float) synthesisHopSize / (float) analysisHopSize;
	ts->frameSize = frameSize;

	return (jlong) ts;
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeTimescaleProcessor_free
(JNIEnv *, jobject, jlong handle)
{
	Timescale* ts = (Timescale*)handle;
	free(ts->omega);
	free(ts->prevPhaseA);
	free(ts->prevPhaseS);
	free(ts);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_dafx_NativeTimescaleProcessor_processFrame
(JNIEnv *env, jobject, jlong handle, jfloatArray _frame)
{
	Timescale* ts = (Timescale*)handle;
	float* frame = (float*)env->GetPrimitiveArrayCritical(_frame, 0);

	int fftSize = ts->frameSize / 2;

	float re, im, abs;
	float nextPhaseA, nextPhaseS;
	float phaseDeltaA, phaseDeltaS;

	for (int i = 1; i < fftSize; i++)
	{
		// Get source Re and Im parts
		re = frame[2 * i];
		im = frame[2 * i + 1];

		// Compute source phase
		nextPhaseA = atan2f(im, re);

		// Compute phase deltas
		phaseDeltaA = princargf(nextPhaseA - ts->prevPhaseA[i] - ts->omega[i]) + ts->omega[i];
		phaseDeltaS = phaseDeltaA * ts->phaseScaleRatio;

		// Compute destination phase
		nextPhaseS = princargf(ts->prevPhaseS[i] + phaseDeltaS);

		// Save computed phase values
		ts->prevPhaseA[i] = nextPhaseA;
		ts->prevPhaseS[i] = nextPhaseS;

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
