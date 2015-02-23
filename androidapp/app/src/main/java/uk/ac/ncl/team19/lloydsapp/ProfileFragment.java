package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return profileView;

    }

}
