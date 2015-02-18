package uk.ac.ncl.team19.lloydsapp;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * @Author Raffaello Perrotta
 *
 * A fragment which will provide the user an opportunity to leave feedback which is then
 * sent to an administrator email address to be reviewed. The email process is done within the background.
 */
public class FeedbackFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View feedbackView = inflater.inflate(R.layout.feedback_layout, container, false);

        // Find all input views.
        final Button feedbackSubmitButton = (Button) feedbackView.findViewById(R.id.submitFeedbackButton);
        final EditText feedbackEditText = (EditText) feedbackView.findViewById(R.id.feedbackEditText);
        final RatingBar feedbackRating = (RatingBar) feedbackView.findViewById(R.id.ratingBar);

        // Set a listener for when submit is pressed.
        feedbackSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UI Effects
                feedbackSubmitButton.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                feedbackSubmitButton.invalidate();
                // Get the rating the user submitted and the feeedback.
                float rating = feedbackRating.getRating();
                String feedback = feedbackEditText.getText().toString();
                // Get configured email to send the message to
                String email = getString(R.string.team_email);

                // Check if any data was entered - if there wasn't give an error message.
                if(rating <= 0.0f || feedback.toString().length() == 0){
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_form), Toast.LENGTH_SHORT).show();
                    return;
                }





            }
        });

        return feedbackView;

    }



}
