package de.jurihock.voicesmith.services;

import android.app.Service;
import android.content.Intent;
import android.test.ServiceTestCase;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public abstract class MockableServiceTestCase<T extends Service> extends ServiceTestCase<T>
{
    /**
     * A workaround variable to inject declared mocks in to the service instance.
     * Don't use this variable directly, just call getService() instead.
     * To initialize mocks run startService() and initMocks().
     * */
    @InjectMocks
    private T serviceInstance;

    private final Class<T> serviceClass;

    protected Class<T> getServiceClass()
    {
        return serviceClass;
    }

    public MockableServiceTestCase(Class<T> serviceClass)
    {
        super(serviceClass);
        this.serviceClass = serviceClass;
    }

    @Override
    protected void startService(Intent intent)
    {
        super.startService(intent);
        serviceInstance = getService();
    }

    protected void startService()
    {
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(getContext(), getServiceClass());

        startService(serviceIntent);
    }

    protected void initMocks()
    {
        if (getService() == null)
        {
            throw new NullPointerException("Start the service first!");
        }

        MockitoAnnotations.initMocks(this);
    }
}
