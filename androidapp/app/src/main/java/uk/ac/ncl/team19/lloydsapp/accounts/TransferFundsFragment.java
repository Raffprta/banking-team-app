package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        transferFundsView.findViewById(R.id.continueTransfer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new TransferConfirmFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
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

}