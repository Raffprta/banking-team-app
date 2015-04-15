package uk.ac.ncl.team19.lloydsapp.features;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;
import uk.ac.ncl.team19.lloydsapp.utils.maps.GooglePlacesResponse;
import uk.ac.ncl.team19.lloydsapp.utils.maps.Place;
import uk.ac.ncl.team19.lloydsapp.utils.maps.Utility;

/**
 * @author Dale Whinham, with conversion to Fragment from Raffaello Perrotta
 * The purpose of this class is to provide an fragment that uses
 * the Google Places API to query for branch information within a specific
 * radius. This information is displayed via a Google maps wrapper.
 *
 *
 */

public class MapsFragment extends SupportMapFragment {

    // Types of search query
    private static final int QUERY_ATM = 0;
    private static final int QUERY_BRANCH = 1;

    // 10 Kilometres (10,000 metres)
    private static final int SEARCH_RADIUS = 10000;

    // Logging tag
    private final String TAG = getClass().getSimpleName();

    // Location
    private Location myLocation = null;

    // Google Map
    private GoogleMap map = null;

    // Branch finder button.
    Button branchButton;

    // ATM Finder button.
    Button atmButton;

    // Postcode entry EditText
    EditText postcodeEntryEditText;

    // The view of the map
    private static View mapView;

    // A boolean symbolising whether the postcode resolution is successful
    boolean postcodeResolved = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Check to see if a map view exists.
        if (mapView != null) {
            ViewGroup parent = (ViewGroup) mapView.getParent();
            // Remove the map to prevent XML fragment duplication
            if (parent != null)
                parent.removeView(mapView);
        }
        try {
            mapView = inflater.inflate(R.layout.fragment_maps, container, false);
        } catch (InflateException e) {
            Log.e("Error", "Fatal error on inflation of map fragment.");
        }

