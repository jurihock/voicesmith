/*******************************************************************************
 * jni/KissFFT/KissFFT.cpp
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

#include "KissFFT.h"
#include "src/kiss_fftr.h"
#include "../Utils.h"
#include <stdlib.h>

// Custom structure to organize the KissFFT stuff
struct KissFFT
{
	int size; // FFT size
	kiss_fftr_cfg forward; // FFT handler
	kiss_fftr_cfg backward; // IFFT handler
	kiss_fft_cpx* spectrum; // spectrum data buffer
	float* halfBuffer; // fftshift auxiliary buffer
};

// Swaps the left and right halves of the value like the MATLAB fftshift function
void fftshift(KissFFT* fft, float* buffer)
{
	const int halfSize = (fft->size / 2);
	const int floatCount = sizeof(float) * halfSize;

	// buffer(0:N/2) => halfBuffer
	memcpy(fft->halfBuffer, buffer, floatCount);

	// buffer(N/2:N) => buffer(0:N/2)
	memcpy(buffer, buffer + halfSize, floatCount);

	// halfBuffer => buffer(N/2:N)
	memcpy(buffer + halfSize, fft->halfBuffer, floatCount);
}

JNIEXPORT jlong JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_alloc(JNIEnv *,
		jobject, jint size)
{
	KissFFT* fft = new KissFFT();

	fft->size = size;

	fft->forward = kiss_fftr_alloc(size, 0, NULL, NULL);
	fft->backward = kiss_fftr_alloc(size, 1, NULL, NULL);

	// The spectrum buffer contains N/2+1 float tuples (Re,Im)
	fft->spectrum = (kiss_fft_cpx*) malloc(
			sizeof(kiss_fft_cpx) * ((size / 2) + 1));

	fft->halfBuffer = (float*) malloc(sizeof(float) * (size / 2));

	return (jlong) fft;
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_free
(JNIEnv *, jobject, jlong handle)
{
	KissFFT* fft = (KissFFT*)handle;

	free(fft->forward);
	free(fft->backward);
	free(fft->spectrum);
	free(fft->halfBuffer);

	free(fft);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_fft
(JNIEnv* env, jobject, jlong handle, jfloatArray _buffer)
{
	KissFFT* fft = (KissFFT*)handle;

	float* buffer = (float*)env->GetPrimitiveArrayCritical(_buffer, 0);

	// Circular shift by N/2
	fftshift(fft, buffer);

	// fft(buffer) => spectrum
	kiss_fftr(fft->forward, buffer, fft->spectrum);

	// Re(Nyquist) => Im(DC)
	fft->spectrum[0].i = fft->spectrum[fft->size/2].r;

	// spectrum => buffer
	memcpy(buffer, fft->spectrum, sizeof(float)*fft->size);

//	LOG("FFT DC (%f,%f)", fft->spectrum[0].r, fft->spectrum[0].i);
//	LOG("FFT Nyquist (%f,%f)", fft->spectrum[fft->size/2].r, fft->spectrum[fft->size/2].i);

	env->ReleasePrimitiveArrayCritical(_buffer, buffer, 0);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_ifft
(JNIEnv* env, jobject, jlong handle, jfloatArray _buffer)
{
	KissFFT* fft = (KissFFT*)handle;

	float* buffer = (float*)env->GetPrimitiveArrayCritical(_buffer, 0);

	// buffer => spectrum
	memcpy(fft->spectrum, buffer, sizeof(float)*fft->size);

	// Im(DC) => Re(Nyquist)
	fft->spectrum[fft->size/2].r = fft->spectrum[0].i;
	fft->spectrum[0].i = 0;
	fft->spectrum[fft->size/2].i = 0;

	// ifft(spectrum) => buffer
	kiss_fftri(fft->backward, fft->spectrum, buffer);

	// Normalize buffer values by 1/N
	for (int i = 0; i < fft->size; i++)
	buffer[i] /= fft->size;

	// Circular shift by N/2
	fftshift(fft, buffer);

	env->ReleasePrimitiveArrayCritical(_buffer, buffer, 0);
}
