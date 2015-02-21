package uk.ac.ncl.team19.lloydsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Raffaello Perrotta
 *
 * A fragment to be loaded in once the Feedback has been submitted. This fragment just contains a view
 * showing some feedback to the user that their feedback was indeed successfully submitted.
 *
 */

public class FeedbackSubmittedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View feedbackSubmitView = inflater.inflate(R.layout.fragment_feedback_submitted, container, false);
        return feedbackSubmitView;

    }

}