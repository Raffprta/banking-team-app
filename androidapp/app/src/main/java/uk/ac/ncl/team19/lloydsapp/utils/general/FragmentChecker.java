package uk.ac.ncl.team19.lloydsapp.utils.general;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * @author Raffaello Perrotta
 *
 * A general purpose class to compare if a fragment you are currently located on is equivalent to the
 * class that the method is executing.
 */
public class FragmentChecker {

    /**
     * Method to check if you are on the same fragment that you made a callback from.
     * This is useful to cancel requests in the eventuality that a user navigates away from the fragment in mid-request.
     *
     * @param fm The fragment manager of the activity.
     * @param cl The class itself you are calling it from as a fragment instance.
     *
     * @return boolean representing whether you are in the same class or not. True if you are, false if not.
     */
    public static boolean checkFragment(FragmentManager fm, Fragment cl){
        // Immediately check if you are actually on the right fragment.
        // Find currently loaded fragment.
        Fragment entry = fm.findFragmentById(R.id.container);

        // If you are not on the right fragment return false. This is based on references.
        return cl == entry;

    }

}
