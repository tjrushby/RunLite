package com.tjrushby.runlite.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tjrushby.runlite.R;

import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity {
    private boolean checkedDarkThemeEnabled;

    protected boolean changedTheme;

    // int used to denote the currently applied theme: 0 for light, 1 for dark
    protected int currentTheme;

    protected SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        currentTheme = 0;
        checkDarkThemeEnabled();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        // only check the theme once to avoid looping between onCreate() and onResume()
        if(!checkedDarkThemeEnabled) {
            checkDarkThemeEnabled();

            if(changedTheme) {
                // theme has been changed since last draw, restart the activity to reflect this
                Timber.d("theme changed. restarting activity...");
                restartActivity();
            }
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
        boolean darkThemeEnabled = getDarkThemeEnabled();

        if(darkThemeEnabled && currentTheme == 0) {
            // dark theme is enabled but the light theme is currently applied, set the dark theme
            // and flag that the theme has been changed since last draw
            setDarkTheme();
            currentTheme = 1;
            changedTheme = true;
        } else if(!darkThemeEnabled && currentTheme == 1) {
            // dark theme is disabled but is currently applied, set the dark theme and flag that
            // the theme has been changed since last draw
            setLightTheme();
            currentTheme = 0;
            changedTheme = true;
        } else {
            // the selected theme is currently applied, flag that the theme has not been changed
            // since last draw
            changedTheme = false;
        }

        checkedDarkThemeEnabled = true;
    }

    protected boolean getDarkThemeEnabled() {
        if(sharedPrefs == null) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }

        return sharedPrefs.getBoolean(getString(R.string.pref_key_dark_mode), false);
    }
}
