package uk.ac.ncl.team19.lloydsapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.SecureChar;
import uk.ac.ncl.team19.lloydsapp.api.response.AuthResponse;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

import static android.content.DialogInterface.OnDismissListener;

/**
 * @author Raffaello Perrotta, XML creation by Ivy Tong
 */
public class SecurityActivity extends FragmentActivity implements OnDismissListener{
    // Login credentials
    private String username = "";
    private String password = "";
    private List<SecureChar> secureChars = new ArrayList<>();

    // Logging tag
    private static final String TAG = SecurityActivity.class.getName();

    // Login button
    private Button loginButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch username/password from LoginActivity
        Intent intent = getIntent();
        username = intent.getStringExtra(getString(R.string.login_user_bundle));
        password = intent.getStringExtra(getString(R.string.login_pass_bundle));

        setContentView(R.layout.security_page);

        // Fetch security digits
        final EditText firstCharacter = ((EditText)findViewById(R.id.codeOne));
        final EditText secondCharacter = ((EditText)findViewById(R.id.codeTwo));
        final EditText thirdCharacter = ((EditText)findViewById(R.id.codeThree));

        // Fetch security digit prompts
        final TextView digitOne = (TextView)findViewById(R.id.digitOne);
        final TextView digitTwo = (TextView)findViewById(R.id.digitTwo);
        final TextView digitThree = (TextView)findViewById(R.id.digitThree);

        // Automatically move to next text box when character is entered
        firstCharacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                if (len == 1) {
                    secondCharacter.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        secondCharacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                if (len == 1) {
                    thirdCharacter.requestFocus();
                } else if (len == 0) {
                    firstCharacter.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        thirdCharacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                if (len == 0) {
                    secondCharacter.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set the random integers for the security code
        List<Integer> randomInts = new ArrayList<>(Constants.MINIMUM_SECURITY_CODE_LENGTH);

        for (int i = 1; i <= Constants.MINIMUM_SECURITY_CODE_LENGTH; i++){
            randomInts.add(i);
        }

        // Get three random unique integers.
        Collections.shuffle(randomInts);
        final List<Integer> uniqueRandomInts = randomInts.subList(0, 3);

        // Sort list
        Collections.sort(uniqueRandomInts);

        // Set random digits
        digitOne.setText(Integer.toString(uniqueRandomInts.get(0)));
        digitTwo.setText(Integer.toString(uniqueRandomInts.get(1)));
        digitThree.setText(Integer.toString(uniqueRandomInts.get(2)));

        // Set button listener.
        loginButton = (Button)findViewById(R.id.securityButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UI effects.
                GraphicsUtils.buttonClickEffectShow(v);

                // Check if the user entered a character. If not set the error message.
                if(firstCharacter == null || firstCharacter.getText().length() <= 0){
                    firstCharacter.setError(getString(R.string.err_empty_sec));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                if(secondCharacter == null || secondCharacter.getText().length() <= 0){
                    secondCharacter.setError(getString(R.string.err_empty_sec));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                if(thirdCharacter == null || thirdCharacter.getText().length() <= 0){
                    thirdCharacter.setError(getString(R.string.err_empty_sec));
                    GraphicsUtils.buttonClickEffectHide(v);
                    return;
                }

                // Assemble secure chars map
                secureChars.add(new SecureChar(uniqueRandomInts.get(0), firstCharacter.getText().charAt(0)));
                secureChars.add(new SecureChar(uniqueRandomInts.get(1), secondCharacter.getText().charAt(0)));
                secureChars.add(new SecureChar(uniqueRandomInts.get(2), thirdCharacter.getText().charAt(0)));

                // Start Login task
                ProgressDialog.showLoading(SecurityActivity.this);
                APIConnector ac = new APIConnector(SecurityActivity.this);
                ac.authenticate(username, password, secureChars, new Callback<AuthResponse>() {
                    @Override
                    public void success(AuthResponse ar, Response response) {
                        // Dismiss progress dialog
                        ProgressDialog.removeLoading(SecurityActivity.this);

                        switch (ar.getStatus()) {
                            case SUCCESS:
                                // Store device token in shared preferences
                                Log.i(TAG, "Received device token: " + ar.getDeviceToken());
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SecurityActivity.this);
                                sp.edit().putString(getString(R.string.sp_device_token), ar.getDeviceToken()).apply();

                                // Launch MainMenu activity
                                Intent securityIntent = new Intent(SecurityActivity.this, MainMenuActivity.class);
                                startActivity(securityIntent);
                                break;

                            case ERROR:
                                fail(ar.getErrorMessage());
                                break;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // Dismiss progress dialog
                        ProgressDialog.removeLoading(SecurityActivity.this);
                        fail(error.getMessage());
                    }

                    private void fail(String errorMessage) {
                        showErrorDialog(errorMessage);

                        // Hide the effects on the button
                        GraphicsUtils.buttonClickEffectHide(loginButton);
                    }

                    private void showErrorDialog(String errorString) {
                        Bundle b = new Bundle();
                        b.putString(getString(R.string.custom_bundle), errorString);
                        b.putString(getString(R.string.custom_type_bundle), getString(R.string.custom_colour_type_red));
                        CustomDialog custom = new CustomDialog();
                        custom.setArguments(b);
                        custom.show(getSupportFragmentManager(), "Custom Dialog");
                    }
                });
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // Go back one activity when the error dialog is dismissed
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the login page.
    }
}
