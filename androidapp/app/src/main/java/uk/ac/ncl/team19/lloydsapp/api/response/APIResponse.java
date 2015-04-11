package uk.ac.ncl.team19.lloydsapp.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dale Whinham on 25/03/15.
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