package raffprta.eu.achievementsexample;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * @author Raffaello Perrotta
 * Example GestureListener that can be called (after construction) within a
 * View's TouchListener. like in this example code:
    view.setOnTouchListener(new OnTouchListener{
        public boolean onTouchEvent(MotionEvent evt) {
            return theListener.onTouchEvent(evt);
        }
    })
 */

public class GestureListener implements GestureDetector.OnGestureListener{

    Activity activity;
    Intent i;

    public GestureListener(Activity activity, Intent i){
        this.activity = activity;
        this.i = i;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.w("Info: ", "You pressed down!");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.w("Info:", "That was a long down press!");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.w("Info:", "You swiped the screen!");
        // Do something such as: activity.startActivity(i);
        return false;
    }


}
