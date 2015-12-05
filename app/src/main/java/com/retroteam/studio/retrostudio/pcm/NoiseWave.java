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

import java.io.Serializable;
import java.util.Random;

public class NoiseWave extends SoundWave
{
	private static double last = 0.0;

	public NoiseWave()
	{
		super();
	}
	public NoiseWave(int sampleRate)
	{
		super(sampleRate);
	}

	@Override
	public double waveForm(int i, double pitch, double noteLength, double tempo)
	{
		double v = 0.0;
		if (Math.random() < (pitch / (3 * Note.B))) v = Math.random() * 2 - 1;
		return v;
	}
}