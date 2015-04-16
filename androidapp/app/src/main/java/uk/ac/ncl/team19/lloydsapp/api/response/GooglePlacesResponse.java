package uk.ac.ncl.team19.lloydsapp.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.Place;

/**
 * @author Dale Whinham
 *
 * A class to represent the response given by the Google Places backend ontaining status results,
 * error messages and HTML attributes of the request that was made.
 */

public class GooglePlacesResponse {
    public enum Status {
        @SerializedName("OK")
        OK,
        @SerializedName("ZERO_RESULTS")
        ZERO_RESULTS,
        @SerializedName("OVER_QUERY_LIMIT")
        OVER_QUERY_LIMIT,
        @SerializedName("REQUEST_DENIED")
        REQUEST_DENIED,
        @SerializedName("INVALID_REQUEST")
        INVALID_REQUEST
    }

    private Status status;

    @SerializedName("error_message")
    private String errorMessage;

    @SerializedName("html_attributions")
    private List<String> htmlAttributions;

    @SerializedName("results")
    private List<Place> results;

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public List<Place> getResults() {
        return results;
    }

    public void setResults(List<Place> results) {
        this.results = results;
    }
}