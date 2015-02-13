package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Author Raffaello Perrotta
 *
 * A fragment which will provide the user an opportunity to leave feedback which is then
 * sent to an administrator email address to be reviewed.
 */
public class FeedbackFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View pushView = inflater.inflate(R.layout.feedback_layout, container, false);
        return pushView;

    }

}
