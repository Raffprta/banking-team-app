package uk.ac.ncl.team19.lloydsapp.utils.push;

/**
 * @author Dale Whinham
 *
 * A data class to represent what a Lloyds Notification consists of. This includes
 * a unique id, a time in milliseconds, a notification type which is then iconised as
 * well as the message itself.
 */

public class LloydsNotification {
    // Notification types
    public static final String TYPE_HEARTBEAT = "heartbeat";
    public static final String TYPE_INFO = "info";
    public static final String TYPE_OFFER = "offer";

    private final int id;
    private long dateInMillis;
    private String notificationType;
    private String notificationMessage;

    public LloydsNotification(int id, long dateInMillis, String notificationType, String notificationMessage) {
        this.id = id;
        this.dateInMillis = dateInMillis;
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public int getId(){
        return this.id;
    }
}
