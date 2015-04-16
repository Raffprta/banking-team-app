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
import uk.ac.ncl.team19.lloydsapp.adapters.TagSpinnerAdapter;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov
 * XML design and structure.
 * @author Raffaello Perrotta
 * Minor modifications to the XML. Client-side validation. Passing information between fragments.
 * @author Dale Whinham
 * Backend integration. Refactor information passed between fragments to serialisable.
 *
 * This class provides a Fragment for making payments to other accounts. This will hence transition to
 * a confirmation fragment unless the user cancels it.
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
        final Spinner fromAccount = (Spinner) paymentView.findViewById(R.id.accounts);
        final EditText toAccNo = (EditText) paymentView.findViewById(R.id.toAccNo);
        final EditText toSortCode = (EditText) paymentView.findViewById(R.id.toSortCode);
        final EditText transactionReference = (EditText) paymentView.findViewById(R.id.reference);
        final EditText amountToPay = (EditText) paymentView.findViewById(R.id.amount);
        final Spinner tags = (Spinner) paymentView.findViewById(R.id.tag);

        // Setup spinner
        tags.setAdapter(new TagSpinnerAdapter(getActivity()));

        // Unbundle accounts
        args = getArguments();
        accounts = (List<BankAccount>) args.getSerializable(Constants.BUNDLE_KEY_ACCOUNT_LIST);

        // Populate Spinner with bank accounts
        fromAccount.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accounts));

        paymentView.findViewById(R.id.continuePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Not a number entered as account number.
                if(!toAccNo.getText().toString().matches("\\d+")){
                    toAccNo.setError(getString(R.string.err_acc_not_no));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Account number not eight digits long.
                if(toAccNo.getText().toString().length() != Constants.ACC_NO_SIZE){
                    toAccNo.setError(getString(R.string.err_acc_len_not_eight));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Not a number or dash entered as sortcode number.
                if(!toSortCode.getText().toString().matches("^[\\d-]*$")){
                    toSortCode.setError(getString(R.string.err_sc_not_no));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Sortcode number not six digits long.
                if(toSortCode.getText().toString().length() != Constants.SORT_CODE_NO_SIZE){
                    toSortCode.setError(getString(R.string.err_acc_not_six));
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
                args.putString(Constants.BUNDLE_KEY_TO_ACC_NO, toAccNo.getText().toString());
                args.putString(Constants.BUNDLE_KEY_TO_SORT_CODE, toSortCode.getText().toString());
                args.putLong(Constants.BUNDLE_KEY_AMOUNT, CurrencyMangler.sterlingStringToInteger(amountToPay.getText().toString()));
                args.putString(Constants.BUNDLE_KEY_REF, transactionReference.getText().toString());
                args.putSerializable(Constants.BUNDLE_KEY_TAG, Transaction.Tag.getTag(tags.getSelectedItemId()));
                args.putString(Constants.BUNDLE_KEY_TAG_STRING, (String) tags.getSelectedItem());

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
