package uk.ac.ncl.team19.lloydsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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
import android.view.MotionEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsDashboardFragment;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsInfoFragment;
import uk.ac.ncl.team19.lloydsapp.accounts.MakePaymentFragment;
import uk.ac.ncl.team19.lloydsapp.accounts.PaymentConfirmFragment;
import uk.ac.ncl.team19.lloydsapp.accounts.TransactionsFragment;
import uk.ac.ncl.team19.lloydsapp.accounts.TransferConfirmFragment;
import uk.ac.ncl.team19.lloydsapp.accounts.TransferFundsFragment;
import uk.ac.ncl.team19.lloydsapp.dialogs.HelpMenuOverlayDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.LogOffDialog;
import uk.ac.ncl.team19.lloydsapp.drawer.NavigationDrawerFragment;
import uk.ac.ncl.team19.lloydsapp.features.FeedbackFragment;
import uk.ac.ncl.team19.lloydsapp.features.HealthFragment;
import uk.ac.ncl.team19.lloydsapp.features.MapsFragment;
import uk.ac.ncl.team19.lloydsapp.features.ProductsFragment;
import uk.ac.ncl.team19.lloydsapp.features.ProfileFragment;
import uk.ac.ncl.team19.lloydsapp.features.PushFragment;
import uk.ac.ncl.team19.lloydsapp.features.SetGoalsFragment;
import uk.ac.ncl.team19.lloydsapp.features.Settings;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;


/**
 * @author Dale Whinham, Raffaello Perrotta
 * Main activity associated with the drawer layout menu. Google Play services are also
 * enabled within this activity as well as the Fragment Management
 */

public class MainMenuActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // Countdown timer to determine when user auto-logs out. 1000 represents the countdown interval in millis.
    private CountDownTimer countDownTimer = new CountDownTimer(Constants.TIME_MILLIS_LOGOFF,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            // Do nothing on tick.
        }

        @Override
        public void onFinish() {
            // If the timer was not reset. Then the app was IDLE. Hence, log off.
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra(getString(R.string.bundle_autokick), true);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the content.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Build up the drawer menu.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);

        // Force the drawer to close as in the activity creation the drawer is closed after the menu is inflated
        // hence interfering with the Google Splash screen.
        if(mNavigationDrawerFragment.isDrawerOpen()){
            ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT);
        }

        // Count the number of times you login. This is incremented in the shared preferences.
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        int currentLogins = sp.getInt(Constants.SP_LOGINS, 0);
        // Increment the preference.
        sp.edit().putInt(Constants.SP_LOGINS, currentLogins+1).apply();

        // Store the date of the first login.
        if(sp.getInt(Constants.SP_LOGINS, 0) == 1){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String dateJoined = sdf.format(new Date());
            sp.edit().putString(Constants.SP_FIRST_LOGIN, dateJoined).apply();
        }

        // Debug the shared preferences
        Log.i("NUMBER OF LOGINS", Integer.toString(sp.getInt(Constants.SP_LOGINS, 0)));
        Log.i("DATE JOINED:", sp.getString(Constants.SP_FIRST_LOGIN, null));

        // Start the auto-log off timer
        countDownTimer.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // Override clicking the back button as the user may not access the login and security page.
    // Fragments are instead kept track of.
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if there are fragments on the stack.
        if(fragmentManager.getBackStackEntryCount() > 1){
            // Pop the back stack and immediately replace it in an atomic action
            fragmentManager.popBackStackImmediate();
            // Update the title as per the new fragment
            getSupportActionBar().setTitle(getSupportFragmentManager().findFragmentById(R.id.container).toString());
        }
    }

    private PushFragment push = new PushFragment();
    private MapsFragment map = new MapsFragment();
    private ProductsFragment products = new ProductsFragment();
    private AccountsDashboardFragment accountsDashboard = new AccountsDashboardFragment();
    private ProfileFragment profile = new ProfileFragment();
    private HealthFragment health = new HealthFragment();
    private SetGoalsFragment goals = new SetGoalsFragment();

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // add back stack listener
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // Update the title regardless of what is being loaded in.
                getSupportActionBar().setTitle(getSupportFragmentManager().findFragmentById(R.id.container).toString());
            }
        });

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
                // Determine whether goals were set or not, load the setting of goals if not.
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

                if(sp.getBoolean(Constants.SP_GOALS_SET, false)){
                    fragmentManager.beginTransaction().replace(R.id.container, health, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                }else{
                    fragmentManager.beginTransaction().replace(R.id.container, goals, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                }

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

                if(map == null || !map.isAdded())
                    map = new MapsFragment();
                fragmentManager.beginTransaction().replace(R.id.container, map, mTitle.toString()).addToBackStack(mTitle.toString()).commit();
                break;
            case 7:
                DialogFragment confirm = new LogOffDialog();
                confirm.show(fragmentManager, "LogOffDialog");
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Settings()).addToBackStack(getString(R.string.settings)).commit();
            return true;
        }

        // If the help menu was clicked.
        if (id == R.id.action_help){
            // Make a new bundle to pass information.
            Bundle bundle = new Bundle();
            // Find currently loaded fragment.
            Fragment entry = getSupportFragmentManager().findFragmentById(R.id.container);
            String fragmentAt = entry.getClass().getSimpleName();
            // Make the help menu
            HelpMenuOverlayDialog help = new HelpMenuOverlayDialog();

            // Switch the class you're currently at.
            if(fragmentAt.equals(ProfileFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_profile_page));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(PushFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_noti));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(AccountsDashboardFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_acc_dashboard));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(AccountsInfoFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_current_acc));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(TransactionsFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_transactions));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(MakePaymentFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_mk_payment));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(TransferFundsFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_transfer));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(FeedbackFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_feedback));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(MapsFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_map));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(SetGoalsFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_set_goals));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(HealthFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_health));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(PaymentConfirmFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_confirm));
                help.setArguments(bundle);
            }
            else if(fragmentAt.equals(TransferConfirmFragment.class.getSimpleName())){
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_confirm));
                help.setArguments(bundle);
            }else{
                bundle.putString(Constants.BUNDLE_KEY_HELP, getString(R.string.man_no_class));
                help.setArguments(bundle);
            }

            help.show(getSupportFragmentManager(), "Help Menu");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        profile.onActivityResult(requestCode, resultCode, intent);

    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        // Cancel and restart the timer
        countDownTimer.cancel();
        countDownTimer.start();
        return true;
    }

    @Override
    public String toString(){
        return getString(R.string.profile_page);
    }
}
