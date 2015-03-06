package uk.ac.ncl.team19.lloydsapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta - a custom dialog to be used to send text to the
 * application.
 *
 * Use within a fragment:
 *
 *  Bundle b = new Bundle();
    b.putString(getString(R.string.custom_bundle), "The message for this dialog");
    b.putString(getString(R.string.custom_type_bundle), getString(R.string.custom_colour_type_red));
    CustomDialog custom = new CustomDialog();
    custom.setArguments(b);
    custom.show(getChildFragmentManager(), "Custom Dialog");
 */
public class CustomDialog extends android.support.v4.app.DialogFragment{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(v);

        // Set the text of the information box
        String text = this.getArguments().getString(getString(R.string.custom_bundle));
        ((TextView)v.findViewById(R.id.customText)).setText(text);

        // If specified, set the colour as red if it's an error message, otherwise the default is green.
        String type = this.getArguments().getString(getString(R.string.custom_type_bundle));
        if(type != null)
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));


        Button dismissButton = (Button) v.findViewById(R.id.customDismissButton);

        // Set the listener to the Button for when it is clicked.
        dismissButton.setOnTouchListener(new View.OnTouchListener() {
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

        // Create the AlertDialog object and return it
        return builder.create();
    }

}