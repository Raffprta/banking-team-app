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
 * @author Raffaello Perrota, XML by Ivy Tong
 */
public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Set button listener.
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if the user entered anything.
                String username = ((EditText)findViewById(R.id.userID)).getText().toString();
                String password = ((EditText)findViewById(R.id.userPass)).getText().toString();
                
                // Display error if nothing was entered.
                if(username == null || password == null || username.length() <= 0 || password.length() <= 0){
                    Bundle b = new Bundle();
                    b.putString(getString(R.string.custom_bundle), getString(R.string.err_empty_login));
                    b.putString(getString(R.string.custom_type_bundle), getString(R.string.custom_colour_type_red));
                    CustomDialog custom = new CustomDialog();
                    custom.setArguments(b);
                    custom.show(getSupportFragmentManager(), "Custom Dialog");
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }
                // UI effects.
                GraphicsUtils.buttonClickEffectShow(v);
                /// Pass login credentials to the security intent for login.
                Bundle b = new Bundle();
                b.putString(getString(R.string.login_user_bundle), username);
                b.putString(getString(R.string.login_pass_bundle), password);

                Intent securityIntent = new Intent(LoginActivity.this, SecurityActivity.class);
                // Pass the bundle to the Intent.
                securityIntent.putExtras(b);
                // Start the activity
                startActivity(securityIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the previous page.
    }

}
