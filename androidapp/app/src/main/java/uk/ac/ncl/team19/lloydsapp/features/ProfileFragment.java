package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsDashboardFragment;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov (XML Designs) and Raffaello Perrotta
 *
 * Profile page fragment, which contains hotlinks as well as all gamification aspects.
 */

public class ProfileFragment extends Fragment{

    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View profileView = inflater.inflate(R.layout.profile_page, container, false);

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
                // Show all gamification buttons
                GraphicsUtils.buttonClickEffectShow(v);

                if(mGoogleApiClient == null)
                    // Get an instance of the currently initialised Google API Client.
                    mGoogleApiClient = new GoogleApiClient.Builder(ProfileFragment.this.getActivity())
                            .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) ProfileFragment.this.getActivity())
                            .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) ProfileFragment.this.getActivity())
                            .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                            .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

            // Determine whether to Connect or Disconnect to/from Google Play.
                if(!mGoogleApiClient.isConnected()){
                    // Show all views.
                    mGoogleApiClient.connect();
                    ((Button)v).setText(getString(R.string.google_play_sign_out));
                    profileView.findViewById(R.id.googlePlayAchievements).setVisibility(View.VISIBLE);
                    profileView.findViewById(R.id.googlePlayLeaderboard).setVisibility(View.VISIBLE);
                }else{
                    // Hide all views and disconnect
                    mGoogleApiClient.disconnect();
                    ((Button)v).setText(getString(R.string.google_play_sign_in));
                    profileView.findViewById(R.id.googlePlayAchievements).setVisibility(View.GONE);
                    profileView.findViewById(R.id.googlePlayLeaderboard).setVisibility(View.GONE);

                }

                // Hide button effects
                GraphicsUtils.buttonClickEffectHide(v);

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

}
