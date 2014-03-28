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

import android.content.Intent;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import de.jurihock.voicesmith.audio.AudioDeviceManager;
import de.jurihock.voicesmith.audio.HeadsetManager;
import de.jurihock.voicesmith.audio.HeadsetMode;
import de.jurihock.voicesmith.io.AudioDevice;
import de.jurihock.voicesmith.io.dummy.DummyInDevice;
import de.jurihock.voicesmith.io.dummy.DummyOutDevice;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.Mockito.*;

public abstract class AudioServiceTestBase<T extends AudioService> extends MockableServiceTestCase<T> implements ServiceListener
{
    private ServiceFailureReason serviceFailureReason;

    @Mock
    private HeadsetManager headsetManagerMock;

    @Mock
    private AudioDeviceManager deviceManagerMock;

    public AudioServiceTestBase(Class<T> serviceClass)
    {
        super(serviceClass);
    }

    private static void sleep(double seconds)
    {
        try
        {
            long millis = (long)(seconds * 1000D);
            Thread.sleep(millis);
        }
        catch (InterruptedException exception)
        {
            return;
        }
    }

    @Override
    public void onServiceFailed(ServiceFailureReason reason)
    {
        serviceFailureReason = reason;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        serviceFailureReason = null;
    }

    @Override
    protected void startService()
    {
        super.startService();

        getService().setListener(this);
    }

    @Override
    protected void initMocks()
    {
        super.initMocks();

        doNothing().when(headsetManagerMock).restoreVolumeLevel(any(HeadsetMode.class));
        doNothing().when(headsetManagerMock).setBluetoothScoOn(any(boolean.class));
        doNothing().when(headsetManagerMock).registerHeadsetDetector();
        doNothing().when(headsetManagerMock).unregisterHeadsetDetector();

        try
        {
            AudioDevice dummyInDevice = new DummyInDevice(getContext());
            AudioDevice dummyOutDevice = new DummyOutDevice(getContext());

            when(deviceManagerMock.getOutputDevice(any(HeadsetMode.class))).thenReturn(dummyOutDevice);
            when(deviceManagerMock.getInputDevice(any(HeadsetMode.class))).thenReturn(dummyInDevice);
        }
        catch (IOException exception)
        {
            fail("Unexpected IOException while initializing audio device mocks!");
        }
    }

    @SmallTest
    public void testServiceStartable()
    {
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(getContext(), getServiceClass());

        startService(serviceIntent);
    }

    @SmallTest
    public void testServiceBindable()
    {
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(getContext(), getServiceClass());

        bindService(serviceIntent);
    }

    @LargeTest
    public void testAudioRouting_AudioDevice_NEGATIVE() throws IOException
    {
        startService();
        initMocks();

        T service = getService();

        when(headsetManagerMock.isWiredHeadsetOn()).thenReturn(true);
        when(headsetManagerMock.isBluetoothHeadsetOn()).thenReturn(false);
        when(headsetManagerMock.isBluetoothScoOn()).thenReturn(false);
        when(headsetManagerMock.waitForBluetoothSco()).thenReturn(false);

        when(deviceManagerMock.getOutputDevice(any(HeadsetMode.class))).thenThrow(new IOException());
        when(deviceManagerMock.getInputDevice(any(HeadsetMode.class))).thenThrow(new IOException());

        service.setBluetoothHeadsetSupport(false);
        {
            service.startThread();

            assertEquals(service.isThreadRunning(), false);
            assertEquals(service.getActualHeadsetMode(), HeadsetMode.WIRED_HEADSET);
            assertEquals(serviceFailureReason, ServiceFailureReason.AudioDeviceInitialization);
        }
    }

    @LargeTest
    public void testAudioRouting_WiredHeadset_POSITIVE()
    {
        startService();
        initMocks();

        T service = getService();

        when(headsetManagerMock.isWiredHeadsetOn()).thenReturn(true);
        when(headsetManagerMock.isBluetoothHeadsetOn()).thenReturn(false);
        when(headsetManagerMock.isBluetoothScoOn()).thenReturn(false);
        when(headsetManagerMock.waitForBluetoothSco()).thenReturn(false);

        service.setBluetoothHeadsetSupport(false);
        {
            service.startThread();
            sleep(1);

            assertEquals(service.isThreadRunning(), true);
            assertEquals(service.getActualHeadsetMode(), HeadsetMode.WIRED_HEADSET);
            assertEquals(serviceFailureReason, null);
        }

        service.setBluetoothHeadsetSupport(true);
        {
            service.startThread();
            sleep(1);

            assertEquals(service.isThreadRunning(), true);
            assertEquals(service.getActualHeadsetMode(), HeadsetMode.WIRED_HEADSET);
            assertEquals(serviceFailureReason, null);
        }
    }

    @LargeTest
    public void testAudioRouting_WiredHeadset_NEGATIVE()
    {
        startService();
        initMocks();

        T service = getService();

        when(headsetManagerMock.isWiredHeadsetOn()).thenReturn(false);
        when(headsetManagerMock.isBluetoothHeadsetOn()).thenReturn(false);
        when(headsetManagerMock.isBluetoothScoOn()).thenReturn(false);
        when(headsetManagerMock.waitForBluetoothSco()).thenReturn(false);

        service.setBluetoothHeadsetSupport(false);
        {
            service.startThread();

            assertEquals(service.isThreadRunning(), false);
            assertEquals(service.getActualHeadsetMode(), HeadsetMode.WIRED_HEADSET);
            assertEquals(serviceFailureReason, ServiceFailureReason.InitialHeadsetAvailability);
        }

        service.setBluetoothHeadsetSupport(true);
        {
            service.startThread();

            assertEquals(service.isThreadRunning(), false);
            assertEquals(service.getActualHeadsetMode(), HeadsetMode.WIRED_HEADSET);
            assertEquals(serviceFailureReason, ServiceFailureReason.InitialHeadsetAvailability);
        }
    }
}