        branchButton =  (Button) mapView.findViewById(R.id.branchFinder);
        atmButton = (Button) mapView.findViewById(R.id.atmFinder);
        postcodeEntryEditText = (EditText) mapView.findViewById(R.id.postCode);
        return mapView;
    }


    /**
     * Set up the map from the view.
     */
    private void makeMap() {
        // Get the fragment, located in the child fragment manager.
        FragmentManager myFragmentManager = getChildFragmentManager();
        SupportMapFragment mySupportMapFragment
                = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.googleMap);
        map = mySupportMapFragment.getMap();

        // Check to see if location services are on - if not, show an information message.
        if(!isLocationServicesOn(getActivity().getApplicationContext())){
            Bundle b = new Bundle();
            b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.error_loc_svc_disabled));
            CustomDialog custom = new CustomDialog();
            custom.setArguments(b);
            custom.show(getChildFragmentManager(), "Custom Dialog");
        }

        // Map will try to determine location
        map.setMyLocationEnabled(true);

        // Set a listener to check for location changes.
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                // Store location
                myLocation = location;

                // Get latitude and longitude of location.
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Move the camera to zoom into the location.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));

                // Attempt to get a postcode of the location.
                String postcode = Utility.locationToPostcode(getActivity().getApplicationContext(), location);

                // If the postcode was resolved, set the text and use the postcode.
                if (postcode != null) {
                    postcodeEntryEditText.setText(postcode);
                    postcodeResolved = true;
                }else{
                    postcodeResolved = false;
                }

                // Disable listener
                map.setOnMyLocationChangeListener(null);
                map.setMyLocationEnabled(false);

                map.addMarker(new MarkerOptions()
                        .title(getString(R.string.your_loc))
                        .snippet(getString(R.string.loc_now))
                        .position(latLng));

            }
        });

    }

    // Check to see if the location services are enabled.
    public boolean isLocationServicesOn(Context context) {
        String provider;
        int locationToggle;

        // Check settings on varsions KitKat and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            try {
                locationToggle = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }

            return locationToggle != Settings.Secure.LOCATION_MODE_OFF;
        // Otherwise return via the standard way.
        }else{
            provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(provider);
        }


    }

    @Override
    public String toString(){
        return getString(R.string.location_page);
    }

    @Override
    public void onAttach(android.app.Activity activity){
        super.onAttach(activity);
    }

    /**
     * Helper function common to both branch and ATM finder to assist with setting the correct location to
     * be queried in the Async. tasks. If no location or postcode is present then an error message is shown to
     * the user.
     *
     * True is returned if no error conditions were met. False if otherwise.
     */
    public boolean googlePlacesHelper(){

        // Check if user has entered any postcodes.
        postcodeResolved = postcodeEntryEditText.length() > 0;

        // Bail out if location is undetermined
        if (myLocation == null) {
            Bundle b = new Bundle();
            b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.error_undetermined_loc));
            b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
            CustomDialog custom = new CustomDialog();
            custom.setArguments(b);
            custom.show(getChildFragmentManager(), "Custom Dialog");
            return false;
        }

        LatLng currentLocation;

        if(postcodeResolved)
            currentLocation = Utility.locationFromPostcode(getActivity().getApplicationContext(), postcodeEntryEditText.getText().toString());
        else
            currentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        // Bail on unidentified geocoder error.
        if(currentLocation == null){
            Bundle b = new Bundle();
            b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.error_undetermined_loc));
            b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
            CustomDialog custom = new CustomDialog();
            custom.setArguments(b);
            custom.show(getChildFragmentManager(), "Custom Dialog");
            return false;
        }

        // Attempt to convert postcode to Location
        myLocation.setLatitude(currentLocation.latitude);
        myLocation.setLongitude(currentLocation.longitude);

        // Clear map of previous searches and add your location back to it.
        map.clear();
        map.addMarker(new MarkerOptions()
                .title(getString(R.string.your_loc))
                .snippet(getString(R.string.loc_now))
                .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())));

        // Update map position
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 9));
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // Make all map related items
        makeMap();

        // Set up buttons
        branchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                GraphicsUtils.buttonClickEffectShow(v);

                // Do common validation functions to set location.
                if(googlePlacesHelper()){
                    // Query for branches.
                    QueryGooglePlacesTask qg = new QueryGooglePlacesTask();
                    qg.execute(myLocation, QUERY_BRANCH);
                }

                GraphicsUtils.buttonClickEffectHide(v);

            }
        });

        atmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                GraphicsUtils.buttonClickEffectShow(v);

                // Do common validation functions to set location.
                if(googlePlacesHelper()){
                    // Query for branches
                    QueryGooglePlacesTask qg = new QueryGooglePlacesTask();
                    qg.execute(myLocation, QUERY_ATM);
                }

                GraphicsUtils.buttonClickEffectHide(v);

            }
        });


    }


    private GooglePlacesResponse queryGooglePlaces(Location location, int type) throws IOException {
        String nameField = "";
        String typeField = "";

        // Determine what kind of query was requested and set up correct query string.
        switch (type) {
            // Leave name field empty for ATMs (so ATMs from other branches are returned)
            case QUERY_ATM:
                typeField = "atm";
                break;

            // Supply company name to Google Places when searching for bank branches
            case QUERY_BRANCH:
                typeField = "bank";
                nameField = "Lloyds";
                break;
        }

        // Insert latitude, longitude, radius and search fields into the Google Places API URL (defined in strings.xml)
        String postURL = String.format(getString(R.string.places_request_url),
                location.getLatitude(),
                location.getLongitude(),
                SEARCH_RADIUS,
                typeField,
                nameField);

        // Input stream for holding the response
        InputStream inputStream = null;

        // Output stream for sending the POST data
        DataOutputStream dataOutputStream = null;
        Log.i(TAG, "Querying Google Places using URL: " + postURL);
        try {
            URL url = new URL(postURL);
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

        private int type;

        @Override
        protected GooglePlacesResponse doInBackground(Object... params) {

            // Show loading
            ProgressDialog.showLoading(MapsFragment.this);

            // Attempt to query Google Places
            try {
                type = (int)params[1];
                GooglePlacesResponse response = queryGooglePlaces((Location)params[0], (int)params[1]);
                // Loading has finished so remove the fragment.
                ProgressDialog.removeLoading(MapsFragment.this);
                return response;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(GooglePlacesResponse googlePlacesResponse) {

            if (googlePlacesResponse == null) {
                // Query failed
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_no_results), Toast.LENGTH_LONG).show();
            } else {
                List<Place> places = googlePlacesResponse.getResults();

                // Query succeeded
                Log.d (TAG, places.size() + " results found!");

                for (Place p: places) {
                    uk.ac.ncl.team19.lloydsapp.utils.maps.Location location = p.getGeometry().getLocation();

                    // If you are mathematically in vicinity, then update the state of the achievement to be unlocked on next sign in
                    if(Math.abs(p.getGeometry().getLocation().getLatitude() - location.getLatitude()) <= Constants.LLOYDS_VICINITY
                       && Math.abs(p.getGeometry().getLocation().getLongitude() - location.getLongitude()) <= Constants.LLOYDS_VICINITY
                       && type == QUERY_BRANCH){
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(Constants.SP_ACH_BRANCH_EXPLORER, true).apply();

                    }

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