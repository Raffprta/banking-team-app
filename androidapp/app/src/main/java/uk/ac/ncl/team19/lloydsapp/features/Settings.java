package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.PreferenceFragment;

/**
 * @author Raffaello Perrotta
 *
 * A lass to represent user settings which are used to determine certain
 * preferences both on the backend and on the phone.
 */
public class Settings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public String toString(){
        return getString(R.string.settings);
    }

}
