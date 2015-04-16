package uk.ac.ncl.team19.lloydsapp.api.request;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.SecureChar;

/**
 * @author Dale Whinham
 *
 * A method to pass an authorisation request to the server which includes username, password and secure
 * characters. This method is loaded into the APIConnector.
 */
public class AuthRequest {
    private String username;
    private String password;
    private List<SecureChar> secureChars;

    public AuthRequest(String username, String password, List<SecureChar> secureChars) {
        this.username = username;
        this.password = password;
        this.secureChars = secureChars;
    }
}
