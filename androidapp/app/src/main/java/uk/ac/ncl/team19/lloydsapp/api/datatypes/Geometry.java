/**
 * Google Places JSON Response Wrapper Classes
 *
 * CSC2022 Team Project 2014
 * Dale Whinham / 130343550
 */

package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    private PlaceLocation location;

    public PlaceLocation getLocation() {
        return location;
    }

    public void setLocation(PlaceLocation location) {
        this.location = location;
    }
}
