package raffprta.eu.achievementsexample;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author Raffaello Perrotta
 * A wrapper for the Google API Client so that it can be passed using intent.putextra
 */
public class GoogleApiWrapper implements Serializable {

    private GoogleApiClient api;

    public GoogleApiWrapper(GoogleApiClient api) {
        this.api = api;
    }

    public String serialize(){
        return (new Gson()).toJson(this.api);
    }

    public GoogleApiClient getAPI(){
        return this.api;
    }



}
