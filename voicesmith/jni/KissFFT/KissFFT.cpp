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
#include "../LogCat.h"
#include <stdlib.h>

// Custom structure to organize the KissFFT stuff
struct KissFFT
{
	kiss_fftr_cfg forward; 	// FFT handler
	kiss_fftr_cfg backward; // IFFT handler
	kiss_fft_cpx* spectrum; // Spectrum data buffer
	int size; 				// FFT size
};

JNIEXPORT jlong JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_alloc
(JNIEnv *, jobject, jint size)
{
	KissFFT* fft = new KissFFT();

	fft->forward = kiss_fftr_alloc(size, 0, NULL, NULL);
	fft->backward = kiss_fftr_alloc(size, 1, NULL, NULL);

	// The spectrum buffer contains N/2+1 float tuples (Re,Im)
	fft->spectrum = (kiss_fft_cpx*) malloc(
			sizeof(kiss_fft_cpx) * ((size / 2) + 1));

	fft->size = size;

	return (jlong) fft;
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_free
(JNIEnv *, jobject, jlong handle)
{
	KissFFT* fft = (KissFFT*)handle;

	free(fft->forward);
	free(fft->backward);
	free(fft->spectrum);

	free(fft);
}

JNIEXPORT void JNICALL Java_de_jurihock_voicesmith_dsp_KissFFT_fft
(JNIEnv* env, jobject, jlong handle, jfloatArray _buffer)
{
	KissFFT* fft = (KissFFT*)handle;

	float* buffer = (float*)env->GetPrimitiveArrayCritical(_buffer, 0);

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

	env->ReleasePrimitiveArrayCritical(_buffer, buffer, 0);
}
