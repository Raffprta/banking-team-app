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
public class PaymentSuccessfulFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View paymentSuccessView = inflater.inflate(R.layout.payment_success, container, false);

        TextView accountType = (TextView)paymentSuccessView.findViewById(R.id.accountTypeSuccess);
        TextView accountNoFrom = (TextView)paymentSuccessView.findViewById(R.id.accountNumberSuccess);
        TextView accountSortCodeFrom = (TextView)paymentSuccessView.findViewById(R.id.sortCodeSuccess);
        TextView accountName = (TextView)paymentSuccessView.findViewById(R.id.nameSuccess);
        TextView accountNoTo = (TextView)paymentSuccessView.findViewById(R.id.accountNumberToSuccess);
        TextView accountSortCodeTo = (TextView)paymentSuccessView.findViewById(R.id.sortCodeSuccess);
        TextView paymentAmount = (TextView)paymentSuccessView.findViewById(R.id.amountSuccess);

        // Set the values from the bundle, i.e. what the user entered in the previous fragment.
        // TODO: accountNoFrom and accountSortCodeFrom are taken from API.
        accountType.setText(this.getArguments().getString(getString(R.string.from_account_pay_bundle)));
        accountName.setText(this.getArguments().getString(getString(R.string.to_name_pay_bundle)));
        accountNoTo.setText(this.getArguments().getString(getString(R.string.acc_no_pay_bundle)));
        accountSortCodeTo.setText(this.getArguments().getString(getString(R.string.sort_code_pay_bundle)));
        paymentAmount.setText(Double.toString(this.getArguments().getDouble(getString(R.string.amount_pay_bundle))));


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
