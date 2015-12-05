/*
 * Retro Studio Copyright 2015 Retro Team
 * This file is part of Retro Studio.
 *
 * Retro Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Retro Studio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Retro Studio.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.retroteam.studio.retrostudio.pcm;

public class SawtoothWave extends SoundWave
{
	public SawtoothWave()
	{
		super();
	}
	public SawtoothWave(int sampleRate)
	{
		super(sampleRate);
	}

	@Override
	public double waveForm(int i, double pitch, double noteLength, double tempo)
	{
		double k = pitch / sampleRate();
		return -1.0 + ((i % (2 * sampleRate() / pitch)) * k);
	}
}