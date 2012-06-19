/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/Window.java
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

public final class Window
{
	private final int		frameSize;
	private final int		hopSize;
	private final boolean	isPeriodic;
	private final boolean	isWeighted;

	/**
	 * @param isPeriodic
	 *            Compute first N coefficients for the N+1 window.
	 * @param isWeighted
	 *            Weight window according to the Weighted Overlap Add (WOLA) routine.
	 * */
	public Window(int frameSize, int hopSize, boolean isPeriodic, boolean isWeighted)
	{
		this.frameSize = frameSize;
		this.hopSize = hopSize;
		this.isPeriodic = isPeriodic;
		this.isWeighted = isWeighted;
	}

	public float[] hann()
	{
		final float[] window = new float[frameSize];
		final int N = (isPeriodic) ? frameSize + 1 : frameSize;

		for (int n = 0; n < frameSize; n++)
		{
			window[n] = 0.5F * (1F - cos(2F * PI * n / (N - 1F)));
		}

		if (isWeighted)
		{
			float weighting = 0;

			for (int i = 0; i < frameSize; i++)
				weighting += window[i] * window[i];

			weighting = 1 / sqrt(weighting / hopSize);

			for (int i = 0; i < frameSize; i++)
				window[i] *= weighting;
		}

		return window;
	}
}