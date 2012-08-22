package de.jurihock.voicesmith.audio;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
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

	private boolean					bluetoothScoOn					= false;

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
			Utils.log(new IOException("Unknown HeadsetMode!"));
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
			Utils.log(new IOException("Unknown HeadsetMode!"));
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

	// TODO: How to correct detect that?
	public boolean isBluetoothHeadsetOn()
	{
		boolean isHeadsetConnected = false;

		try
		{
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null && adapter.isEnabled())
			{
				isHeadsetConnected = true;

				// Set<BluetoothDevice> devices = adapter.getBondedDevices();
				// for (BluetoothDevice device : devices)
				// {
				// BluetoothClass bluetoothClass = device.getBluetoothClass();
				// if (bluetoothClass == null) continue;
				//
				// int deviceClass = bluetoothClass.getDeviceClass();
				// if (bluetoothClass.hasService(Service.RENDER)
				// || deviceClass == Device.AUDIO_VIDEO_CAR_AUDIO
				// || deviceClass == Device.AUDIO_VIDEO_HANDSFREE
				// || deviceClass == Device.AUDIO_VIDEO_WEARABLE_HEADSET
				// || deviceClass == Device.AUDIO_VIDEO_PORTABLE_AUDIO
				// || deviceClass == 1036) // TODO: Bluetooth device
				// // classes?
				// {
				// isHeadsetConnected = true;
				// break;
				// }
				// }
			}
		}
		catch (Exception exception)
		{
			Utils.log(exception);
		}

		return isHeadsetConnected
			&& audio.isBluetoothScoAvailableOffCall();
	}

	public boolean isBluetoothScoOn()
	{
		return bluetoothScoOn;
	}

	public void setBluetoothScoOn(boolean on)
	{
		if (bluetoothScoOn == on) return;

		if (bluetoothScoOn = on)
		{
			Utils.log("Start Bluetooth SCO");
			audio.startBluetoothSco();
		}
		else
		{
			Utils.log("Stop Bluetooth SCO");
			audio.stopBluetoothSco();
		}
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
						boolean isWiredHeadsetOff =
							intent.getIntExtra("state", 0) == 0;

						if (isWiredHeadsetOff && listener != null)
						{
							listener.onWiredHeadsetOff();
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
							// Utils.log("Bluetooth connected");
							break;
						case BLUETOOTH_STATE_DISCONNECTED:
						case BLUETOOTH_STATE_ERROR:
						default:
							Utils.log("Bluetooth headset disconnected.");
							if (listener != null)
							{
								listener.onBluetoothHeadsetOff();
							}
							break;
						}
					}
				}
			};

			IntentFilter filter = new IntentFilter();
			{
				filter.addAction(Intent.ACTION_HEADSET_PLUG);
				filter.addAction(ACTION_BLUETOOTH_STATE_CHANGED);
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
