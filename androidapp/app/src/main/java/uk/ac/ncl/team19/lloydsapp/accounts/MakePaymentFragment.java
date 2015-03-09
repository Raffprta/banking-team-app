package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov (XML Design), Raffaello Perrotta
 *
 * Fragment for making payments to other accounts.
 */

public class MakePaymentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View paymentView = inflater.inflate(R.layout.make_a_payment, container, false);

        // Get all views containing information.
        final Spinner fromAccount = (Spinner)paymentView.findViewById(R.id.accountsSpinner);
        final EditText toAccountPersonName = (EditText)paymentView.findViewById(R.id.toName);
        final EditText accountNumber = (EditText)paymentView.findViewById(R.id.accountNumberPayment);
        final EditText sortCode = (EditText)paymentView.findViewById(R.id.sortCodePayment);
        final EditText amountToPay = (EditText)paymentView.findViewById(R.id.amountPayment);


        paymentView.findViewById(R.id.continuePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Check validation conditions.
                if(toAccountPersonName.getText().toString() == null || toAccountPersonName.getText().toString().length() <= 0){
                    toAccountPersonName.setError(getString(R.string.err_acc_name_empty));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

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

                // If all validation conditions passed, then set a Bundle of information to pass.
                Bundle b = new Bundle();
                b.putDouble(getString(R.string.amount_pay_bundle), Double.parseDouble(amountToPay.getText().toString()));
                b.putString(getString(R.string.to_name_pay_bundle), toAccountPersonName.getText().toString());
                b.putString(getString(R.string.sort_code_pay_bundle), sortCode.getText().toString());
                b.putString(getString(R.string.acc_no_pay_bundle), accountNumber.getText().toString());
                b.putString(getString(R.string.from_account_pay_bundle), fromAccount.getSelectedItem().toString());

                PaymentConfirmFragment paymentConfirmation = new PaymentConfirmFragment();
                paymentConfirmation.setArguments(b);

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

}
