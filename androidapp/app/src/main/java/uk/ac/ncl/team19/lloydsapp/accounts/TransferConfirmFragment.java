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
 * @author Raffaello Perrotta, XML by Yessengerey Bolatov
 * @author Dale Whinham - backend integration
 */
public class TransferConfirmFragment extends Fragment {

    private Bundle args = getArguments();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View transferConfirmView = inflater.inflate(R.layout.transfer_confirm, container, false);

        TextView fromAccountName = (TextView) transferConfirmView.findViewById(R.id.fromAccount);
        TextView fromAccountNo = (TextView) transferConfirmView.findViewById(R.id.fromAccNo);
        TextView fromSortCode = (TextView) transferConfirmView.findViewById(R.id.fromSortCode);
        TextView toAccountName = (TextView) transferConfirmView.findViewById(R.id.toAccount);
        TextView toAccountNo = (TextView) transferConfirmView.findViewById(R.id.toAccNo);
        TextView toSortCode = (TextView) transferConfirmView.findViewById(R.id.toSortCode);
        TextView amount = (TextView) transferConfirmView.findViewById(R.id.amount);

        // Set values taken from bundle, TODO account numbers and sort code are from the API.

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

    @Override
    public String toString(){
        return getString(R.string.confirm_transfer_page);
    }

}
