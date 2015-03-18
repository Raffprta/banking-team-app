package uk.ac.ncl.team19.lloydsapp.features;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrota, XML designs by Yessengerey Bolatov
 */
public class SetGoalsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final View goalsView = inflater.inflate(R.layout.set_goals_page, container, false);

        // Get all respective fields
        Spinner goalsSpinner = (Spinner)goalsView.findViewById(R.id.timeSpinner);
        final EditText spendAmount = (EditText)goalsView.findViewById(R.id.spendAmount);
        final EditText saveAmount = (EditText)goalsView.findViewById(R.id.saveAmount);
        final EditText overdraftAmount = (EditText)goalsView.findViewById(R.id.overdraftAmount);
        final RadioGroup donateGroup = (RadioGroup)goalsView.findViewById(R.id.setGoalsGroup);

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
        goalsView.findViewById(R.id.setGoalsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Validate spend amount
                // Check if GBP decimal or otherwise is entered
                if(!spendAmount.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")){
                    spendAmount.setError(getString(R.string.err_health_spend_format));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check to see if the user entered a value that is too low
                if(Double.parseDouble(spendAmount.getText().toString()) < Constants.MIN_SPEND){
                    spendAmount.setError(getString(R.string.err_health_spend_min) + Double.toString(Constants.MIN_SPEND));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Validate save amount
                // Check if GBP decimal or otherwise is entered
                if(!saveAmount.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")){
                    saveAmount.setError(getString(R.string.err_health_save_format));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check to see if the user entered a value that is too low
                if(Double.parseDouble(saveAmount.getText().toString()) < Constants.MIN_SAVE){
                    saveAmount.setError(getString(R.string.err_health_save_min) + Double.toString(Constants.MIN_SAVE));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Validate overdraft amount
                // Check if GBP decimal or otherwise is entered
                if(!overdraftAmount.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")){
                    overdraftAmount.setError(getString(R.string.err_health_od_format));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Check to see if the user entered a value that is too low
                if(Double.parseDouble(overdraftAmount.getText().toString()) < Constants.MIN_OVERDRAFT){
                    overdraftAmount.setError(getString(R.string.err_health_od_min) + Double.toString(Constants.MIN_OVERDRAFT));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Validate if the user has selected any of the radio buttons.
                int radioButtonID = donateGroup.getCheckedRadioButtonId();

                // If no view is found, then the user did not select anything.
                if(radioButtonID == -1){
                    // Use dialog to display the message over setError as setError is graphically unappealing
                    // on radio buttons
                    Bundle b = new Bundle();
                    b.putString(getString(R.string.custom_bundle), getString(R.string.err_no_selection));
                    b.putString(getString(R.string.custom_type_bundle), getString(R.string.custom_colour_type_red));
                    CustomDialog custom = new CustomDialog();
                    custom.setArguments(b);
                    custom.show(getChildFragmentManager(), "Custom Dialog");
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                GraphicsUtils.buttonClickEffectHide(v);

            }
        });

        return goalsView;

    }

    @Override
    public String toString(){
        return getString(R.string.set_goals_page);
    }

}
