package com.example.onepiece.slidingMenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.mainPage.MainActivity;


public class AccountActivity extends AppCompatActivity {

    private TextView showname;
    private TextView showpwd;
    private View mNightView = null;
    private WindowManager mWindowManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        init();

        showname = findViewById(R.id.username);
        showname.setText(getIntent().getStringExtra("getusername"));
        showpwd = findViewById(R.id.password);
        showpwd.setText(getIntent().getStringExtra("getpwd"));


        Button button1 = findViewById(R.id.login);
        Button button2 = findViewById(R.id.regi);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textView1 = findViewById(R.id.username);
                TextView textView2 = findViewById(R.id.password);
                String s = textView1.getText().toString();
                String s2 = textView2.getText().toString();
                //判断用户名和密码是否为空
                if(null == s || "".equals(s)){
                    Toast.makeText(AccountActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(null == s2 || "".equals(s2)){
                    Toast.makeText(AccountActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    ////////*****这里需要判断用户名与密码匹配
                    Toast.makeText(AccountActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("getusername" , s);
                    setResult(4,intent);
                    finish();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent_data = new Intent(AccountActivity.this,DataActivity.class);
                startActivityForResult(intent_data,5);
            }
        });
        Toolbar toolbar = findViewById(R.id.account_toolbar);
        toolbar.setTitle("切换账户");
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
    public void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode){
            case 5:
                showname = findViewById(R.id.username);
                showname.setText(intent.getStringExtra("getusername"));
                showpwd = findViewById(R.id.password);
                showpwd.setText(intent.getStringExtra("getpwd"));
                break;
        }

    }
}
