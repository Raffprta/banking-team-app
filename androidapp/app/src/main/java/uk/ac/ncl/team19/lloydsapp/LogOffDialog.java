package uk.ac.ncl.team19.lloydsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * @author Raffaello Perrotta
 *
 * A dialog fragment that can be loaded and used with its respective layout
 * to allow the user to log off if he wishes to do so from the application.
 *
 */
public class LogOffDialog extends DialogFragment implements View.OnClickListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.log_off, null));

        // Set up buttons
        final Button yesButton = (Button) inflater.inflate(R.layout.log_off, null).findViewById(R.id.yesButton);
        final Button noButton = (Button) inflater.inflate(R.layout.log_off, null).findViewById(R.id.noButton);

        yesButton.setOnClickListener(this);

        noButton.setOnClickListener(this);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(View v) {

        Log.i("Test", "TestClickOut");

        switch(v.getId()){
            case R.id.yesButton:
                break;
            case R.id.noButton:
                Log.i("Test", "Test");
                // No was pressed, cancel the action.
                LogOffDialog.this.getDialog().cancel();
        }
    }


}
