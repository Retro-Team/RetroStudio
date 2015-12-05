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

package com.retroteam.studio.retrostudio;

import com.retroteam.studio.retrostudio.pcm.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project wrapper class for serializing and saving/loading.
 */
public class ProjectWrapper implements Serializable{

    public Project project;
    public TimeSignature tSig;
    public String title;
    public Double tempo;
    public ArrayList<MeasureTagGroup> mTagList;


    public ProjectWrapper(Project project, TimeSignature tSig, String title, Double tempo, ArrayList<MeasureTagGroup> mTagList)
    {
        this.project = project;
        this.tSig = tSig;
        this.title = title;
        this.tempo = tempo;
        this.mTagList = mTagList;

    }



}
