package com.sinapsi.android.view.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ComponentName;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sinapsi.android.AndroidAppConsts;
import com.sinapsi.android.Lol;
import com.sinapsi.android.R;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.android.utils.animation.ViewTransitionManager;
import com.sinapsi.client.web.SinapsiWebServiceFacade;
import com.sinapsi.model.impl.AvailabilityMap;
import com.sinapsi.model.impl.ComponentsAvailability;
import com.sinapsi.engine.builder.MacroBuilder;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.utils.HashMapBuilder;

import retrofit.RetrofitError;

public class EditorActivity extends SinapsiActionBarActivity implements ActionBar.TabListener {


    private static final String DATA_FRAGMENT_TAG = "data";
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

    private ViewTransitionManager transitionManager;

    private TriggerSectionFragment triggerFragment;
    private ActionsSectionFragment actionsFragment;

    private boolean created = false;

    private DataFragment dataFragment;

    private enum States {
        EDITOR,
        PROGRESS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_editor);


        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (DataFragment) fm.findFragmentByTag(DATA_FRAGMENT_TAG);

        if (dataFragment == null) {
            dataFragment = new DataFragment();
            fm.beginTransaction()
                    .add(dataFragment, DATA_FRAGMENT_TAG)
                    .commit();

            dataFragment.setEditorInput((MacroInterface) params[0]);
            //LOAD DATA HERE
        }

        actionsFragment = new ActionsSectionFragment();
        triggerFragment = new TriggerSectionFragment();

        addFragmentForConnectionListening(actionsFragment);
        addFragmentForConnectionListening(triggerFragment);


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

