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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.retroteam.studio.retrostudio.pcm.SoundStream;

import java.lang.Thread;

/**
 * Retu
 * Class for playing back a 8 bit SoundStream on a thread.
 */

public class ThreadedPlayback {

    public AudioTrack at;
    public byte[] music = null;

    public ThreadedPlayback(SoundStream stream) {

        music = stream.getBytes();
        at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT, music.length, AudioTrack.MODE_STREAM);
    }

    public void stop(){
        at.stop();
        at.release();
    }

    public void play() {
        at.play();

        new Thread (new Runnable(){
            public void run() {
                at.write(music, 0, music.length);
            }
        }).start();
    }
}
