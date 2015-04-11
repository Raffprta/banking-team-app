package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * Created by Dale Whinham on 25/03/15.
 */
public class UpdateSettingsRequest {
    boolean emailNotifications;
    boolean pushNotifications;

    public UpdateSettingsRequest(boolean emailNotifications, boolean pushNotifications) {
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
    }
}