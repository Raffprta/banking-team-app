package uk.ac.ncl.team19.lloydsapp.api.request;

import java.util.List;

/**
 * Created by Dale Whinham on 25/03/15.
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
