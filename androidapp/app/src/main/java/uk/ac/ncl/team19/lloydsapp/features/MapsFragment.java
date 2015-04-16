package uk.ac.ncl.team19.lloydsapp.features;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.GooglePlacesConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Place;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.PlaceLocation;
import uk.ac.ncl.team19.lloydsapp.api.response.GooglePlacesResponse;
import uk.ac.ncl.team19.lloydsapp.api.utility.ErrorHandler;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;
import uk.ac.ncl.team19.lloydsapp.utils.maps.Utility;

/**
 * @author Dale Whinham
 * All Maps logic. All backend integration. Implementation of helper methods and callbacks.
 * @author Raffaello Perrotta
 * Error handling. Conversion to fragment. Abstract maps view to Viewgroup.
 *
 * The purpose of this class is to provide an fragment that uses
 * the Google Places API to query for branch information within a specific
 * radius. This information is displayed via a Google maps wrapper.
 *
 *
 */

public class MapsFragment extends SupportMapFragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
            currentLocation = Utility.locationFromPostcode(getActivity(), postcodeEntryEditText.getText().toString());
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
                if (googlePlacesHelper()) {
                    // Show loading
                    ProgressDialog.showLoading(MapsFragment.this);

                    // Query for branches.
                    GooglePlacesConnector gpc = new GooglePlacesConnector(getActivity());
                    gpc.findLloydsBranches(myLocation, SEARCH_RADIUS, googlePlacesCallback);
                }

                GraphicsUtils.buttonClickEffectHide(v);
            }
        });

        atmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Do common validation functions to set location.
                if (googlePlacesHelper()) {
                    // Show loading
                    ProgressDialog.showLoading(MapsFragment.this);

                    // Query for ATMs
                    GooglePlacesConnector gpc = new GooglePlacesConnector(getActivity());
                    gpc.findLloydsAtms(myLocation, SEARCH_RADIUS, googlePlacesCallback);
                }

                GraphicsUtils.buttonClickEffectHide(v);
            }
        });
    }

    // Callback for when Places query has completed
    private final Callback<GooglePlacesResponse> googlePlacesCallback = new Callback<GooglePlacesResponse>() {
        @Override
        public void success(GooglePlacesResponse googlePlacesResponse, Response response) {
            // Loading has finished so remove the progress dialog fragment
            ProgressDialog.removeLoading(MapsFragment.this);

            switch (googlePlacesResponse.getStatus()) {
                case ZERO_RESULTS:
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_no_results), Toast.LENGTH_LONG).show();
                    break;

                // We got some results - display them
                case OK:
                    List<Place> places = googlePlacesResponse.getResults();
                    Log.d (TAG, places.size() + " results found!");

                    // Flag to say a nearby branch was found
                    boolean foundNearbyBranch = false;
                    for (Place p: places) {
                        PlaceLocation location = p.getGeometry().getLocation();
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // Is this a branch? (as opposed to an ATM)
                        boolean isBranch = p.getTypes().contains("branch");

                        // If you are mathematically in vicinity of a branch, then update the state of the achievement to be unlocked on next sign in
                        if (!foundNearbyBranch
                                && isBranch
                                && Math.abs(lat - myLocation.getLatitude()) <= Constants.LLOYDS_VICINITY
                                && Math.abs(lng - myLocation.getLongitude()) <= Constants.LLOYDS_VICINITY) {
                            foundNearbyBranch = true;
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            sp.edit().putBoolean(Constants.SP_ACH_BRANCH_EXPLORER, true).apply();
                        }

                        if (lat != 0 && lng != 0) {
                            // Add map markers
                            map.addMarker(new MarkerOptions()
                                    .title(p.getName())
                                    .snippet(p.getVicinity())
                                    .position(new LatLng(lat, lng))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                    }
                    break;

                // Display any errors returned by Places API
                default:
                    ErrorHandler.fail(getFragmentManager(), googlePlacesResponse.getErrorMessage());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            // Loading has finished so remove the progress dialog fragment
            ProgressDialog.removeLoading(MapsFragment.this);
            ErrorHandler.fail(getActivity(), getFragmentManager(), error);
        }
    };
}