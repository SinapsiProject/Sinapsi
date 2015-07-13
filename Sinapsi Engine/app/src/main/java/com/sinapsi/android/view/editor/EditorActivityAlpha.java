package com.sinapsi.android.view.editor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.R;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.android.background.SinapsiBackgroundService;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.android.utils.animation.ViewTransitionManager;
import com.sinapsi.android.utils.lists.ArrayListAdapter;
import com.sinapsi.engine.builder.ActionBuilder;
import com.sinapsi.engine.builder.ComponentsAvailability;
import com.sinapsi.engine.builder.MacroBuilder;
import com.sinapsi.engine.builder.ParameterBuilder;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.utils.HashMapBuilder;

import retrofit.RetrofitError;

public class EditorActivityAlpha extends SinapsiActionBarActivity implements ActionBar.TabListener {

    public static final String NO_CHANGES_BOOLEAN = "NO_CHANGES_BOOLEAN";
    private Boolean changed = true;

    private MacroInterface input;
    private MacroBuilder macroBuilder;

    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

    private ViewTransitionManager transitionManager;

    private TriggerSectionFragment triggerFragment;
    private ActionsSectionFragment actionsFragment;

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
        actionsFragment = new ActionsSectionFragment();
        triggerFragment = new TriggerSectionFragment();

        addFragmentForConnectionListening(actionsFragment);
        addFragmentForConnectionListening(triggerFragment);

        setContentView(R.layout.activity_editor);


        Lol.printNullity(this, "params", params);
        Lol.d(this, "params size: " + params.length);

        input = (MacroInterface) params[0]; //TODO: maintain reference, for example, with a retained root fragment

        transitionManager = new ViewTransitionManager(new HashMapBuilder<String, List<View>>()
                .put(States.EDITOR.name(), Collections.singletonList(
                        findViewById(R.id.editor_pager)
                ))
                .put(States.PROGRESS.name(), Collections.singletonList(
                        findViewById(R.id.editor_progress)
                ))
                .create());



        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.editor_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(sectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        //transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());
    }

