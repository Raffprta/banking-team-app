package uk.ac.ncl.team19.lloydsapp.api.response;

/**
 * @author Dale Whinham
 *
 * A class to represent the response given by the backend when a device token is generated.
 */
public class AuthResponse extends APIResponse {
    private String deviceToken;

    public String getDeviceToken() {
        return deviceToken;
    }
}