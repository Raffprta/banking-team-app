package uk.ac.ncl.team19.lloydsapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta
 * @author Dale Whinham - minor alteration to simplify Bundle key access
 *
 * Dialog which shows methods for updating individual help items on the screen.
 *
 */
public class HelpMenuOverlayDialog extends DialogFragment {

    private Dialog builtDialog;
    private TextView helpText;
    private String text;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get message to display
        text = this.getArguments().getString(Constants.BUNDLE_KEY_HELP);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_Transparent);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.help_layout, null);
        builder.setView(dialogView);


        Button dismiss = (Button) dialogView.findViewById(R.id.dismissButton);
        helpText = (TextView) dialogView.findViewById(R.id.helpTextView);
        helpText.setText(text);

        // Set the listener to the dismiss Button for when it is clicked.
        dismiss.setOnTouchListener(new View.OnTouchListener() {
            // Effect code retrieved from: http://stackoverflow.com/questions/7175873/click-effect-on-button-in-android
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        GraphicsUtils.buttonClickEffectShow(v);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        GraphicsUtils.buttonClickEffectHide(v);
                        getDialog().dismiss();
                        break;
                    }
                }
                return false;
            }
        });

        // Create dialog and position it.
        builtDialog = builder.create();
        WindowManager.LayoutParams wmlp = builtDialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        // offset by our triangle height + a few pixels
        wmlp.y += 58;
        wmlp.x = 35;


        return builtDialog;
    }


}
