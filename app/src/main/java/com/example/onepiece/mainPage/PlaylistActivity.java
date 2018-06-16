package com.example.onepiece.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.onepiece.R;
import com.example.onepiece.util.DebugMessage;

public class PlaylistActivity extends FragmentActivity{
    private static final String TAG = DebugMessage.TAG;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new PlaylistFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }


    }
}
