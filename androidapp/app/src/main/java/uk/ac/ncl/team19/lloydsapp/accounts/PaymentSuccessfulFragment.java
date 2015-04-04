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
 * @author Ivy Tong (XML), Raffaello Perrotta
 */
public class PaymentSuccessfulFragment extends Fragment {

    private Bundle args;
    private BankAccount fromAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View paymentSuccessView = inflater.inflate(R.layout.payment_success, container, false);

        TextView fromAccType = (TextView) paymentSuccessView.findViewById(R.id.fromAccType);
        TextView fromAccNo = (TextView) paymentSuccessView.findViewById(R.id.fromAccNo);
        TextView fromSortCode = (TextView) paymentSuccessView.findViewById(R.id.fromSortCode);
        TextView toRef = (TextView) paymentSuccessView.findViewById(R.id.toRef);
        TextView toAccNo = (TextView) paymentSuccessView.findViewById(R.id.toAccNo);
        TextView toSortCode = (TextView) paymentSuccessView.findViewById(R.id.toSortCode);
        TextView amount = (TextView) paymentSuccessView.findViewById(R.id.amount);

        // Set the values from the bundle, i.e. what the user entered in the previous fragment.
        args = getArguments();
        fromAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_FROM_ACC);
        fromAccType.setText(fromAccount.getAccountTypeString(getActivity()));
        fromAccNo.setText(fromAccount.getAccountNumber());
        fromSortCode.setText(fromAccount.getFormattedSortCode());

        toRef.setText(args.getString(Constants.BUNDLE_KEY_TO_REF));
        toAccNo.setText(args.getString(Constants.BUNDLE_KEY_TO_ACC_NO));
        toSortCode.setText(args.getString(Constants.BUNDLE_KEY_TO_SORT_CODE));
        amount.setText(CurrencyMangler.integerToSterlingString(args.getLong(Constants.BUNDLE_KEY_AMOUNT)));

        paymentSuccessView.findViewById(R.id.returnToAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).commit();
            }
        });

        return paymentSuccessView;

    }

    @Override
    public String toString(){
        return getString(R.string.payment_successful_page);
    }

}
