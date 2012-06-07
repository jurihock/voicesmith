/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/dafx/ResampleProcessor.java
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

package de.jurihock.voicesmith.dsp.dafx;

import static de.jurihock.voicesmith.dsp.Math.floor;
import de.jurihock.voicesmith.Disposable;
import de.jurihock.voicesmith.dsp.STFT;

@Deprecated
public final class ResampleProcessor implements Disposable
{
	private final int		lx;
	private final int[]		ix;
	private final int[]		ix1;
	private final float[]	dx;
	private final float[]	dx1;

	private final float[]	frameBufferIn;

	private final boolean	doInverseFFT;
	private STFT			stft;

	public ResampleProcessor(int frameSizeIn, int frameSizeOut, int hopSizeIn, boolean doInverseFFT)
	{
		lx = frameSizeOut;

		final float[] x = new float[lx];

		ix = new int[lx];
		ix1 = new int[lx];
		dx = new float[lx];
		dx1 = new float[lx];

		for (int i = 0; i < lx; i++)
		{
			x[i] = 1 + i * frameSizeIn / (float) lx;

			ix[i] = (int) floor(x[i]);
			ix1[i] = ix[i] + 1;
			dx[i] = x[i] - ix[i];
			dx1[i] = 1 - dx[i];
		}

		frameBufferIn = new float[frameSizeIn + 1];

		this.doInverseFFT = doInverseFFT;

		if (doInverseFFT) stft = new STFT(frameSizeIn, hopSizeIn);
		else stft = null;
	}

	public void dispose()
	{
		if (doInverseFFT)
		{
			stft.dispose();
			stft = null;
		}
	}

	public void processFrame(float[] frameIn, float[] frameOut)
	{
		if (doInverseFFT) stft.ifft(frameIn);

		System.arraycopy(frameIn, 0, frameBufferIn, 0, frameIn.length);
		frameBufferIn[frameBufferIn.length - 1] = 0;

		for (int i = 0; i < lx; i++)
		{
			float value = frameBufferIn[ix[i] - 1] * dx1[i]
				+ frameBufferIn[ix1[i] - 1] * dx[i];

			frameOut[i] = value;
		}
	}
}
