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
	private HeadsetMode		mode			= null;
	private HeadsetManager	headset			= null;

	private ServiceListener	listener		= null;

	protected Preferences	preferences		= null;

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

		preferences.setHeadsetMode(mode);

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

			if (!headset.waitForBluetoothSco(3))
			{
				headset.setBluetoothScoOn(false);

				if (listener != null)
				{
					listener.onServiceFailed();
				}

				return;
			}
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
			new Utils(this).cancelAllNotifications();
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

		disposeAudioDevices();
	}

	private boolean initAudioDevices()
	{
		new Utils(this).log("Initialising audio devices.");

		try
		{
			if (input == null)
			{
				input = new PcmInDevice(this, getHeadsetMode());
				// input = new FileInDevice(this, "test_in.raw"); // TEST
			}

			if (output == null)
			{
				output = new PcmOutDevice(this, getHeadsetMode());
				// output = new FileOutDevice(this, "test_out.raw"); // TEST
			}
		}
		catch (IOException exception)
		{
			new Utils(this).log(exception);
			return false;
		}

		return true;
	}

	private void disposeAudioDevices()
	{
		new Utils(this).log("Disposing audio devices.");

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
			new Utils(this).cancelAllNotifications();
		}
		else if (isThreadRunning())
		{
			new Utils(this).postNotification(
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
		new Utils(this).log("Creating service.");

		super.onCreate();

		preferences = new Preferences(getApplicationContext());
		preferences.registerOnSharedPreferenceChangeListener(this);

		if (mode == null)
		{
			mode = preferences.getHeadsetMode();
		}

		if (headset == null)
		{
			headset = new HeadsetManager(this.getApplicationContext());
			headset.setListener(this);
			headset.registerHeadsetDetector();
		}
	}

	@Override
	public void onDestroy()
	{
		new Utils(this).log("Destroying service.");

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
		new Utils(this).log("Preferences changed => reinitialising service.");

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
