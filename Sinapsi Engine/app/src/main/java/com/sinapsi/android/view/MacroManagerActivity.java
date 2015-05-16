package com.sinapsi.android.view;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sinapsi.android.background.ServiceBindedActionBarActivity;
import com.sinapsi.android.utils.ArrayListAdapter;
import com.sinapsi.android.utils.ViewTransitionManager;
import com.sinapsi.engine.R;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.utils.HashMapBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MacroManagerActivity extends ServiceBindedActionBarActivity {

    private ViewTransitionManager transitionManager;
    private ArrayListAdapter<MacroInterface> macroList;

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

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.new_macro_button);
        RecyclerView macroListRecycler = (RecyclerView) findViewById(R.id.macro_list_recycler);
        macroList = new ArrayListAdapter<MacroInterface>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return null; //TODO: impl
            }

            @Override
            public void onBindViewHolder(ItemViewHolder viewHolder, MacroInterface elem) {
                //TODO: impl
            }
        };

        macroListRecycler.setAdapter(macroList);

        transitionManager = new ViewTransitionManager(new HashMapBuilder<String, List<View>>()
                .put(States.NO_ELEMENTS.name(), Arrays.asList(
                        findViewById(R.id.no_macros_text), fab))
                .put(States.NO_CONNECTION.name(), Collections.singletonList(
                        findViewById(R.id.no_connection_layout)))
                .put(States.LIST, Arrays.asList(
                        macroListRecycler, fab))
                .put(States.PROGRESS, Collections.singletonList(
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

    @Override
    public void onServiceConnected(ComponentName name) {
        super.onServiceConnected(name);
        updateMacroList(service.getMacros());

    }

    private void startEditor() {
        //TODO: implement
    }

    private void updateMacroList(List<MacroInterface> ml) {
        macroList.clear();
        macroList.addAll(ml);
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
