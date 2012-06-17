/*******************************************************************************
 * src/de/jurihock/voicesmith/activities/AudioActivity.java
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

package de.jurihock.voicesmith.activities;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.io.AudioDevice;
import de.jurihock.voicesmith.io.pcm.PcmInDevice;
import de.jurihock.voicesmith.io.pcm.PcmOutDevice;
import de.jurihock.voicesmith.threads.AudioThread;

public abstract class AudioActivity extends Activity implements OnClickListener
{
	private final Class<? extends AudioThread>	threadClass;
	private final int							threadTitleID, threadInfoID;

	protected AudioThread						thread	= null;
	protected AudioDevice						input	= null;
	protected AudioDevice						output	= null;

	protected ToggleButton						ctrlToggleButton;
	protected TextView							ctrlTitle, ctrlSummary;

	private BroadcastReceiver					headsetDetector;

	protected AudioActivity(Class<? extends AudioThread> threadClass, int threadTitleID, int threadInfoID)
	{
		this.threadClass = threadClass;
		this.threadTitleID = threadTitleID;
		this.threadInfoID = threadInfoID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initAudioDevices();
		initAudioThread();

		loadVolumeLevel();
		registerHeadsetDetector();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Utils.log("Disposing an activity.");

		// Utils.cancelAllNotifications(this);

		storeVolumeLevel();
		unregisterHeadsetDetector();

		if (thread != null)
		{
			if (thread.isRunning())
			{
				thread.stop();
			}

			thread.dispose();
			thread = null;
		}

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

	@Override
	protected void onStop()
	{
		super.onStop();

		if (thread.isRunning())
		{
			thread.stop();
		}

		updateControls();
	}

	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);

		initControls();
		updateControls();
	}

	private void initAudioDevices()
	{
		try
		{
			input = new PcmInDevice(this);
			output = new PcmOutDevice(this);
		}
		catch (IOException e)
		{
			Utils.log("Audio devices could not be loaded!");
		}
	}

	private void initAudioThread()
	{
		try
		{
			thread = (AudioThread) threadClass
				.getConstructor(Context.class, AudioDevice.class,
					AudioDevice.class)
				.newInstance(this, input, output);
		}
		catch (Exception e)
		{
			Utils.log("Audio thread class %s could not be instantiated!",
				threadClass.getName());
		}
	}

	private void loadVolumeLevel()
	{
		AudioManager audio = (AudioManager)
			this.getSystemService(Context.AUDIO_SERVICE);

		// VOL = VOL% * (MAX / 100)
		double volumeLevel = audio
			.getStreamMaxVolume(Preferences.PCM_OUT_SOURCE) / 100D;
		volumeLevel *= new Preferences(this).getVolumeLevel();

		audio.setStreamVolume(
			Preferences.PCM_OUT_SOURCE,
			(int) Math.round(volumeLevel),
			AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}

	private void storeVolumeLevel()
	{
		AudioManager audio = (AudioManager)
			this.getSystemService(Context.AUDIO_SERVICE);

		// VOL% = VOL * (100 / MAX)
		double volumeLevel = 100D
			/ audio.getStreamMaxVolume(Preferences.PCM_OUT_SOURCE);
		volumeLevel *= audio.getStreamVolume(Preferences.PCM_OUT_SOURCE);

		new Preferences(this).setVolumeLevel((int) Math.round(volumeLevel));
	}

	private void registerHeadsetDetector()
	{
		this.registerReceiver(
			headsetDetector = new BroadcastReceiver()
			{
				@Override
				public void onReceive(Context context, Intent intent)
				{
					if ((intent.getAction().compareTo(
						Intent.ACTION_HEADSET_PLUG)) == 0)
					{
						boolean isWiredHeadsetOff = intent.getIntExtra(
							"state", 0) == 0;

						if (isWiredHeadsetOff && thread.isRunning())
						{
							Utils.log(context, "Please plug in the headset!");
							onClick(ctrlToggleButton);
						}
					}
				}
			},
			new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}

	private void unregisterHeadsetDetector()
	{
		this.unregisterReceiver(headsetDetector);
		headsetDetector = null;
	}

	private void initControls()
	{
		// Find controls
		ctrlToggleButton = (ToggleButton) findViewById(R.id.ctrlToggleButton);
		ctrlTitle = (TextView) findViewById(R.id.ctrlTitle);
		ctrlSummary = (TextView) findViewById(R.id.ctrlSummary);

		// Set listeners
		ctrlToggleButton.setOnClickListener(this);

		// Set title and info
		ctrlTitle.setText(this.getResources().getText(threadTitleID));
		ctrlSummary.setText(this.getResources().getText(threadInfoID));
	}

	private void updateControls()
	{
		ctrlToggleButton.setChecked(thread.isRunning());
	}

	public void onClick(View view)
	{
		if (thread.isRunning())
		{
			thread.stop();
		}
		else
		{
			AudioManager audio = (AudioManager)
				this.getSystemService(Context.AUDIO_SERVICE);

			if (audio.isWiredHeadsetOn())
			{
				thread.start();
			}
			else
			{
				Utils.log(this, "Please plug in the headset!");
			}
		}

		updateControls();

		// BZZZTT!!1!
		view.performHapticFeedback(0);
	}

	// @Override
	// protected void onPause()
	// {
	// super.onPause();

	// Show notification if activity getting paused
	// while thread is still running
	// if (!this.isFinishing() && thread.isRunning())
	// {
	// Utils.postNotification(this,
	// R.drawable.voicesmith, this.getString(R.string.app_name),
	// this.getString(R.string.app_notification), "",
	// this.getClass());
	// }
	// }

	// @Override
	// protected void onResume()
	// {
	// super.onResume();
	//
	// Utils.cancelAllNotifications(this);
	// }

	// @Override
	// public void onBackPressed()
	// {
	// this.startActivity(new Intent(this,
	// MainActivity.class));
	//
	// super.onBackPressed();
	// }
}
