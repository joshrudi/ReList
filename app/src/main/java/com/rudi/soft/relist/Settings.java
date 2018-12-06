package com.rudi.soft.relist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Boolean isDark;  // theme bool state

        SharedPreferences prefs = this.getSharedPreferences(
                "theme", Context.MODE_PRIVATE);

        try {
            isDark = prefs.getBoolean("theme", false);  // load theme state
        } catch (Exception e) {
            isDark = false;  // assert false otherwise
        }

        // switch in settings for theme
        Switch themeMode = (Switch) findViewById(R.id.night_mode);

        // set toggle to current theme
        themeMode.setChecked(isDark);
        themeMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {  // if we check it

                    // enable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefs.edit().putBoolean("theme", true).apply();
                    recreate();  // recreate so changes take effect
                } else {

                    // diable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefs.edit().putBoolean("theme", false).apply();
                    recreate();  // changes take effect
                }
            }
        });

        // back button to go back to defaults page
        ImageView backBtn = (ImageView) findViewById(R.id.back_settings);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // simply finishing this activity won't work because we need to reset the main activity for
                // theme changes to take effect, therefore we set a flag and pass it into a new main activity
                String flag = "defaults";
                startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("frag", flag));
                finish();  // close it out now
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        // same concept as above "onClick(...)"
        String flag = "defaults";
        startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("frag", flag));
        finish();  // close
    }
}
