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

package de.jurihock.voicesmith.services;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.audio.HeadsetManager;
import de.jurihock.voicesmith.audio.HeadsetManager.HeadsetManagerListener;
import de.jurihock.voicesmith.audio.HeadsetMode;
import de.jurihock.voicesmith.io.AudioDevice;
import de.jurihock.voicesmith.io.pcm.PcmInDevice;
import de.jurihock.voicesmith.io.pcm.PcmOutDevice;
import de.jurihock.voicesmith.threads.AudioThread;

public abstract class AudioService extends Service implements
	HeadsetManagerListener, OnSharedPreferenceChangeListener
{
	// Audio I/O devices:
	private AudioDevice		input			= null;
	private AudioDevice		output			= null;

	// Audio thread and its parameters:
	private AudioThread		thread			= null;
	private Object[]		threadParams	= null;

	// Headset stuff:
	private HeadsetMode		mode			= HeadsetMode.WIRED_HEADSET;
	private HeadsetManager	headset			= null;

	private ServiceListener	listener		= null;

	public void setListener(ServiceListener listener)
	{
		this.listener = listener;
	}

	public HeadsetMode getHeadsetMode()
	{
		return mode;
	}

	public void setHeadsetMode(HeadsetMode mode)
	{
		if (this.mode == mode) return;

		if (isThreadRunning())
		{
			stopThread(true);

			disposeAudioDevices();
			this.mode = mode;

			startThread();
		}
		else
		{
			disposeAudioDevices();
			this.mode = mode;
		}
	}

	protected abstract AudioThread createAudioThread(AudioDevice input, AudioDevice output);

	public Object[] getThreadParams()
	{
		return threadParams;
	}

	public void setThreadParams(Object... threadParams)
	{
		this.threadParams = threadParams;

		if (thread != null)
		{
			thread.configure(threadParams);
		}
	}

	public boolean isThreadRunning()
	{
		return (thread != null) && thread.isRunning();
	}

	public void startThread()
	{
		if (isThreadRunning()) return;

		if (getHeadsetMode() == HeadsetMode.WIRED_HEADSET
			&& !headset.isWiredHeadsetOn())
		{
			if (listener != null)
			{
				listener.onServiceFailed();
			}

			return;
		}

		if (getHeadsetMode() == HeadsetMode.BLUETOOTH_HEADSET
			&& !headset.isBluetoothHeadsetOn())
		{
			if (listener != null)
			{
				listener.onServiceFailed();
			}

			return;
		}

		if (getHeadsetMode() == HeadsetMode.BLUETOOTH_HEADSET
			&& !headset.isBluetoothScoOn())
		{
			headset.setBluetoothScoOn(true);
		}

		if (!initAudioDevices())
		{
			if (listener != null)
			{
				listener.onServiceFailed();
			}

			return;
		}

		headset.restoreVolumeLevel(getHeadsetMode());

		thread = createAudioThread(input, output);
		if (threadParams != null)
		{
			thread.configure(threadParams);
		}
		thread.start();
	}

	public void stopThread(boolean restarting)
	{
		if (isThreadRunning()) thread.stop();

		if (!restarting)
		{
			Utils.cancelAllNotifications(this);
		}

		headset.storeVolumeLevel(getHeadsetMode());

		if (headset.isBluetoothScoOn())
		{
			headset.setBluetoothScoOn(false);
		}

		if (thread != null)
		{
			thread.dispose();
			thread = null;
		}
	}

	private boolean initAudioDevices()
	{
		Utils.log("%s inits audio devices.",
			this.getClass().getName());

		try
		{
			if (input == null)
			{
				input = new PcmInDevice(this, getHeadsetMode());
			}

			if (output == null)
			{
				output = new PcmOutDevice(this, getHeadsetMode());
			}
		}
		catch (IOException exception)
		{
			Utils.log(exception);
			return false;
		}

		return true;
	}

	private void disposeAudioDevices()
	{
		Utils.log("%s disposes audio devices.",
			this.getClass().getName());

		if (input != null)
		{
			input.dispose();
			input = null;
		}

		if (output != null)
		{
			output.dispose();
			output = null;
		}
	}

	/**
	 * Shows notification, if the frontend activity become invisible while
	 * thread is running.
	 * */
	public void setActivityVisible(boolean isActivityVisible, Class<?> activityClass)
	{
		if (isActivityVisible)
		{
			Utils.cancelAllNotifications(this);
		}
		else if (isThreadRunning())
		{
			Utils.postNotification(
				this,
				R.drawable.notification,
				getString(R.string.ApplicationName),
				getString(R.string.ServiceNotificationTitle),
				getString(R.string.ServiceNotificationSubtitle),
				activityClass);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	@Override
	public void onCreate()
	{
		Utils.log("%s is created.",
			this.getClass().getName());

		super.onCreate();

		if (headset == null)
		{
			headset = new HeadsetManager(this.getApplicationContext());
			headset.setListener(this);
			headset.registerHeadsetDetector();
		}

		new Preferences(getApplicationContext())
			.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDestroy()
	{
		Utils.log("%s is destroyed.",
			this.getClass().getName());

		stopThread(false);

		if (headset != null)
		{
			headset.unregisterHeadsetDetector();
			headset.setListener(null);
			headset = null;
		}

		disposeAudioDevices();

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		Utils.log("Preference changed => reinitialising service.");

		if (isThreadRunning())
		{
			stopThread(true);
			disposeAudioDevices();
			startThread();
		}
		else
		{
			disposeAudioDevices();
		}
	}

	public void onWiredHeadsetOff()
	{
		if (getHeadsetMode() == HeadsetMode.WIRED_HEADSET
			&& isThreadRunning())
		{
			stopThread(false);

			if (listener != null)
			{
				listener.onServiceFailed();
			}
		}
	}

	public void onBluetoothHeadsetOff()
	{
		if (getHeadsetMode() == HeadsetMode.BLUETOOTH_HEADSET
			&& isThreadRunning())
		{
			stopThread(false);

			if (listener != null)
			{
				listener.onServiceFailed();
			}
		}
	}
}
