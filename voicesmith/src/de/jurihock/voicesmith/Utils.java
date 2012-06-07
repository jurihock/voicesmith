/*******************************************************************************
 * src/de/jurihock/voicesmith/Utils.java
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

package de.jurihock.voicesmith;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public final class Utils
{
	private static final String				NATIVELIB_NAME	= "Voicesmith";
	private static final String				LOGCAT_TAG		= "Voicesmith";

	private static final int				TOAST_LENGTH	= Toast.LENGTH_SHORT;

	private static final boolean			LOGGING			= false;

	/**
	 * Stopwatch timestamps.
	 * */
	private static final Map<String, Long>	tics			= new HashMap<String, Long>();

	/**
	 * Loads the native library.
	 * */
	public static void loadNativeLibrary()
	{
		try
		{
			System.loadLibrary(NATIVELIB_NAME);
		}
		catch (UnsatisfiedLinkError e)
		{
			Utils.log("Native library %s could not be loaded!", NATIVELIB_NAME);
		}
	}

	public static void postNotification(Context context, int iconID, String tickerText, String contentTitle, String contentText, Class<?> activityClass)
	{
		NotificationManager service = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(
			iconID, tickerText, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		PendingIntent pendingIntent = PendingIntent
			.getActivity(context, 0, intent, 0);

		notification.setLatestEventInfo(context,
			contentTitle, contentText, pendingIntent);

		service.cancel(0);
		service.notify(0, notification);
	}

	public static void cancelAllNotifications(Context context)
	{
		NotificationManager service = (NotificationManager)
			context.getSystemService(Context.NOTIFICATION_SERVICE);

		service.cancelAll();
	}

	/**
	 * Writes a LogCat log entry.
	 * */
	public static void log(String message)
	{
		if (LOGGING) Log.d(LOGCAT_TAG, message);
	}

	/**
	 * Writes a formatted LogCat log entry.
	 * */
	public static void log(String message, Object... args)
	{
		log(String.format(message, args));
	}

	/**
	 * Writes a LogCat log entry.
	 * */
	public static void log(Throwable exception)
	{
		log(Log.getStackTraceString(exception));
	}

	/**
	 * Shows a Toast message.
	 * */
	public static void log(Context context, String message)
	{
		Toast.makeText(context, message, TOAST_LENGTH).show();
	}

	/**
	 * Shows a formatted Toast message.
	 * */
	public static void log(Context context, String message, Object... args)
	{
		log(context, String.format(message, args));
	}

	/**
	 * Starts a stopwatch.
	 * */
	public static synchronized void tic(String tag)
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
	public static synchronized void toc(String tag)
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
