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
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov (XML Design), Raffaello Perrotta
 *
 * Fragment for making transfers to other personal accounts.
 */

public class TransferFundsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View transferFundsView = inflater.inflate(R.layout.transfer_funds_page, container, false);
        final EditText amountToPay = (EditText)transferFundsView.findViewById(R.id.amountToTransfer);
        final Spinner accountToTransferFrom = (Spinner)transferFundsView.findViewById(R.id.accountTransferFrom);
        final Spinner accountToTransferTo = (Spinner)transferFundsView.findViewById(R.id.accountTransferTo);

        transferFundsView.findViewById(R.id.continueTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

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

                // If all of the validation was performed successfully -- set a bundle as the fragment arguments
                // with our data.

                Bundle b = new Bundle();
                b.putDouble(getString(R.string.amount_transfer_bundle), Double.parseDouble(amountToPay.getText().toString()));
                b.putString(getString(R.string.from_account_transfer_bundle), accountToTransferFrom.getSelectedItem().toString());
                b.putString(getString(R.string.to_account_transfer_bundle), accountToTransferTo.getSelectedItem().toString());
                TransferConfirmFragment transferConfirmFragment = new TransferConfirmFragment();
                transferConfirmFragment.setArguments(b);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, transferConfirmFragment).commit();

            }
        });

        transferFundsView.findViewById(R.id.cancelTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return transferFundsView;

    }

    @Override
    public String toString(){
        return getString(R.string.transfer_funds_page);
    }

}
