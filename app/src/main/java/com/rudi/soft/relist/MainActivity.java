package com.rudi.soft.relist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.net.Uri;


public class MainActivity extends AppCompatActivity
        implements ListFragment.OnFragmentInteractionListener,
        StoreFragment.OnFragmentInteractionListener,
        DefaultsFragment.OnFragmentInteractionListener {

    private Fragment mFragment;

    // bottom nav
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:  // shopping lists

                    // set fragment to shopping list
                    mFragment = new ListFragment();
                    updateFragment(mFragment);
                    return true;

                case R.id.navigation_dashboard:  // stores

                    // set fragment to store
                    mFragment = new StoreFragment();
                    updateFragment(mFragment);
                    return true;

                case R.id.navigation_notifications:  // defaults

                    // set fragment to defaults
                    mFragment = new DefaultsFragment();
                    updateFragment(mFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isDark;  // check theme

        SharedPreferences prefs = this.getSharedPreferences(
                "theme", Context.MODE_PRIVATE);

        try {
            isDark = prefs.getBoolean("theme", false);  // grab theme value
        } catch (Exception e) {
            isDark = false;  // default to false
        }

        // if dark set theme to dark, else light
        if (isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // bottom nav
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // android launcher shortcut to store check
        if (!"com.rudi.soft.relist.STOREFRAG".equals(getIntent().getAction())) {

            mFragment = new ListFragment();
            updateFragment(mFragment);
        }

        // if leaving settings app, will have extra data to go back to defaults
        // creates the illusion that we didn't actually reload/recreate the main activity
        // reminder: we need to recreate/reset activity from settings to implement theme toggle
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            // left settings, go back to defaults
            mFragment = new DefaultsFragment();
            updateFragment(mFragment);
            navigation.setSelectedItemId(R.id.navigation_notifications);  // go back to defaults
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        //implement if needed
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume check if we called store shortcut from device's launcher
        checkShortcut();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        finish();  // close app if back button/gesture used
    }

    public void checkShortcut() {  // checks if store shortcut was called

        if ("com.rudi.soft.relist.STOREFRAG".equals(getIntent().getAction())) {

            // if store shortcut was selected, set fragment to stores
            mFragment = new StoreFragment();
            updateFragment(mFragment);

            // get our bottom nav and set its accented icon to store fragment instead of the default
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.navigation_dashboard);
            getIntent().setAction("");
        }
    }

    private void updateFragment(Fragment fragment) {  // updates currently selected fragment

        // update and/or create the fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}