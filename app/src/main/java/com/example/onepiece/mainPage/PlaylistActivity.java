package com.example.onepiece.mainPage;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.onepiece.R;
import com.example.onepiece.util.DebugMessage;

public class PlaylistActivity extends FragmentActivity{
    private static final String TAG = DebugMessage.TAG;
    private View mNightView = null;
    private WindowManager mWindowManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        init();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new PlaylistFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }


    }
    //检测是否切换为夜间模式
    public void init(){
        if(MainActivity.mode){
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置
            lp.y = 10;
            if (mNightView == null) {
                mNightView = new TextView(this);
                mNightView.setBackgroundColor(0x80000000);
            }
            try{
                mWindowManager.addView(mNightView, lp);
            }catch(Exception ex){}
        }
        else{
            try{
                mWindowManager.removeView(mNightView);
            }catch(Exception ex){}
        }
    }
}
