package com.tjrushby.runlite.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tjrushby.runlite.R;

public abstract class BaseActivity extends AppCompatActivity {
    private boolean checkedDarkThemeEnabled;

    protected SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDarkThemeEnabled();
    }

    @Override
    protected void onResume() {
        if(!checkedDarkThemeEnabled) {
            checkDarkThemeEnabled();
            restartActivity();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        checkedDarkThemeEnabled = false;
        super.onPause();
    }

    public void restartActivity() {
        this.recreate();
    }

    private void setDarkTheme() {
        setTheme(R.style.AppThemeDark);
    }

    private void setLightTheme() {
        setTheme(R.style.AppThemeLight);
    }

    private void checkDarkThemeEnabled() {
        if(getDarkThemeEnabled()) {
            setDarkTheme();
        } else {
            setLightTheme();
        }

        checkedDarkThemeEnabled = true;
    }

    private boolean getDarkThemeEnabled() {
        if(sharedPrefs == null) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        return sharedPrefs.getBoolean(getString(R.string.pref_key_dark_mode), false);
    }
}
