package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * @author Raffaello Perrotta
 * Original GCMID update code.
 * @author Dale Whinham
 * Refactor to RetroFit.
 *
 * A lass to provide a method to update the GCMID of a user via the APIConnector.
 */
public class UpdateGcmIdRequest {
    final String gcmId;

    public UpdateGcmIdRequest(String gcmId) {
        this.gcmId = gcmId;
    }
}