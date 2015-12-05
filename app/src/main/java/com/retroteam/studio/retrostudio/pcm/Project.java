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
import java.util.ArrayList;

public class Project implements Serializable
{
    public static final double SNAP = Math.pow(2, 5);

    private TimeSignature timeSignature;
    private ArrayList<Track> tracks;
    private double masterVolume;
    private double tempo;

    public Project(TimeSignature timeSignature)
    {
        this.masterVolume = 0.5;
        this.timeSignature = timeSignature;
        tracks = new ArrayList<Track>();
    }

    public double masterVolume()
    {
        return masterVolume;
    }

    public double tempo()
    {
        return tempo;
    }

    public int size()
    {
        return tracks.size();
    }

    public SoundStream export()
    {
        return export(0);
    }
    public SoundStream export(int measureIndex)
    {
        if (tracks.size() == 0) return new SoundStream();
        if (tracks.size() == 1) return tracks.get(0).export(measureIndex);

        SoundStream[] streams = new SoundStream[tracks.size()];
        int indexOfLongest = 0;

        for (int i = 0; i < tracks.size(); i++)
        {
            streams[i] = tracks.get(i).export(measureIndex);
            if (streams[i].length() > streams[indexOfLongest].length())
            {
                indexOfLongest = i;
            }
        }
        for (int i = 0; i < tracks.size(); i++)
        {
            if (i != indexOfLongest)
            {
                streams[i].append(streams[indexOfLongest].length() - streams[i].length());
            }
        }

        return overlayStreams(streams);
    }

    public Project addTrack(SoundWave soundWave)
    {
        tracks.add(new Track(tempo, masterVolume, timeSignature, soundWave));
        return this;
    }

    public Project deleteTrack(int index)
    {
        if (index >= 0 && index < tracks.size())
        {
            tracks.remove(index);
        }
        return this;
    }

    public Track track(int index)
    {
        if (index >= 0 && index < tracks.size())
        {
            return tracks.get(index);
        }
        else
        {
            return null;
        }
    }

    public Project masterVolume(double masterVolume)
    {
        this.masterVolume = masterVolume;
        for (Track track : tracks)
        {
            track.masterVolume(masterVolume);
        }
        return this;
    }

    public Project tempo(double tempo)
    {
        this.tempo = tempo;
        return this;
    }

    private SoundStream overlayStreams(SoundStream[] streams)
    {
        SoundStream stream = new SoundStream();
        byte[][] bytes = new byte[streams.length][];
        double compressor = (1 / Math.sqrt(tracks.size()));
        for (int i = 0; i < tracks.size(); i++)
        {
            bytes[i] = streams[i].getBytes();
        }
        for (int i = 0; i < bytes[0].length; i++)
        {
            int si = 0;
            for (int j = 0; j < tracks.size(); j++)
            {
                si += ((bytes[j][i] & 0xff) - 128);
            }
            si *= compressor;
            si += 128;
            stream.append(new byte[] { (byte)(si) });
        }
        return stream;
    }
}