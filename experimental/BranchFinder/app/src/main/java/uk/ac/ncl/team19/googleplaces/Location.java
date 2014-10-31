/**
 * Google Places JSON Response Wrapper Classes
 *
 * CSC2022 Team Project 2014
 * Dale Whinham / 130343550
 */

package uk.ac.ncl.team19.googleplaces;

import com.google.gson.annotations.SerializedName;

public class Location{
    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
