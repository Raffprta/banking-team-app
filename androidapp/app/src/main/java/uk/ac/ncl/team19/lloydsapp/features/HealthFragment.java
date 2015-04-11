package uk.ac.ncl.team19.lloydsapp.features;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.AccountDetailsResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.TransactionsResponse;
import uk.ac.ncl.team19.lloydsapp.api.utility.ErrorHandler;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.FragmentChecker;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta (backend integration, algorithms and client side programming)
 * @author Yessengerey Bolatov (all XML).
 * @author Dale Whinham - simplify Bundle key access
 *
 * A class which will hold all information pertaining to the account health status and allow the user
 * a way of interacting with and chosing what goals to set per month.
 */
public class HealthFragment extends Fragment {

    private List<BankAccount> accounts = null;
    private ProgressBar progressBarAPI;
    private GridLayout loadedView;
    private TextView errorOnHealth;
    private List<List<Transaction>> allTransactions;
    private TextView spendAmount;
    private TextView saveAmount;
    private TextView overdraft;
    private ProgressBar hpBar;
    private ProgressBar donateBar;
    private ProgressBar spendBar;
    private ProgressBar saveBar;
    private ProgressBar overdraftBar;
    private SharedPreferences sp;
    private TextView donateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View healthView = inflater.inflate(R.layout.account_health_page, container, false);

