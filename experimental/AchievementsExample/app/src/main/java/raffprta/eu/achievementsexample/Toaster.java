package raffprta.eu.achievementsexample;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Raffaello Perrotta
 * Adapted from: http://www.technotalkative.com/android-custom-toast-notification/
 * Creates a custom view for an Android toast using composition. A user may create
 * an icon displayed to the left of the toast, with text to the right. This is perfect
 * for iconified messages like achievements unlockables!
 */
public class Toaster{

    private Toast toast;
    private ImageView img;
    private Activity a;
    private TextView text;

    /***
     * Displays an iconised image to the left of the custom toast.
     * @param a the activity reference where the toast is displayed
     */
    public Toaster(Activity a){
        this.a = a;
        // Sets an inflater to inflate in respect to the activity that calls it. Sets the layout to the iconified view.
        LayoutInflater inflater = a.getLayoutInflater();
        View view = inflater.inflate(R.layout.iconised_layout, (ViewGroup) a.findViewById(R.id.toastLayout));
        // Get the references to our imageview and text view.
        this.img = (ImageView) view.findViewById(R.id.toastIcon);
        this.text = (TextView) view.findViewById(R.id.toastText);
        // Get a toast and set it a custom view.
        this.toast = new Toast(a);
        toast.setView(view);
    }

    /**
     * @param text, the text you want to display
     * @param imageResource the image resource, i.e. from R.drawable
     */
    public void grabToast(String text, int imageResource){
        this.text.setText(text);
        img.setImageResource(imageResource);
        toast.show();
    }

}
