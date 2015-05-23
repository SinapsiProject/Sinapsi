package com.sinapsi.android.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sinapsi.engine.R;


/**
 * This activity is the main activty, it gives the user the option to view
 * a tutorial of sinapsi, or the option to skip and login/register into the system
 *
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout
        setContentView(R.layout.welcome_layout);

        TextView signUp = (TextView) findViewById(R.id.signUp_text);
        final Intent register = new Intent(this, RegisterActivity.class);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: bypasses login for test purposes
                //startActivityForResult(register, 0);
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(i);
            }
        });


        TextView signIn = (TextView) findViewById(R.id.signIn_text);
        final Intent login = new Intent(this, LoginActivity.class);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(login, 0);
            }
        });

        startService(new Intent(getApplicationContext(), com.sinapsi.android.background.SinapsiBackgroundService.class));

    }
}
