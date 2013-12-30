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
	private AudioThread		thread			  = null;
    private String          threadName        = null;
	private String          threadPreferences = null;

	// Headset stuff:
	private HeadsetMode     desiredMode     = null;
    private HeadsetMode		actualMode	    = null;
	private HeadsetManager	headset			= null;

	private ServiceListener	listener		= null;

	protected Preferences	preferences		= null;

	public void setListener(ServiceListener listener)
	{
		this.listener = listener;
	}

	public HeadsetMode getHeadsetMode()
	{
		return desiredMode;
	}

	public void setHeadsetMode(HeadsetMode mode)
	{
		if (this.desiredMode == mode) return;

        this.desiredMode = mode;
		preferences.setHeadsetMode(mode);

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

    private HeadsetMode getActualHeadsetMode()
    {
        return (actualMode != null) ? actualMode : desiredMode;
    }

    private void setActualHeadsetMode(HeadsetMode mode)
    {
        actualMode = mode;
    }

	protected abstract AudioThread createAudioThread(AudioDevice input, AudioDevice output);

    public String getThreadName()
    {
        return threadName;
    }

    public void setThreadName(String threadName)
    {
        this.threadName = threadName;
    }

	public String getThreadPreferences()
	{
		return threadPreferences;
	}

    public boolean hasThreadPreferences()
    {
        return threadPreferences != null && threadPreferences.length() > 0;
    }

	public void setThreadPreferences(String threadPreferences)
	{
		this.threadPreferences = threadPreferences;

		if (thread != null)
		{
			thread.configure(threadPreferences);
		}
	}

	public boolean isThreadRunning()
	{
		return (thread != null) && thread.isRunning();
	}

	public void startThread()
	{
		if (isThreadRunning()) return;

        setActualHeadsetMode(getHeadsetMode());

		// Fallback to the wired mode if Bluetooth headset mode is set
		// but no device available or Bluetooth initialization fails
		if (getActualHeadsetMode() == HeadsetMode.BLUETOOTH_HEADSET)
		{
			if(headset.isBluetoothHeadsetOn())
			{
				if(!headset.isBluetoothScoOn())
				{
					headset.setBluetoothScoOn(true);

					if (!headset.waitForBluetoothSco())
					{
						headset.setBluetoothScoOn(false);
						setActualHeadsetMode(HeadsetMode.WIRED_HEADSET);
					}
				}
			}
			else
			{
                setActualHeadsetMode(HeadsetMode.WIRED_HEADSET);
			}
		}

        // Return if wired headset mode is actually set but no device available
        if (getActualHeadsetMode() == HeadsetMode.WIRED_HEADSET
                && !headset.isWiredHeadsetOn())
        {
            if (listener != null)
            {
                listener.onServiceFailed();
            }

            return;
        }

        // Return if audio device initialization fails
		if (!initAudioDevices(getActualHeadsetMode()))
		{
			if (listener != null)
			{
				listener.onServiceFailed();
			}

			return;
		}

		headset.restoreVolumeLevel(getActualHeadsetMode());

		thread = createAudioThread(input, output);
        thread.configure(threadPreferences);
		thread.start();
	}

	public void stopThread(boolean restarting)
	{
		if (isThreadRunning()) thread.stop();

		if (!restarting)
		{
			new Utils(this).cancelAllNotifications();
		}

		headset.storeVolumeLevel(getActualHeadsetMode());

		if (headset.isBluetoothScoOn())
		{
			headset.setBluetoothScoOn(false);
		}

        preferences.setAudioThreadPreferences(
                threadName, threadPreferences);

		if (thread != null)
		{
			thread.dispose();
			thread = null;
		}

		disposeAudioDevices();
	}

	private boolean initAudioDevices(HeadsetMode mode)
	{
		new Utils(this).log("Initialising audio devices.");

		try
		{
			if (input == null)
			{
				input = new PcmInDevice(this, mode);

                // TEST: Read input signal from file
				// input = new FileInDevice(this, "voicesmith_input.raw");
			}

			if (output == null)
			{
				output = new PcmOutDevice(this, mode);

                // TEST: Write output signal to file
				// output = new FileOutDevice(this, "voicesmith_output.raw");
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

		if (desiredMode == null)
		{
			desiredMode = preferences.getHeadsetMode();
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

        preferences.unregisterOnSharedPreferenceChangeListener(this);

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
		if (getActualHeadsetMode() == HeadsetMode.WIRED_HEADSET
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
		if (getActualHeadsetMode() == HeadsetMode.BLUETOOTH_HEADSET
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
