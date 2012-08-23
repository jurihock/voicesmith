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

public class NativeResampleProcessor implements Disposable
{
	private final long		handle;

	public NativeResampleProcessor(int frameSizeIn, int frameSizeOut)
	{
		handle = alloc(frameSizeIn, frameSizeOut);
//		Utils.log("NativeResampleProcessor was allocated.");
	}

	public void dispose()
	{
		free(handle);
//		Utils.log("NativeResampleProcessor was freed.");
	}

	public void processFrame(float[] frameIn, float[] frameOut)
	{
		processFrame(handle, frameIn, frameOut);
	}

	// JNI calls
	private native long alloc(int frameSizeIn, int frameSizeOut);
	private native void free(long handle);
	private native void processFrame(long handle, float[] frameIn, float[] frameOut);
}
