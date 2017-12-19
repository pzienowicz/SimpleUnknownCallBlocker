package pl.pzienowicz.unknowncallblocker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PhoneCallStateListener extends PhoneStateListener {

    public static final int NOTIFICATION_ID = 546435;
    private Context context;

    public PhoneCallStateListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:

                boolean isEnabled = prefs.getBoolean("serviceEnabled", false);

                if(!isEnabled) {
                    return;
                }

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                //Turn ON the mute
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);

                try {
                    if (incomingNumber == null || incomingNumber.length() == 0) {
                        createNotification();
                        endCall();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

                //Turn OFF the mute
                audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }

    private void endCall() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(telephonyManager.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method method = null;
        try {
            assert myClass != null;
            method = myClass.getDeclaredMethod("getITelephony");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        assert method != null;
        method.setAccessible(true);
        ITelephony telephonyService = null;
        try {
            telephonyService = (ITelephony) method.invoke(telephonyManager);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        assert telephonyService != null;
        telephonyService.endCall();
    }

    /**
     * Tworzy i wyświetla powiadomienie na pasku
     */
    private void createNotification()
    {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getText(R.string.app_name))
                .setContentText(context.getString(R.string.unknown_number_blocked));

        Notification n;

        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = builder.getNotification();
        } else {
            n = builder.build();
        }

        nm.notify(NOTIFICATION_ID, n);
    }

}
