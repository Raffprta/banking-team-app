package uk.ac.ncl.team19.lloydsapp.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * Created by Dale Whinham on 17/03/15. Modified by Raffaello Perrotta.
 */
public class APIConnector {
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_ERROR = "error";

    private static final String JSON_FIELD_ERROR_MESSAGE = "errorMessage";
    private static final String JSON_FIELD_DEVICE_TOKEN = "deviceToken";
    private static final String JSON_FIELD_STATUS = "status";
    private static final String JSON_FIELD_USERNAME = "username";
    private static final String JSON_FIELD_PASSWORD = "password";
    private static final String JSON_FIELD_SECURE_CHARACTERS = "secureChars";
    private static final String JSON_FIELD_EMAIL_NOTIFICATIONS = "email_notifications";
    private static final String JSON_FIELD_PUSH_NOTIFICATIONS = "push_notifications";

    // Need a context for retrieving string resources
    private Context ctx;

    public APIConnector(Context ctx) {
        this.ctx = ctx;
    }

    public String authenticate(String username, String password, Map<Integer, Character> secureChars) throws Exception {
        JSONObject jo = getLoginJSONObject(username, password, secureChars);

        URL url = new URL(ctx.getString(R.string.api_base_url) + '/' + ctx.getString(R.string.api_auth));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // Authentication headers
        conn.addRequestProperty("API-Key", ctx.getString(R.string.api_key));

        // Write JSON-encoded login parameters to output stream
        byte[] outputBytes = jo.toString().getBytes("UTF-8");
        OutputStream os = conn.getOutputStream();
        os.write(outputBytes);
        os.close();

        // Open the connection
        conn.connect();

        // Get the response from the server
        InputStream inputStream;
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        Scanner scanner = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }

        // Success
        if (responseCode == HttpURLConnection.HTTP_OK) {
            JSONObject jsonResponse = new JSONObject(stringBuilder.toString());

            // On error (eg. bad login/pass), throw exception with the error mesage
            if (jsonResponse.getString(JSON_FIELD_STATUS).equals(STATUS_ERROR)) {
                throw new Exception(jsonResponse.getString(JSON_FIELD_ERROR_MESSAGE));
            }

            // Otherwise return the device token
            return jsonResponse.getString(JSON_FIELD_DEVICE_TOKEN);
        } else {
            throw new Exception(ctx.getString(R.string.api_error_connection) + ": " + stringBuilder.toString());
        }
    }

    public boolean updateSettings(boolean emailNotifications, boolean pushNotifications, String token) throws Exception {
        // Get the JSON data representation to be passed to the API
        JSONObject jo = getSettingsJSONObject(emailNotifications, pushNotifications);

        // Make a new connection.
        URL url = new URL(ctx.getString(R.string.api_base_url) + '/' + ctx.getString(R.string.api_settings));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set params
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // Authentication headers
        conn.addRequestProperty("API-Key", ctx.getString(R.string.api_key));
        conn.addRequestProperty("Device-Token", token);

        // Write JSON-encoded login parameters to output stream
        byte[] outputBytes = jo.toString().getBytes("UTF-8");
        OutputStream os = conn.getOutputStream();
        os.write(outputBytes);
        os.close();

        // Open the connection
        conn.connect();

        // Get the response from the server
        InputStream inputStream;
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        Scanner scanner = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }

        // Success
        if (responseCode == HttpURLConnection.HTTP_OK) {
            JSONObject jsonResponse = new JSONObject(stringBuilder.toString());

            if (jsonResponse.getString(JSON_FIELD_STATUS).equals(STATUS_ERROR)) {
                throw new Exception(jsonResponse.getString(JSON_FIELD_ERROR_MESSAGE));
            }

            // Return true as the method succeeded.
            return true;
        } else {
            throw new Exception(ctx.getString(R.string.api_error_connection) + ": " + stringBuilder.toString());
        }
    }

    private static JSONObject getLoginJSONObject(String username, String password, Map<Integer, Character> secureChars) throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put(JSON_FIELD_USERNAME, username);
        jo.put(JSON_FIELD_PASSWORD, password);

        JSONObject sc = new JSONObject();
        for (int i : secureChars.keySet()) {
            sc.put(String.valueOf(i), secureChars.get(i));
        }

        jo.put(JSON_FIELD_SECURE_CHARACTERS, sc);

        return jo;
    }

    private static JSONObject getSettingsJSONObject(boolean emailNotifications, boolean pushNotifications){
        JSONObject jo = new JSONObject();

        int emailNotificationRepresentation = emailNotifications ? 1 : 0;
        int pushNotificationRepresentation = pushNotifications ? 1 : 0;

        try {
            jo.put(JSON_FIELD_EMAIL_NOTIFICATIONS, Integer.toString(emailNotificationRepresentation));
            jo.put(JSON_FIELD_PUSH_NOTIFICATIONS, Integer.toString(pushNotificationRepresentation));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jo;
    }
}