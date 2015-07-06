package com.sinapsi.android.view;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.SinapsiAndroidApplication;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.android.utils.DialogUtils;
import com.sinapsi.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends SinapsiActionBarActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerListView;

    private LinearLayout drawerLinearLayout;

    private Map<String, SinapsiFragment> fragmentMap = new HashMap<>();
    private String[] fragmentTitles;

    private CharSequence currentTitle;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!((SinapsiAndroidApplication)getApplication()).isLoggedIn()){
            Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }else{
            startService(new Intent(getApplicationContext(), com.sinapsi.android.background.SinapsiBackgroundService.class));
        }


        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        drawerLinearLayout = (LinearLayout) findViewById(R.id.left_drawer_linear);

        //Add here all the fragments
        initFragmentMap(
                new MacroManagerFragment(),
                new AboutFragment());

        drawerListView.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.drawer_list_item,
                fragmentTitles));
        drawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawerToggle();

        //TODO: play/pause engine button

        currentTitle = fragmentTitles[0];
        selectItem(0);

    }

    private void initFragmentMap(SinapsiFragment... fragments){
        List<String> titles = new ArrayList<>();
        for(SinapsiFragment sf: fragments){
            fragmentMap.put(sf.getName(this), sf);
            titles.add(sf.getName(this));
            addFragmentForConnectionListening(sf);
        }
        fragmentTitles= titles.toArray(new String[titles.size()]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: impl
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        //TODO impl

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment = fragmentMap.get(fragmentTitles[position]);
        Lol.printNullity(this, "selected fragment with name '" + fragmentTitles[position] + "'" , fragment);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerListView.setItemChecked(position, true);
        setTitle(fragmentTitles[position]);
        drawerLayout.closeDrawer(drawerLinearLayout);
    }

    @Override
    public void setTitle(CharSequence title) {
        currentTitle = title;
        getSupportActionBar().setTitle(currentTitle);
    }


    private void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(currentTitle);
                invalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onServiceConnected(ComponentName name) {
        super.onServiceConnected(name);

        TextView userText = (TextView) findViewById(R.id.user_info_text_view);
        userText.setText(service.getLoggedUser().getEmail());
        userText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showYesNoDialog(
                        MainActivity.this,
                        getString(R.string.log_out),
                        getString(R.string.are_you_sure_log_out),
                        false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //ON YES
                                service.getWeb().logout();
                                Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //ON NO
                                dialog.cancel();
                            }
                        });
            }
        });
    }
}
