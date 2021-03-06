package uk.ac.ncl.team19.lloydsapp.features;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;
import uk.ac.ncl.team19.lloydsapp.utils.general.MailHelper;

/**
 * @author Raffaello Perrotta
 * Create all email processing code as well as fragment set up and related async. tasks.
 * @author Dale Whinham
 * Simplify Bundle key access
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
                GraphicsUtils.buttonClickEffectShow(v);
                // Get the rating the user submitted and the feeedback.
                float rating = feedbackRating.getRating();
                String feedback = feedbackEditText.getText().toString();

                // Check if any data was entered - if there wasn't give an error message.
                if(rating <= 0.0f || feedback.length() == 0){
                    // Show error as custom dialog.
                    Bundle b = new Bundle();
                    b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.error_form));
                    b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
                    CustomDialog custom = new CustomDialog();
                    custom.setArguments(b);
                    custom.show(getChildFragmentManager(), "Custom Dialog");
                    // Hide effects.
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Show progress bar and execute the async. email task.
                ProgressDialog.showLoading(FeedbackFragment.this);
                (new EmailTask()).execute(getString(R.string.email_subject),
                        getString(R.string.rating) + "\n" + rating + "\n\n" +
                                getString(R.string.feedback) + "\n" + feedback);



            }
        });

        return feedbackView;

    }

    /**
     * @author Raffaello Perrotta
     *
     * A private class which performs an async. task which sets up a Mailer as a daemon which then sends the
     * feedback email to a pre-configured email client accessible via the Installation guide to view the feedback.
     */
    private class EmailTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            // Attempt to send email
            MailHelper mailer = new MailHelper(getString(R.string.team_email),
                    getString(R.string.team_email), params[0], params[1], FeedbackFragment.this);
            try {
                // Send the email itself.
                mailer.sendAuthenticated();
            } catch (Exception e) {
                // An error occurred
                Toast.makeText(FeedbackFragment.this.getActivity().getApplicationContext(), getString(R.string.error_email), Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // Remove the loading dialog.
            ProgressDialog.removeLoading(FeedbackFragment.this);
            // Load new Fragment in place telling the user the message was submitted.
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new FeedbackSubmittedFragment()).commit();

        }
    }

    @Override
    public String toString(){
        return getString(R.string.feedback_page);
    }



}
