package uk.ac.ncl.team19.lloydsapp.utils.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.Branch;

/**
 * @author Dale Whinham
 * All implementations except location from postcode
 * @author Raffaello Perrotta
 * location from postcode.
 *
 * Utility class with miscellaneous functions which are used by Maps code. This class is documented
 * thoroughly with its functionalities.
 *
 */
public class Utility {
    // Tag for logging
    private static final String TAG = Utility.class.getSimpleName();

    /**
     * Checks if the device has an Internet connection.
     * @param ctx the current Application Context
     * @return true if the device has an Internet connection available, false otherwise
     */
    public static boolean deviceHasInternetConnection(Context ctx) {
        // Get system connectivity manager
        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Query system for Internet connectivity
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // True if Internet connection available
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Converts a Location to a postcode using a Geocoder.
     * @param ctx the current Application Context
     * @param location the Location to convert
     * @return the postcode as a String, null if it was unsuccessful
     */
    public static String locationToPostcode(Context ctx, Location location) {
        // Return null if the Geocoder API is not functional (no backend present)
        if (!Geocoder.isPresent()) {
            return null;
        }

        Log.i(TAG, "Geocoder is present, querying...");
        Geocoder geocoder = new Geocoder(ctx);

        try {
            // Get one address using the Geocoder API
            List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            // getFromLocation can return null or empty list
            if (results != null && !results.isEmpty()) {
                // We got a result, return its postcode
                return results.get(0).getPostalCode();
            }else{
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "An error occurred while geocoding: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Converts an Address to a Location using a Geocoder.
     * @param ctx the current Application Context
     * @param branch the Branch to convert
     * @return the postcode as a String, null if it was unsuccessful
     */
    public static Address branchToAddress(Context ctx, Branch branch) {
        // Return null if the Geocoder API is not functional (no backend present)
        if (!Geocoder.isPresent()) {
            return null;
        }

        Log.i(TAG, "Geocoder is present, querying...");
        Geocoder geocoder = new Geocoder(ctx);

        try {
            // Get one Location using the Geocoder API
            List<Address> results = geocoder.getFromLocationName(branch.getLocationString(), 1);

            Log.d(TAG, "Using '" + branch.getLocationString() + "' as query.");

            // getFromLocationName can return null or empty list
            if (results != null && !results.isEmpty()) {
                // We got a result, return its Address
                return results.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "An error occurred while geocoding: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public static LatLng locationFromPostcode(Context ctx, String postCode) {

        final Geocoder geocoder = new Geocoder(ctx);
        Address address;

        try {
            List<Address> addresses = geocoder.getFromLocationName(postCode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "An error occurred while geocoding: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

}
