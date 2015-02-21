package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author XML by Yessengerey Bolatov, conversion into Fragment by Raffaello Perrotta
 */
public class AccountsInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View accountsInfoView = inflater.inflate(R.layout.account_information, container, false);
        return accountsInfoView;

    }

}