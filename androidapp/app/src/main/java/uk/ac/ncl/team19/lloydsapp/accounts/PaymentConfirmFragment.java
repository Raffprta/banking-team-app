package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Ivy Tong (XML), Raffaello Perrotta
 */
public class PaymentConfirmFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View paymentConfirmView = inflater.inflate(R.layout.payment_confirm, container, false);

        TextView accountType = (TextView)paymentConfirmView.findViewById(R.id.accountTypeConfirm);
        TextView accountNoFrom = (TextView)paymentConfirmView.findViewById(R.id.accountNoConfirm);
        TextView accountSortCodeFrom = (TextView)paymentConfirmView.findViewById(R.id.sortCodeConfirm);
        TextView accountName = (TextView)paymentConfirmView.findViewById(R.id.nameToConfirm);
        TextView accountNoTo = (TextView)paymentConfirmView.findViewById(R.id.accountNoToConfirm);
        TextView accountSortCodeTo = (TextView)paymentConfirmView.findViewById(R.id.sortCodeToConfirm);
        TextView paymentAmount = (TextView)paymentConfirmView.findViewById(R.id.amountConfirm);

        // Set the values from the bundle, i.e. what the user entered in the previous fragment.
        // TODO: accountNoFrom and accountSortCodeFrom are taken from API.
        accountType.setText(this.getArguments().getString(getString(R.string.from_account_pay_bundle)));
        accountName.setText(this.getArguments().getString(getString(R.string.to_name_pay_bundle)));
        accountNoTo.setText(this.getArguments().getString(getString(R.string.acc_no_pay_bundle)));
        accountSortCodeTo.setText(this.getArguments().getString(getString(R.string.sort_code_pay_bundle)));
        paymentAmount.setText(Double.toString(this.getArguments().getDouble(getString(R.string.amount_pay_bundle))));

        // On Clicking the Confirm button
        paymentConfirmView.findViewById(R.id.confirmPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                PaymentSuccessfulFragment paymentsSuccess = new PaymentSuccessfulFragment();
                // Pass the bundle arguments to the next fragment.
                paymentsSuccess.setArguments(getArguments());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, paymentsSuccess).commit();
            }
        });
        // On Clicking the cancel button, return to the previous page.
        paymentConfirmView.findViewById(R.id.cancelPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return paymentConfirmView;

    }

}
