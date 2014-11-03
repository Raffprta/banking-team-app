package raffprta.eu.achievementsexample;

import android.app.Activity;
import android.os.Bundle;

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

    @Override
    protected void onStop() {
        super.onStop();
    }

}
