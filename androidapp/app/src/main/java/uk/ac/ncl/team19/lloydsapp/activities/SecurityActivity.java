package uk.ac.ncl.team19.lloydsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta, XML creation by Ivy Tong
 */
public class SecurityActivity extends FragmentActivity {
    // Login credentials
    private String username = "";
    private String password = "";
    private Map<Integer, Character> secureChars = new HashMap<>();

    // Logging tag
    private static final String TAG = SecurityActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch username/password from LoginActivity
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.login_user_bundle));
        password = intent.getStringExtra(getString(R.string.login_pass_bundle));

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

                // Assemble secure chars map
                secureChars.put(2, secondCharacter.getText().charAt(0));
                secureChars.put(4, fourthCharacter.getText().charAt(0));
                secureChars.put(6, sixthCharacter.getText().charAt(0));

                // Start Login task
                // TODO: Progress dialog show/hide
                LoginTask lt = new LoginTask();
                lt.execute();
            }
        });
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {
        private String errorString = null;

        @Override
        protected String doInBackground(Void... voids) {
            APIConnector ac = new APIConnector(getApplicationContext());
            try {
                return ac.authenticate(username, password, secureChars);
            } catch (Exception e) {
                errorString = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                // TODO: Error dialog, go back to login screen, etc.
                Log.e(TAG, errorString);
            } else {
                // Store device token in shared preferences
                Log.i(TAG, "Received device token: " + response);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SecurityActivity.this);
                sp.edit().putString(getString(R.string.sp_device_token), response).apply();

                // Launch MainMenu activity
                Intent securityIntent = new Intent(SecurityActivity.this, MainMenuActivity.class);
                startActivity(securityIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the login page.
    }

}
