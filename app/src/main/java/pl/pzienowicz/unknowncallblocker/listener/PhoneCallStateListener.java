package pl.pzienowicz.unknowncallblocker.listener;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import java.lang.reflect.Method;

import pl.pzienowicz.unknowncallblocker.R;
import pl.pzienowicz.unknowncallblocker.util.MyAudioManager;
import pl.pzienowicz.unknowncallblocker.util.MyNotificationChannel;

public class PhoneCallStateListener extends PhoneStateListener {

    private static final int NOTIFICATION_ID = 2425;
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

                MyAudioManager audioManager = new MyAudioManager(context);
                audioManager.mute();

                try {
                    if (incomingNumber == null || incomingNumber.length() == 0) {
                        endCall();
                        createNotification();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Block", e.getMessage());
                }

                audioManager.unmute();
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }

    private void endCall() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            endCallForApi28();
            return;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        Class<?> myClass = Class.forName(telephonyManager.getClass().getName());
        Method method = myClass.getDeclaredMethod("getITelephony");
        method.setAccessible(true);

        ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
        telephonyService.endCall();
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void endCallForApi28() {
        TelecomManager tm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            tm = (TelecomManager)context.getSystemService(Context.TELECOM_SERVICE);
            if (tm == null) {
                throw new NullPointerException("tm == null");
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            tm.endCall();
        } else {
            //nothing to do for android O - ending calls is not supported in any way
        }
    }

    private void createNotification()
    {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyNotificationChannel.create(context));

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getText(R.string.app_name))
                .setContentText(context.getString(R.string.unknown_number_blocked));

        nm.notify(NOTIFICATION_ID, builder.build());
    }

}
