package uk.ac.ncl.team19.lloydsapp.utils.maps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class with miscellaneous functions
 * Created by Dale Whinham on 22/10/14.
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

    /**
     * Attempts to convert an InputStream to a String.
     * @param inputStream the InputStream
     * @return A String object
     * @throws java.io.IOException
     */
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        // Courtesy of http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
