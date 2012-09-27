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

package de.jurihock.voicesmith.audio;

import java.io.IOException;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.SystemClock;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;

/**
 * Provides headset management routines.
 */
public final class HeadsetManager
{
	private static final int		WIRED_HEADSET_SOURCE			= AudioManager.STREAM_MUSIC;
	private static final int		BLUETOOTH_HEADSET_SOURCE		= 6;
	// Undocumented "6" instead of STREAM_VOICE_CALL:
	// http://stackoverflow.com/questions/4472613/android-bluetooth-earpiece-volume

	// Another undocumented Bluetooth constants:
	// http://www.netmite.com/android/mydroid/2.0/frameworks/base/core/java/android/bluetooth/BluetoothHeadset.java
	private static final String		ACTION_BLUETOOTH_STATE_CHANGED	= "android.bluetooth.headset.action.STATE_CHANGED";
	private static final String		BLUETOOTH_STATE					= "android.bluetooth.headset.extra.STATE";
	private static final int		BLUETOOTH_STATE_ERROR			= -1;
	private static final int		BLUETOOTH_STATE_DISCONNECTED	= 0;
	private static final int		BLUETOOTH_STATE_CONNECTING		= 1;
	private static final int		BLUETOOTH_STATE_CONNECTED		= 2;

	private final Context			context;
	private final AudioManager		audio;

	private HeadsetManagerListener	listener						= null;
	private BroadcastReceiver		headsetDetector					= null;

	public HeadsetManager(Context context)
	{
		this.context = context;

		audio = (AudioManager)
			context.getSystemService(Context.AUDIO_SERVICE);
	}

	public void setListener(HeadsetManagerListener listener)
	{
		this.listener = listener;
	}

	public void restoreVolumeLevel(HeadsetMode headsetMode)
	{
		int source;

		switch (headsetMode)
		{
		case WIRED_HEADSET:
			source = WIRED_HEADSET_SOURCE;
			break;

		case BLUETOOTH_HEADSET:
			source = BLUETOOTH_HEADSET_SOURCE;
			break;
		default:
			new Utils(context).log(new IOException("Unknown HeadsetMode!"));
			source = WIRED_HEADSET_SOURCE;
		}

		// VOL = VOL% * (MAX / 100)
		double volumeLevel = audio
			.getStreamMaxVolume(source) / 100D;
		volumeLevel *= new Preferences(context).getVolumeLevel();

		audio.setStreamVolume(
			source,
			(int) Math.round(volumeLevel),
			// Display the volume dialog
			// AudioManager.FLAG_SHOW_UI);
			// Display nothing
			AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}

	public void storeVolumeLevel(HeadsetMode headsetMode)
	{
		int source;

		switch (headsetMode)
		{
		case WIRED_HEADSET:
			source = WIRED_HEADSET_SOURCE;
			break;

		case BLUETOOTH_HEADSET:
			source = BLUETOOTH_HEADSET_SOURCE;
			break;
		default:
			new Utils(context).log(new IOException("Unknown HeadsetMode!"));
			source = WIRED_HEADSET_SOURCE;
		}

		// VOL% = VOL * (100 / MAX)
		double volumeLevel = 100D
			/ audio.getStreamMaxVolume(source);
		volumeLevel *= audio.getStreamVolume(source);

		new Preferences(context).setVolumeLevel(
			(int) Math.round(volumeLevel));
	}

	public boolean isWiredHeadsetOn()
	{
		return audio.isWiredHeadsetOn();
	}

	public boolean isBluetoothHeadsetOn()
	{
		boolean isHeadsetConnected = false;

		try
		{
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null && adapter.isEnabled())
			{
				Set<BluetoothDevice> devices = adapter.getBondedDevices();

				isHeadsetConnected = devices != null
					&& devices.size() > 0;

				// TODO: Check device classes, what sort of devices it is
			}
		}
		catch (Exception exception)
		{
			new Utils(context).log(exception);
		}

		return isHeadsetConnected
			&& audio.isBluetoothScoAvailableOffCall();
	}

	public boolean isBluetoothScoOn()
	{
		return audio.isBluetoothScoOn();
	}

