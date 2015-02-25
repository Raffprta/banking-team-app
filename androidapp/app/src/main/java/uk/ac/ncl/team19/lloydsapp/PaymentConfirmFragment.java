package uk.ac.ncl.team19.lloydsapp;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Ivy Tong (XML), Raffaello Perrotta
 */
public class PaymentConfirmFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View paymentConfirmView = inflater.inflate(R.layout.payment_confirm, container, false);
        // On Clicking the Confirm button
        paymentConfirmView.findViewById(R.id.confirmPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new PaymentSuccessfulFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });
        // On Clicking the cancel button, return to the previous page.
        paymentConfirmView.findViewById(R.id.cancelPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return paymentConfirmView;

    }

}
