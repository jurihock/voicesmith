package de.jurihock.voicesmith.services;

import java.util.EventListener;

public interface ServiceListener extends EventListener
{
	void onServiceFailed();
}