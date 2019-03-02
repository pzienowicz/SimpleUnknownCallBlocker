package pl.pzienowicz.unknowncallblocker.util;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

public class MyAudioManager {

    private AudioManager audioManager;

    public MyAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void mute() {
        //Turn ON the mute
        if(audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
            }
        }
    }

    public void unmute() {
        //Turn OFF the mute
        if(audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_RING, false);
            }
        }
    }
}
