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
public class PaymentSuccessfulFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View paymentSuccessView = inflater.inflate(R.layout.payment_success, container, false);


        paymentSuccessView.findViewById(R.id.returnToAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.title_section2)).commit();
            }
        });

        return paymentSuccessView;

    }

}