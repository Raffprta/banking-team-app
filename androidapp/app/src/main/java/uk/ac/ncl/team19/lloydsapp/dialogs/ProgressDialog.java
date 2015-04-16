package uk.ac.ncl.team19.lloydsapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * @author Raffaello Perrotta
 *
 * A dialog fragment that can be loaded and used globally throughout the application
 * which allows the application to inform that something is loading interdetminately
 * Usage:
 *          ProgressDialog.showLoading(fragment)
 *              Shows the progress bar spinning.
 *          ProgressDialog.removeLoading(fragment)
 *              Removes the progress bar spinning.
 *
 */
public class ProgressDialog extends DialogFragment {

    // Instance variables
    private static final ProgressDialog instance = new ProgressDialog();
    private static final String TAG = "Loading";
    private static boolean shown = false;

    /**
     * Method to show the indeterminate loading dialog.
     * @param frag The fragment you want to show the ProgressDialog to.
     */
    public static void showLoading(Fragment frag){
        if(!shown)
            frag.getActivity().getSupportFragmentManager().beginTransaction().add(instance,
                TAG).commit();
        else
            return;

        shown = true;
    }

    /**
     * Mirror progress bar as above for FragmentActivities
     * @param activity the activity to display the progress bar to.
     */
    public static void showLoading(FragmentActivity activity){
        if(!shown)
            activity.getSupportFragmentManager().beginTransaction().add(instance,
                    TAG).commit();
        else
            return;

        shown = true;
    }

    /**
     * Method to remove the ProgressBar.
     * @param frag The fragment you want to show the ProgressDialog to.
     */
    public static void removeLoading(Fragment frag){

        if(shown)
            frag.getActivity().getSupportFragmentManager().beginTransaction().remove(instance).commit();
        else
            return;

        shown = false;

    }

    /**
     * As above. Method to remove the ProgressBar though for fragmentactivities.
     * @param activity The activity you want to show the ProgressDialog to.
     */
    public static void removeLoading(FragmentActivity activity){

        if(shown)
            activity.getSupportFragmentManager().beginTransaction().remove(instance).commit();
        else
            return;

        shown = false;

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(v);
        // Create the AlertDialog object and return it
        AlertDialog dialog =  builder.create();
        // Prevent the user from cancelling the dialog.
        dialog.setCanceledOnTouchOutside(false);
        // Idem for the back button
        dialog.setCancelable(false);
        return dialog;
    }



}