        // Make shared preferences object
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Calculate how many days the user has set the goals for.
        String dateThen = sp.getString(Constants.SP_GOALS_START, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

        int daysBetween = 0;

        try {
            daysBetween = (int)(((new Date()).getTime() - sdf.parse(dateThen).getTime()) / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int daysPeriod = 7;

        // If the period is set per week, return 7 otherwise it's a monthly period hence return 30.
        daysPeriod = sp.getInt(Constants.SP_GOALS_SET_FOR, -1) == Constants.WEEKLY ? 7 : 30;

        Log.i("Debugging Goals:", "Have been set for " + Integer.toString(daysPeriod) + " days.");
        Log.i("Debugging Goals:", "Currently they have been going for " + Integer.toString(daysBetween) + " days");

        // If the number of days the goals have been active for exceed what the user wants them for, i.e. 30 = month
        // 7 = week.
        if(daysBetween >=  daysPeriod){
            Bundle b = new Bundle();
            b.putBoolean(Constants.BUNDLE_KEY_GOALS_EXPIRY, true);
            SetGoalsFragment setGoals = new SetGoalsFragment();
            setGoals.setArguments(b);
            // Immediately transition to the set goals fragment to reset the goals.
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, setGoals).addToBackStack(getString(R.string.account_health_page)).commit();
        }

        progressBarAPI = (ProgressBar) healthView.findViewById(R.id.progressBarAPI);
        errorOnHealth = (TextView) healthView.findViewById(R.id.errorOnHealth);
        loadedView = (GridLayout) healthView.findViewById(R.id.accountHealthGridLayout);
        loadedView.setVisibility(View.INVISIBLE);

        // Get a Fragment Manager.
        final FragmentManager fragmentManager = HealthFragment.this.getFragmentManager();

        // Make a call to the API.
        final APIConnector ac = new APIConnector(getActivity());

        // Get a list of all of the bank accounts belonging to the user
        ac.getAccountDetails(new Callback<AccountDetailsResponse>() {

            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {

                // Fail silently if not on the same class.
                if(!FragmentChecker.checkFragment(fragmentManager, HealthFragment.this))
                    return;

                // Remove progress bar
                progressBarAPI.setVisibility(View.INVISIBLE);

                if (accountDetailsResponse.getStatus() == APIResponse.Status.SUCCESS) {
                    // Store accounts
                    accounts = accountDetailsResponse.getAccounts();

                    // Debug
                    Log.i("Number of Accounts", Integer.toString(accounts.size()));

                    // Show message if no bank accounts returned
                    if (accounts.size() == 0) {
                        errorOnHealth.setText(R.string.account_info_no_accounts);
                        errorOnHealth.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Reinstate progress bar
                    progressBarAPI.setVisibility(View.VISIBLE);

                    // If there were bank accounts, now attempt to get transactions from them.
                    String from = sp.getString(Constants.SP_GOALS_START, null);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    Date fromDate = null;

                    try {
                        fromDate = sdf.parse(from);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    getTransactionsList(ac, accounts, fromDate, new Date());

                }else{
                    ErrorHandler.fail(getFragmentManager(), accountDetailsResponse.getErrorMessage());
                    progressBarAPI.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                // Handle error
                ErrorHandler.fail(getActivity(), getFragmentManager(), retrofitError);
                progressBarAPI.setVisibility(View.GONE);
            }

        });

        // Get the Health Bars.
        hpBar = (ProgressBar) healthView.findViewById(R.id.progressBar);
        spendBar = (ProgressBar) healthView.findViewById(R.id.spendProgressBar);
        saveBar = (ProgressBar) healthView.findViewById(R.id.saveProgressBar);
        overdraftBar = (ProgressBar) healthView.findViewById(R.id.overdraftProgressBar);

        // Set the values of the goals.
        TextView perMonthOrPerWeek = (TextView)healthView.findViewById(R.id.perMonthOrPerWeek);
        spendAmount = (TextView)healthView.findViewById(R.id.spendTextView);
        saveAmount = (TextView)healthView.findViewById(R.id.saveTextView);
        overdraft = (TextView)healthView.findViewById(R.id.overdraftTextView);

        // Donate views
        TextView donateStartView = (TextView)healthView.findViewById(R.id.donateStartView);
        donateTextView = (TextView)healthView.findViewById(R.id.donateTextView);
        donateBar = (ProgressBar)healthView.findViewById(R.id.donateProgressBar);



        // If we are not donating, hide all views.
        if(!sp.getBoolean(Constants.SP_GOALS_DONATE, false)){
            donateStartView.setVisibility(View.GONE);
            donateTextView.setVisibility(View.GONE);
            donateBar.setVisibility(View.GONE);
        }

        // Populate other fields
        if(sp.getInt(Constants.SP_GOALS_SET_FOR, 1) == Constants.WEEKLY){
            perMonthOrPerWeek.setText(getString(R.string.weekly));
        }else if(sp.getInt(Constants.SP_GOALS_SET_FOR, 1) == Constants.MONTHLY){
            perMonthOrPerWeek.setText(getString(R.string.monthly));
        }

        spendAmount.setText(Float.toString(sp.getFloat(Constants.SP_GOALS_SPEND, -1)));
        saveAmount.setText(Float.toString(sp.getFloat(Constants.SP_GOALS_SAVE, -1)));
        overdraft.setText(Float.toString(sp.getFloat(Constants.SP_GOALS_OVERDRAFT, -1)));


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

        healthView.findViewById(R.id.backToProfileFromHealth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new ProfileFragment()).addToBackStack(getString(R.string.account_health_page)).commit();
            }
        });

        healthView.findViewById(R.id.setGoals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new SetGoalsFragment()).addToBackStack(getString(R.string.account_health_page)).commit();
            }
        });

        return healthView;

    }

    private int spinLock;

    /**
     * Helper method to get a list of transactions from each account.
     * @param ac The APIConnector
     * @param accounts The list of accounts to get the transactions from.
     * @param from The date from
     * @param to The date to - this will typically be "now".
     */
    private void getTransactionsList(APIConnector ac, List<BankAccount> accounts, Date from, Date to){

        allTransactions = new ArrayList<>();

        // Lock to determine when to calculate bank accounts
        spinLock = accounts.size();

        // Get a Fragment Manager.
        final FragmentManager fragmentManager = HealthFragment.this.getFragmentManager();

        for(BankAccount account : accounts){
            ac.getTransactions(account.getId(), from, to, new Callback<TransactionsResponse>() {

                @Override
                public void success(TransactionsResponse transactionsResponse, Response response) {

                    // Fail silently if not on the same class.
                    if(!FragmentChecker.checkFragment(fragmentManager, HealthFragment.this))
                        return;

                    if (transactionsResponse.getStatus() == APIResponse.Status.SUCCESS) {
                        // Start populating our list of transactions
                        List<Transaction> transaction = transactionsResponse.getTransactions();

                        // Store the transactions
                        if(transaction != null && transaction.size() > 0){
                            allTransactions.add(transaction);
                        }

                        // Decrement lock.
                        spinLock--;

                        // Calculate the health only once, when the lock is ready (i.e. all accounts processed).
                        if(spinLock == 0){
                            workOutHealth(allTransactions);
                            // On health calculation success re-enable the views.
                            progressBarAPI.setVisibility(View.GONE);
                            loadedView.setVisibility(View.VISIBLE);
                        }

                    }else{
                        progressBarAPI.setVisibility(View.GONE);
                        ErrorHandler.fail(getFragmentManager(), transactionsResponse.getErrorMessage());
                    }

                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    // Handle error
                    ErrorHandler.fail(getActivity(), getFragmentManager(), retrofitError);
                    progressBarAPI.setVisibility(View.GONE);
                }

            });

        }
    }

    /**
     * Helper method to work out the health of the bank user.
     * @param transactions This parameter is expected to not have any empty sets or nulls within it.
     *                     This is done in the APIConnector implementation in this page.
     */
    private void workOutHealth(List<List<Transaction>> transactions){

        int spend = 0;
        int moneyIn = 0;
        int save = 0;
        int overdraft = 0;
        int donationAmount = 0;
        boolean donation = false;

        // List with the user's bank ids.
        List<Long> accountIds = new ArrayList<>();

        for(BankAccount acc : accounts)
            accountIds.add(acc.getId());

        for(List<Transaction> transaction : transactions){
            for(Transaction item : transaction){
                // Determine if the amount is ingoing or outgoing.
                if(accountIds.contains(item.getToAccountId())){
                    // This is a payment to the user.
                    moneyIn += item.getAmount()/100;
                }else{
                    spend += item.getAmount()/100;
                }

                // Check if a donation was made and record the amount.
                if(item.getTag() == Transaction.Tag.DONATION){
                    donation = true;
                    donationAmount += item.getAmount()/100;
                }

            }
        }

        // Calculate overdraft that the user currently has eaten into.
        for(BankAccount acc : accounts){
            // If the account balance is less than zero then the user has used overdraft in this account.
            if(acc.getBalance() < 0)
                overdraft += Math.abs(acc.getBalance());
        }

        // Calculate save amount.
        save = moneyIn - spend;

        double calculation = save / sp.getFloat(Constants.SP_GOALS_SAVE, -1);

        // Populate healthbars.
        int percentage = Math.min((int) (calculation * 100), Constants.HEALTH_PERFECT);
        saveBar.setProgress(percentage);

        // Helpful Debug methods
        Log.i("Debug: Save ", Integer.toString(save));
        Log.i("Debug: Spend ",  Integer.toString(spend));

        // If the user is under or on his max. spending allowance you automatically get 100.
        if(spend <= sp.getFloat(Constants.SP_GOALS_SPEND, -1)){
            percentage = Constants.HEALTH_PERFECT;
        }else{
            // The algorithm looks at the ratio of the amount out : your target and will multiply that ratio by
            // ten. For example if you budgeted £100 to spend and you spent £400 the ratio is 4. This is multiplied by
            // ten to become 40. This amount is then taken from 100. To leave 60%. If the ratio is > 10 then the value will
            // be 0.
            calculation = (spend / sp.getFloat(Constants.SP_GOALS_SPEND, -1) * 10);
            percentage = Math.min(spend / (int)calculation, 100);
        }

        spendBar.setProgress(percentage);

        // If a donation was made it does not matter how much the donation was, the user has completed this goal.
        if(donation)
            donateBar.setProgress(Constants.HEALTH_PERFECT);

        // Set the amount the user has donated
        donateTextView.setText(CurrencyMangler.integerToSterlingString((long)donationAmount));

        int goalsOverdraft = (int)sp.getFloat(Constants.SP_GOALS_OVERDRAFT, -1);
        /**
         * The algorithm for the overdraft is as follows. If no overdraft was used then the health bar
         * is set to perfect (100).
         * If some overdraft was used BUT it was planned when you set the goals (i.e.) it is less than or equal to the
         * amount you set then the health is set to "good" (75%)
         * If you exceed the overdraft then the health is set as as ratio * 10 by how much you exceeded it offset from
         * 75%. If this goes below 0 then the maximum value is taken.
         * If the value of the goals for the overdraft is zero, then the calculation is adjusted to prevent division by zero.
         */
        if(overdraft == 0)
            overdraftBar.setProgress(Constants.HEALTH_PERFECT);
        else if(overdraft <= goalsOverdraft)
            overdraftBar.setProgress(Constants.HEALTH_GOOD);
        else if(overdraft > goalsOverdraft && goalsOverdraft != 0)
            overdraftBar.setProgress(Math.max(Constants.HEALTH_GOOD - ((overdraft / goalsOverdraft) * 10), 0));
        else
            overdraftBar.setProgress(Math.max(Constants.HEALTH_GOOD - overdraft, 0));

        // Program final health bar.
        int noBars = 3;
        int finalAverage = (spendBar.getProgress() + saveBar.getProgress() + overdraftBar.getProgress());
        if(sp.getBoolean(Constants.SP_GOALS_DONATE, false)){
            finalAverage += donateBar.getProgress();
            noBars++;
        }

        finalAverage /= noBars;

        hpBar.setProgress(finalAverage);

        // Store the value in the shared preferences
        sp.edit().putInt(Constants.SP_ACCOUNTS_HP, finalAverage).apply();

        // Colour the bars
        colourBar(spendBar);
        colourBar(saveBar);
        colourBar(overdraftBar);
        colourBar(donateBar);
        colourBar(hpBar);

    }

    /**
     * Helper method to colour the progress bars in.
     * @param bar the bar to colour
     */
    private void colourBar(ProgressBar bar){
        // Determine what colour to set.
        if(bar.getProgress() >= Constants.HEALTH_GOOD){
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar));
        }else if(bar.getProgress() >= Constants.HEALTH_AVG && hpBar.getProgress() < Constants.HEALTH_GOOD){
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_medium));
        }else if(bar.getProgress() >= Constants.HEALTH_POOR && hpBar.getProgress() < Constants.HEALTH_AVG){
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_poor));
        }else{
            bar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_dismal));
        }
    }

    @Override
    public String toString(){
        return getString(R.string.account_health_page);
    }

}
