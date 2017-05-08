package website.suggestion.littlehelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import static android.content.Context.MODE_PRIVATE;
import static website.suggestion.littlehelper.Constants.NOTIFICATION_REQUEST_CODE;

/**
 * Created by Excelsior on 5/6/17.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    /**
     * Handles the Broadcast message sent when Geofence Exit Transition is triggered
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    /**
     * Build and send notification
     */
    private void sendNotification(Context context) {
        // Retrieve saved message from device
        SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.my_prefs), MODE_PRIVATE);
        String mMessage = prefs.getString(context.getResources().getString(R.string.message), null);

        // Create default message if there's no saved message.
        if (TextUtils.isEmpty(mMessage)) {
            mMessage = "Reminder";
        }

        // Delete current stored message
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(context.getResources().getString(R.string.message));
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.little_helper_logo)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setVisibility(1000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentText(mMessage)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
