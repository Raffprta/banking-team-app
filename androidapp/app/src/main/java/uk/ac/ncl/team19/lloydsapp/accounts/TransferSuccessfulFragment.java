package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta, XML by Yessengerey Bolatov
 * @author Dale Whinham - backend integration
 */
public class TransferSuccessfulFragment extends Fragment {

    private Bundle args;
    private BankAccount fromAccount;
    private BankAccount toAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View transferSuccessView = inflater.inflate(R.layout.transfer_success, container, false);

        TextView fromAccountName = (TextView) transferSuccessView.findViewById(R.id.fromAccount);
        TextView fromAccountNo = (TextView) transferSuccessView.findViewById(R.id.fromAccNo);
        TextView fromSortCode = (TextView) transferSuccessView.findViewById(R.id.fromSortCode);
        TextView toAccountName = (TextView) transferSuccessView.findViewById(R.id.toAccount);
        TextView toAccountNo = (TextView) transferSuccessView.findViewById(R.id.toAccNo);
        TextView toSortCode = (TextView) transferSuccessView.findViewById(R.id.toSortCode);
        TextView amount = (TextView) transferSuccessView.findViewById(R.id.amount);

        // Set values taken from bundle, i.e. what the user entered in the previous fragment.
        args = getArguments();
        fromAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_FROM_ACC);
        toAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_TO_ACC);

        fromAccountName.setText(fromAccount.toString());
        fromAccountNo.setText(fromAccount.getAccountNumber());
        fromSortCode.setText(fromAccount.getFormattedSortCode());
        toAccountName.setText(toAccount.toString());
        toAccountNo.setText(toAccount.getAccountNumber());
        toSortCode.setText(toAccount.getFormattedSortCode());
        amount.setText(CurrencyMangler.integerToSterlingString(args.getLong(Constants.BUNDLE_KEY_AMOUNT)));

        transferSuccessView.findViewById(R.id.backToAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsDashboardFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        return transferSuccessView;
    }

    @Override
    public String toString(){
        return getString(R.string.transfer_successful_page);
    }

}
