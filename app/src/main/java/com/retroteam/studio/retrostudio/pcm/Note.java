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
 *  Contains base values for every note and the logic to shift a note
 *  up or down an octave.
 */

import java.io.Serializable;

public class Note implements Serializable
{
	public static final double REST = 0.0;
	public static final double C = 32.703;
	public static final double CSHARP = 34.648;
	public static final double DFLAT = CSHARP;
	public static final double D = 36.708;
	public static final double DSHARP = 38.891;
	public static final double EFLAT = DSHARP;
	public static final double E = 41.203;
	public static final double F = 43.654;
	public static final double FSHARP = 46.249;
	public static final double GFLAT = FSHARP;
	public static final double G = 48.999;
	public static final double GSHARP = 51.913;
	public static final double AFLAT = GSHARP;
	public static final double A = 55.0;
	public static final double ASHARP = 58.270;
	public static final double BFLAT = ASHARP;
	public static final double B = 61.735;

	// Rounds doubles to the nearest 0.001.
	private static double round(double d)
	{
		d *= 1000;
		d += 0.5;
		int di = (int) d;
		d = ((double) di) / 1000;
		return d;
	}

	// Instance variables.
	private double pitch; // rest, c, c#, d, etc.

	// Musical notes are in scientific pitch C4 is middle-c.
	public Note(double note, int sub)
	{
		if (sub >= 1)
		{
			pitch = round(note * Math.pow(2, sub));
		}
		else
		{
			pitch = round(note / Math.pow(2, Math.abs(sub - 1)));
		}
	}

	public Note pitch(double pitch)
	{
		this.pitch = pitch;
		return this;
	}

	public double pitch()
	{
		return pitch;
	}
}
