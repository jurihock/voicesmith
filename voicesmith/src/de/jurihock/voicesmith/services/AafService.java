package de.jurihock.voicesmith.services;

import android.content.Intent;
import android.os.IBinder;
import de.jurihock.voicesmith.AAF;
import de.jurihock.voicesmith.io.AudioDevice;
import de.jurihock.voicesmith.threads.AudioThread;

/**
 * Implementation of the vocoder audio service.
 * */
public final class AafService extends AudioService
{
	// The AAF type
	private AAF	aaf	= AAF.valueOf(0);

	public AAF getAaf()
	{
		return aaf;
	}

	public void setAaf(AAF aaf)
	{
		if (this.aaf == aaf) return;

		if (isThreadRunning())
		{
			stopThread(true);
			
			this.aaf = aaf;
			setThreadParams((Object[]) null);
			
			startThread();
		}
		else
		{
			this.aaf = aaf;
			setThreadParams((Object[]) null);
		}
	}

	@Override
	protected AudioThread createAudioThread(AudioDevice input, AudioDevice output)
	{
		return AudioThread.create(this, input, output, aaf);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new ServiceBinder<AafService>(this);
	}
}
