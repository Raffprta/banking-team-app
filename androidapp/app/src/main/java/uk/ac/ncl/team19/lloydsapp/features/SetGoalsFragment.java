package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * @author Raffaello Perrota, XML designs by Yessengerey Bolatov
 */
public class SetGoalsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View goalsView = inflater.inflate(R.layout.set_goals_page, container, false);
        return goalsView;

    }

}
