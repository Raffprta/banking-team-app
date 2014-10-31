/**
 * Google Places JSON Response Wrapper Classes
 *
 * CSC2022 Team Project 2014
 * Dale Whinham / 130343550
 */

package uk.ac.ncl.team19.googleplaces;

import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
