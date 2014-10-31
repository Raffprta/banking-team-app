package uk.ac.ncl.team19.lloydsapp;

import android.app.Activity;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ncl.team19.googleplaces.GooglePlacesResponse;
import uk.ac.ncl.team19.googleplaces.Place;

public class MainActivity extends Activity {
    // Types of search query
    private static final int QUERY_ATM = 0;
    private static final int QUERY_BRANCH = 1;

    // Constants
    private static final String BRANCH_FINDER_POST_URL = "http://www.lloydsbank.com/branch_locator/application.asp?inapplication=yes";
    private static final String BRANCH_FINDER_REFERER_URL = "http://www.lloydsbank.com/branch_locator/search.asp";

    private static final String GOOGLE_PLACES_POST_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&types=bank&name=Lloyds&key=AIzaSyAnOqe2hy_znuRnk6pfijuHXOFBH_Z6r6M";

    private static final int SEARCH_RADIUS = 10000;

    // Logging tag
    private final String TAG = getClass().getSimpleName();

    // Location
    private Location myLocation = null;

    // Google Map
    private GoogleMap map = null;

    // List of branches
    private List<Branch> branches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable ActionBar progress bar
        requestWindowFeature(Window.FEATURE_PROGRESS);

        // Apply layout
        setContentView(R.layout.activity_main);

        // Enable cookie storage
        CookieManager.setDefault(new CookieManager());

        // References to UI elements
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();

        // Map will try to determine location
        map.setMyLocationEnabled(true);

        Log.i(TAG, "Internet available? " + Utility.deviceHasInternetConnection(this));

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                // Store location
                myLocation = location;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String postcode = Utility.locationToPostcode(getApplicationContext(), location);

                if (postcode != null) {
                    Toast.makeText(getApplicationContext(), "Your postcode is roughly: " + postcode, Toast.LENGTH_LONG).show();
                }

                // Disable listener
                map.setOnMyLocationChangeListener(null);
                map.setMyLocationEnabled(false);

                map.addMarker(new MarkerOptions()
                        .title("Your Location")
                        .snippet("A description.")
                        .position(latLng));

