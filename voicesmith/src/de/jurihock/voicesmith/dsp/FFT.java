/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/FFT.java
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

package de.jurihock.voicesmith.dsp;

import static de.jurihock.voicesmith.dsp.Math.PI;
import static de.jurihock.voicesmith.dsp.Math.cos;
import static de.jurihock.voicesmith.dsp.Math.sqrt;
import de.jurihock.voicesmith.Disposable;

public final class FFT implements Disposable
{
	private final float[]	window;
	private final float[]	fftShiftBuffer;

	private KissFFT			fft;

	public FFT(int frameSize, int hopSize)
	{
		this.window = hannWindow(frameSize, hopSize, false);
		this.fftShiftBuffer = new float[frameSize / 2];
		this.fft = new KissFFT(frameSize);
	}

	public void dispose()
	{
		fft.dispose();
		fft = null;
	}

	public float[] window()
	{
		return window;
	}

	public void fft(float[] value)
	{
		fftshift(value);

		// Utils.tic("fft");
		fft.fft(value);
		// Utils.toc("fft");
	}

	public void ifft(float[] value)
	{
		// Utils.tic("ifft");
		fft.ifft(value);
		// Utils.toc("ifft");

		fftshift(value);
	}

	/**
	 * Swaps the left and right halves of the value like the MATLAB fftshift
	 * function.
	 * */
	private synchronized void fftshift(float[] value)
	{
		final int halfSize = value.length / 2;

		System.arraycopy(value, 0, fftShiftBuffer, 0, halfSize);
		System.arraycopy(value, halfSize, value, 0, halfSize);
		System.arraycopy(fftShiftBuffer, 0, value, halfSize, halfSize);
	}

	/**
	 * Returns first N coefficients of the N+1 Hann window.
	 * */
	private static float[] hannWindow(int N, int hopSize, boolean weight)
	{
		final float[] window = new float[N];

		// Compute Hann window coefficients
		for (int n = 0; n < N; n++)
		{
			window[n] = 0.5F * (1F - cos(2F * PI * n / (N - 1)));
		}

		// Compute COLA window weighting factor
		if (weight)
		{
			float cola_weighting = 0;

			for (int i = 0; i < N; i++)
				cola_weighting += window[i] * window[i];

			cola_weighting = 1 / sqrt(cola_weighting / hopSize);

			for (int i = 0; i < N; i++)
				window[i] *= cola_weighting;
		}

		return window;
	}
}
