package com.sinapsi.android.view.editor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.R;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.engine.builder.ActionBuilder;
import com.sinapsi.engine.builder.ParameterBuilder;
import com.sinapsi.engine.builder.TriggerBuilder;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.TriggerDescriptor;

import java.util.Locale;

/**
 * Created by Giuseppe on 18/07/15.
 */
public class TriggerSectionFragment extends SinapsiFragment implements EditorActivity.EditorUpdatableFragment, ParameterListAdapter.ParametersUpdateListener {

    private ParameterListAdapter triggerParameters = new ParameterListAdapter(this);
    private View rootView;

    private boolean recallUpdateAfterOnCreate = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.editor_trigger_fragment, container, false);

        RecyclerView triggerParamsRecyclerView = (RecyclerView) rootView.findViewById(R.id.trigger_parameter_list_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        triggerParamsRecyclerView.setAdapter(triggerParameters);
        triggerParamsRecyclerView.setLayoutManager(llm);
        triggerParamsRecyclerView.setHasFixedSize(true);

        if (recallUpdateAfterOnCreate) {
            recallUpdateAfterOnCreate = false;
            updateView((EditorActivity) getActivity());
        }

        return rootView;
    }


    @Override
    public void updateView(EditorActivity editorActivity) {
        EditorActivity activity = (EditorActivity) getActivity();
        if (activity == null) activity = editorActivity;
        final EditorActivity.DataFragment df = activity.getDataFragment();
        Lol.printNullity(this, "df", df);
        if (rootView == null) {
            recallUpdateAfterOnCreate = true;
            return;
        }
        EditText macroNameText = (EditText) rootView.findViewById(R.id.edittext_macro_editor_macro_name);
        macroNameText.setText(df.getMacroBuilder().getName());

        macroNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                df.getMacroBuilder().setName(s.toString());
            }
        });

        ((TextView) rootView.findViewById(R.id.textview_macro_editor_trigger_name)).setText(df.getMacroBuilder().getTrigger().getName());


        ((TextView) rootView.findViewById(R.id.textview_macro_editor_trigger_device)).setText(
                EditorActivity.getDeviceLabelText(
                        df.getAvailabilityTable(),
                        df.getMacroBuilder().getTrigger().getDeviceId(),
                        service.getDevice().getId()));


        triggerParameters.clear();
        triggerParameters.addAll(df.getMacroBuilder().getTrigger().getParameters());

        final EditorActivity finalActivity = activity;
        ((ImageButton) rootView.findViewById(R.id.select_trigger_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ComponentSelectionDialogBuilder().newTriggerSelectionDialog(
                        finalActivity,
                        service.getDevice().getId(),
                        MacroComponent.ComponentTypes.TRIGGER,
                        new ComponentSelectionDialogBuilder.ComponentSelectionCallback() {
                            @Override
                            public void onComponentSelected(MacroComponent component, int deviceId) {
                                TriggerDescriptor selected = (TriggerDescriptor) component;
                                Lol.d("#### SELECTED: '"+selected.getName()+"' on device: "+deviceId);
                                TriggerBuilder tb = new TriggerBuilder(selected, deviceId);
                                df.getMacroBuilder().setTrigger(tb);
                                updateView(finalActivity);
                            }
                        }
                ).show();
            }
        });
    }

    @Override
    public String getName(Context context) {
        Locale l = Locale.getDefault();
        return context.getString(R.string.title_section_trigger).toUpperCase(l);
    }

    @Override
    public void onParameterUpdate(int position, ParameterBuilder builder) {
        EditorActivity activity = (EditorActivity) getActivity();
        if (rootView == null || activity == null || activity.getDataFragment() == null) {
            return;
        }

        EditorActivity.DataFragment df = activity.getDataFragment();
        Lol.d("UPDATING TRIGGER PARAMETER");
        Lol.d("builder.getBoolValue() == " + builder.getBoolValue().toString());
        df.getMacroBuilder().getTrigger().getParameters().remove(position);
        df.getMacroBuilder().getTrigger().getParameters().add(position, builder);


    }
}
