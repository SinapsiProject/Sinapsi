package com.sinapsi.android.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sinapsi.android.background.ServiceBoundActionBarActivity;
import com.sinapsi.engine.R;

public class MainActivity extends ServiceBoundActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo: impl (app drawer)

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: impl
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //TODO impl

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }











}
