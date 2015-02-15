package uk.ac.ncl.team19.lloydsapp;

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
import java.util.List;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View pushView = inflater.inflate(R.layout.push_layout, container, false);
        // Store context
        context = getActivity().getApplicationContext();

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
            mAdapter = new LloydsNotificationAdapter(mListDataSet);
            mRecyclerView.setAdapter(mAdapter);

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

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
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

    // Broadcast receiver to listen for changes to notification DB
    // casued by push messages being received
    private class DataChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GcmIntentService.DATA_REFRESH_INTENT)) {
                if (mAdapter != null) {
                    // Refresh list of notifications from database
                    getNotificationsFromDB();

                    mAdapter = new LloydsNotificationAdapter(mListDataSet);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }
    }
}
