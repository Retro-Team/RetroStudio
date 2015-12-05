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
/*
 *  Here's where all the mathy stuff lives.
 */

import java.io.Serializable;
import java.util.Random;

public abstract class SoundWave implements Serializable
{
	public static int DEFAULT_SAMPLE_RATE = 8000;

	private int sampleRate;

	public SoundWave()
	{
		this(SoundWave.DEFAULT_SAMPLE_RATE);
	}

	public SoundWave(int sampleRate)
	{
		this.sampleRate = sampleRate;
	}

	public double sampleRate()
	{
		return sampleRate;
	}

	public double getSin(int i, double pitch)
	{
		return Math.sin( (i * pitch) * (Math.PI / sampleRate) );
	}

	public byte[] play(double pitch, double amplitude, double noteLength, double tempo)
	{
		double t = ((60.0 / tempo) * sampleRate * noteLength);
		byte[] stream = new byte[(int)(t)];
		for (int i = 0; i < stream.length; i++)
			stream[i] = (byte)((128.0 * amplitude * waveForm(i, pitch, noteLength, tempo)) + 128);
		return stream;
	}

	public byte[] rest(double noteLength, double tempo)
	{
		double t = ((60.0 / tempo) * sampleRate * noteLength);
		byte[] stream = new byte[(int)t];
		for (int i = 0; i < stream.length; i++)
			stream[i] = 0;
		return stream;
	}

	// 'waveForm' is a value between 1.0 and -1.0 which is
	// automatically multiplied by the volume in the 'play' method.
	public abstract double waveForm(int i, double pitch, double noteLength, double tempo);
}
