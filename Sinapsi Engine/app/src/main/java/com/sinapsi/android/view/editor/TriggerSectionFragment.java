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
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.R;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.engine.builder.ParameterBuilder;

import java.util.Locale;

/**
 * Created by Giuseppe on 18/07/15.
 */
public class TriggerSectionFragment extends SinapsiFragment implements EditorActivityAlpha.EditorUpdatableFragment, ParameterListAdapter.ParametersUpdateListener {

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
            updateView((EditorActivityAlpha) getActivity());
        }

        return rootView;
    }


    @Override
    public void updateView(EditorActivityAlpha editorActivity) {
        EditorActivityAlpha activity = (EditorActivityAlpha) getActivity();
        if (activity == null) activity = editorActivity;
        final EditorActivityAlpha.DataFragment df = activity.getDataFragment();
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
                EditorActivityAlpha.getDeviceLabelText(
                        df.getAvailabilityTable(),
                        df.getMacroBuilder().getTrigger().getDeviceId(),
                        service.getDevice().getId()));


        triggerParameters.clear();
        triggerParameters.addAll(df.getMacroBuilder().getTrigger().getParameters());
    }

    @Override
    public String getName(Context context) {
        Locale l = Locale.getDefault();
        return context.getString(R.string.title_section_trigger).toUpperCase(l);
    }

    @Override
    public void onParameterUpdate(int position, ParameterBuilder builder) {
        EditorActivityAlpha activity = (EditorActivityAlpha) getActivity();
        if (rootView == null || activity == null || activity.getDataFragment() == null) {
            return;
        }

        EditorActivityAlpha.DataFragment df = activity.getDataFragment();
        Lol.d("UPDATING TRIGGER PARAMETER");
        Lol.d("builder.getBoolValue() == " + builder.getBoolValue().toString());
        df.getMacroBuilder().getTrigger().getParameters().remove(position);
        df.getMacroBuilder().getTrigger().getParameters().add(position, builder);


    }
}
