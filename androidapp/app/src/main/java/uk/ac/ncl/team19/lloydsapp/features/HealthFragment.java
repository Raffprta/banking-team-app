package uk.ac.ncl.team19.lloydsapp.features;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.AccountDetailsResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.TransactionsResponse;
import uk.ac.ncl.team19.lloydsapp.api.utility.ErrorHandler;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta, XML by Yessengerey Bolatov.
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View healthView = inflater.inflate(R.layout.account_health_page, container, false);

        final ProgressBar progressBar = (ProgressBar) healthView.findViewById(R.id.progressBar);
        progressBarAPI = (ProgressBar) healthView.findViewById(R.id.progressBarAPI);
        errorOnHealth = (TextView) healthView.findViewById(R.id.errorOnHealth);
        loadedView = (GridLayout) healthView.findViewById(R.id.accountHealthGridLayout);
        loadedView.setVisibility(View.INVISIBLE);


        // Make shared preferences object
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Make a call to the API.
        final APIConnector ac = new APIConnector(getActivity());

        // Get a list of all of the bank accounts belonging to the user
        ac.getAccountDetails(new Callback<AccountDetailsResponse>() {

            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {

                // Remove progress bar
                progressBar.setVisibility(View.INVISIBLE);

                if (accountDetailsResponse.getStatus() == APIResponse.Status.SUCCESS) {
                    // Store accounts
                    accounts = accountDetailsResponse.getAccounts();

                    // Show message if no bank accounts returned
                    if (accounts.size() == 0) {
                        errorOnHealth.setText(R.string.account_info_no_accounts);
                        return;
                    }

                    // Reinstate progress bar
                    progressBar.setVisibility(View.VISIBLE);

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

        // Get the Health Bar.
        final ProgressBar hpBar = (ProgressBar) healthView.findViewById(R.id.progressBar);

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

        // Set the values of the goals.
        TextView perMonthOrPerWeek = (TextView)healthView.findViewById(R.id.perMonthOrPerWeek);
        TextView spendAmount = (TextView)healthView.findViewById(R.id.spendTextView);
        TextView saveAmount = (TextView)healthView.findViewById(R.id.saveTextView);
        TextView overdraft = (TextView)healthView.findViewById(R.id.overdraftTextView);

        // Donate views
        TextView donateStartView = (TextView)healthView.findViewById(R.id.donateStartView);
        TextView donateTextView = (TextView)healthView.findViewById(R.id.donateTextView);
        ProgressBar donateBar = (ProgressBar)healthView.findViewById(R.id.donateProgressBar);



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

    /**
     * Helper method to get a list of transactions from each account.
     * @param ac The APIConnector
     * @param accounts The list of accounts to get the transactions from.
     * @param from The date from
     * @param to The date to - this will typically be "now".
     */
    private void getTransactionsList(APIConnector ac, List<BankAccount> accounts, Date from, Date to){

        for(BankAccount account : accounts){
            ac.getTransactions(account.getId(), from, to, new Callback<TransactionsResponse>() {

                @Override
                public void success(TransactionsResponse transactionsResponse, Response response) {
                    if (transactionsResponse.getStatus() == APIResponse.Status.SUCCESS) {
                        // On Success
                        progressBarAPI.setVisibility(View.GONE);
                        loadedView.setVisibility(View.VISIBLE);
                    }else{
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

        }

    }

    @Override
    public String toString(){
        return getString(R.string.account_health_page);
    }

}
