package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Author Yao Tong, conversion to Fragment by Raffaello Perrotta
 */
public class AccountsDashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View accountsDashboardView = inflater.inflate(R.layout.account_dashboard_page, container, false);
        return accountsDashboardView;

    }

}
