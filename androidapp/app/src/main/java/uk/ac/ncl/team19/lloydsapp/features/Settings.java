package uk.ac.ncl.team19.lloydsapp.features;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.PreferenceFragment;

/**
 * @author Raffaello Perrotta
 * @author Dale Whinham
 *
 * A class to represent user settings which are used to determine certain
 * preferences both on the backend and on the phone.
 */
public class Settings extends PreferenceFragment {

    private SharedPreferences sp;

    private final String TAG = getClass().getName();

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
                APIConnector ac = new APIConnector(getActivity());
                ac.updateSettings(
                        sp.getBoolean(getString(R.string.sp_notif_email), false),
                        sp.getBoolean(getString(R.string.sp_notif_push), false),
                        new Callback<APIResponse>() {
                            @Override
                            public void success(APIResponse apiResponse, Response response) {
                                // Remove progress bar
                                ProgressDialog.removeLoading(Settings.this);

                                switch (apiResponse.getStatus()) {
                                    case SUCCESS:
                                        Log.i(TAG, "Settings update successful.");
                                        break;

                                    case ERROR:
                                        Log.e(TAG, "Settings update failed: " + apiResponse.getErrorMessage());
                                        showErrorDialog(apiResponse.getErrorMessage());
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // Remove progress bar
                                ProgressDialog.removeLoading(Settings.this);
                                showErrorDialog(error.getMessage());
                            }

                            private void showErrorDialog(String errorMessage) {
                                // Make a new error dialog and display it
                                Bundle b = new Bundle();
                                b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, errorMessage);
                                b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
                                CustomDialog custom = new CustomDialog();
                                custom.setArguments(b);
                                custom.show(getActivity().getSupportFragmentManager(), "Custom Dialog");
                            }
                        }
                );
            }
        });
    }

    @Override
    public String toString(){
        return getString(R.string.settings);
    }
}