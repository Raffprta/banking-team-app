package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View paymentView = inflater.inflate(R.layout.make_a_payment, container, false);
        return paymentView;

    }

}
