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
 * @Author Raffaello Perrotta, XML by Yessengerey Bolatov
 */
public class TransferSuccessfulFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View transferSuccessView = inflater.inflate(R.layout.transfer_success, container, false);

        TextView accountTypeFrom = (TextView)transferSuccessView.findViewById(R.id.transferAccountTypeSuccessFrom);
        TextView accountNoFrom = (TextView)transferSuccessView.findViewById(R.id.transferAccountNoFrom);
        TextView accountSortCodeFrom = (TextView)transferSuccessView.findViewById(R.id.transferSortcodeFrom);
        TextView accountTypeTo = (TextView)transferSuccessView.findViewById(R.id.transferAccountTypeSuccessTo);
        TextView accountNoTo = (TextView)transferSuccessView.findViewById(R.id.transferAccountNoTo);
        TextView accountSortCodeTo = (TextView)transferSuccessView.findViewById(R.id.transferSortcodeTo);
        TextView paymentAmount = (TextView)transferSuccessView.findViewById(R.id.transferPaymentSuccess);

        // Set values taken from bundle, TODO account numbers and sort code are from the API.
        accountTypeFrom.setText(this.getArguments().getString(getString(R.string.from_account_transfer_bundle)));
        accountTypeTo.setText(this.getArguments().getString(getString(R.string.to_account_transfer_bundle)));
        paymentAmount.setText(Double.toString(this.getArguments().getDouble(getString(R.string.amount_transfer_bundle))));

        transferSuccessView.findViewById(R.id.backToAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        return transferSuccessView;

    }

    @Override
    public String toString(){
        return getString(R.string.transfer_successful_page);
    }

}
