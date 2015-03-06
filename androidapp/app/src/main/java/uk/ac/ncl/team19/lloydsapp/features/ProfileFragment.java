package uk.ac.ncl.team19.lloydsapp.features;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsDashboardFragment;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;
import uk.ac.ncl.team19.lloydsapp.utils.play.BaseGameUtils;

/**
 * @author Yessengerey Bolatov (XML Designs) and Raffaello Perrotta
 *
 * Profile page fragment, which contains hotlinks as well as all gamification aspects.
 */

public class ProfileFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    View profileView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Inflate the default view for the profile page.
        profileView = inflater.inflate(R.layout.profile_page, container, false);

        // Get the Health Bar.
        final ProgressBar hpBar = (ProgressBar) profileView.findViewById(R.id.hpBar);

        // Show the percentage points of the current health
        hpBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(getString(R.string.custom_bundle), getString(R.string.your_prog) + " " + Integer.toString(hpBar.getProgress()) + "%");
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

        // Set up status  and bio with stored value.
        if(sp.contains(getString(R.string.sp_status))){
            statusEditText.setText(sp.getString(getString(R.string.sp_status), null));
        }

        if(sp.contains(getString(R.string.sp_bio))){
            bioEditText.setText(sp.getString(getString(R.string.sp_bio), null));
        }

        // Listeners for when the status is updated
        statusEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sp.edit().putString(getString(R.string.sp_status), statusEditText.getText().toString()).apply();
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
                sp.edit().putString(getString(R.string.sp_bio), bioEditText.getText().toString()).apply();
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

        // Remove loading bar
        ProgressDialog.removeLoading(ProfileFragment.this);

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

}
