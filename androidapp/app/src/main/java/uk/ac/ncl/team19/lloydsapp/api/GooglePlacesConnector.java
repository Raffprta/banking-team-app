package uk.ac.ncl.team19.lloydsapp.api;

import android.content.Context;
import android.location.Location;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.POST;
import retrofit.http.Query;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.response.GooglePlacesResponse;

/**
 * Created by Dale Whinham on 15/04/15.
 */
public class GooglePlacesConnector {
    private RestAdapter restAdapter;
    private GooglePlacesService service;

    private interface GooglePlacesService {
        @POST("/nearbysearch/json")
        void nearbySearch(
                @Query("location") String location,
                @Query("radius") Integer radius,
                @Query("types") String types,
                Callback<GooglePlacesResponse> cb
        );
    }

    public GooglePlacesConnector(Context ctx) {
        final String baseURL = ctx.getString(R.string.places_base_url);
        final String apiKey = ctx.getString(R.string.places_api_key);

        // This request interceptor adds the API key and company name to every request
        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("name", "Lloyds");
                request.addQueryParam("key", apiKey);
            }
        };

        // Set up REST adapter
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setRequestInterceptor(interceptor)
                .build();

        service = restAdapter.create(GooglePlacesService.class);
    }

    public void findLloydsBranches(Location location, int radius, Callback<GooglePlacesResponse> cb) {
        service.nearbySearch(formatLocation(location), radius, "bank", cb);
    }

    public void findLloydsAtms(Location location, int radius, Callback<GooglePlacesResponse> cb) {
        service.nearbySearch(formatLocation(location), radius, "atm", cb);
    }

    private String formatLocation(Location location) {
        return String.format("%f,%f", location.getLatitude(), location.getLongitude());
    }
}