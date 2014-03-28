/*
 * Voicesmith <http://voicesmith.jurihock.de/>
 *
 * Copyright (c) 2011-2014 Juergen Hock
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
 */

package de.jurihock.voicesmith.dsp.processors;

import static de.jurihock.voicesmith.dsp.Math.floor;
import de.jurihock.voicesmith.Disposable;

/**
 * Replaced by the NativeResampleProcessor.
 * */
@Deprecated
public final class ResampleProcessor implements Disposable
{
	private final int[]		ix;
	private final int[]		ix1;
	private final float[]	dx;
	private final float[]	dx1;

	public ResampleProcessor(int frameSizeIn, int frameSizeOut)
	{
		ix = new int[frameSizeOut];
		ix1 = new int[frameSizeOut];
		dx = new float[frameSizeOut];
		dx1 = new float[frameSizeOut];

		for (int i = 0; i < frameSizeOut; i++)
		{
			float x = 1 + i * (float) frameSizeIn / (float) frameSizeOut;

			ix[i] = (int) floor(x);
			ix1[i] = ix[i] + 1;
			dx[i] = x - ix[i];
			dx1[i] = 1 - dx[i];
		}
	}

	public void dispose()
	{
	}

	public void processFrame(float[] frameIn, float[] frameOut)
	{
		int lastValue = frameOut.length - 1;

		for (int i = 0; i < lastValue; i++)
		{
			frameOut[i] = frameIn[ix[i] - 1] * dx1[i]
				+ frameIn[ix1[i] - 1] * dx[i];
		}

		frameOut[lastValue] = frameIn[ix[lastValue] - 1] * dx1[lastValue];
		// + 0 * dx[lastValue];
	}
}
