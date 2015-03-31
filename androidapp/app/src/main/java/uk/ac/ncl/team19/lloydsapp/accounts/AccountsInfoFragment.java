package uk.ac.ncl.team19.lloydsapp.accounts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.features.HealthFragment;
import uk.ac.ncl.team19.lloydsapp.features.SetGoalsFragment;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author XML by Yessengerey Bolatov, conversion into Fragment by Raffaello Perrotta
 * @author Dale Whinham - XML modifications, added backend integration
 */
public class AccountsInfoFragment extends Fragment {

    private BankAccount account;
    private List<BankAccount> accounts;

    private Bundle args;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View accountsInfoView = inflater.inflate(R.layout.account_information, container, false);

        // References to text views
        TextView accountType = (TextView) accountsInfoView.findViewById(R.id.accountType);
        TextView userName = (TextView) accountsInfoView.findViewById(R.id.userName);
        TextView accountNumber = (TextView) accountsInfoView.findViewById(R.id.accountNumber);
        TextView sortCode = (TextView) accountsInfoView.findViewById(R.id.sortCode);
        TextView balance = (TextView) accountsInfoView.findViewById(R.id.balance);
        TextView availableFunds = (TextView) accountsInfoView.findViewById(R.id.availableFunds);

        // Populate text views with account info
        args = getArguments();
        account = (BankAccount) args.getSerializable("ACCOUNT");

        if (account != null) {
            String balanceString = String.format("%s %s", getString(R.string.account_info_balance), account.getFormattedBalance());
            String availableFundsString = String.format("%s %s", getString(R.string.account_info_avail_funds), account.getFormattedAvailableFunds());

            accountType.setText(account.getAccountTypeString(getActivity()));
            // FIXME: Need to get user details on login
            userName.setText("FIXME");
            accountNumber.setText(account.getAccountNumber());
            sortCode.setText(account.getFormattedSortCode());
            balance.setText(balanceString);
            availableFunds.setText(availableFundsString);
        }

        accountsInfoView.findViewById(R.id.viewTransactions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TransactionsFragment transactionsFragment = new TransactionsFragment();
                transactionsFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.container, transactionsFragment).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsInfoView.findViewById(R.id.makePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new MakePaymentFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsInfoView.findViewById(R.id.makeTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new TransferFundsFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsInfoView.findViewById(R.id.accountHealthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                // Determine whether goals were set or not, load the setting of goals if not.
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

                if(sp.getBoolean(getString(R.string.sp_goals_set), false)){
                    fragmentManager.beginTransaction().replace(R.id.container, new HealthFragment()).addToBackStack(null).commit();
                }else{
                    fragmentManager.beginTransaction().replace(R.id.container, new SetGoalsFragment()).addToBackStack(null).commit();
                }
            }
        });

        return accountsInfoView;

    }

    @Override
    public String toString(){
        return getString(R.string.account_info_page);
    }

}