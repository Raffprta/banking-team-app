package uk.ac.ncl.team19.lloydsapp.utils.push;


public class LloydsNotification {
    private long dateInMillis;
    private String notificationType;
    private String notificationMessage;

    public LloydsNotification(long dateInMillis, String notificationType, String notificationMessage) {
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
}
