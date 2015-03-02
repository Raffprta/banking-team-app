package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsDashboardFragment;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

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
        profileView = inflater.inflate(R.layout.profile_page, container, false);

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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
