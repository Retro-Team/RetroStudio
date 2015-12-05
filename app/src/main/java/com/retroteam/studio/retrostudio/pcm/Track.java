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

public class Track implements Serializable
{
    private ArrayList<Measure> measures;
    private TimeSignature timeSignature;
    private SoundWave soundWave;
    private double masterVolume;
    private double volume;
    private double amplitude;
    private double tempo;

    public Track(double tempo, double masterVolume, TimeSignature timeSignature, SoundWave soundWave)
    {
        this.timeSignature = timeSignature;
        this.masterVolume = masterVolume;
        this.volume = 0.5; // default value
        this.amplitude = masterVolume * volume; // default value
        this.tempo = tempo;
        this.soundWave = soundWave;
        measures = new ArrayList<Measure>();
    }

    public double amplitude()
    {
        return amplitude;
    }

    public double masterVolume()
    {
        return masterVolume;
    }

    public double volume()
    {
        return volume;
    }

    public int size()
    {
        return measures.size();
    }

    public Measure measure(int index)
    {
        if (index >= 0 && index < measures.size())
        {
            return measures.get(index);
        }
        else
        {
            return null;
        }
    }

    public Track addMeasure()
    {
        measures.add(new Measure(timeSignature));
        return this;
    }
    public Track addMeasure(Measure measure)
    {
        Measure clone = new Measure(timeSignature);
        for (int i = 0; i < measure.length(); i++)
        {
            clone.replace(i, measure.getNote(i));
        }
        measures.add(clone);
        return this;
    }
    public Track addMeasure(int index)
    {
        measures.add(index, new Measure(timeSignature));
        return this;
    }
    public Track addMeasure(int index, Measure measure)
    {
        Measure clone = new Measure(timeSignature);
        for (int i = 0; i < measure.length(); i++)
        {
            clone.replace(i, measure.getNote(i));
        }
        measures.add(index, clone);
        return this;
    }

    public Track volume(double volume)
    {
        this.volume = volume;
        amplitude = masterVolume * volume;
        return this;
    }

    public Track masterVolume(double masterVolume)
    {
        this.masterVolume = masterVolume;
        amplitude = masterVolume * volume;
        return this;
    }

    public Track soundWave(SoundWave soundWave)
    {
        this.soundWave = soundWave;
        return this;
    }

    public SoundStream export()
    {
        return export(0);
    }
    public SoundStream export(int measureIndex)
    {
        SoundStream stream = new SoundStream();
        Measure[] measure = measures.toArray(new Measure[measures.size()]);
        for (int i = measureIndex; i < measure.length; i++)
        {
            int n = 0;
            while (n < measure[i].length())
            {
                int m = n + 1;
                double noteLength = (1 / Project.SNAP);
                while (m < measure[i].length())
                {
                    if (measure[i].note(n) == measure[i].note(m))
                    {
                        noteLength += (1 / Project.SNAP);
                    }
                    else
                    {
                        break;
                    }
                    m++;
                }
                stream.append(soundWave.play(measure[i].note(n), amplitude, noteLength, tempo));
                n = m;
            }
        }
        return stream;
    }

    public SoundWave soundWave()
    {
        return soundWave;
    }
}
