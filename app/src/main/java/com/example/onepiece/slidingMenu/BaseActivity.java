package com.example.onepiece.slidingMenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

public class BaseActivity extends AppCompatActivity {
    private static boolean enableNightMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!enableNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    /**
     *If enabled night mode
     * @return  true or false
     */
    public boolean isEnableNightMode() {
        return enableNightMode;
    }

    /**
     * enable night mode or not
     * @param enableNightMode   true or false
     */
    public void setEnableNightMode(boolean enableNightMode) {
        BaseActivity.enableNightMode = enableNightMode;
        if(enableNightMode) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate();
    }
}
