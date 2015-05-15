package com.sinapsi.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sinapsi.android.background.ServiceBindedActionBarActivity;
import com.sinapsi.android.utils.ViewTransitionManager;
import com.sinapsi.engine.R;
import com.sinapsi.utils.HashMapBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MacroManagerActivity extends ServiceBindedActionBarActivity {

    private ViewTransitionManager transitionManager;

    private enum States {
        NO_ELEMENTS,
        NO_CONNECTION,
        LIST,
        PROGRESS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_manager);

        //todo: bind to service

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.new_macro_button);

        transitionManager = new ViewTransitionManager(new HashMapBuilder<String, List<View>>()
                .put(States.NO_ELEMENTS.name(), Arrays.asList(
                        findViewById(R.id.no_macros_text), fab))
                .put(States.NO_CONNECTION.name(), Arrays.asList(
                        findViewById(R.id.no_connection_layout)))
                .put(States.LIST, Arrays.asList(
                        findViewById(R.id.macro_list_recycler), fab))
                .put(States.PROGRESS, Arrays.asList(
                        findViewById(R.id.macro_list_progress)))
                .create());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditor();
            }
        });

        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());
    }

    private void startEditor() {
        //TODO: implement
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_macro_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
