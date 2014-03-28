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

package de.jurihock.voicesmith;

/**
 * Altered Auditory Feedback modes.
 * */
public enum AAF
{
	/**
	 * Frequency-shifted Auditory Feedback
	 * */
	FAF,

	/**
	 * Delayed Auditory Feedback
	 * */
	DAF,

	/**
	 * Delayed Auditory Feedback (min. possible delay, raw signal pumping)
	 * */
	FastDAF;

	private static final AAF[] aafValues = AAF.values();

	public static int count()
	{
		return aafValues.length;
	}

	public static AAF valueOf(int aafIndex)
	{
		return aafValues[aafIndex];
	}
}
