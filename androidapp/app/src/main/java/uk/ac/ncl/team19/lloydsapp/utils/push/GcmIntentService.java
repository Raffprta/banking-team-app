package uk.ac.ncl.team19.lloydsapp.utils.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Calendar;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.features.PushFragment;

public class GcmIntentService extends IntentService {
    private static final String NOTIFICATION_TITLE = "Lloyds Bank";
    private static final long[] VIBRATE_PATTERN = { 0, 500, 150, 500 };
    private static final int NOTIFICATION_ID = 1;

    public static final String DATA_REFRESH_INTENT = "uk.ac.ncl.team19.lloydsapp.utils.push.refreshdata";

    private NotificationManager mNotificationManager;

    // Log tag for debugging
    private String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            }

            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            }

            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                sendNotification(extras.getString("message"));
                Log.i(TAG, "Received: " + extras.toString());

                // Store notification in database
                NotificationsDataSource dataSource = new NotificationsDataSource(getApplicationContext());

                // Open database
                dataSource.open();

                // Retrieve records
                dataSource.createNotification( Calendar.getInstance().getTimeInMillis(),
                                               messageType,
                                               extras.getString("message"));

                // Close database (important)
                dataSource.close();

                // Do stuff that alters the content of my local SQLite Database
                sendBroadcast(new Intent(DATA_REFRESH_INTENT));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Post a notification (shows up in status bar etc)
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PushFragment.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(NOTIFICATION_TITLE)
                        .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                        .setContentText(msg)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(VIBRATE_PATTERN);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}