package uk.ac.ncl.team19.lloydsapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrota, XML by Ivy Tong
 */
public class LoginActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Set button listener.
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UI effects.
                GraphicsUtils.buttonClickEffectShow(v);
                /// TODO Login auth. Login is done here.
                Intent securityIntent = new Intent(LoginActivity.this, SecurityActivity.class);
                startActivity(securityIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the previous page.
    }

}
