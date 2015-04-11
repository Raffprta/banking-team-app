package uk.ac.ncl.team19.lloydsapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta - a custom dialog to be used to send text to the
 * application.
 * @author Dale Whinham - simplify show() and bundle key usage
 *
 * Use within a fragment:
 *
 *  Bundle b = new Bundle();
    b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, "The message for this dialog");
    b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true));
    CustomDialog custom = new CustomDialog();
    custom.setArguments(b);
    custom.show(getChildFragmentManager());
 */
public class CustomDialog extends android.support.v4.app.DialogFragment{
    public void show(FragmentManager manager) {
        show(manager, "Custom Dialog");
    }

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
        String text = this.getArguments().getString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE);
        ((TextView)v.findViewById(R.id.customText)).setText(text);

        // If specified, set the colour as red if it's an error message, otherwise the default is green.
        Boolean isError = this.getArguments().getBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, false);
        if(isError)
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

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity != null && activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

}
