package de.jurihock.voicesmith.services;

import android.content.Intent;
import android.os.IBinder;
import de.jurihock.voicesmith.DAFX;
import de.jurihock.voicesmith.io.AudioDevice;
import de.jurihock.voicesmith.threads.AudioThread;

/**
 * Implementation of the vocoder audio service.
 * */
public final class DafxService extends AudioService
{
	// The DAFX type
	private DAFX	dafx	= DAFX.valueOf(0);

	public DAFX getDafx()
	{
		return dafx;
	}

	public void setDafx(DAFX dafx)
	{
		if (this.dafx == dafx) return;

		if (isThreadRunning())
		{
			stopThread(true);
			
			this.dafx = dafx;
			setThreadParams((Object[]) null);
			
			startThread();
		}
		else
		{
			this.dafx = dafx;
			setThreadParams((Object[]) null);
		}
	}

	@Override
	protected AudioThread createAudioThread(AudioDevice input, AudioDevice output)
	{
		return AudioThread.create(this, input, output, dafx);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new ServiceBinder<DafxService>(this);
	}
}
