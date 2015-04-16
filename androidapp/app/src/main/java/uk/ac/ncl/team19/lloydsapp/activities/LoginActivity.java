package uk.ac.ncl.team19.lloydsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrota
 * Automatic log off display mechanism. Client-side validation. Intent management.
 * @author Ivy Tong
 * All XML Designs.
 * @author Dale Whinham
 * Add method to go to the site.
 *
 * This class is the entry point to our program. It provides inputs for the user to log into their account
 * as well as passing these inputs to the next activity - the security activity. Client-side validation
 * is done on this page, and this page is what is displayed on logging off in any way.
 */
public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Get the previous intent, if any.
        Intent intent = getIntent();
        // If the app was auto logged off.
        if(intent != null && intent.getBooleanExtra(getString(R.string.bundle_autokick), false)){
            Bundle b = new Bundle();
            b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.err_autokick));
            CustomDialog custom = new CustomDialog();
            custom.setArguments(b);
            custom.show(getSupportFragmentManager(), "Custom Dialog");
        }

        // Set button listener.
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if the user entered anything.
                EditText username = ((EditText)findViewById(R.id.userID));
                EditText password = ((EditText)findViewById(R.id.userPass));

                // Display error if nothing was entered in the username.
                if(username.getText().toString() == null || username.getText().toString().length() <= 0){
                    username.setError(getString(R.string.err_empty_login));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }
                // Idem for the password
                if(password.getText().toString() == null || password.getText().toString().length() <= 0){
                    password.setError(getString(R.string.err_empty_pass));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // UI effects.
                GraphicsUtils.buttonClickEffectShow(v);
                /// Pass login credentials to the security intent for login.
                Bundle b = new Bundle();
                b.putString(Constants.BUNDLE_KEY_USERNAME, username.getText().toString());
                b.putString(Constants.BUNDLE_KEY_PASSWORD, password.getText().toString());

                Intent securityIntent = new Intent(LoginActivity.this, SecurityActivity.class);
                // Pass the bundle to the Intent.
                securityIntent.putExtras(b);
                // Start the activity
                startActivity(securityIntent);
                // Hide the effects on the button
                GraphicsUtils.buttonClickEffectHide(v);
            }
        });

        // Enable visit website link
        TextView visitWebsite = (TextView) findViewById(R.id.visitWebsite);
        visitWebsite.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the previous page.
    }


}
