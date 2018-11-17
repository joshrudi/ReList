package com.example.joshr.relist;

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    mFragment = new ListFragment();
                    updateFragment(mFragment);
                    return true;

                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    mFragment = new StoreFragment();
                    updateFragment(mFragment);
                    return true;

                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
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

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAFAFA")));
        //actionBar.setTitle(Html.fromHtml("<font color='#636468'>Android Now</font>"));

        boolean isDark;

        SharedPreferences prefs = this.getSharedPreferences(
                "theme", Context.MODE_PRIVATE);

        try {
            isDark = prefs.getBoolean("theme", false);
        } catch (Exception e) {
            isDark = false;
        }

        if (isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (!"com.example.joshr.relist.STOREFRAG".equals(getIntent().getAction())) {
            mFragment = new ListFragment();
            updateFragment(mFragment);
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            mFragment = new DefaultsFragment();
            updateFragment(mFragment);
            navigation.setSelectedItemId(R.id.navigation_notifications);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        //implement if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkShortcut();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public void checkShortcut() {

        if ("com.example.joshr.relist.STOREFRAG".equals(getIntent().getAction())) {
            mFragment = new StoreFragment();
            updateFragment(mFragment);
            getIntent().setAction("");
        }
    }

    private void updateFragment(Fragment fragment) {
        //update and/or initiate the fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}