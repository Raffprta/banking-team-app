/**
 * Google Places JSON Response Wrapper Classes
 *
 * CSC2022 Team Project 2014
 * Dale Whinham / 130343550
 */

package uk.ac.ncl.team19.googleplaces;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlacesResponse {
    @SerializedName("html_attributions")
    private List<String> htmlAttributions;

    @SerializedName("results")
    private List<Place> results;

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