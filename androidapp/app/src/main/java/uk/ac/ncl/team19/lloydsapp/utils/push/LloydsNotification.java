package uk.ac.ncl.team19.lloydsapp.utils.push;


public class LloydsNotification {
    // Notification types
    public static final String TYPE_HEARTBEAT = "heartbeat";
    public static final String TYPE_INFO = "info";
    public static final String TYPE_OFFER = "offer";

    private int id;
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
