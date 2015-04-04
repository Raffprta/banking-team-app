package uk.ac.ncl.team19.lloydsapp.features;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.plus.Plus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsDashboardFragment;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;
import uk.ac.ncl.team19.lloydsapp.utils.notifications.Toaster;
import uk.ac.ncl.team19.lloydsapp.utils.play.BaseGameUtils;

/**
 * @author Yessengerey Bolatov (XML Designs) and Raffaello Perrotta
 * @author Dale Whinham - simplify Bundle key access
 *
 * Profile page fragment, which contains hotlinks as well as all gamification aspects.
 */

public class ProfileFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private View profileView;
    private AchievementBuffer buff = null;
    private final String TAG = "PLAY UPDATE";

    private SharedPreferences sp;
    private ProgressBar hpBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Inflate the default view for the profile page.
        profileView = inflater.inflate(R.layout.profile_page, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Get the Health Bar.
        hpBar = (ProgressBar) profileView.findViewById(R.id.hpBar);


        // Show the percentage points of the current health
        hpBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.your_prog) + " " + Integer.toString(hpBar.getProgress()) + "%");
                CustomDialog custom = new CustomDialog();
                custom.setArguments(b);
                custom.show(getChildFragmentManager(), "Custom Dialog");
            }
        });

        // This is for when loading from the previously loaded fragment.
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            View v = profileView.findViewById(R.id.googlePlaySignIn);
            // Show all views.
            ((Button)v).setText(getString(R.string.google_play_sign_out));
            profileView.findViewById(R.id.googlePlayAchievements).setVisibility(View.VISIBLE);
            profileView.findViewById(R.id.googlePlayLeaderboard).setVisibility(View.VISIBLE);
        }

        profileView.findViewById(R.id.accountDashBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsDashboardFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();

            }
        });

        profileView.findViewById(R.id.accountHealth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Determine whether goals were set or not, load the setting of goals if not.

                if(sp.getBoolean(Constants.SP_GOALS_SET, false)){
                    fragmentManager.beginTransaction().replace(R.id.container, new HealthFragment()).addToBackStack(null).commit();
                }else{
                    fragmentManager.beginTransaction().replace(R.id.container, new SetGoalsFragment()).addToBackStack(null).commit();
                }

            }
        });

        profileView.findViewById(R.id.googlePlaySignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(mGoogleApiClient == null)
                // Get an instance of the currently initialised Google API Client.
                mGoogleApiClient = new GoogleApiClient.Builder(ProfileFragment.this.getActivity())
                        .addConnectionCallbacks(ProfileFragment.this)
                        .addOnConnectionFailedListener(ProfileFragment.this)
                        .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

            // Determine whether to connect or disconnect
            if(mGoogleApiClient.isConnected()){
                // Disconnect immediately.
                mGoogleApiClient.disconnect();
                // Hide all views
                profileView.findViewById(R.id.googlePlaySignIn);
                ((Button)v).setText(getString(R.string.google_play_sign_in));
                profileView.findViewById(R.id.googlePlayAchievements).setVisibility(View.GONE);
                profileView.findViewById(R.id.googlePlayLeaderboard).setVisibility(View.GONE);
                // Debug
                Log.i("Google Play", "Services Disconnected.");
            }else{
                // Show progress bar
                ProgressDialog.showLoading(ProfileFragment.this);
                // Connect
                mGoogleApiClient.connect();
            }

            }
        });

        profileView.findViewById(R.id.googlePlayAchievements).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                // If the API is initialised, then request achievements.
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sign_in_failed), Toast.LENGTH_LONG).show();
                }

                GraphicsUtils.buttonClickEffectHide(v);

            }
        });

        profileView.findViewById(R.id.googlePlayLeaderboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                // If the API is initialised, then request leaderboards.
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, getString(R.string.leaderboard_id)), 1);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sign_in_failed), Toast.LENGTH_LONG).show();
                }

                GraphicsUtils.buttonClickEffectHide(v);
            }
        });

        // Get the Status and Bio Edit Text
        final EditText statusEditText = (EditText)profileView.findViewById(R.id.profile_status_edit_text);
        final EditText bioEditText = (EditText)profileView.findViewById(R.id.profile_bio_edit_text);

        // The shared preferences key/value pair system.
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Set the username
        ((TextView)profileView.findViewById(R.id.usernameProfile)).setText(sp.getString(Constants.SP_USERNAME, null));

        // Set up status  and bio with stored value.
        if(sp.contains(Constants.SP_STATUS)){
            statusEditText.setText(sp.getString(Constants.SP_STATUS, null));
        }

        if(sp.contains(Constants.SP_BIO)){
            bioEditText.setText(sp.getString(Constants.SP_BIO, null));
        }

        // Listeners for when the status is updated
        statusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sp.edit().putString(Constants.SP_STATUS, statusEditText.getText().toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Listeners for when the bio is updated
        bioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sp.edit().putString(Constants.SP_BIO, bioEditText.getText().toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return profileView;

    }

    @Override
    public void onConnected(Bundle bundle) {

        // Get the sign in button.
        View v = profileView.findViewById(R.id.googlePlaySignIn);

        // Show all gamification buttons
        GraphicsUtils.buttonClickEffectShow(v);

        // Show all views.
        ((Button)v).setText(getString(R.string.google_play_sign_out));
        profileView.findViewById(R.id.googlePlayAchievements).setVisibility(View.VISIBLE);
        profileView.findViewById(R.id.googlePlayLeaderboard).setVisibility(View.VISIBLE);

        // Hide button effects
        GraphicsUtils.buttonClickEffectHide(v);

        // Debug
        Log.i("Google Play", "Services Connected.");

        // Register the current player.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String registeredPlayer = sp.getString(Constants.SP_PLAYID, null);

        if(registeredPlayer == null || !registeredPlayer.equals(playerId)){
            sp.edit().putString(Constants.SP_PLAYID, playerId).apply();
            // Update on the server side
            APIConnector ac = new APIConnector(getActivity());
            ac.updatePlayId(playerId, new Callback<APIResponse>() {
                @Override
                public void success(APIResponse apiResponse, Response response) {
                    switch (apiResponse.getStatus()) {
                        case SUCCESS:
                            Log.i(TAG, "Player ID update successful.");
                            break;

                        case ERROR:
                            Log.e(TAG, "Player ID update failed: " + apiResponse.getErrorMessage());
                            showErrorDialog(apiResponse.getErrorMessage());
                    }
                }

                @Override
                public void failure(RetrofitError error) {
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

            });

        }

        // Remove loading bar whatever the case.
        ProgressDialog.removeLoading(ProfileFragment.this);
        // Update the achievement progress.
        updateAchievements();


    }

    @Override
    public void onResume(){
        super.onResume();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            updateAchievements();
    }

    /*
     * Method to update the achievements based from information of the shared preferences.
     */
    private void updateAchievements(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Toaster toaster = new Toaster(getActivity());

        // If a login was detected then first timer can be unlocked - the shared preference must not contain an instance of it being unlocked.
        if(sp.getString(Constants.SP_FIRST_LOGIN, null) != null && !sp.getBoolean(Constants.SP_ACH_FIRST_LOGIN, false)){
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_first_timer));
            toaster.grabToastForWearable(getString(R.string.first_timer_unlock), getString(R.string.wearable_preview), R.drawable.ic_action_help);
            sp.edit().putBoolean(Constants.SP_ACH_FIRST_LOGIN, true).apply();
        }

        if(sp.getInt(Constants.SP_LOGINS, 0) >= Constants.GOLD_LOGIN && !sp.getBoolean(Constants.SP_ACH_GOLD_LOGIN, false)){
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_gold_login));
            toaster.grabToastForWearable(getString(R.string.gold_login_unlock), getString(R.string.wearable_preview), R.drawable.gold_login);
            sp.edit().putBoolean(Constants.SP_ACH_GOLD_LOGIN, true).apply();
        }

        if(sp.getInt(Constants.SP_LOGINS, 0) >= Constants.SILVER_LOGIN && !sp.getBoolean(Constants.SP_ACH_SILVER_LOGIN, false)){
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_silver_login));
            toaster.grabToastForWearable(getString(R.string.silver_login_unlock), getString(R.string.wearable_preview), R.drawable.silver_login);
            sp.edit().putBoolean(Constants.SP_ACH_SILVER_LOGIN, true).apply();
        }

        if(sp.getInt(Constants.SP_LOGINS, 0) >= Constants.BRONZE_LOGIN && !sp.getBoolean(Constants.SP_ACH_BRONZE_LOGIN, false)){
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_bronze_login));
            toaster.grabToastForWearable(getString(R.string.bronze_login_unlock), getString(R.string.wearable_preview), R.drawable.bronze_login);
            sp.edit().putBoolean(Constants.SP_ACH_BRONZE_LOGIN, true).apply();
        }

        // Calculate how many days the user has been a member for.
        String dateThen = sp.getString(Constants.SP_FIRST_LOGIN, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

        int daysBetween = 0;

        try {
            daysBetween = (int)(((new Date()).getTime() - sdf.parse(dateThen).getTime()) / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Debug
        Log.i("TIME PASSED", Long.toString(daysBetween));
        Log.i("TIME PASSED LAST", Integer.toString(sp.getInt(Constants.SP_LAST_DATE_INC,0)));

        int daysNow = sp.getInt(Constants.SP_LAST_DATE_INC, 0);

        // If they are not equal we can increment the value
        if(daysBetween != daysNow){
            // Store the days between value
            sp.edit().putInt(Constants.SP_LAST_DATE_INC, daysBetween).apply();
            // Increment the achievement appropriately.
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_old_timer), daysBetween-daysNow);
        }

        // Unlock the achievement after a month passes
        if(daysBetween >= Constants.OLD_TIMER && !sp.getBoolean(Constants.SP_ACH_OLD_TIMER, false)){
            toaster.grabToastForWearable(getString(R.string.old_timer_unlock), getString(R.string.wearable_preview), R.drawable.ic_action_help);
            sp.edit().putBoolean(Constants.SP_ACH_OLD_TIMER, true).apply();
        }

        // Check to see if a branch has been encountered (true)
        if(sp.getBoolean(Constants.SP_ACH_BRANCH_EXPLORER, false) && !sp.getBoolean(Constants.SP_ACH_BRANCH_EXPLORER_MUTEX, false)){
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_branch_explorer));
            toaster.grabToastForWearable(getString(R.string.branch_finder_unlock), getString(R.string.wearable_preview), R.drawable.branch_explorer);
            // Lock the mutex so this achievement can't be unlocked again.
            sp.edit().putBoolean(Constants.SP_ACH_BRANCH_EXPLORER_MUTEX, true).apply();
        }

        // Magic number is in fact a random achievement
        Random r = new Random();
        int magicNoAttempt = r.nextInt(Constants.MAGIC_NO_SEEDER);

        // Test to see if the magic number is achieved
        if(magicNoAttempt == Constants.MAGIC_NO)
            sp.edit().putBoolean(Constants.SP_MAGIC_NO, true).apply();

        // Check to see if the magic number has been encountered (true)
        if(sp.getBoolean(Constants.SP_MAGIC_NO, false) && !sp.getBoolean(Constants.SP_MAGIC_NO_MUTEX, false)){
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_magic_number));
            toaster.grabToastForWearable(getString(R.string.magic_no_unlock), getString(R.string.wearable_preview), R.drawable.ic_action_help);
            // Lock the mutex so this achievement can't be unlocked again.
            sp.edit().putBoolean(Constants.SP_MAGIC_NO_MUTEX, true).apply();
        }

        // If there is no achievements buffer.
        if(buff == null){
            // Attempt to asynchronously load the data to a buffer.
            getActivity().runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    ProgressDialog.showLoading(ProfileFragment.this);
                    new AchievementsGrabber().execute();
                }
            });
        }



    }

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    /**
     *
     * In the event of a failed sign in.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {

                BaseGameUtils.showActivityResultError(this.getActivity(),
                        requestCode, resultCode, R.string.gamehelper_sign_in_failed);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Get the sign in button.
        View v = profileView.findViewById(R.id.googlePlaySignIn);

        // Hide button effects
        GraphicsUtils.buttonClickEffectHide(v);

        // Remove loading bar
        ProgressDialog.removeLoading(ProfileFragment.this);

        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            if (!BaseGameUtils.resolveConnectionFailure(this.getActivity(),
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.gamehelper_sign_in_failed))) {

                mResolvingConnectionFailure = false;
            }
        }

    }

    /**
     * An async. task for the purpose of grabbing a buffer of all of the available achievements
     * such that it will be possible to calculate the scores of the currently achieved achievements to
     * update the leaderboards with.
     */
    private class AchievementsGrabber extends AsyncTask<Void, Void, AchievementBuffer> {

        @Override
        protected AchievementBuffer doInBackground(Void... params) {
            // False checks the achievements cache first
            PendingResult res = Games.Achievements.load(mGoogleApiClient, false);
            // Call, with 30 seconds of wait time
            Achievements.LoadAchievementsResult r = (Achievements.LoadAchievementsResult)res.await(30, TimeUnit.SECONDS );

            // If the network is kaput or we have no fall back on cached data.
            if((r.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK &&
                    r.getStatus().getStatusCode() != GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA)){
                Log.w("Error: ", GamesStatusCodes.getStatusString(r.getStatus().getStatusCode()));
                return null;
            }
            // Return the buffer to the caller
            AchievementBuffer buffer = r.getAchievements();
            buff = buffer;
            return buffer;
        }

        @Override
        protected void onPostExecute(AchievementBuffer buff) {

            ProgressDialog.removeLoading(ProfileFragment.this);
            int points = 0;

            for(Achievement ach : buff){
                // For all unlocked achievements get their points mapping
                if(ach.getState() == Achievement.STATE_UNLOCKED){
                    // Increment the points by the achievement point value
                    points += Constants.ACHIEVEMENTS_POINTS.get(ach.getAchievementId());
                }
            }

            Log.i("POINTS GAINED:", Integer.toString(points));
            // Submit the score to the Play leaderboard
            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_id), points);

        }


    }

    @Override
    public void onViewStateRestored (Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        // Set its value, if it has been calculated - at default this is 100.
        hpBar.setProgress(sp.getInt(Constants.SP_ACCOUNTS_HP, 100));

        // Determine what colour to set.
        if(hpBar.getProgress() >= Constants.HEALTH_GOOD){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar));
        }else if(hpBar.getProgress() >= Constants.HEALTH_AVG && hpBar.getProgress() < Constants.HEALTH_GOOD){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_medium));
        }else if(hpBar.getProgress() >= Constants.HEALTH_POOR && hpBar.getProgress() < Constants.HEALTH_AVG){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_poor));
        }else{
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_dismal));
        }
    }

    @Override
    public String toString(){
        return getString(R.string.profile_page);
    }


}
