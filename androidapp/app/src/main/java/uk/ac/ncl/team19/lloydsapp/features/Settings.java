package uk.ac.ncl.team19.lloydsapp.features;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.PreferenceFragment;

/**
 * @author Raffaello Perrotta
 *
 * A lass to represent user settings which are used to determine certain
 * preferences both on the backend and on the phone.
 */
public class Settings extends PreferenceFragment {

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs);


        // Register a listener for when a setting is modified by the user.
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                ProgressDialog.showLoading(Settings.this);
                UpdateSettingsTask ust = new UpdateSettingsTask();
                ust.execute();
            }
        });

    }

    @Override
    public String toString(){
        return getString(R.string.settings);
    }

    private class UpdateSettingsTask extends AsyncTask<Void, Void, Boolean> {

        private String errorString = null;

        @Override
        protected Boolean doInBackground(Void... voids) {
            APIConnector ac = new APIConnector(getActivity().getApplicationContext());
            try {
                return ac.updateSettings(sp.getBoolean(getString(R.string.sp_notif_email), false),
                        sp.getBoolean(getString(R.string.sp_notif_push), false),
                        sp.getString(getString(R.string.sp_device_token), null));
            } catch (Exception e) {
                errorString = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean response) {
            // Remove progress bar
            ProgressDialog.removeLoading(Settings.this);

            if (response == null || response.booleanValue() == false) {
                // Make a new error dialog and display it
                Bundle b = new Bundle();
                b.putString(getString(R.string.custom_bundle), errorString);
                b.putString(getString(R.string.custom_type_bundle), getString(R.string.custom_colour_type_red));
                CustomDialog custom = new CustomDialog();
                custom.setArguments(b);
                custom.show(getActivity().getSupportFragmentManager(), "Custom Dialog");

            } else {
                // Store device token in shared preferences
                Log.i("SETTINGS UPDATE SUCCESS", response.toString());
            }
        }
    }

}
