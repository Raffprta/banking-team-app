package uk.ac.ncl.team19.lloydsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import uk.ac.ncl.team19.lloydsapp.utils.play.BaseGameUtils;


/**
 * @author Dale Whinham, Raffaello Perrotta
 * Main activity associated with the drawer layout menu. Google Play services are also
 * enabled within this activity as well as the Fragment Management
 */

public class MainMenuActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // The API Client
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the content.
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Builds the APIs we want to access; it *seems* Google+ is necessary
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

        // Build up the drawer menu.
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Force the drawer to close as in the activity creation the drawer is closed after the menu is inflated
        // hence interfering with the Google Splash screen.
        if(mNavigationDrawerFragment.isDrawerOpen()){
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT);
        }

        // Builds the APIs we want to access; it *seems* Google+ is necessary
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    // Override clicking the back button as the user may not access the login and security page.
    // Fragments are instead kept track of.
    @Override
    public void onBackPressed() {
        // TODO Fix Minor bug with the titles
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if there are fragments on the stack.
        if(fragmentManager.getBackStackEntryCount() > 1){
            mTitle = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            fragmentManager.popBackStack();
        }
    }

    private PushFragment push = new PushFragment();
    private MapsFragment map = new MapsFragment();
    private ProductsFragment products = new ProductsFragment();
    private AccountsDashboardFragment accountsDashboard = new AccountsDashboardFragment();
    private ProfileFragment profile = new ProfileFragment();

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();



        switch(position){
            case 0:
                mTitle = getString(R.string.profile_page);
                fragmentManager.beginTransaction().replace(R.id.container, profile, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 1:
                mTitle = getString(R.string.accounts_dashboard_page);
                fragmentManager.beginTransaction().replace(R.id.container, accountsDashboard, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 2:
                mTitle = getString(R.string.account_health_page);
                fragmentManager.beginTransaction().replace(R.id.container, new Fragment(), mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 3:
                mTitle = getString(R.string.notifications_page);
                fragmentManager.beginTransaction().replace(R.id.container, push, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 4:
                mTitle = getString(R.string.other_products_page);
                fragmentManager.beginTransaction().replace(R.id.container, products, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 5:
                mTitle = getString(R.string.feedback_page, mTitle.toString());
                // State saving is unimportant and undesirable for the feedback section, hence load a new Object.
                fragmentManager.beginTransaction().replace(R.id.container, new FeedbackFragment(), mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 6:
                mTitle = getString(R.string.location_page);
                // Temporary remove adding to stack TODO : Bug in duplication.
                if(map == null || !map.isAdded())
                    map = new MapsFragment();
                fragmentManager.beginTransaction().replace(R.id.container, map, mTitle.toString()).commit();
                break;
            case 7:
                DialogFragment confirm = new LogOffDialog();
                confirm.show(fragmentManager, "LogOffDialog");
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings Menu will be linked here.", Toast.LENGTH_SHORT).show();
            return true;
        }

        // If the help menu was clicked.
        if (id == R.id.action_help){
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.help_bundle), "Some placeholder text for now");
            HelpMenuOverlayDialog help = new HelpMenuOverlayDialog();
            help.setArguments(bundle);
            help.show(getSupportFragmentManager(), "Help Menu");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Google API related callbacks.
     */

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

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
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.gamehelper_sign_in_failed))) {
                mResolvingConnectionFailure = false;
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("INFO: ", "Google Play Services Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    /**
     *
     * Int the event of a failed sign in.
     */
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {

                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.gamehelper_sign_in_failed);
            }
        }
    }

}
