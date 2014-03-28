/*
 * Voicesmith <http://voicesmith.jurihock.de/>
 *
 * Copyright (c) 2011-2014 Juergen Hock
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
 */

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
