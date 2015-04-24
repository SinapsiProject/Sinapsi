package com.sinapsi.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sinapsi.engine.R;


/**
 * This activity is the main activty, it's give the user the possibility to view
 * a tutorial of sinapsi, or the possibility to skip and login/register into the system
 *
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout
        setContentView(R.layout.welcome_layout);

        TextView signUp = (TextView) findViewById(R.id.signUp_text);
        final Intent login = new Intent(this, LoginActivity.class);
        //login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(login, 0);
            }
        });
    }
}
