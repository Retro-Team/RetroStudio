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
 *  Wrapper class for a byte array for 8 bit sounds.
 */

import java.io.Serializable;

public class SoundStream implements Serializable
{
	private byte[] stream;

	public SoundStream()
	{
		stream = new byte[0];
	}
	public SoundStream(byte[] stream)
	{
		this.stream = stream;
	}
	public SoundStream(byte[] ... streams)
	{
		this.stream = new byte[0];
		this.append(streams);
	}
	public SoundStream(SoundStream ss)
	{
		this.stream = ss.getBytes();
	}
	public SoundStream(SoundStream ... ss)
	{
		stream = new byte[0];
		for (SoundStream s : ss) append(s);
	}

	public SoundStream append(byte[] b)
	{
		byte[] a = stream;
		stream = new byte[a.length + b.length];
		int i, j;
		for (i = 0; i < a.length; i++)
		{
			stream[i] = a[i];
		}
		for (j = 0; j < b.length; j++)
		{
			stream[i++] = b[j];
		}

		return this;
	}
	public SoundStream append(byte[] ... streams)
	{
		byte[] a = stream;
		int length = a.length;
		for (byte[] s : streams) length += s.length;
		stream = new byte[length];
		int i, j;
		for (i = 0; i < a.length; i++)
		{
			stream[i] = a[i];
		}
		for (byte[] s : streams)
		{
			for (j = 0; j < s.length; j++)
			{
				stream[i++] = s[j];
			}
		}

		return this;
	}
	public SoundStream append(SoundStream ss)
	{
		append(ss.getBytes());

		return this;
	}
	public SoundStream append(SoundStream ... ss)
	{
		for ( SoundStream s : ss )
		{
			append(s.getBytes());
		}

		return this;
	}
	public SoundStream append(int n)
	{
		byte[] tail = new byte[n];
		for (int i = 0; i < n; i++) tail[i] = (byte)(128);
		append(tail);
		return this;
	}

	public SoundStream overlay(byte[] b)
	{
		if (b.length > stream.length)
		{
			byte[] tail = new byte[b.length - stream.length];
			for (int i = 0; i < tail.length; i++)
			{
				tail[i] = (byte)(128);
			}
			append(tail);
		}

		for (int i = 0; i < stream.length; i++)
		{
			/*
			int si = (stream[i] & 0xff) - 128;
			int bi = (b[i] & 0xff) - 128;
			si = si + bi - (si * bi);
			si &= 0xff;
			si += 128;
			stream[i] = (byte)(si & 0xff);
			*/

			int si = stream[i] & 0xff;
			int bi = b[i] & 0xff;
			si -= 128;
			bi -= 128;
			double result = (si + bi) * (1 / Math.sqrt(2));
			result += 128;
			stream[i] = (byte)result;
		}

		return this;
	}
	public SoundStream overlay(byte[] ... streams)
	{
		for (byte[] b : streams)
			overlay(b);
		return this;
	}
	public SoundStream overlay(SoundStream ss)
	{
		overlay(ss.getBytes());
		return this;
	}
	public SoundStream overlay(SoundStream ... ss)
	{
		for (SoundStream s : ss)
			overlay(s.getBytes());
		return this;
	}

	public byte[] getBytes()
	{
		return stream;
	}

	public int length()
	{
		return stream.length;
	}
}
