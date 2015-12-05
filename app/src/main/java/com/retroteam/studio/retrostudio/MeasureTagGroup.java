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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Group of measure tags associated with a measure. Get saved and loaded with ProjectWrapper.
 */
public class MeasureTagGroup implements Serializable {

    public int row;
    public int column;
    public boolean hasNotes;
    public int guiSNAP;
    public ArrayList<int[]> filledNotes;

    public MeasureTagGroup(int row, int column, boolean hasNotes, int guiSNAP, ArrayList<int[]> filledNotes){

        this.row = row;
        this.column = column;
        this.hasNotes = hasNotes;
        this.guiSNAP = guiSNAP;
        this.filledNotes = filledNotes;
    }
}
