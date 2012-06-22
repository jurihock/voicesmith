/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/stft/StftPostprocessor.java
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

package de.jurihock.voicesmith.dsp.stft;

import static de.jurihock.voicesmith.dsp.Math.max;
import static de.jurihock.voicesmith.dsp.Math.min;
import static de.jurihock.voicesmith.dsp.Math.round;

import java.util.Arrays;

import de.jurihock.voicesmith.Disposable;
import de.jurihock.voicesmith.dsp.KissFFT;
import de.jurihock.voicesmith.dsp.Window;
import de.jurihock.voicesmith.io.AudioDevice;

/**
 * Writes frames referred to "Constant Overlapp And Add" procedure into the
 * audio sink. Frames get shifted by the given hop size, normalized, weighted
 * and optinal transformed into time domain.
 * */
public final class StftPostprocessor implements Disposable
{
	private final AudioDevice	output;
	private final int			frameSize;
	private final int			hopSize;
	private final boolean		doInverseFFT;

	private KissFFT				fft	= null;
	private final float[]		window;

	private final short[]		prevFrame, nextFrame;
	private int					frameCursor;

	public StftPostprocessor(AudioDevice output, int frameSize, int hopSize, boolean doInverseFFT)
	{
		this.output = output;
		this.frameSize = frameSize;
		this.hopSize = hopSize;
		this.doInverseFFT = doInverseFFT;

		fft = new KissFFT(frameSize);
		window = new Window(frameSize, true).hann();

		prevFrame = new short[frameSize];
		nextFrame = new short[frameSize];
		frameCursor = 0;
	}

	public void dispose()
	{
		if (fft != null)
		{
			fft.dispose();
			fft = null;
		}
	}

	public void processFrame(float[] frame)
	{
		if (doInverseFFT) fft.ifft(frame);

		// Prepare left frame part
		synthesizeFrame(
			frame, 0,
			prevFrame, frameCursor,
			frameSize - frameCursor,
			window);

		// Prepare right frame part
		synthesizeFrame(
			frame, frameSize - frameCursor,
			nextFrame, 0,
			frameCursor,
			window);

		// Increment and handle frame cursor
		frameCursor += hopSize;
		if (frameCursor >= frameSize)
		{
			// Reset frame cursor
			frameCursor -= frameSize;

			// Write ready frame to output
			output.write(prevFrame);

			// Prepare frame buffers
			System.arraycopy(nextFrame, 0, prevFrame, 0, frameSize);
			Arrays.fill(nextFrame, (short) 0);
		}
	}

	/**
	 * Performs the synthesis step with weighting.
	 * */
	private static void synthesizeFrame(float[] src, int offsetSrc, short[] dst, int offsetDst, int count, float[] window)
	{
		if (count == 0) return;

		for (int i = 0; i < count; i++)
		{
			// Get the source value
			float value = src[i + offsetSrc];

			// Multiply with window coefficient
			value *= window[i + offsetSrc];

			// Expand and add it to destination value
			value = min(1F, max(-1F, value));
			dst[i + offsetDst] += (short) round(value * 32767F);
		}
	}
}
