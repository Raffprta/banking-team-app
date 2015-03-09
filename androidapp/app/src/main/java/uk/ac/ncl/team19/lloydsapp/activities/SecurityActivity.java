package uk.ac.ncl.team19.lloydsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta, XML creation by Ivy Tong
 */
public class SecurityActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_page);

        // Set button listener.
        findViewById(R.id.securityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UI effects.
                GraphicsUtils.buttonClickEffectShow(v);

                String secondCharacter = ((EditText)findViewById(R.id.codeOne)).getText().toString();
                String fourthCharacter = ((EditText)findViewById(R.id.codeTwo)).getText().toString();
                String sixthCharacter = ((EditText)findViewById(R.id.codeThree)).getText().toString();

                // Check if the user entered a character.

                if(secondCharacter == null || fourthCharacter == null || sixthCharacter == null
                   || secondCharacter.length() <= 0 || fourthCharacter.length() <= 0 || sixthCharacter.length() <= 0){
                    Bundle b = new Bundle();
                    b.putString(getString(R.string.custom_bundle), getString(R.string.err_empty_sec));
                    b.putString(getString(R.string.custom_type_bundle), getString(R.string.custom_colour_type_red));
                    CustomDialog custom = new CustomDialog();
                    custom.setArguments(b);
                    custom.show(getSupportFragmentManager(), "Custom Dialog");
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                /// TODO Login auth. is done here.
                Intent securityIntent = new Intent(SecurityActivity.this, MainMenuActivity.class);
                startActivity(securityIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the login page.
    }

}
