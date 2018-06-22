package com.example.onepiece.slidingMenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.onepiece.R;
import com.example.onepiece.mainPage.MainActivity;


public class LightActivity extends BaseActivity {

    private View mNightView = null;
    private WindowManager mWindowManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        init();


        Toolbar toolbar = findViewById(R.id.light_toolbar);
        toolbar.setTitle("夜间模式");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
    public void modeDay(View v) {
        Intent intent = new Intent();
        intent.putExtra("getmode1", "false");
        setResult(7,intent);

        try{
            mWindowManager.removeView(mNightView);
        }catch(Exception ex){}
        //setEnableNightMode(false);
    }

    public void modeNight(View v) {
        Intent intent = new Intent();
        intent.putExtra("getmode2", "true");
        setResult(6,intent);

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

        //setEnableNightMode(true);
    }

}
