package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

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
        feedbackSubmitView.findViewById(R.id.backToProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new ProfileFragment()).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });
        return feedbackSubmitView;

    }

}