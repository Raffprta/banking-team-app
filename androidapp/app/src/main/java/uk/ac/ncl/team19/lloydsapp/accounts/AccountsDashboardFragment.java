package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.response.AccountDetailsResponse;

/**
 * @Author Yao Tong, Yessengerey Bolatov conversion to Fragment by Raffaello Perrotta
 */
public class AccountsDashboardFragment extends Fragment {

    private static final String TAG = AccountsDashboardFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View accountsDashboardView = inflater.inflate(R.layout.account_dashboard, container, false);

        // TODO : Are we only having two accounts, if not then this needs to be more programmatic
        accountsDashboardView.findViewById(R.id.currentAccounts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsDashboardView.findViewById(R.id.savingsAccounts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        // TODO: Remove this, replace with UI updates
        APIConnector ac = new APIConnector(getActivity());
        ac.getAccountDetails(new Callback<AccountDetailsResponse>() {
            @Override
            public void success(AccountDetailsResponse accountDetailsResponse, Response response) {
                List<BankAccount> accounts = accountDetailsResponse.getAccounts();
                for(BankAccount b: accounts) {
                    Log.i(TAG, String.format("Got account ID %d, acc no: %s, sort code: %s, balance £%.2f", b.getId(), b.getAccountNumber(), b.getSortCode(), b.getBalance() / 100.0));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        return accountsDashboardView;

    }

    @Override
    public String toString(){
        return getString(R.string.accounts_dashboard_page);
    }

}
