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

public class Measure implements Serializable
{
    private Note[] notes;
    private TimeSignature timeSignature;

    public Measure(TimeSignature timeSignature)
    {
        this.timeSignature = timeSignature;
        Note[] notes = new Note[length()];
        for (int i = 0; i < notes.length; i++)
        {
            notes[i] = new Note(Note.REST, 0);
        }
        this.notes = notes;
    }

    public Measure replace(int index, Note note)
    {
        if (index >= 0 && index < notes.length)
        {
            notes[index] = note;
        }
        return this;
    }

    public Note getNote(int index)
    {
        if (index >= 0 && index < notes.length)
        {
            return notes[index];
        }
        else
        {
            return null;
        }
    }

    public double note(int index)
    {
        if (index >= 0 && index < notes.length)
        {
            return notes[index].pitch();
        }
        else
        {
            return 0.0;
        }
    }

    public int length()
    {
        return (int)((timeSignature.beats() / timeSignature.notes()) * Project.SNAP);
    }
}
