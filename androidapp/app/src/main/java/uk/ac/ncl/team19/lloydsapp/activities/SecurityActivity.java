package uk.ac.ncl.team19.lloydsapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta, XML creation by Ivy Tong
 */
public class SecurityActivity extends Activity {

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
