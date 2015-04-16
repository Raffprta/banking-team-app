package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * @author Raffaello Perrotta
 *
 * A class to provide a method to update the Google Play ID of a user via the APIConnector.
 */
public class UpdatePlayIdRequest {

    String playId;

    public UpdatePlayIdRequest(String playId) {
        this.playId = playId;
    }

}
