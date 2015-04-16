package uk.ac.ncl.team19.lloydsapp.utils.notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * @author Raffaello Perrotta
 *
 * Adapted from: http://www.technotalkative.com/android-custom-toast-notification/
 * Creates a custom view for an Android toast using composition. A user may create
 * an icon displayed to the left of the toast, with text to the right. This is perfect
 * for iconified messages like achievements unlockables!
 */
public class Toaster{

    private final Toast toast;
    private final ImageView img;
    private final Activity a;
    private final TextView text;
    private String title;

    // Static int accumulates notification ids for wearables
    private static int id = 0;
    private final String NOTIFICATION_ID = "notification_id";

    /***
     * Displays an iconised image to the left of the custom toast.
     * @param a the activity reference where the toast is displayed
     */
    public Toaster(Activity a){
        this.a = a;
        // Sets an inflater to inflate in respect to the activity that calls it. Sets the layout to the iconified view.
        LayoutInflater inflater = a.getLayoutInflater();
        View view = inflater.inflate(R.layout.iconised_layout, (ViewGroup) a.findViewById(R.id.toastLayout));
        // Get the references to our imageview and text view.
        this.img = (ImageView) view.findViewById(R.id.toastIcon);
        this.text = (TextView) view.findViewById(R.id.toastText);
        // Get a toast and set it a custom view.
        this.toast = new Toast(a);
        toast.setView(view);
    }

    /**
     * @param text, the text you want to display
     * @param imageResource the image resource, i.e. from R.drawable
     */
    public void grabToast(String text, int imageResource){
        this.text.setText(text);
        this.img.setImageResource(imageResource);
        toast.show();
    }

    /**
     * Modify the title of a notification passed to an Android smart watch.
     * @param text the title of the notification
     */
    public void modifyWearableTitle(String text){
        this.title = text;
    }

    /**
     * Effectively the same as grabToast but the notification is sent to a smart watch.
     * @param text, the text you want to display in the notification
     * @param previewText the smaller text you want to display underneath the icon of the watch. No longer than a word is ideal.
     * @param imageResource the image resource displayed in the circular watch notification slot, i.e. from R.drawable
     */
    public void grabToastForWearable(String text, String previewText, int imageResource){
        // Get the shared preference value of whether notifications are on or off.
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        if(!sp.getBoolean(a.getString(R.string.sp_notif_wearable), false)){
            // If they are disabled, do not send a message.
            return;
        }

        // Increment the id of the notification
        id++;

        // Builds the intent which will be launched to the wearable device
        Intent viewIntent = new Intent(this.a, WearableActivity.class);
        viewIntent.putExtra(NOTIFICATION_ID, id);

        PendingIntent viewPendingIntent = PendingIntent.getActivity(this.a, 0, viewIntent, 0);
        // Build the notification, defaults will ensure the watch will vibrate when receiving
        // such a notification and auto cancel dismisses the notification on the user clicking it.
        // Add action sets the icon with a small preview text attached to it.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this.a)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(this.title)
                        .setContentText(text)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent)
                        .addAction(imageResource,previewText, viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this.a);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(id, notificationBuilder.build());

    }

}
