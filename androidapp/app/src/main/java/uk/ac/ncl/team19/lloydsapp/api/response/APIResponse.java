package uk.ac.ncl.team19.lloydsapp.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Dale Whinham
 *
 * A generic class that represents a reply from the backend including the status
 * of the request as well as a custom JSON field as well as any respective error messages.
 *
 */
public class APIResponse {
    public enum Status {
        @SerializedName("error")
        ERROR,
        @SerializedName("success")
        SUCCESS
    }

    private Status status;
    private String errorMessage;

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}