    @Override
    public void onServiceConnected(ComponentName name) {
        super.onServiceConnected(name);
        Lol.d(this, "onServiceConnected() called");
        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());
        service.updateAvailabilityTable(new SinapsiBackgroundService.AvailabilityUpdateCallback() {
            @Override
            public void onAvailabilityUpdateSuccess() {
                macroBuilder = new MacroBuilder(service.getDevice().getId(), service.getAvailabilityTable(), input);
                updateFragments();
                transitionManager.makeTransitionIfDifferent(States.EDITOR.name());
            }

            @Override
            public void onAvailabilityUpdateFailure(Throwable error) {
                if (error instanceof RetrofitError) {
                    DialogUtils.handleRetrofitError(error, EditorActivityAlpha.this, false);
                } else {
                    DialogUtils.showOkDialog(EditorActivityAlpha.this,
                            "Error",
                            "Something has gone wrong while downloading the availability" +
                                    " of components on other devices from server. Local " +
                                    "components will still be available", false);
                }
                macroBuilder = new MacroBuilder(service.getDevice().getId(), service.getAvailabilityTable(), input);
                updateFragments();
                transitionManager.makeTransitionIfDifferent(States.EDITOR.name());
            }

            @Override
            public void onAvailabilityUpdateOffline() {
                //TODO: switch to Offline Mode
                macroBuilder = new MacroBuilder(service.getDevice().getId(), service.getAvailabilityTable(), input);
                updateFragments();
                transitionManager.makeTransitionIfDifferent(States.EDITOR.name());
            }
        });
    }

    private void updateFragments(){
        for(int i = 0; i < sectionsPagerAdapter.getCount(); ++i){
            Fragment f = sectionsPagerAdapter.getItem(i);
            if(f instanceof EditorUpdatableFragment){
                ((EditorUpdatableFragment) f).updateView();
            }
        }
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
        if (id == R.id.action_done) {
            endEditing();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(NO_CHANGES_BOOLEAN, changed);
    }

    private void endEditing(){
        MacroInterface result;
        if(changed) result = macroBuilder.build(service.getEngine());
        else result = input;
        returnActivity(result, changed);
    }

    private void onGoingBack(){
        //call this to ask confirm to go back discarding changes
        //TODO: impl
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position){
                case 0:{
                    return triggerFragment;
                }
                case 1:{
                    return actionsFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return triggerFragment.getName(EditorActivityAlpha.this);
                case 1:
                    return actionsFragment.getName(EditorActivityAlpha.this);
            }
            return null;
        }
    }


    public static class ActionsSectionFragment extends SinapsiFragment implements EditorUpdatableFragment {

        private ActionListAdapter actionList;
        private MacroBuilder macroBuilder;
        private Map<Integer, ComponentsAvailability> availabilityTable;
        private boolean recallUpdateAfterOnCreate = false;
        private View rootView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.editor_actions_fragment, container, false);

            if(recallUpdateAfterOnCreate){
                recallUpdateAfterOnCreate = false;
                updateView();
            }
            return rootView;
        }

        public void setMacroBuilder(MacroBuilder macroBuilder){
            this.macroBuilder = macroBuilder;
        }

        @Override
        public void updateView() {
            this.availabilityTable = service.getAvailabilityTable();
            if(rootView == null){
                recallUpdateAfterOnCreate = true;
                return;
            }
            actionList = new ActionListAdapter(availabilityTable);

            actionList.clear();
            actionList.addAll(macroBuilder.getActions());
            Lol.d(this, "actionList.size() == " + actionList.size());
            actionList.notifyDataSetChanged();
            Lol.d(this, "View updated!");
        }

        @Override
        public String getName(Context context) {
            Locale l = Locale.getDefault();
            return context.getString(R.string.title_section_actions).toUpperCase(l);
        }
    }

    public static class TriggerSectionFragment extends SinapsiFragment implements EditorUpdatableFragment {

        private ParameterListAdapter triggerParameters = new ParameterListAdapter();
        private MacroBuilder macroBuilder;
        private View rootView;
        private Map<Integer, ComponentsAvailability> availabilityTable;
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

            if(recallUpdateAfterOnCreate){
                recallUpdateAfterOnCreate = false;
                updateView();
            }

            return rootView;
        }

        public void setMacroBuilder(MacroBuilder macroBuilder){
            this.macroBuilder = macroBuilder;
        }



        @Override
        public void updateView() {
            this.availabilityTable = service.getAvailabilityTable();
            if(rootView == null){
                recallUpdateAfterOnCreate = true;
                return;
            }
            ((TextView) rootView.findViewById(R.id.edittext_macro_editor_macro_name)).setText(macroBuilder.getName());
            ((TextView) rootView.findViewById(R.id.textview_macro_editor_trigger_name)).setText(macroBuilder.getTrigger().getName());

            ComponentsAvailability t = availabilityTable.get(macroBuilder.getTrigger().getDeviceId());
            if(t == null){
                ((TextView) rootView.findViewById(R.id.textview_macro_editor_trigger_device)).setText("on Device with id: " + macroBuilder.getTrigger().getDeviceId());
            }else{
                ((TextView) rootView.findViewById(R.id.textview_macro_editor_trigger_device)).setText("on Device: "+t.getDevice().getModel());
            }

            triggerParameters.clear();
            triggerParameters.addAll(macroBuilder.getTrigger().getParameters());
        }

        @Override
        public String getName(Context context) {
            Locale l = Locale.getDefault();
            return context.getString(R.string.title_section_trigger).toUpperCase(l);
        }
    }


    public static class ParameterListAdapter extends ArrayListAdapter<ParameterBuilder> {

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            return new View(parent.getContext());//TODO: impl
        }
        @Override
        public void onBindViewHolder(ItemViewHolder viewHolder, ParameterBuilder elem, int position) {
            //TODO: impl
        }

    }

    public static class ActionListAdapter extends ArrayListAdapter<ActionBuilder>{


        private final Map<Integer, ComponentsAvailability> availabilityTable;

        public ActionListAdapter(Map<Integer, ComponentsAvailability> availabilityMap){
            this.availabilityTable = availabilityMap;
        }

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            Lol.d(this, "onCreateView() called");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_editor_element, parent, false);
            //TODO: buttons
            return v;
        }
        @Override
        public void onBindViewHolder(ItemViewHolder ivh, ActionBuilder elem, int position) {
            Lol.d(this, "onBindViewHolder() called");
            View v = ivh.itemView;

            ((TextView) v.findViewById(R.id.textview_macro_editor_action_name)).setText(elem.getName());
            ComponentsAvailability t = availabilityTable.get(elem.getDeviceId());
            if(t == null){
                ((TextView) v.findViewById(R.id.textview_macro_editor_action_device)).setText("on Device with id: "+elem.getDeviceId());
            }else{
                ((TextView) v.findViewById(R.id.textview_macro_editor_action_device)).setText("on Device: "+t.getDevice().getModel());
            }
            //TODO: impl
        }

    }

    public static interface EditorUpdatableFragment {
        public void updateView();
    }

}
