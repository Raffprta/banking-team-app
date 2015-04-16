package uk.ac.ncl.team19.lloydsapp.utils.notifications;

import android.app.Activity;
import android.os.Bundle;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * @author Raffaello Perrotta
 * A class that specifies an activity for wearable devices to load notifications through it.
 */
public class WearableActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iconised_layout);
    }

}
