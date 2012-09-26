/*******************************************************************************
 * Voicesmith <http://voicesmith.jurihock.de/>
 * Copyright (c) 2011-2012 Juergen Hock
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

package de.jurihock.voicesmith.dsp.processors;

import de.jurihock.voicesmith.Disposable;

public final class NativeTimescaleProcessor implements Disposable
{
	private final long handle;

	public NativeTimescaleProcessor(int frameSize, int analysisHopSize, int synthesisHopSize)
	{
		handle = alloc(frameSize, analysisHopSize, synthesisHopSize);
	}
	
	public void dispose()
	{
		free(handle);
	}
	
	public void processFrame(float[] frame)
	{
		processFrame(handle, frame);
	}
	
	// JNI calls
	private native long alloc(int frameSize, int analysisHopSize, int synthesisHopSize);
	private native void free(long handle);
	private native void processFrame(long handle, float[] buffer);
}
