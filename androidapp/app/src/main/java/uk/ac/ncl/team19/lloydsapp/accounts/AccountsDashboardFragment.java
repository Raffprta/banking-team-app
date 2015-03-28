package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.AccountDetailsResponse;

/**
 * @Author Yao Tong, Yessengerey Bolatov conversion to Fragment by Raffaello Perrotta
 */
public class AccountsDashboardFragment extends Fragment {

    private static final String TAG = AccountsDashboardFragment.class.getName();

    private ProgressBar progressBar = null;
    List<BankAccount> accounts = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final ViewGroup accountsDashboardView = (ViewGroup) inflater.inflate(R.layout.account_dashboard, container, false);

        progressBar = (ProgressBar) accountsDashboardView.findViewById(R.id.progressBar);

        final ViewGroup accountsList = (ViewGroup) accountsDashboardView.findViewById(R.id.accountsList);

        //inflater.inflate(R.layout.account, accountsList);
        //inflater.inflate(R.layout.account, accountsList);

        // TODO : Are we only having two accounts, if not then this needs to be more programmatic

//
//        accountsDashboardView.findViewById(R.id.savingsAccounts).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
//            }
//        });

        // Get the account details from the backend
        APIConnector ac = new APIConnector(getActivity());
        ac.getAccountDetails(new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                if (accountDetailsResponse.getStatus() == APIResponse.Status.SUCCESS) {
                    // Store accounts
                    accounts = accountDetailsResponse.getAccounts();

                    // Hide progress wheel
                    progressBar.setVisibility(View.GONE);

                    // Show message if no bank accounts returned
                    if (accounts.size() == 0) {
                        TextView noBankAccounts = (TextView) accountsDashboardView.findViewById(R.id.noBankAccounts);
                        noBankAccounts.setVisibility(View.VISIBLE);
                        return;
                    }

                    LayoutInflater inflater = LayoutInflater.from(getActivity());

                    for (final BankAccount a: accounts) {
                        // Inflate a new account view (but don't add to root yet)
                        View accountView = inflater.inflate(R.layout.account, accountsList, false);

                        // Set click listener
                        accountView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                AccountsInfoFragment infoFragment = new AccountsInfoFragment();

                                // Bundle up the account object so we can pass it to the AccountsInfoFragment
                                Bundle args = new Bundle();
                                args.putSerializable("ACCOUNT", a);
                                infoFragment.setArguments(args);

                                fragmentManager.beginTransaction().replace(R.id.container, infoFragment).addToBackStack(getString(R.string.accounts_dashboard_page)).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
                            }
                        });

                        // Get references to each textview
                        TextView accountType = (TextView) accountView.findViewById(R.id.accountType);
                        TextView accountNumberAndSortCode = (TextView) accountView.findViewById(R.id.accountNumberAndSortCode);
                        TextView balance = (TextView) accountView.findViewById(R.id.balance);
                        TextView availableFunds = (TextView) accountView.findViewById(R.id.availableFunds);

                        // Populate text views with information
                        String accountTypeString = a.getAccountTypeString(getActivity());
                        String accountNumberAndSortCodeString = String.format("%s %s", a.getFormattedSortCode(), a.getAccountNumber());
                        String balanceString = a.getFormattedBalance();
                        String availableFundsString = String.format("%s %s", getString(R.string.account_info_avail_funds), a.getFormattedAvailableFunds());

                        accountType.setText(accountTypeString);
                        accountNumberAndSortCode.setText(accountNumberAndSortCodeString);
                        balance.setText(balanceString);
                        availableFunds.setText(availableFundsString);

                        // Add view to container
                        accountsList.addView(accountView);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                // Remove progress wheel
                accountsList.removeView(progressBar);
            }
        });

        return accountsDashboardView;

    }

    @Override
    public String toString(){
        return getString(R.string.accounts_dashboard_page);
    }

}
