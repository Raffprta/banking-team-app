package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;

/**
 * @author Raffaello Perrota, XML by Yessengerey Bolatov.
 *
 * A class which will hold all information pertaining to the account health status and allow the user
 * a way of interacting with and chosing what goals to set per month.
 */
public class HealthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View healthView = inflater.inflate(R.layout.account_health_page, container, false);

        // Get the Health Bar.
        final ProgressBar hpBar = (ProgressBar) healthView.findViewById(R.id.progressBar);

        if(hpBar.getProgress() >= 75){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar));
        }else if(hpBar.getProgress() >= 50 && hpBar.getProgress() < 75){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_medium));
        }else if(hpBar.getProgress() >= 25 && hpBar.getProgress() < 50){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_poor));
        }else{
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_dysmal));
        }

        // Show the percentage points of the current health
        hpBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(getString(R.string.custom_bundle), getString(R.string.your_prog) + " " + Integer.toString(hpBar.getProgress()) + "%");
                CustomDialog custom = new CustomDialog();
                custom.setArguments(b);
                custom.show(getChildFragmentManager(), "Custom Dialog");
            }
        });

        return healthView;

    }

}
