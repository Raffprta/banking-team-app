package raffprta.eu.achievementsexample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

/**
 * @author Raffaello Perrotta
 * A modularised class which should be re-usable and extendable for providing and managing Google
 * API related services. This class initialises and builds the related APIs and manages any connection events.
 * Activities wishing to use these APIs should extend this class.
 * This class uses the good practises listed here: https://developer.android.com/google/auth/api-client.html
 */
public class BaseGameManagerActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    // This variable tracks the APIs ability to attempt a resolution to a Play error or unforseen event
    private boolean mResolvingError = false;
    // Variable to store the state for the resolution variable
    private final String STATE_RESOLVING_ERROR = "state_resolver";
    // This enumeration doesn't seem to be provided anywhere in ConnectionResult's static members
    // although it's listed in the API developer page as the code to use for the resolver.
    private final int REQUEST_RESOLVE_ERROR = 1001;

    private Intent achievementsIntent;
    private Intent leaderBoardIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_main);
        // Checks if there's a saved instance of the app, and if there is it grabs the STATE_RESOLVING_ERROR
        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Builds the APIs we want to access; it *seems* Google+ is necessary
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

        final Button selectAchievements = (Button) findViewById(R.id.button6);
        final Button selectLeaderboard = (Button) findViewById(R.id.button7);

        selectAchievements.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                achievementsIntent = new Intent(getApplicationContext(), AchievementsActivity.class);
                startActivity(achievementsIntent);
                // Set animation transitions
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        selectLeaderboard.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                leaderBoardIntent = new Intent(getApplicationContext(), LeaderboardActivity.class);
                startActivity(leaderBoardIntent);
                // Set animation transitions
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    @Override
    protected void onStart() {
        // Get the api client rolling, if fails the onConnectionFailed is called and will attempt
        // to resolve the issue.
        super.onStart();
        if(!mResolvingError)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt reconnection, if fails it should be delegated to onConnectionFailed()
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // The addition of class name checking is purely so that the resolver only tries to initiate
        // itself in the base class if not it will fail. Otherwise resolving in every activity is just
        //  ugly/user bloat as popups are displayed from the API. This mechanism could lead to adding
        //  sign in buttons for Google play or otherwise changing this design to message passing between activities.
        final String NAME = "raffprta.eu.achievementsexample.BaseGameManagerActivity";
        // If the error can't be resolved, forget about trying to resolve it again.
        if (mResolvingError) {
            return;
        }else if(connectionResult.hasResolution() && ((Object)this).getClass().getName().equals(NAME)){
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // Attempt reconnection
                mGoogleApiClient.connect();
            }
        }else{
            // The problem cannot be resolved (i.e. no internet data, or tester account isn't authorised)
            mResolvingError = true;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This method is called once a user has resolved their connection issue
        // (i.e. by signing in by the prompts)
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }else{
                displayErrorMessage();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    /** This displays an error message to the user if the connection is dropped or didn't start properly **/
    protected void displayErrorMessage(){
        Toast.makeText(getApplicationContext(), getString(R.string.error_no_sign_in),
                Toast.LENGTH_LONG).show();
    }

    /** This getter is what is used to grab the API state in extending classes **/
    protected GoogleApiClient getGoogleAPI(){
        return mGoogleApiClient;
    }

}
