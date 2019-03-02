package pl.pzienowicz.unknowncallblocker.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class MyNotificationChannel {

    public static String create(Context context) {
        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = "4234532";

            // The user-visible name of the channel.
            CharSequence channelName = "Blocker";
            // The user-visible description of the channel.
            String channelDescription = "Call blocked Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate = true;
//            int channelLockscreenVisibility = Notification.;

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
//            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
}
