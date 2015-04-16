package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * @author Raffaello Perrotta
 * Original Update Settings Code.
 * @author Dale Whinham
 * Refactor to RetroFit
 *
 * A class to provide a method to update the settings of a user via the APIConnector.
 */
public class UpdateSettingsRequest {
    final boolean emailNotifications;
    final boolean pushNotifications;

    public UpdateSettingsRequest(boolean emailNotifications, boolean pushNotifications) {
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
    }
}