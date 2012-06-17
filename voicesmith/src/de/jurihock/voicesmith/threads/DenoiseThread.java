/*******************************************************************************
 * src/de/jurihock/voicesmith/threads/DenoiseThread.java
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

package de.jurihock.voicesmith.threads;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.Preferences.FrameType;
import de.jurihock.voicesmith.dsp.stft.StftPostprocessor;
import de.jurihock.voicesmith.dsp.stft.StftPreprocessor;

public class DenoiseThread extends AudioThread
{
	private final float[]		buffer;

	private StftPreprocessor	preprocessor	= null;
	private StftPostprocessor	postprocessor	= null;

	public DenoiseThread(Context context, AudioDevice input, AudioDevice output)
	{
		super(context, input, output);
		
		Preferences preferences = new Preferences(context);

		buffer = new float[preferences.getFrameSize(FrameType.Medium)];
		Utils.log("Denoise frame size is %s.", buffer.length);

		preprocessor = new StftPreprocessor(input,
			preferences.getFrameSize(FrameType.Medium),
			preferences.getHopSize(FrameType.Medium),
			true,
			true);

		postprocessor = new StftPostprocessor(output,
			preferences.getFrameSize(FrameType.Medium),
			preferences.getHopSize(FrameType.Medium),
			true);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		disposeProcessors();
	}

	private void disposeProcessors()
	{
		if (preprocessor != null)
		{
			preprocessor.dispose();
			preprocessor = null;
		}

		if (postprocessor != null)
		{
			postprocessor.dispose();
			postprocessor = null;
		}
	}

	@Override
	protected void doProcessing()
	{
		while (!Thread.interrupted())
		{
			preprocessor.processFrame(buffer);
			postprocessor.processFrame(buffer);
		}
	}
}
