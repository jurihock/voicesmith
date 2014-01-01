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

import greendroid.app.GDApplication;
import de.jurihock.voicesmith.activities.HomeActivity;

public final class Application extends GDApplication
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		Utils.loadNativeLibrary();
	}

	@Override
	public Class<?> getHomeActivityClass()
	{
		return HomeActivity.class;
	}
}
