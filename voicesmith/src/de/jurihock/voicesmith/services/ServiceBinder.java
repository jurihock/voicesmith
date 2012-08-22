package de.jurihock.voicesmith.services;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.os.Binder;

/**
 * Service binder which should return a reference to the service instance.
 * */
public final class ServiceBinder<T extends Service> extends Binder
{
	private final WeakReference<T>	service;

	public ServiceBinder(T service)
	{
		this.service = new WeakReference<T>(service);
	}

	public T getServiceInstance()
	{
		return service.get();
	}
}
