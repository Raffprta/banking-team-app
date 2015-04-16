package uk.ac.ncl.team19.lloydsapp.utils.general;

import android.graphics.PorterDuff;
import android.view.View;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * @author Raffaello Perrotta
 *
 * Static class to provide utility methods for drawing graphics.
 */

public class GraphicsUtils {

    public static void buttonClickEffectShow(View v){
        v.getBackground().setColorFilter(v.getResources().getColor(R.color.lloyds_orange), PorterDuff.Mode.SRC_ATOP);
        v.invalidate();
    }

    public static void buttonClickEffectHide(View v){
        v.getBackground().clearColorFilter();
        v.invalidate();
    }
}
