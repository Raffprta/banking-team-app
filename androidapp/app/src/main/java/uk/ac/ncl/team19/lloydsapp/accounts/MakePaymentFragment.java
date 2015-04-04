package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov (XML Design), Raffaello Perrotta
 * @author Dale Whinham - backend integration
 *
 * Fragment for making payments to other accounts.
 */

public class MakePaymentFragment extends Fragment {

    private Bundle args;
    private List<BankAccount> accounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View paymentView = inflater.inflate(R.layout.make_a_payment, container, false);

        // Get all views containing information.
        final Spinner fromAccount = (Spinner)paymentView.findViewById(R.id.accountsSpinner);
        final EditText transactionReference = (EditText)paymentView.findViewById(R.id.reference);
        final EditText accountNumber = (EditText)paymentView.findViewById(R.id.accountNumberPayment);
        final EditText sortCode = (EditText)paymentView.findViewById(R.id.sortCodePayment);
        final EditText amountToPay = (EditText)paymentView.findViewById(R.id.amountPayment);

        // Unbundle accounts
        args = getArguments();
        accounts = (List<BankAccount>) args.getSerializable(Constants.BUNDLE_KEY_ACCOUNT_LIST);

        // Populate Spinner with bank accounts
        fromAccount.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts));

        paymentView.findViewById(R.id.continuePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Not a number entered as account number.
                if(!accountNumber.getText().toString().matches("\\d+")){
                    accountNumber.setError(getString(R.string.err_acc_not_no));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Account number not eight digits long.
                if(accountNumber.getText().toString().length() != Constants.ACC_NO_SIZE){
                    accountNumber.setError(getString(R.string.err_acc_len_not_eight));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Not a number or dash entered as sortcode number.
                if(!sortCode.getText().toString().matches("^[\\d-]*$")){
                    sortCode.setError(getString(R.string.err_sc_not_no));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Sortcode number not six digits long.
                if(sortCode.getText().toString().length() != Constants.SORT_CODE_NO_SIZE){
                    sortCode.setError(getString(R.string.err_acc_not_six));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check if an amount was entered
                if(amountToPay.getText().toString() == null || amountToPay.getText().toString().length() <= 0){
                    amountToPay.setError(getString(R.string.err_payment_empty));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check if GBP decimal or otherwise is entered
                if(!amountToPay.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")){
                    amountToPay.setError(getString(R.string.err_payment_wrong_form));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // If all validation conditions passed, then add information to bundle and pass to the next fragment
                args.putSerializable(Constants.BUNDLE_KEY_FROM_ACC, (BankAccount) fromAccount.getSelectedItem());
                args.putString(Constants.BUNDLE_KEY_TO_REF, transactionReference.getText().toString());
                args.putString(Constants.BUNDLE_KEY_TO_SORT_CODE, sortCode.getText().toString());
                args.putString(Constants.BUNDLE_KEY_TO_ACC_NO, accountNumber.getText().toString());
                args.putLong(Constants.BUNDLE_KEY_AMOUNT, CurrencyMangler.sterlingStringToInteger(amountToPay.getText().toString()));

                PaymentConfirmFragment paymentConfirmation = new PaymentConfirmFragment();
                paymentConfirmation.setArguments(args);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, paymentConfirmation).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        paymentView.findViewById(R.id.cancelPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return paymentView;

    }

    @Override
    public String toString(){
        return getString(R.string.make_payment_page);
    }

}
