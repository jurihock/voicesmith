/*
 * Voicesmith <http://voicesmith.jurihock.de>
 *
 * Copyright (c) 2011-2012 by Juergen Hock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.jurihock.voicesmith.activities;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.services.AudioService;
import de.jurihock.voicesmith.services.ServiceBinder;

/**
 * Base class for a front-end activity which interacts with its service.
 */
public abstract class AudioServiceActivity<T extends AudioService> extends
	GDActivity
{
	// Service parameters:
	private final Class<T>		serviceClass;
	private T					serviceInstance		= null;
	private Context				serviceContext		= null;
	private Intent				serviceIntent		= null;
	private ServiceConnection	serviceConnection	= null;

	protected AudioServiceActivity(Class<T> serviceClass)
	{
		this.serviceClass = serviceClass;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		addActionBarItem(ActionBarItem.Type.Settings);
	}

	protected T getService()
	{
		Utils.assertTrue(serviceInstance != null,
			"Service %s wasn't properly instantiated!",
			serviceClass.getName());

		return serviceInstance;
	}

	private void startService()
	{
		if (serviceContext == null)
		{
			serviceContext = this.getApplicationContext();
		}

		if (serviceIntent == null)
		{
			serviceIntent = new Intent(
				serviceContext,
				serviceClass);
		}

		if (!Utils.isServiceRunning(serviceContext, serviceClass))
		{
			if (startService(serviceIntent) == null)
			{
				Utils.log(this, "Unable to start service %s!",
					serviceClass.getName());
			}
		}
	}

	private void stopService()
	{
		if (serviceIntent != null)
		{
			stopService(serviceIntent);
		}
	}

	private void bindService()
	{
		if (serviceConnection == null)
		{
			serviceConnection = new ServiceConnection()
			{
				public void onServiceConnected(ComponentName component, IBinder _binder)
				{
					@SuppressWarnings("unchecked")
					ServiceBinder<T> binder = (ServiceBinder<T>) _binder;
					serviceInstance = binder.getServiceInstance();

					// Notify the subclass
					AudioServiceActivity.this.onServiceConnected();
				}

				public void onServiceDisconnected(ComponentName component)
				{
					// Notify the subclass
					AudioServiceActivity.this.onServiceDisconnected();
					serviceInstance = null;
				}
			};
		}

		if (serviceInstance == null)
		{
			if (!bindService(serviceIntent,
				serviceConnection, Context.BIND_AUTO_CREATE))
			{
				Utils.log(this, "Unable to bind service %s!",
					serviceClass.getName());
			}
		}
	}

	private void unbindService()
	{
		if (serviceConnection != null)
		{
			unbindService(serviceConnection);

			// Notify the subclass
			onServiceDisconnected();
			serviceInstance = null;
		}
	}

	/**
	 * The reference to the service instance is now available.
	 */
	protected void onServiceConnected()
	{
	}

	/**
	 * The reference to the service instance is no longer available.
	 */
	protected void onServiceDisconnected()
	{
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		startService();
		bindService();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		unbindService();
	}
	
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position)
	{
		// Show preference activity
		if (position == 0)
		{
			Intent intent = new Intent(this, PreferenceActivity.class);
			intent.putExtra("caller", this.getClass().getName());
			startActivity(intent);
		}

		return super.onHandleActionBarItemClick(item, position);
	}
}
