package com.example.onepiece.slidingMenu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.mainPage.MainActivity;
import com.example.onepiece.model.UserBean;
import com.example.onepiece.util.HttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import static com.example.onepiece.util.HttpUtils.*;

public class DataActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private View mNightView = null;
    private WindowManager mWindowManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        init();

        textView1 = findViewById(R.id.edt_register_account);
        textView2 = findViewById(R.id.edt_register_pwd);
        Button button_count = findViewById(R.id.register_btn_sure);
        button_count.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String username = textView1.getText().toString();
                String password = textView2.getText().toString();
                //判断用户名和密码是否为空
                if(null == username || "".equals(username)){
                    Toast.makeText(DataActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(null == password || "".equals(password)){
                    Toast.makeText(DataActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 6){
                    Toast.makeText(DataActivity.this,"密码长度应当大于六位",Toast.LENGTH_SHORT).show();
                }
                else{
                    register(username, password);
                }

            }
        });
        Toolbar toolbar = findViewById(R.id.data_toolbar);
        toolbar.setTitle("注册");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentIntent();
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
    
    private void sentIntent() {
        String s1 = textView1.getText().toString();
        String s2 = textView2.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("getusername" ,s1);
        intent.putExtra("getpwd",s2);
        setResult(5,intent);
    }
    
    void register(String username, String password) {
        Retrofit retrofit = getRetrofit();
        MyApi api = retrofit.create(MyApi.class);
        UserBean user = new UserBean();
        user.setUsername(username);
        user.setPassword(password);
        api.register(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Toast.makeText(DataActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        sentIntent();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        String errorMessage = e.getMessage();
                        if (errorMessage.contains("403")) {
                            textView1.setText("");
                            textView1.setHint("用户名已被注册");
                        } else if (errorMessage.contains("400")){
                            Toast.makeText(DataActivity.this, "请求错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {}
                });
    }
}
