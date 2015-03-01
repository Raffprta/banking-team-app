package uk.ac.ncl.team19.lloydsapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta
 *
 * A dialog fragment that can be loaded and used with its respective layout
 * to allow the user to log off if he wishes to do so from the application.
 *
 */
public class LogOffDialog extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.log_off, null);
        builder.setView(v);

        Button yesButton = (Button) v.findViewById(R.id.yesButton);
        Button noButton = (Button) v.findViewById(R.id.noButton);

        // Set the listener to the Button for when it is clicked.
        yesButton.setOnTouchListener(new View.OnTouchListener() {
            // Effet code retrieved from: http://stackoverflow.com/questions/7175873/click-effect-on-button-in-android
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

        // Set the listener to the Button for when it is clicked.
        noButton.setOnTouchListener(new View.OnTouchListener() {
            // Effect code retrieved from: http://stackoverflow.com/questions/7175873/click-effect-on-button-in-android
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        getDialog().dismiss();
                        break;
                    }
                }
                return false;
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }


}
