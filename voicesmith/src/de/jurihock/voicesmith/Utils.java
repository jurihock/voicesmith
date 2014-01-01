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

package de.jurihock.voicesmith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public final class Utils
{
	private static final String				NATIVELIB_NAME	= "Voicesmith";
	private static final String				LOGCAT_TAG		= "Voicesmith";
	private static final int				TOAST_LENGTH	= Toast.LENGTH_LONG;

	private final Context					context;
	private final Preferences				preferences;

	/**
	 * Stopwatch timestamps.
	 * */
	private static final Map<String, Long>	tics			= new HashMap<String, Long>();

	public Utils(Context context)
	{
		this.context = context;
		this.preferences = new Preferences(context);
	}

	/**
	 * Loads the native library.
	 * */
	public static void loadNativeLibrary()
	{
		try
		{
			System.loadLibrary(NATIVELIB_NAME);
		}
		catch (UnsatisfiedLinkError exception)
		{
            Log.d(LOGCAT_TAG, String.format(
                    "Native library %s could not be loaded!",
                    NATIVELIB_NAME));
		}
	}

    public String getVersionString(int formatResId)
    {
        return getVersionString(context.getString(formatResId));
    }

    public String getVersionString(String format)
    {
        try
        {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return String.format(
                    format,
                    info.versionName,
                    info.versionCode);
        }
        catch (PackageManager.NameNotFoundException exception)
        {
            new Utils(context).log(exception);
        }

        return null;
    }

	/**
	 * Checks if a local service is just running.
	 * */
	public boolean isServiceRunning(Class<?> serviceClass)
	{
		ActivityManager manager = (ActivityManager)
			context.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> services =
			manager.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo service : services)
		{
			if (service.service.getClassName().equals(
				serviceClass.getName()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Tries to mount the external storage, makes necessary dirs and finally
	 * returns a File instance for assigned file path.
	 * */
	public File mountFile(String path) throws IOException
	{
		if (!Environment.MEDIA_MOUNTED.equals(
			Environment.getExternalStorageState()))
		{
			throw new IOException("Unable to mount external storage!");
		}

		File file = new File(
			Environment.getExternalStorageDirectory(),
			path);

		File dir = file.getParentFile();
		if (!dir.exists() && !dir.mkdirs())
		{
			throw new IOException(String.format(
				"Unable to make directory '%s'!",
				dir.getAbsolutePath()));
		}

		return file;
	}

	public void postNotification(int iconID, String tickerText, String contentTitle, String contentText, Class<?> activityClass)
	{
		NotificationManager service = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(
			iconID, tickerText, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL
			| Notification.FLAG_NO_CLEAR;

		Intent intent = new Intent(context, activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent
			.getActivity(context, 0, intent, 0);

		notification.setLatestEventInfo(context,
			contentTitle, contentText, pendingIntent);

		service.cancel(0);
		service.notify(0, notification);
	}

	public void cancelAllNotifications()
	{
		NotificationManager service = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);

		service.cancelAll();
	}

	/**
	 * Writes a LogCat log entry.
	 * */
	public void log(String message)
	{
		if (preferences.isLoggingOn())
		{
			Log.d(LOGCAT_TAG, message);
		}
	}

	/**
	 * Writes a formatted LogCat log entry.
	 * */
	public void log(String message, Object... args)
	{
		log(String.format(message, args));
	}

	/**
	 * Writes a LogCat log entry.
	 * */
	public void log(Throwable exception)
	{
		log("[EXCEPTION] " + Log.getStackTraceString(exception));
	}

	/**
	 * Shows a Toast message.
	 * */
	public void toast(String message)
	{
		Toast.makeText(context, message, TOAST_LENGTH).show();
	}

	/**
	 * Shows a formatted Toast message.
	 * */
	public void toast(String message, Object... args)
	{
		toast(String.format(message, args));
	}

	/**
	 * Writes a LogCat log entry if condition is FALSE.
	 * */
	public void assertTrue(boolean condition, String message)
	{
		if (!condition) log("[ASSERT] " + message);
	}

	/**
	 * Writes a formatted LogCat log entry if condition is FALSE.
	 * */
	public void assertTrue(boolean condition, String message, Object... args)
	{
		assertTrue(condition, String.format(message, args));
	}

	/**
	 * Starts a stopwatch.
	 * */
	public synchronized void tic(String tag)
	{
		if (tics.containsKey(tag))
		{
			tics.remove(tag);
		}

		long tic = SystemClock.elapsedRealtime(); // ms
		// long tic = System.nanoTime(); // ns

		tics.put(tag, tic);
	}

	/**
	 * Stops a stopwatch and prints out the time difference.
	 * */
	public synchronized void toc(String tag)
	{
		long toc = SystemClock.elapsedRealtime(); // ms
		// long toc = System.nanoTime(); // ns

		if (tics.containsKey(tag))
		{
			long tic = tics.remove(tag);

			log("%s: %d ms", tag, (toc - tic)); // ms
			// log("%s: %f ms", tag, (toc - tic) / 1000D); // ns/1000
		}
	}
}
