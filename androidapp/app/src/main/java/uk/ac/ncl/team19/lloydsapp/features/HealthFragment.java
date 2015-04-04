package uk.ac.ncl.team19.lloydsapp.features;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrota, XML by Yessengerey Bolatov.
 * @author Dale Whinham - simplify Bundle key access
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

        // Determine what colour to set.
        if(hpBar.getProgress() >= Constants.HEALTH_GOOD){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar));
        }else if(hpBar.getProgress() >= Constants.HEALTH_AVG && hpBar.getProgress() < Constants.HEALTH_GOOD){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_medium));
        }else if(hpBar.getProgress() >= Constants.HEALTH_POOR && hpBar.getProgress() < Constants.HEALTH_AVG){
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_poor));
        }else{
            hpBar.setProgressDrawable(getResources().getDrawable(R.drawable.hpbar_dismal));
        }

        // Set the values of the goals.
        TextView perMonthOrPerWeek = (TextView)healthView.findViewById(R.id.perMonthOrPerWeek);
        TextView spendAmount = (TextView)healthView.findViewById(R.id.spendTextView);
        TextView saveAmount = (TextView)healthView.findViewById(R.id.saveTextView);
        TextView overdraft = (TextView)healthView.findViewById(R.id.overdraftTextView);

        // Donate views
        TextView donateStartView = (TextView)healthView.findViewById(R.id.donateStartView);
        TextView donateTextView = (TextView)healthView.findViewById(R.id.donateTextView);
        ProgressBar donateBar = (ProgressBar)healthView.findViewById(R.id.donateProgressBar);


        // Make shared preferences object
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // If we are not donating, hide all views.
        if(!sp.getBoolean(getString(R.string.sp_goals_donate), false)){
            donateStartView.setVisibility(View.GONE);
            donateTextView.setVisibility(View.GONE);
            donateBar.setVisibility(View.GONE);
        }

        // Populate other fields
        if(sp.getInt(getString(R.string.sp_goals_set_for), 1) == Constants.WEEKLY){
            perMonthOrPerWeek.setText(getString(R.string.weekly));
        }else if(sp.getInt(getString(R.string.sp_goals_set_for), 1) == Constants.MONTHLY){
            perMonthOrPerWeek.setText(getString(R.string.monthly));
        }

        spendAmount.setText(Float.toString(sp.getFloat(getString(R.string.sp_goals_spend), -1)));
        saveAmount.setText(Float.toString(sp.getFloat(getString(R.string.sp_goals_save), -1)));
        overdraft.setText(Float.toString(sp.getFloat(getString(R.string.sp_goals_overdraft), -1)));


        // Show the percentage points of the current health
        hpBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.your_prog) + " " + Integer.toString(hpBar.getProgress()) + "%");
                CustomDialog custom = new CustomDialog();
                custom.setArguments(b);
                custom.show(getChildFragmentManager(), "Custom Dialog");
            }
        });

        healthView.findViewById(R.id.backToProfileFromHealth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new ProfileFragment()).addToBackStack(getString(R.string.account_health_page)).commit();
            }
        });

        healthView.findViewById(R.id.setGoals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new SetGoalsFragment()).addToBackStack(getString(R.string.account_health_page)).commit();
            }
        });

        return healthView;

    }

    @Override
    public String toString(){
        return getString(R.string.account_health_page);
    }

}
