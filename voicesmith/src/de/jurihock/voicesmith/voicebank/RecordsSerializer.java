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

package de.jurihock.voicesmith.voicebank;


// Depends on org.simpleframework.xml

public final class RecordsSerializer
{
	public static Records deserialize()
	{
//		try
//		{
//			Serializer serializer = new Persister();
//			File file = Utils.mountFile(Preferences.RECORDS_FILE);
//			
//			if (file.exists())
//			{
//				return serializer.read(Records.class, file);
//			}
//			else
//			{
//				return new Records();
//			}
//		}
//		catch (Exception exception)
//		{
//			Utils.log(exception);
//		}

		return null;
	}

	public static boolean serialize(Records records)
	{
//		try
//		{
//			Serializer serializer = new Persister();
//			File file = Utils.mountFile(Preferences.RECORDS_FILE);
//
//			serializer.write(records, file);
//
//			return true;
//		}
//		catch (Exception exception)
//		{
//			Utils.log(exception);
//		}

		return false;
	}
}
