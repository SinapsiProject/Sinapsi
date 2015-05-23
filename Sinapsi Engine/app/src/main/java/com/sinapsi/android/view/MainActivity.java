package com.sinapsi.android.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.android.background.SinapsiFragment;
import com.sinapsi.engine.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends SinapsiActionBarActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerListView;

    private Map<String, SinapsiFragment> fragmentMap = new HashMap<>();
    private String[] fragmentTitles;

    private CharSequence currentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //TODO: check if this will change the current fragment showed
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);

        initFragmentMap();

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

        //TODO: set main fragment
        currentTitle = fragmentTitles[0];
    }

    private void initFragmentMap(){
        List<String> titles = new ArrayList<>();
        //ADD HERE ALL THE FRAGMENTS
        MacroManagerFragment macroManagerFragment = new MacroManagerFragment();
        fragmentMap.put(macroManagerFragment.getName(this), macroManagerFragment);
        titles.add(macroManagerFragment.getName(this));
        addFragmentForConnectionListening(macroManagerFragment);
        //...
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
        int id = item.getItemId();

        //TODO impl

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment = fragmentMap.get(fragmentTitles[position]);
        Lol.printNullity(this, "selected fragment with name '" + fragmentTitles[position] + "'" , fragment);

        /*Bundle args = new Bundle();
        args.putInt(..., ...);
        fragment.setArguments(args);*/

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerListView.setItemChecked(position, true);
        setTitle(fragmentTitles[position]);
        drawerLayout.closeDrawer(drawerListView);
    }

    @Override
    public void setTitle(CharSequence title) {
        currentTitle = title;
        getSupportActionBar().setTitle(currentTitle);
    }








}
