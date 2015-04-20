package com.sinapsi.android.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
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
    }
}
