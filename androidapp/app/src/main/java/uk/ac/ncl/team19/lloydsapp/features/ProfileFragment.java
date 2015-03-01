package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.accounts.AccountsDashboardFragment;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov (XML Designs) and Raffaello Perrotta
 *
 * Profile page fragment, which contains hotlinks as well as all gamification aspects.
 */

public class ProfileFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View profileView = inflater.inflate(R.layout.profile_page, container, false);

        profileView.findViewById(R.id.accountDashBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new AccountsDashboardFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();

            }
        });

        profileView.findViewById(R.id.googlePlaySignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
            }
        });

        return profileView;

    }

}
