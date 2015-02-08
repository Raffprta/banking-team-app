package uk.ac.ncl.team19.lloydsapp.utils.maps;

import java.util.List;

/**
 * A class to represent a bank branch.
 * Created by Dale Whinham on 22/10/14.
 */
public class Branch {
    private String name;
    private List<String> addressLines;
    private String postcode;
    private double latitude;
    private double longitude;

    public Branch(String name, List<String> addressLines, String postcode) {
        this.name = name;
        this.addressLines = addressLines;
        this.postcode = postcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddressLines() {
        return addressLines;
    }

    public void setAddressLines(List<String> addressLines) {
        this.addressLines = addressLines;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

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

    public String getLocationString() {
        if (addressLines != null && addressLines.size() > 0) {
            return addressLines.get(0) + ", " + postcode;
        } else {
            return postcode;
        }
    }

    @Override
    public String toString() {
        String string = "[" + name + ", ";

        for (String s: addressLines) {
            string += s + ", ";
        }

        string += String.format("%s (%f, %f)]", postcode, latitude, longitude);

        return string;
    }
}
