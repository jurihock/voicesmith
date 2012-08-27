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

package de.jurihock.voicesmith.threads;

import android.content.Context;
import de.jurihock.voicesmith.FrameType;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.dsp.processors.AmplifyProcessor;
import de.jurihock.voicesmith.io.AudioDevice;

public class LowDelayThread extends AudioThread
{
	private final AmplifyProcessor	amplifier;
	private final short[]			buffer;

	public LowDelayThread(Context context, AudioDevice input, AudioDevice output)
	{
		super(context, input, output);

		Preferences preferences = new Preferences(context);

		FrameType frameType = FrameType.Small;
		int frameSize = preferences.getFrameSize(
			frameType, input.getSampleRate());

		amplifier = new AmplifyProcessor(context);

		buffer = new short[frameSize];

		Utils.log("Delay frame size is %s.", buffer.length);
	}

	@Override
	protected final void doProcessing()
	{
		while (!Thread.interrupted())
		{
			// Utils.tic("IN");
			input.read(buffer);
			// Utils.toc("IN");

			amplifier.processFrame(buffer);

			// Utils.tic("OUT");
			output.write(buffer);
			// Utils.toc("OUT");

		}
	}
}
