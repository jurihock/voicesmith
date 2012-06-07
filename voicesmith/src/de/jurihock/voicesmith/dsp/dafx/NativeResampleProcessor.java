/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/dafx/NativeResampleProcessor.java
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

import de.jurihock.voicesmith.Disposable;
import de.jurihock.voicesmith.dsp.STFT;

public class NativeResampleProcessor implements Disposable
{
	private final long		handle;

	private final boolean	doInverseFFT;
	private STFT			stft;

	public NativeResampleProcessor(int frameSizeIn, int frameSizeOut, int hopSizeIn, boolean doInverseFFT)
	{
		this.doInverseFFT = doInverseFFT;

		if (doInverseFFT) stft = new STFT(frameSizeIn, hopSizeIn);
		else stft = null;

		handle = alloc(frameSizeIn, frameSizeOut);
//		Utils.log("NativeResampleProcessor was allocated.");
	}

	public void dispose()
	{
		if (doInverseFFT)
		{
			stft.dispose();
			stft = null;
		}
		
		free(handle);
//		Utils.log("NativeResampleProcessor was freed.");
	}

	public void processFrame(float[] frameIn, float[] frameOut)
	{
		if (doInverseFFT) stft.ifft(frameIn);

		processFrame(handle, frameIn, frameOut);
	}

	// JNI calls
	private native long alloc(int frameSizeIn, int frameSizeOut);
	private native void free(long handle);
	private native void processFrame(long handle, float[] frameIn, float[] frameOut);
}
