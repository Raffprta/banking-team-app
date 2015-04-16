package uk.ac.ncl.team19.lloydsapp.features;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrota
 * Full implementation of client-side and server-side integration. Algorithms for setting the data and
 * passing them to the Health monitoring classes.
 * @author Yessengerey Bolatov
 * All XML Designs.
 * @author Dale Whinham
 * Simplify Bundle key access
 *
 * A class to provide to the user to set their goals. Goals an be how much a user spends, saves, donates as well as
 * how much overdraft they can use, if any, in a set period. Goals are automatically reset after the period.
 */
public class SetGoalsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View goalsView = inflater.inflate(R.layout.set_goals_page, container, false);

        Bundle bundle = getArguments();

        // If this page was loaded due to goals expiry, then check the bundle.
        if(bundle != null){
            if(bundle.getBoolean(Constants.BUNDLE_KEY_GOALS_EXPIRY)){
                Bundle b = new Bundle();
                b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.goals_expired));
                CustomDialog custom = new CustomDialog();
                custom.setArguments(b);
                custom.show(getChildFragmentManager());
            }
        }

        // Get all respective fields
        final Spinner goalsSpinner = (Spinner)goalsView.findViewById(R.id.timeSpinner);
        final EditText spendAmount = (EditText)goalsView.findViewById(R.id.spendAmount);
        final EditText saveAmount = (EditText)goalsView.findViewById(R.id.saveAmount);
        final EditText overdraftAmount = (EditText)goalsView.findViewById(R.id.overdraftAmount);
        final RadioGroup donateGroup = (RadioGroup)goalsView.findViewById(R.id.setGoalsGroup);
        final RadioButton yesButton = (RadioButton)goalsView.findViewById(R.id.setGoalsYesCheckBox);
        final RadioButton noButton = (RadioButton)goalsView.findViewById(R.id.setGoalsNoCheckBox);
        final Button goalsButton = (Button) goalsView.findViewById(R.id.setGoalsButton);

        // Get shared preferences
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // If goals are already set then disable all editable views until reset button clicked.
        if(sp.getBoolean(Constants.SP_GOALS_SET, false)){
            // Fixes android bug on views
            goalsSpinner.setEnabled(false);
            goalsSpinner.setClickable(false);
            goalsSpinner.setFocusable(false);
            spendAmount.setFocusable(false);
            saveAmount.setFocusable(false);
            overdraftAmount.setFocusable(false);
            yesButton.setEnabled(false);
            yesButton.setClickable(false);
            yesButton.setFocusable(false);
            noButton.setEnabled(false);
            noButton.setClickable(false);
            noButton.setFocusable(false);
            goalsButton.setEnabled(false);
            goalsButton.setClickable(false);
            goalsButton.setFocusable(false);
        }

        // On Clicking the cancel button, return to the previous page.
        goalsView.findViewById(R.id.cancelGoalsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Set the goals
        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Validate spend amount
                // Check if GBP decimal or otherwise is entered
                if (!spendAmount.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")) {
                    spendAmount.setError(getString(R.string.err_health_spend_format));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check to see if the user entered a value that is too low
                if (Double.parseDouble(spendAmount.getText().toString()) < Constants.MIN_SPEND) {
                    spendAmount.setError(getString(R.string.err_health_spend_min) + Double.toString(Constants.MIN_SPEND));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Validate save amount
                // Check if GBP decimal or otherwise is entered
                if (!saveAmount.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")) {
                    saveAmount.setError(getString(R.string.err_health_save_format));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check to see if the user entered a value that is too low
                if (Double.parseDouble(saveAmount.getText().toString()) < Constants.MIN_SAVE) {
                    saveAmount.setError(getString(R.string.err_health_save_min) + Double.toString(Constants.MIN_SAVE));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Validate overdraft amount
                // Check if GBP decimal or otherwise is entered
                if (!overdraftAmount.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")) {
                    overdraftAmount.setError(getString(R.string.err_health_od_format));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check to see if the user entered a value that is too low
                if (Double.parseDouble(overdraftAmount.getText().toString()) < Constants.MIN_OVERDRAFT) {
                    overdraftAmount.setError(getString(R.string.err_health_od_min) + Double.toString(Constants.MIN_OVERDRAFT));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Validate if the user has selected any of the radio buttons.
                int radioButtonID = donateGroup.getCheckedRadioButtonId();

                // If no view is found, then the user did not select anything.
                if (radioButtonID == -1) {
                    // Use dialog to display the message over setError as setError is graphically unappealing
                    // on radio buttons
                    Bundle b = new Bundle();
                    b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.err_no_selection));
                    b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
                    CustomDialog custom = new CustomDialog();
                    custom.setArguments(b);
                    custom.show(getChildFragmentManager(), "Custom Dialog");
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }


                // If the input was correct - set that the goals were indeed set
                sp.edit().putBoolean(Constants.SP_GOALS_SET, true).apply();

                // Set the date starting from.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String dateSet = sdf.format(new Date());
                sp.edit().putString(Constants.SP_GOALS_START, dateSet).apply();

                // Set whether it is weekly or monthly.
                int weeklyOrMonthly = goalsSpinner.getSelectedItemPosition() + 1;
                sp.edit().putInt(Constants.SP_GOALS_SET_FOR, weeklyOrMonthly).apply();

                // Set spend amount
                sp.edit().putFloat(Constants.SP_GOALS_SPEND, Float.parseFloat(spendAmount.getText().toString())).apply();

                // Set save amount
                sp.edit().putFloat(Constants.SP_GOALS_SAVE, Float.parseFloat(saveAmount.getText().toString())).apply();

                // Set overdraft amount
                sp.edit().putFloat(Constants.SP_GOALS_OVERDRAFT, Float.parseFloat(overdraftAmount.getText().toString())).apply();

                // Set whether to donate or not
                if (radioButtonID == R.id.setGoalsYesCheckBox)
                    sp.edit().putBoolean(Constants.SP_GOALS_DONATE, true).apply();
                else
                    sp.edit().putBoolean(Constants.SP_GOALS_DONATE, false).apply();

                // Various debug methods
                Log.i("Debug Is the goal set?", Boolean.toString(sp.getBoolean(Constants.SP_GOALS_SET, false)));
                Log.i("Debug date", sp.getString(Constants.SP_GOALS_START, null));
                Log.i("Debug weeklyOrMonthly", Integer.toString(sp.getInt(Constants.SP_GOALS_SET_FOR, -1)));
                Log.i("Debug save amount", Float.toString(sp.getFloat(Constants.SP_GOALS_SAVE, -1)));
                Log.i("Debug spend amount", Float.toString(sp.getFloat(Constants.SP_GOALS_SPEND, -1)));
                Log.i("Debug overdraft amount", Float.toString(sp.getFloat(Constants.SP_GOALS_OVERDRAFT, -1)));
                Log.i("Debug are we donating?", Boolean.toString(sp.getBoolean(Constants.SP_GOALS_DONATE, false)));

                // Make a new fragment transaction to go to the health page.
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new HealthFragment()).addToBackStack(getString(R.string.account_health_page)).commit();

                GraphicsUtils.buttonClickEffectHide(v);


            }
        });

        // Reset the goals
        goalsView.findViewById(R.id.resetGoalsButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sp.edit().putBoolean(Constants.SP_GOALS_SET, false).apply();

                // Reload page via fragment transaction
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.detach(SetGoalsFragment.this);
                fragmentTransaction.attach(SetGoalsFragment.this);
                fragmentTransaction.commit();
            }

        });

        return goalsView;

    }

    @Override
    public String toString(){
        return getString(R.string.set_goals_page);
    }

}
