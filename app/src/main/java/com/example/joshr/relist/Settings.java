package com.example.joshr.relist;

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

        Boolean isDark;

        SharedPreferences prefs = this.getSharedPreferences(
                "theme", Context.MODE_PRIVATE);

        try {
            isDark = prefs.getBoolean("theme", false);
        } catch (Exception e) {
            isDark = false;
        }

        Switch themeMode = (Switch) findViewById(R.id.night_mode);
        themeMode.setChecked(isDark);
        themeMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefs.edit().putBoolean("theme", true).apply();
                    recreate();
                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefs.edit().putBoolean("theme", false).apply();
                    recreate();
                }
            }
        });

        ImageView backBtn = (ImageView) findViewById(R.id.back_settings);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String flag = "defaults";
                startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("frag", flag));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        String flag = "defaults";
        startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("frag", flag));
        finish();
    }
}