                map.moveCamera(CameraUpdateFactory.zoomBy(7));
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_find_branches_scraper) {
            // Enable progress bar
            setProgressBarIndeterminate(true);
            setProgressBarVisibility(true);

            QueryLloydsBranchFinderTask qb = new QueryLloydsBranchFinderTask();
            qb.execute(myLocation);
            return true;
        } else if (id == R.id.action_find_branches_gplaces) {
            // Enable progress bar
            setProgressBarIndeterminate(true);
            setProgressBarVisibility(true);

            QueryGooglePlacesTask qg = new QueryGooglePlacesTask();
            qg.execute(myLocation);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Branch> queryLloydsBranchFinder(int queryType, Location location) throws IOException {
        // Attempt to convert location to postcode
        String postcode = Utility.locationToPostcode(getApplicationContext(), location);

        // Abort if we don't have a postcode to use
        if (postcode == null) {
            return null;
        }

        Log.i(TAG, "Postcode determined to be: " + postcode);

        // Input stream for holding the response
        InputStream inputStream = null;

        // Output stream for sending the POST data
        DataOutputStream dataOutputStream = null;

        try {
            URL url = new URL(BRANCH_FINDER_POST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);     // milliseconds
            conn.setConnectTimeout(15000);  // milliseconds
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            // Lloyds branch finder requires referer to be set
            conn.setRequestProperty("Referer", BRANCH_FINDER_REFERER_URL);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Search fields
            String formFields = String.format(  "step=%d" +
                                                "&bl_query_select=%d" +
                                                "&bl_town=%s" +
                                                "&bl_postcode=%s" +
                                                "&bl_district=%s" +
                                                "&bl_sort_by=%d" +
                                                "&continue.x=%d" +
                                                "&continue.y=%d" +
                                                //"&continue=continue" +
                                                "&hidden_xmas_opening=0",
                                                1,
                                                queryType,
                                                "",
                                                postcode.replaceAll("\\s", ""), // Remove whitespace from postcode
                                                "",
                                                0, 0, 0 );

            // Replace all spaces with '+'
            formFields = formFields.replaceAll(" ", "+");

            Log.d(TAG, "Using form fields: " + formFields);

            dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(formFields);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(TAG, "The response was: " + response);

            inputStream = conn.getInputStream();

            // Convert the InputStream into a string
            String responseBody = Utility.inputStreamToString(inputStream);

            return parseHtmlResponse(responseBody);
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

    private GooglePlacesResponse queryGooglePlaces(Location location) throws IOException {
        // Input stream for holding the response
        InputStream inputStream = null;

        // Output stream for sending the POST data
        DataOutputStream dataOutputStream = null;

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

    private List<Branch> parseHtmlResponse(String html) {
        // List of parsed Branches
        List<Branch> addresses = new ArrayList<Branch>();

        // Parse HTML into DOM object
        Document doc = Jsoup.parse(html);

        // Search results are enclosed in <div class="line"> tags
        Elements searchResults = doc.select("div.line");

        // Progress bar no longer indeterminate
        setProgressBarIndeterminate(false);

        for (int i = 0; i < searchResults.size(); i++) {
            /*
            Lloyds branch finder returns results like this:

            <div class="line">
                <a href="...">3 Station Road Ashington</a>
                <br>
                3 Station Road, Ashington, Northumberland, NE63 9UZ
            </div>
             */

            // Get the current element
            Element e = searchResults.get(i);

            // Update progress bar
            setProgress(i * 10000 / searchResults.size());

            // First line/branch name is enclosed in an <a> tag
            String branchName = e.select("a")
                    .first().text()             // the first <a> tag's inner text
                    .replaceAll("\u00a0", " ")  // HTML &nbsp;s get converted to Unicode 0x00a0 character; replace them with ASCII spaces
                    .trim();                    // remove leading and trailing spaces

            // Split address lines by comma
            String[] splitAddress = e.ownText().split(",");

            // List of address lines
            List<String> addressLines = new ArrayList<String>();

            // Add all but last individual address lines to address line list
            for (int j = 0; j < splitAddress.length - 1; j++) {
                // Replace &nbsp;/Unicode 0x00a0 with ASCII space and trim leading/trailing spaces
                String line = splitAddress[j].replaceAll("\u00a0", " ").trim();

                // Store the address line
                addressLines.add(line);
            }

            // Last address line is the postcode (hopefully!)
            String postcode = splitAddress[splitAddress.length - 1];

            // Clean up whitespace
            postcode = postcode.replaceAll("\u00a0", " ").trim();

            // Create a new branch object with the parsed data
            Branch branch = new Branch(branchName, addressLines, postcode);

            // Get location data from Geocoding API and store it with the Branch
            Address a = Utility.branchToAddress(getApplicationContext(), branch);

            if (a != null) {
                branch.setLongitude(a.getLongitude());
                branch.setLatitude(a.getLatitude());
            } else {
                Log.e(TAG, "Couldn't geocode branch: " + branch.toString());
            }

            // Add the Branch to the list
            addresses.add(branch);
        }

        return addresses;
    }

    private class QueryGooglePlacesTask extends AsyncTask<Location, Void, GooglePlacesResponse> {
        @Override
        protected GooglePlacesResponse doInBackground(Location... locations) {
            // Attempt to query the Lloyds Branch Finder
            try {
                return queryGooglePlaces(locations[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(GooglePlacesResponse googlePlacesResponse) {
            // Stop progress bar
            setProgressBarVisibility(false);

            if (googlePlacesResponse == null) {
                // Query failed
                Toast.makeText(getApplicationContext(), "No branches found.", Toast.LENGTH_LONG).show();
            } else {
                List<Place> places = googlePlacesResponse.getResults();

                // Query succeeded
                Log.d (TAG, places.size() + " branches found!");

                for (Place p: places) {
                    uk.ac.ncl.team19.googleplaces.Location location = p.getGeometry().getLocation();
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

    private class QueryLloydsBranchFinderTask extends AsyncTask<Location, Void, List<Branch>> {
        @Override
        protected List<Branch> doInBackground(Location... locations) {
            // Attempt to query the Lloyds Branch Finder
            try {
                return queryLloydsBranchFinder(QUERY_BRANCH, locations[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Branch> results) {
            // Stop progress bar
            setProgressBarVisibility(false);

            if (results == null) {
                // Query failed
                Toast.makeText(getApplicationContext(), "No branches found.", Toast.LENGTH_LONG).show();
            } else {
                // Query succeeded
                Log.d (TAG, results.size() + " branches found!");

                for (Branch b: results) {
                    if (b.getLongitude() != 0 && b.getLatitude() != 0) {
                        // Add map markers
                        map.addMarker(new MarkerOptions()
                                .title(b.getName() + " (Scraped)")
                                .snippet(b.getAddressLines().toString())
                                .position(new LatLng(b.getLatitude(), b.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                }

                // Store results
                branches = results;
            }
        }
    }
}
