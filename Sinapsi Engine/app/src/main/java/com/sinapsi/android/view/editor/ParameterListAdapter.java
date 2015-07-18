package com.sinapsi.android.view.editor;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.R;
import com.sinapsi.android.utils.lists.ArrayListAdapter;
import com.sinapsi.engine.builder.ParameterBuilder;
import com.sinapsi.engine.parameters.FormalParamBuilder;
import com.sinapsi.engine.parameters.StringMatchingModeChoices;
import com.sinapsi.utils.JSONUtils;

/**
 * Created by Giuseppe on 14/07/15.
 */
public class ParameterListAdapter extends ArrayListAdapter<ParameterBuilder> {

    private final ParametersUpdateListener listener;
    private View rootView;

    public ParameterListAdapter(ParametersUpdateListener listener){
        this.listener = listener;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parameter_editor_element, parent, false);

        Lol.d("ON CREATE VIEW CALLED IN PARAMETER LIST ADAPTER");

        return rootView;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, final ParameterBuilder elem, final int position) {
        View v = viewHolder.itemView;

        Lol.d("ON BIND VIEW HOLDER CALLED FOR ELEM: " + elem.getName());

        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.parameter_set_checkbox);
        checkBox.setVisibility(elem.isOptional() ? View.VISIBLE : View.GONE);

        Spinner spinner = (Spinner) v.findViewById(R.id.parameter_spinner);
        EditText stringEdittext = (EditText) v.findViewById(R.id.parameter_string_edittext);
        EditText numEdittext = (EditText) v.findViewById(R.id.parameter_number_edittext);

        TextView paramName = (TextView) v.findViewById(R.id.parameter_name);
        TextView paramType = (TextView) v.findViewById(R.id.parameter_type);

        paramName.setText(elem.getName());
        paramType.setText(elem.getType().name());

        switch (elem.getType()) {
            case CHOICE: {

                checkBox.setChecked(elem.getStrValue() != null);
                spinner.setVisibility(View.VISIBLE);
                stringEdittext.setVisibility(View.GONE);
                numEdittext.setVisibility(View.GONE);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                        v.getContext(),
                        R.layout.spinner_item,
                        elem.getChoiceEntries());
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);

                if (elem.getStrValue() != null) {
                    int found = 0;
                    for (int i = 0; i < elem.getChoiceEntries().length; ++i) {
                        if (elem.getChoiceEntries()[i].equals(elem.getStrValue())) {
                            found = i;
                            break;
                        }
                    }
                    spinner.setSelection(found);
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                        elem.setStrValue(elem.getChoiceEntries()[spinnerPosition]);
                        checkBox.setChecked(elem.getStrValue() != null);
                        listener.onParameterUpdate(position, elem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        elem.setStrValue(null);
                        checkBox.setChecked(elem.getStrValue() != null);
                    }
                });
            }
            break;
            case STRING: {
                checkBox.setChecked(elem.getStrValue() != null);
                spinner.setVisibility(View.GONE);
                stringEdittext.setVisibility(View.VISIBLE);
                numEdittext.setVisibility(View.GONE);

                if (elem.getStrValue() != null) stringEdittext.setText(elem.getStrValue());


            }
            break;

            case INT: {
                checkBox.setChecked(elem.getIntValue() != null);
                spinner.setVisibility(View.GONE);
                stringEdittext.setVisibility(View.GONE);
                numEdittext.setVisibility(View.VISIBLE);

                if (elem.getIntValue() != null)
                    numEdittext.setText(elem.getIntValue().toString());
            }
            break;

            case BOOLEAN: {
                checkBox.setChecked(elem.getBoolValue() != null);
                spinner.setVisibility(View.VISIBLE);
                stringEdittext.setVisibility(View.GONE);
                numEdittext.setVisibility(View.GONE);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                        v.getContext(),
                        R.layout.spinner_item,
                        FormalParamBuilder.STYLED_BOOL_CONSTANTS.get(elem.getBoolStyle().ordinal()));
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);

                if (elem.getBoolValue() != null) {
                    if (elem.getBoolValue())
                        spinner.setSelection(0);
                    else
                        spinner.setSelection(1);
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                        if (spinnerPosition == 0) {
                            elem.setBoolValue(Boolean.TRUE);
                        } else {
                            elem.setBoolValue(Boolean.FALSE);
                        }
                        checkBox.setChecked(elem.getBoolValue() != null);
                        listener.onParameterUpdate(position, elem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        elem.setBoolValue(null);
                        checkBox.setChecked(elem.getBoolValue() != null);
                    }
                });
            }
            break;

            case STRING_ADVANCED: {
                checkBox.setChecked(elem.getStrValue() != null);

                spinner.setVisibility(View.VISIBLE);
                stringEdittext.setVisibility(View.VISIBLE);
                numEdittext.setVisibility(View.GONE);

                if (elem.getStrValue() != null) stringEdittext.setText(elem.getStrValue());

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                        v.getContext(),
                        R.layout.spinner_item,
                        JSONUtils.getEnumNames(StringMatchingModeChoices.class));
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);

                if (elem.getStringMatchingMode() != null) {
                    spinner.setSelection(elem.getStringMatchingMode().ordinal());
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                        elem.setStringMatchingMode(StringMatchingModeChoices.values()[spinnerPosition]);
                        listener.onParameterUpdate(position, elem);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //does nothing
                    }
                });
            }
            break;
        }

        stringEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                elem.setStrValue(s.toString());
                listener.onParameterUpdate(position, elem);
            }
        });

        numEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String original = s.toString();
                Integer x;
                if (original.equals("")) {
                    x = null;
                } else {
                    x = Integer.parseInt(s.toString());
                }
                elem.setIntValue(x);
                checkBox.setChecked(elem.getIntValue() != null);
                listener.onParameterUpdate(position, elem);
            }
        });

        numEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String original = v.getText().toString();
                Integer x;
                if (original.equals("")) {
                    x = null;
                } else {
                    x = Integer.parseInt(v.getText().toString());
                }
                elem.setIntValue(x);
                checkBox.setChecked(elem.getIntValue() != null);
                listener.onParameterUpdate(position, elem);
                return true;
            }
        });


    }

    public static interface ParametersUpdateListener{
        public void onParameterUpdate(int position, ParameterBuilder builder);
    }

}