        created = true;
        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());
    }

    @Override
    public void onServiceConnected(ComponentName name) {
        super.onServiceConnected(name);
        Lol.d(this, "onServiceConnected() called and activity is " + (created ? "created" : "not created") + "with dataFragment " + (dataFragment == null ? "null" : "not null"));
        transitionManager.makeTransitionIfDifferent(States.PROGRESS.name());
        updateAvailabilityTable(new AvailabilityUpdateCallback() {
            @Override
            public void onAvailabilityUpdateSuccess(Map<Integer, ComponentsAvailability> availabilityTable) {
                Lol.d(EditorActivity.class, "onAvailabilityUpdateSuccess");
                if (dataFragment.getMacroBuilder() == null)
                    dataFragment.setMacroBuilder(new MacroBuilder(service.getDevice().getId(), availabilityTable, dataFragment.getEditorInput()));
                dataFragment.setAvailabilityTable(availabilityTable);
                updateFragments();
                transitionManager.makeTransitionIfDifferent(States.EDITOR.name());


            }

            @Override
            public void onAvailabilityUpdateFailure(Throwable error, Map<Integer, ComponentsAvailability> availabilityTable) {
                Lol.d(EditorActivity.class, "onAvailabilityUpdateFailure");
                if (error instanceof RetrofitError) {
                    DialogUtils.handleRetrofitError(error, EditorActivity.this, false);
                } else {
                    DialogUtils.showOkDialog(EditorActivity.this,
                            "Error",
                            "Something has gone wrong while downloading the availability" +
                                    " of components on other devices from server. Local " +
                                    "components will still be available", false);
                }
                if (dataFragment.getMacroBuilder() == null)
                    dataFragment.setMacroBuilder(new MacroBuilder(service.getDevice().getId(), availabilityTable, dataFragment.getEditorInput()));
                dataFragment.setAvailabilityTable(availabilityTable);
                updateFragments();
                transitionManager.makeTransitionIfDifferent(States.EDITOR.name());
            }

            @Override
            public void onAvailabilityUpdateOffline(Map<Integer, ComponentsAvailability> availabilityTable) {
                Lol.d(EditorActivity.class, "onAvailabilityUpdateOffline");
                //TODO: switch to Offline Mode
                if (dataFragment.getMacroBuilder() == null)
                    dataFragment.setMacroBuilder(new MacroBuilder(service.getDevice().getId(), availabilityTable, dataFragment.getEditorInput()));
                dataFragment.setAvailabilityTable(availabilityTable);
                updateFragments();
                transitionManager.makeTransitionIfDifferent(States.EDITOR.name());
            }
        });
    }

    private void updateFragments() {
        for (int i = 0; i < sectionsPagerAdapter.getCount(); ++i) {
            Lol.d("################ UPDATING A FRAGMENT ################");
            Fragment f = sectionsPagerAdapter.getItem(i);
            if (f instanceof EditorUpdatableFragment) {
                ((EditorUpdatableFragment) f).updateView(this);
            }
        }
    }

    private void updateAvailabilityTable(final EditorActivity.AvailabilityUpdateCallback callback) {
        final Map<Integer, ComponentsAvailability> availabilityTable = new HashMap<>();
        //noinspection ConstantConditions
        if (!AndroidAppConsts.DEBUG_EDITOR_OFFLINE && service.isOnline()) {
            service.getWeb().getAvailableComponents(service.getDevice(), new SinapsiWebServiceFacade.WebServiceCallback<AvailabilityMap>() {
                @Override
                public void success(AvailabilityMap triplets, Object response) {
                    availabilityTable.clear(); //TODO change availabilityTable's type in availability map
                    addLocalAvailability(availabilityTable);
                    for (ComponentsAvailability t : triplets) {
                        Lol.d(EditorActivity.class, "AVAILABILITY: DEVICE ID: " + t.getDevice().getId() + " - MODEL: " + t.getDevice().getModel());
                        if (t.getDevice().getId() != service.getDevice().getId())
                            availabilityTable.put(t.getDevice().getId(), t);
                    }
                    callback.onAvailabilityUpdateSuccess(availabilityTable);
                }

                @Override
                public void failure(Throwable error) {
                    availabilityTable.clear();
                    addLocalAvailability(availabilityTable);
                    callback.onAvailabilityUpdateFailure(error, availabilityTable);
                }
            });
        } else {
            availabilityTable.clear();
            addLocalAvailability(availabilityTable);
            callback.onAvailabilityUpdateOffline(availabilityTable);
        }
    }

    private void addLocalAvailability(Map<Integer, ComponentsAvailability> availabilityTable) {
        availabilityTable.put(
                service.getDevice().getId(),
                new ComponentsAvailability(
                        service.getDevice(),
                        service.getEngine().getAvailableTriggerDescriptors(),
                        service.getEngine().getAvailableActionDescriptors()
                ));
    }

    public DataFragment getDataFragment() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(DATA_FRAGMENT_TAG);
        return (DataFragment) fragment;
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateFragments();
    }

    private void endEditing() {

        MacroInterface result;
        //noinspection ConstantConditions
        if (AndroidAppConsts.DEBUG_EDITOR || dataFragment.isChanged()){
            if(dataFragment.getMacroBuilder().validate()) {
                result = dataFragment.getMacroBuilder().build(service.getEngine());
            }else{
                DialogUtils.showOkDialog(this, "Invalid macro", "This macro is invalid or incomplete and cannot be saved.", false);
                return;
            }
        }
        else result = dataFragment.getEditorInput();
        //noinspection ConstantConditions
        returnActivity(result, AndroidAppConsts.DEBUG_EDITOR || dataFragment.isChanged());
    }

    private void onGoingBack() {
        //call this to ask confirm to go back discarding changes
        //TODO: impl
    }

    public static interface AvailabilityUpdateCallback {
        public void onAvailabilityUpdateSuccess(Map<Integer, ComponentsAvailability> availabilityTable);

        public void onAvailabilityUpdateFailure(Throwable error, Map<Integer, ComponentsAvailability> availabilityTable);

        public void onAvailabilityUpdateOffline(Map<Integer, ComponentsAvailability> availabilityTable);
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
            switch (position) {
                case 0: {
                    return triggerFragment;
                }
                case 1: {
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
                    return triggerFragment.getName(EditorActivity.this);
                case 1:
                    return actionsFragment.getName(EditorActivity.this);
            }
            return null;
        }
    }


    public static String getDeviceLabelText(Map<Integer, ComponentsAvailability> availabilityTable, int deviceId, int currentDeviceId) {
        String result = "on Device"; //TODO: localization
        ComponentsAvailability t = availabilityTable.get(deviceId);
        if (t == null) {
            result += " with id: " + deviceId;
        } else {
            result += ": " + t.getDevice().getModel();
            if (deviceId == currentDeviceId) {
                result += " (this device)";
            } else {
                result += " (" + t.getDevice().getType() + ")";
            }
        }
        return result;
    }

    public static class DataFragment extends Fragment {

        private boolean changed = false;
        private MacroInterface editorInput;
        private MacroBuilder macroBuilder;
        private Map<Integer, ComponentsAvailability> availabilityTable;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public boolean isChanged() {
            return changed;
        }

        public void setChanged(boolean changed) {
            this.changed = changed;
        }

        public MacroInterface getEditorInput() {
            return editorInput;
        }

        public void setEditorInput(MacroInterface editorInput) {
            this.editorInput = editorInput;
        }

        public MacroBuilder getMacroBuilder() {
            return macroBuilder;
        }

        public void setMacroBuilder(MacroBuilder macroBuilder) {
            this.macroBuilder = macroBuilder;
        }

        public Map<Integer, ComponentsAvailability> getAvailabilityTable() {
            return availabilityTable;
        }

        public void setAvailabilityTable(Map<Integer, ComponentsAvailability> availabilityTable) {
            this.availabilityTable = availabilityTable;
        }
    }

    public static interface EditorUpdatableFragment {
        public void updateView(EditorActivity editorActivity);
    }

}
