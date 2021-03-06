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
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov
 * Full XML design.
 * @author Raffaello Perrotta
 * Fragment handling, transactions and getting references to the views used in the banking.
 * @author Dale Whinham
 * Various XML modifications. Refactor information to use serialisable methods.
 *
 * This class provides an implementation of the various features which a user can access pertaining
 * to their individual bank account. The user will have options to make transfers, payments, view transactions history
 * and other information relating to their account.
 */
public class AccountsInfoFragment extends Fragment {

    // The account we're looking at
    private BankAccount currentAccount;

    // A list of all accounts to pass to transfer fragments
    private List<BankAccount> accounts;

    private Bundle args;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View accountsInfoView = inflater.inflate(R.layout.account_information, container, false);

        // References to text views
        TextView accountType = (TextView) accountsInfoView.findViewById(R.id.accountType);
        TextView accountNickname = (TextView) accountsInfoView.findViewById(R.id.accountNickname);
        TextView accountNumber = (TextView) accountsInfoView.findViewById(R.id.accountNumber);
        TextView sortCode = (TextView) accountsInfoView.findViewById(R.id.sortCode);
        TextView balance = (TextView) accountsInfoView.findViewById(R.id.balance);
        TextView availableFunds = (TextView) accountsInfoView.findViewById(R.id.availableFunds);

        // Get arguments from bundle
        args = getArguments();
        currentAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_CURRENT_ACCOUNT);
        accounts = (List<BankAccount>) args.getSerializable(Constants.BUNDLE_KEY_ACCOUNT_LIST);

        // Populate text views with account info
        if (currentAccount != null) {
            String balanceString = String.format("%s %s", getString(R.string.account_info_balance), currentAccount.getFormattedBalance());
            String availableFundsString = String.format("%s %s", getString(R.string.account_info_avail_funds), currentAccount.getFormattedAvailableFunds());

            accountType.setText(currentAccount.getAccountTypeString(getActivity()));
            // Set information.
            accountNickname.setText(currentAccount.getNickname());
            accountNumber.setText(currentAccount.getAccountNumber());
            sortCode.setText(currentAccount.getFormattedSortCode());
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

                // Pass arguments from this fragment to the next
                MakePaymentFragment makePaymentFragment = new MakePaymentFragment();
                makePaymentFragment.setArguments(args);

                fragmentManager.beginTransaction().replace(R.id.container, makePaymentFragment).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsInfoView.findViewById(R.id.makeTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                // Pass arguments from this fragment to the next
                TransferFundsFragment transferFundsFragment = new TransferFundsFragment();
                transferFundsFragment.setArguments(args);

                fragmentManager.beginTransaction().replace(R.id.container, transferFundsFragment).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsInfoView.findViewById(R.id.accountHealthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                // Determine whether goals were set or not, load the setting of goals if not.
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

                if(sp.getBoolean(Constants.SP_GOALS_SET, false)){
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