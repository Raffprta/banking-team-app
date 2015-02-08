package uk.ac.ncl.team19.lloydsapp;


import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import uk.ac.ncl.team19.lloydsapp.utils.maps.*;

/**
 * @Author Dale Whinham, with conversion to Fragment from Raffaello Perrotta
 * The purpose of this class is to provide an fragment that uses
 * the Google Places API to query for branch information within a specific
 * radius. This information is displayed via a Google maps wrapper.
 *
 * TODO Items:
 *  - Progress Bar needs to be its own separate view.
 *  - Better UI for getting bank locations and entering custom location. The buttons are just to
 *  - demonstrate what code you would use in the UI components to update the map fragment.
 *
 */

public class MapsFragment extends SupportMapFragment {

    // Types of search query
    private static final int QUERY_ATM = 0;
    private static final int QUERY_BRANCH = 1;

    // Default POST URL
    private String GOOGLE_PLACES_POST_URL;

    // 10 Kilometres (10,000 metres)
    private static final int SEARCH_RADIUS = 10000;

    // Logging tag
    private final String TAG = getClass().getSimpleName();

    // Location
    private Location myLocation = null;

    // Google Map
    private GoogleMap map = null;

    // List of branches
    private List<Branch> branches;

    // Branch finder button.
    Button branchButton;

    // ATM Finder button.
    Button atmButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View mapView = inflater.inflate(R.layout.fragment_maps, container, false);
        branchButton =  (Button) mapView.findViewById(R.id.branchFinder);
        atmButton = (Button) mapView.findViewById(R.id.atmFinder);
        return mapView;
    }

    /**
     * Set up the map from the view.
     */
    private void makeMap(){
        // Get the fragment, located in the child fragment manager.
        FragmentManager myFragmentManager = getChildFragmentManager();
        SupportMapFragment mySupportMapFragment
                = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.googleMap);
        map = mySupportMapFragment.getMap();

        // Map will try to determine location
        map.setMyLocationEnabled(true);

        Log.i(TAG, "Internet available? " + Utility.deviceHasInternetConnection(getActivity()));

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                // Store location
                myLocation = location;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String postcode = Utility.locationToPostcode(getActivity().getApplicationContext(), location);

                if (postcode != null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Your postcode is roughly: " + postcode, Toast.LENGTH_LONG).show();
                }

                // Disable listener
                map.setOnMyLocationChangeListener(null);
                map.setMyLocationEnabled(false);

                map.addMarker(new MarkerOptions()
                        .title(getString(R.string.your_loc))
                        .snippet(getString(R.string.loc_now))
                        .position(latLng));

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));

            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        // Enable cookie storage
        CookieManager.setDefault(new CookieManager());
        // Make all map related items
        makeMap();
        // Set up buttons
        branchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Clear map of previous searches and add your location back to it.
                map.clear();
                map.addMarker(new MarkerOptions()
                        .title(getString(R.string.your_loc))
                        .snippet(getString(R.string.loc_now))
                        .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
                // Query for branches.
                QueryGooglePlacesTask qg = new QueryGooglePlacesTask();
                qg.execute(myLocation, QUERY_BRANCH);
            }
        });

        atmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Clear map of previous searches and add your location back to it.
                map.clear();
                map.addMarker(new MarkerOptions()
                        .title(getString(R.string.your_loc))
                        .snippet(getString(R.string.loc_now))
                        .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));
                // Query for branches
                QueryGooglePlacesTask qg = new QueryGooglePlacesTask();
                qg.execute(myLocation, QUERY_ATM);
            }
        });

    }


    private GooglePlacesResponse queryGooglePlaces(Location location, int type) throws IOException {

        // Determine what kind of query was requested and pass it to the request URL.
        if(type == QUERY_ATM)
            GOOGLE_PLACES_POST_URL = getString(R.string.POST_ATM);
        else if(type == QUERY_BRANCH)
            GOOGLE_PLACES_POST_URL = getString(R.string.POST_BRANCH);

        // Input stream for holding the response
        InputStream inputStream = null;

        // Output stream for sending the POST data
        DataOutputStream dataOutputStream = null;
        Log.w("S", GOOGLE_PLACES_POST_URL);
        try {
            // Insert latitude, longitude and radius into the Google Place API URL
            URL url = new URL(String.format(GOOGLE_PLACES_POST_URL, location.getLatitude(), location.getLongitude(), SEARCH_RADIUS));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);     // milliseconds
            conn.setConnectTimeout(15000);  // milliseconds
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "The response was: " + responseCode);

            inputStream = conn.getInputStream();

            // Convert the InputStream into a string
            String responseBody = Utility.inputStreamToString(inputStream);

            // Convert JSON response into Java objects
            Gson gson = new Gson();
            return gson.fromJson(responseBody, GooglePlacesResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close streams no matter what happens
            if (inputStream != null) {
                inputStream.close();
            }

            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
        }
        return null;
    }

    // Async task with multiple parameters.
    private class QueryGooglePlacesTask extends AsyncTask<Object, Void, GooglePlacesResponse> {
        @Override
        protected GooglePlacesResponse doInBackground(Object... params) {
            // Attempt to query the Lloyds Branch Finder
            try {
                return queryGooglePlaces((Location)params[0], (int)params[1]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(GooglePlacesResponse googlePlacesResponse) {

            if (googlePlacesResponse == null) {
                // Query failed
                Toast.makeText(getActivity().getApplicationContext(), "No results found.", Toast.LENGTH_LONG).show();
            } else {
                List<Place> places = googlePlacesResponse.getResults();

                // Query succeeded
                Log.d (TAG, places.size() + "results found!");

                for (Place p: places) {
                    uk.ac.ncl.team19.lloydsapp.utils.maps.Location location = p.getGeometry().getLocation();
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    if (lat != 0 && lng != 0) {
                        // Add map markers
                        map.addMarker(new MarkerOptions()
                                .title(p.getName())
                                .snippet(p.getVicinity())
                                .position(new LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }
        }
    }


}
