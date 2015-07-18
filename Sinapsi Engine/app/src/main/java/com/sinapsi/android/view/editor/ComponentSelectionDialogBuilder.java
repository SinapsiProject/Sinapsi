package com.sinapsi.android.view.editor;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sinapsi.android.R;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroComponent;
import com.sinapsi.model.impl.ActionDescriptor;
import com.sinapsi.model.impl.ComponentsAvailability;
import com.sinapsi.model.impl.TriggerDescriptor;

import java.util.ArrayList;
import java.util.Map;


/**
 * Used to build a dialog to allow the user to select a new Trigger or Action for the editor
 */
public class ComponentSelectionDialogBuilder {

    public Dialog newTriggerSelectionDialog(EditorActivity activity, int currentDeviceId, MacroComponent.ComponentTypes type, final ComponentSelectionCallback callback){
        final Dialog result = new Dialog(activity);
        result.setContentView(R.layout.component_selection_dialog);
        RecyclerView recycler = (RecyclerView) result.findViewById(R.id.component_list_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        recycler.setLayoutManager(llm);
        EditorActivity.DataFragment df = activity.getDataFragment();
        Map<Integer, ComponentsAvailability> availabilityMap = df.getAvailabilityTable();

        ComponentSelectionCallback interceptedCallback = new ComponentSelectionCallback() {
            @Override
            public void onComponentSelected(MacroComponent component, int deviceId) {
                result.dismiss();
                callback.onComponentSelected(component, deviceId);
            }
        };

        ComponentSelectionAdapter adapter = new ComponentSelectionAdapter(currentDeviceId, interceptedCallback);
        for(Integer i: availabilityMap.keySet()){
            adapter.getList().add(new ComponentSelectionElement(availabilityMap.get(i).getDevice(), SelectionElementType.DEVICE, availabilityMap.get(i).getDevice().getId()));
            switch (type){
                case TRIGGER:{
                    for(TriggerDescriptor td: availabilityMap.get(i).getTriggers().values()){
                        adapter.getList().add(new ComponentSelectionElement(td, SelectionElementType.COMPONENT, availabilityMap.get(i).getDevice().getId()));
                    }
                }
                    break;

                case ACTION:{
                    for(ActionDescriptor ad: availabilityMap.get(i).getActions().values()){
                        adapter.getList().add(new ComponentSelectionElement(ad, SelectionElementType.COMPONENT, availabilityMap.get(i).getDevice().getId()));
                    }
                }
                    break;
            }
        }
        recycler.setAdapter(adapter);

        switch (type){
            case TRIGGER:
                result.setTitle("Select Trigger:"); //TODO: localization
                break;
            case ACTION:
                result.setTitle("Select Action:"); //TODO: localization
                break;
        }

        return result;
    }

    public interface ComponentSelectionCallback {
        public void onComponentSelected(MacroComponent component, int deviceId);
    }

    public class ComponentSelectionAdapter extends RecyclerView.Adapter<ComponentSelectionAdapter.ItemViewHolder>{

        private int currentDeviceId;
        private ComponentSelectionCallback callback;

        private ArrayList<ComponentSelectionElement> array = new ArrayList<>();

        public ComponentSelectionAdapter(int currentDeviceId, ComponentSelectionCallback callback){
            this.currentDeviceId = currentDeviceId;
            this.callback = callback;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = null;
            switch (viewType){
                case 0://DEVICE
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_selection_group, parent, false);
                    break;

                case 1://COMPONENT
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_selection_element, parent, false);
                    break;
            }
            return new ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, final int position) {

            switch (array.get(position).getType()){
                case DEVICE:{
                    DeviceInterface device = (DeviceInterface) array.get(position).getElement();
                    TextView label = (TextView) holder.itemView.findViewById(R.id.device_label);
                    if(array.get(position).getDeviceId() == currentDeviceId){
                        label.setText(device.getModel() + " (this device)");
                    }else{
                        label.setText(device.getModel() + " ("+ device.getType()+")");
                    }
                }
                    break;

                case COMPONENT:{
                    final MacroComponent component = (MacroComponent) array.get(position).getElement();
                    TextView label = (TextView) holder.itemView.findViewById(R.id.component_label);
                    label.setText(component.getName());

                    View.OnClickListener selectComponentListener = new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            callback.onComponentSelected(component, array.get(position).getDeviceId());
                        }
                    };

                    label.setOnClickListener(selectComponentListener);
                    holder.itemView.setOnClickListener(selectComponentListener);
                }
                    break;
            }

        }

        @Override
        public int getItemViewType(int position) {
            return array.get(position).getType().ordinal();
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{

            public ItemViewHolder(View itemView) {
                super(itemView);
            }


        }

        public ArrayList<ComponentSelectionElement> getList(){
            return array;
        }


    }

    private class ComponentSelectionElement{

        private Object element;
        private SelectionElementType type;
        private int deviceId;

        public ComponentSelectionElement(Object element, SelectionElementType type, int deviceId) {
            this.element = element;
            this.type = type;
            this.deviceId = deviceId;
        }

        public Object getElement() {
            return element;
        }

        public void setElement(Object element) {
            this.element = element;
        }

        public SelectionElementType getType() {
            return type;
        }

        public void setType(SelectionElementType type) {
            this.type = type;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }
    }

    public enum SelectionElementType{
        DEVICE,
        COMPONENT
    }
}