	public void setBluetoothScoOn(boolean on)
	{
		if (audio.isBluetoothScoOn() == on) return;

		if (on)
		{
			new Utils(context).log("Starting Bluetooth SCO.");
			audio.startBluetoothSco();
		}
		else
		{
			new Utils(context).log("Stopping Bluetooth SCO.");
			audio.stopBluetoothSco();
		}
	}

	/**
	 * Waits until Bluetooth SCO becomes available.
	 * 
	 * @param timeout
	 *            Max timeout in seconds.
	 * */
	public boolean waitForBluetoothSco(int timeout)
	{
		final long maxTime = 1000 * timeout;
		final long sleepTime = 50;

		final long start = SystemClock.elapsedRealtime();
		long end = start;

		while (!audio.isBluetoothScoOn())
		{
			end = SystemClock.elapsedRealtime();

			if (end - start > maxTime)
			{
				return false;
			}
			
			try
			{
				Thread.sleep(sleepTime);
			}
			catch (InterruptedException exception)
			{
			}
		}

		new Utils(context).log(
			"Waited %s ms for Bluetooth SCO.",
			end - start);

		return true;
	}

	public void registerHeadsetDetector()
	{
		if (headsetDetector == null)
		{
			headsetDetector = new BroadcastReceiver()
			{
				@Override
				public void onReceive(Context context, Intent intent)
				{
					// WIRED HEADSET BROADCAST

					boolean isWiredHeadsetBroadcast = intent.getAction()
						.equals(Intent.ACTION_HEADSET_PLUG);

					if (isWiredHeadsetBroadcast)
					{
						boolean isWiredHeadsetPlugged =
							intent.getIntExtra("state", 0) == 1;

						if (isWiredHeadsetPlugged)
						{
							new Utils(context).log(
								"Wired headset plugged.");
						}
						else
						{
							new Utils(context).log(
								"Wired headset unplugged.");

							if (listener != null)
							{
								listener.onWiredHeadsetOff();
							}
						}

						// TODO: Maybe handle the microphone indicator too
					}

					// BLUETOOTH HEADSET BROADCAST

					boolean isBluetoothHeadsetBroadcast = intent.getAction()
						.equals(ACTION_BLUETOOTH_STATE_CHANGED);

					if (isBluetoothHeadsetBroadcast)
					{
						int bluetoothHeadsetState = intent.getIntExtra(
							BLUETOOTH_STATE,
							BLUETOOTH_STATE_ERROR);

						switch (bluetoothHeadsetState)
						{
						case BLUETOOTH_STATE_CONNECTING:
						case BLUETOOTH_STATE_CONNECTED:
							new Utils(context).log(
								"Bluetooth headset connecting or connected.");
							break;
						case BLUETOOTH_STATE_DISCONNECTED:
						case BLUETOOTH_STATE_ERROR:
						default:
							new Utils(context).log(
								"Bluetooth headset disconnected or error.");
							if (listener != null)
							{
								listener.onBluetoothHeadsetOff();
							}
							break;
						}
					}

					// BLUETOOTH SCO BROADCAST

					boolean isBluetoothScoBroadcast = intent.getAction()
						.equals(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);

					if (isBluetoothScoBroadcast)
					{
						int bluetoothScoState = intent.getIntExtra(
							AudioManager.EXTRA_SCO_AUDIO_STATE,
							AudioManager.SCO_AUDIO_STATE_ERROR);

						switch (bluetoothScoState)
						{
						case AudioManager.SCO_AUDIO_STATE_CONNECTED:
							new Utils(context).log(
								"Bluetooth SCO connected.");
							break;
						case AudioManager.SCO_AUDIO_STATE_DISCONNECTED:
						case AudioManager.SCO_AUDIO_STATE_ERROR:
							new Utils(context).log(
								"Bluetooth SCO disconnected or error.");
							break;
						}
					}
				}
			};

			IntentFilter filter = new IntentFilter();
			{
				filter.addAction(Intent.ACTION_HEADSET_PLUG);
				filter.addAction(ACTION_BLUETOOTH_STATE_CHANGED);
				filter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
			}

			context.registerReceiver(headsetDetector, filter);
		}
	}

	public void unregisterHeadsetDetector()
	{
		if (headsetDetector != null)
		{
			context.unregisterReceiver(headsetDetector);
			headsetDetector = null;
		}
	}

	public interface HeadsetManagerListener
	{
		void onWiredHeadsetOff();

		void onBluetoothHeadsetOff();
	}
}
