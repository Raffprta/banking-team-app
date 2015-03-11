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
public class TransferConfirmFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View transferConfirmView = inflater.inflate(R.layout.transfer_confirm, container, false);

        TextView accountTypeFrom = (TextView)transferConfirmView.findViewById(R.id.accountTransTypeFrom);
        TextView accountNoFrom = (TextView)transferConfirmView.findViewById(R.id.accountNoTransFrom);
        TextView accountSortCodeFrom = (TextView)transferConfirmView.findViewById(R.id.accountSortcodeTransFrom);
        TextView accountTypeTo = (TextView)transferConfirmView.findViewById(R.id.accountTypeTransTo);
        TextView accountNoTo = (TextView)transferConfirmView.findViewById(R.id.accountNoTransTo);
        TextView accountSortCodeTo = (TextView)transferConfirmView.findViewById(R.id.accountSortcodeTransTo);
        TextView paymentAmount = (TextView)transferConfirmView.findViewById(R.id.amountTrans);

        // Set values taken from bundle, TODO account numbers and sort code are from the API.
        accountTypeFrom.setText(this.getArguments().getString(getString(R.string.from_account_transfer_bundle)));
        accountTypeTo.setText(this.getArguments().getString(getString(R.string.to_account_transfer_bundle)));
        paymentAmount.setText(Double.toString(this.getArguments().getDouble(getString(R.string.amount_transfer_bundle))));

        transferConfirmView.findViewById(R.id.confirmTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TransferSuccessfulFragment transferSuccessfulFragment = new TransferSuccessfulFragment();
                transferSuccessfulFragment.setArguments(getArguments());
                fragmentManager.beginTransaction().replace(R.id.container, transferSuccessfulFragment).commit();
            }
        });

        transferConfirmView.findViewById(R.id.cancelTransferInConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return transferConfirmView;

    }

}
