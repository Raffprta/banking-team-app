package uk.ac.ncl.team19.lloydsapp.features;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.push.DBOpenHelper;
import uk.ac.ncl.team19.lloydsapp.utils.push.GcmIntentService;
import uk.ac.ncl.team19.lloydsapp.utils.push.LloydsNotification;
import uk.ac.ncl.team19.lloydsapp.utils.push.LloydsNotificationAdapter;
import uk.ac.ncl.team19.lloydsapp.utils.push.NotificationsDataSource;
import uk.ac.ncl.team19.lloydsapp.utils.push.SwipeableRecyclerViewTouchListener;

/**
 * @author Dale Whinham with minor conversion to fragment by Raffaello Perrotta
 * Purpose: To provide a fragment which allows Google Cloud Messaging services to
 * send and render messages to the screen of the bank user.
 */
public class PushFragment extends Fragment {

    Context context;

    BroadcastReceiver dataChangedBroadcastReceiver;

    // RecyclerView stuff
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LloydsNotificationAdapter mAdapter;

    private List<LloydsNotification> mListDataSet;

    // GCM stuff
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // Project number from developer console
    private static final String SENDER_ID = "689728249892";

    // Database access
    private NotificationsDataSource dataSource;


    GoogleCloudMessaging gcm;
    String regId;

    // Logging tag for debugging
    private static final String TAG = "MainActivity";

    // Shared preferences
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View pushView = inflater.inflate(R.layout.push_layout, container, false);
        // Store context
        context = getActivity().getApplicationContext();

        // Get s.p.
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Bail out if Google Play services are unavailable
        if (hasGooglePlayServices()) {
            // Init GCM
            gcm = GoogleCloudMessaging.getInstance(getActivity());
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            }

            mRecyclerView = (RecyclerView) pushView.findViewById(R.id.my_recycler_view);

            // This setting can improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // Use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // Refresh notifications
            getNotificationsFromDB();

            // Setup RecyclerView adapter
            mAdapter = new LloydsNotificationAdapter(mListDataSet, context);
            mRecyclerView.setAdapter(mAdapter);

            // Cache indexes we changed
            final List<Integer> cache = new ArrayList<Integer>();


            // Set up swiping for removing push notification cards.
            SwipeableRecyclerViewTouchListener swipeTouchListener =
                    new SwipeableRecyclerViewTouchListener(mRecyclerView,
                            new SwipeableRecyclerViewTouchListener.SwipeListener() {
                                @Override
                                public boolean canSwipe(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                    // For each of the cards, get the one that was swiped
                                    for (int position : reverseSortedPositions) {
                                        // Get the id of the card.
                                        int id = mListDataSet.get(position).getId();
                                        // Remove it from the card view.
                                        mListDataSet.remove(position);
                                        // Delete the message from the actual database
                                        dataSource.open();
                                        dataSource.deleteRow(DBOpenHelper.TABLE_NOTIFICATIONS, id);
                                        dataSource.close();
                                        // Notify that the item was removed.
                                        mAdapter.notifyItemRemoved(position);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }

                                // Idem as above, though with actions for being swiped to the right.
                                @Override
                                public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        int id = mListDataSet.get(position).getId();
                                        mListDataSet.remove(position);
                                        dataSource.open();
                                        dataSource.deleteRow(DBOpenHelper.TABLE_NOTIFICATIONS, id);
                                        dataSource.close();
                                        mAdapter.notifyItemRemoved(position);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            });

            mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        return pushView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for Google Play Services
        hasGooglePlayServices();

        // Register broadcast receiver
        if (dataChangedBroadcastReceiver == null) {
            dataChangedBroadcastReceiver = new DataChangedBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(GcmIntentService.DATA_REFRESH_INTENT);
        getActivity().registerReceiver(dataChangedBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister receiver when app is suspended
        if (dataChangedBroadcastReceiver != null) {
            getActivity().unregisterReceiver(dataChangedBroadcastReceiver);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean hasGooglePlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
        } else {

            Log.i(TAG, "Registration ID found: " + registrationId);
        }

        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously and
     * stores the registration ID in the application's shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    ProgressDialog.showLoading(PushFragment.this);
                    UpdateGCMTask ugt = new UpdateGCMTask();
                    ugt.execute();

                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.execute(null, null, null);
    }

    private static void storeRegistrationId(Context context, String regId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }


    private void getNotificationsFromDB() {
        // Get notifications from database for RecyclerView
        dataSource = new NotificationsDataSource(context);

        // Open database
        dataSource.open();

        // Retrieve records
        mListDataSet = dataSource.getAllNotifications();

        // Close database (important)
        dataSource.close();
    }

    @Override
    public String toString(){
        return getString(R.string.notifications_page);
    }

    // Broadcast receiver to listen for changes to notification DB
    // casued by push messages being received
    private class DataChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GcmIntentService.DATA_REFRESH_INTENT)) {
                if (mAdapter != null) {
                    // Refresh list of notifications from database
                    getNotificationsFromDB();

                    mAdapter = new LloydsNotificationAdapter(mListDataSet, context);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }

    private class UpdateGCMTask extends AsyncTask<Void, Void, Boolean> {

        private String errorString = null;

        @Override
        protected Boolean doInBackground(Void... voids) {
            APIConnector ac = new APIConnector(getActivity().getApplicationContext());
            try {
                return ac.updateGcmId(regId, sp.getString(getString(R.string.sp_device_token), null));
            } catch (Exception e) {
                errorString = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean response) {
            // Remove progress bar
            ProgressDialog.removeLoading(PushFragment.this);

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
                Log.i("GCMID UPDATE SUCCESS", response.toString());
            }
        }
    }
}
