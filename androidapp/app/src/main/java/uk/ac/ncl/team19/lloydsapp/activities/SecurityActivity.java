package uk.ac.ncl.team19.lloydsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import uk.ac.ncl.team19.lloydsapp.R;
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

                EditText secondCharacter = ((EditText)findViewById(R.id.codeOne));
                EditText fourthCharacter = ((EditText)findViewById(R.id.codeTwo));
                EditText sixthCharacter = ((EditText)findViewById(R.id.codeThree));

                // Check if the user entered a character. If not set the error message.
                if(secondCharacter == null || secondCharacter.getText().length() <= 0){
                    secondCharacter.setError(getString(R.string.err_empty_sec));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                if(fourthCharacter == null || fourthCharacter.getText().length() <= 0){
                    fourthCharacter.setError(getString(R.string.err_empty_sec));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                if(sixthCharacter == null || sixthCharacter.getText().length() <= 0){
                    sixthCharacter.setError(getString(R.string.err_empty_sec));
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
