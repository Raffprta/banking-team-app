package uk.ac.ncl.team19.lloydsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

/**
 * @author Raffaello Perrotta
 *
 * A dialog fragment that can be loaded and used with its respective layout
 * to allow the user to log off if he wishes to do so from the application.
 *
 */
public class LogOffDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.log_off, null));
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
