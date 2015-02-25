package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

        // TODO : Are we only having two accounts, if not then this needs to be more programmatic
        accountsDashboardView.findViewById(R.id.accountCardView1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        accountsDashboardView.findViewById(R.id.accountCardView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsInfoFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        return accountsDashboardView;

    }

}
