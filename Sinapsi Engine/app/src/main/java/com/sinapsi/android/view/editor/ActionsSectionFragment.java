package com.sinapsi.android.view.editor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sinapsi.android.Lol;
import com.sinapsi.android.R;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.engine.builder.ActionBuilder;
import com.sinapsi.engine.builder.ComponentBuilderValidityStatus;
import com.sinapsi.engine.builder.ParameterBuilder;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.ComponentsAvailability;

import java.util.Locale;
import java.util.Map;

/**
 * Created by Giuseppe on 18/07/15.
 */
public class ActionsSectionFragment extends SinapsiFragment implements EditorActivity.EditorUpdatableFragment {

    private View rootView;

    private boolean recallUpdateAfterOnCreate = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.editor_actions_fragment, container, false);
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

        if (rootView == null) {
            recallUpdateAfterOnCreate = true;
            return;
        }
        updateActionList(df, service.getDevice().getId());
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.new_action_button);
        final EditorActivity finalActivity = activity;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ComponentSelectionDialogBuilder().newTriggerSelectionDialog(
                        finalActivity,
                        service.getDevice().getId(),
                        MacroComponent.ComponentTypes.ACTION,
                        new ComponentSelectionDialogBuilder.ComponentSelectionCallback() {
                            @Override
                            public void onComponentSelected(MacroComponent component, int deviceId) {
                                ActionDescriptor selected = (ActionDescriptor) component;
                                Lol.d("#### SELECTED: '"+selected.getName()+"' on device: "+deviceId);
                                ActionBuilder ab = new ActionBuilder(selected, deviceId);
                                df.getMacroBuilder().getActions().add(ab);
                                updateActionList(df, service.getDevice().getId());
                            }
                        }
                ).show();
            }
        });
    }

    @Override
    public String getName(Context context) {
        Locale l = Locale.getDefault();
        return context.getString(R.string.title_section_actions).toUpperCase(l);
    }

    private void updateActionList(final EditorActivity.DataFragment df,
                                  int currentDeviceId) {
        LinearLayout actionListView = (LinearLayout) rootView.findViewById(R.id.action_list);
        actionListView.removeAllViews();
        for (int i = 0; i < df.getMacroBuilder().getActions().size(); ++i) {
            final ActionBuilder ab = df.getMacroBuilder().getActions().get(i);
            final int finalI = i;
            View iv = createActionView(
                    actionListView,
                    ab,
                    i,
                    df,
                    currentDeviceId,
                    new ActionChangedCallback() {
                        @Override
                        public void onActionChanged(ActionBuilder elem) {
                            df.getMacroBuilder().getActions().remove(finalI);
                            df.getMacroBuilder().getActions().add(finalI, elem);
                        }
                    });
            actionListView.addView(iv);
        }
        actionListView.postInvalidate();
    }


    public View createActionView(ViewGroup parent,
                                 final ActionBuilder elem,
                                 int position,
                                 EditorActivity.DataFragment df,
                                 int currentDeviceId,
                                 final ActionChangedCallback actionChangedCallback) {

        Map<Integer, ComponentsAvailability> availabilityTable = df.getAvailabilityTable();

        final ParameterListAdapter parameters = new ParameterListAdapter(new ParameterListAdapter.ParametersUpdateListener() {
            @Override
            public void onParameterUpdate(int position, ParameterBuilder builder) {
                elem.getParameters().remove(position);
                elem.getParameters().add(position, builder);
                actionChangedCallback.onActionChanged(elem);
            }
        });
        Lol.d(this, "onCreateView() called");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_editor_element, parent, false);
        final RecyclerView parametersRecyclerView = (RecyclerView) v.findViewById(R.id.action_parameter_list_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        parametersRecyclerView.setLayoutManager(llm);
        //TODO: buttons


        ((TextView) v.findViewById(R.id.textview_macro_editor_action_name)).setText(elem.getName() + ((elem.getValidity() != ComponentBuilderValidityStatus.VALID) ? " (INVALID)" : ""));
        ((TextView) v.findViewById(R.id.textview_macro_editor_action_device)).setText(EditorActivity.getDeviceLabelText(availabilityTable, elem.getDeviceId(), currentDeviceId));

        ImageButton deleteButton = (ImageButton) v.findViewById(R.id.delete_action_button);
        deleteButton.setOnClickListener(new DeleteActionClickListener(position, df, currentDeviceId));

        parameters.clear();
        parameters.addAll(elem.getParameters());

        parametersRecyclerView.setAdapter(parameters);
        parametersRecyclerView.setHasFixedSize(true);
        //TODO: impl
        return v;
    }

    private interface ActionChangedCallback {
        public void onActionChanged(ActionBuilder elem);
    }

    private class DeleteActionClickListener implements View.OnClickListener {

        private int position;
        private EditorActivity.DataFragment dataFragment;
        private int currentDeviceId;

        public DeleteActionClickListener(int position, EditorActivity.DataFragment dataFragment, int currentDeviceId) {
            this.position = position;
            this.dataFragment = dataFragment;
            this.currentDeviceId = currentDeviceId;
        }

        @Override
        public void onClick(View v) {
            dataFragment.getMacroBuilder().getActions().remove(position);
            updateActionList(dataFragment, currentDeviceId);
        }
    }
}
