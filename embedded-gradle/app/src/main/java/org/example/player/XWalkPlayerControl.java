package org.example.player;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;

/**
 * Created by junweifu on 5/29/2016.
 */
public class XWalkPlayerControl extends PlayerControl {
    private int videoDuration;

    public XWalkPlayerControl(ExoPlayer exoPlayer) {
        super(exoPlayer);

        videoDuration = 0;
    }

    public void setDuration(int duration) {
        videoDuration = duration;
    }

    @Override
    public int getDuration() {
        return Math.max(videoDuration, super.getDuration());
    }

}
