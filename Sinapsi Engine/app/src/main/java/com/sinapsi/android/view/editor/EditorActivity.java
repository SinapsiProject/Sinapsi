package com.sinapsi.android.view.editor;

import android.content.ComponentName;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.android.R;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.android.utils.animation.ViewTransitionManager;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.engine.builder.MacroBuilder;
import com.sinapsi.engine.components.TriggerScreenPower;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.TriggerDescriptor;
import com.sinapsi.utils.HashMapBuilder;
import com.sinapsi.utils.Triplet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;

public class EditorActivity extends SinapsiActionBarActivity {

    public static final String NO_CHANGES_BOOLEAN = "NO_CHANGES_BOOLEAN";

    private Boolean changed = true;
    private MacroInterface input;
    private MacroBuilder macroBuilder;

    private Map<Integer, Triplet<DeviceInterface, List<TriggerDescriptor>, List<ActionDescriptor>>> availabilityTable = new HashMap<>();

    private ViewTransitionManager transitionManager;

    private enum States {
        EDITOR,
        PROGRESS
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(NO_CHANGES_BOOLEAN)){
                changed = savedInstanceState.getBoolean(NO_CHANGES_BOOLEAN);
            }
        }
        setContentView(R.layout.activity_editor);

        Lol.printNullity(this, "params", params);
        Lol.d(this, "params size: " + params.length);

        input = (MacroInterface) params[0];

        macroBuilder = new MacroBuilder(input);

        transitionManager = new ViewTransitionManager(new HashMapBuilder<String, List<View>>()
                .put(States.EDITOR.name(), Arrays.asList(
                        findViewById(R.id.editor_layout)
                ))
                .put(States.PROGRESS.name(), Arrays.asList(
                        findViewById(R.id.editor_progress)
                ))
                .create());




        /*final TextView tv = ((TextView) findViewById(R.id.test_text));
        tv.setText(input.getName());

        Button returnButton = (Button) findViewById(R.id.return_macro_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setName(tv.getText().toString());
                returnActivity(input, changed);
            }
        });*/


        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());

    }

    @Override
    public void onServiceConnected(ComponentName name) {
        super.onServiceConnected(name);

        updateAvailabilityTable();

    }

    private void updateAvailabilityTable() {
        service.getWeb().getAvailableComponents(service.getDevice(), new SinapsiWebServiceFacade.WebServiceCallback<List<Triplet<DeviceInterface, List<TriggerDescriptor>, List<ActionDescriptor>>>>() {
            @Override
            public void success(List<Triplet<DeviceInterface, List<TriggerDescriptor>, List<ActionDescriptor>>> triplets, Object response) {
                availabilityTable.clear();
                addLocalAvailability();
                for (Triplet<DeviceInterface, List<TriggerDescriptor>, List<ActionDescriptor>> t : triplets) {
                    if (t.getFirst().getId() != service.getDevice().getId())
                        availabilityTable.put(t.getFirst().getId(), t);
                }
            }

            @Override
            public void failure(Throwable error) {
                if (error instanceof RetrofitError) {
                    DialogUtils.handleRetrofitError(error, EditorActivity.this, false);
                } else {
                    DialogUtils.showOkDialog(EditorActivity.this,
                            "Error",
                            "Something has gone wrong while downloading the availability" +
                                    " of components on other devices from server. Local " +
                                    "components will still be available", false);
                }
                availabilityTable.clear();
                addLocalAvailability();
            }
        });
    }

    private void addLocalAvailability(){
        availabilityTable.put(
                service.getDevice().getId(),
                new Triplet<>(
                        service.getDevice(),
                        service.getEngine().getAvailableTriggerDescriptors(),
                        service.getEngine().getAvailableActionDescriptors()
                ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(NO_CHANGES_BOOLEAN, changed);
    }
}
