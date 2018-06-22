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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;

public class DataActivity extends AppCompatActivity {
    private String filePath = "/data/data/com.lg.slidingmenudemo/file/";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private View mNightView = null;
    private WindowManager mWindowManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        init();

        Button button_count = findViewById(R.id.register_btn_sure);
        button_count.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textView1 = findViewById(R.id.edt_register_account);
                TextView textView2 = findViewById(R.id.edt_register_pwd);
                String s = textView1.getText().toString();
                String s2 = textView2.getText().toString();
                //判断用户名和密码是否为空
                if(null == s || "".equals(s)){
                    Toast.makeText(DataActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(null == s2 || "".equals(s2)){
                    Toast.makeText(DataActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(s2.length() < 6){
                    Toast.makeText(DataActivity.this,"密码长度应当大于六位",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(DataActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    ////////******应当在这里与服务器连接，将用户名和密码传输到服务器，下面有将字符串写为文本文件的代码
                    Intent intent = new Intent();
                    intent.putExtra("getusername" ,s);
                    intent.putExtra("getpwd",s2);
                    setResult(5,intent);
                    finish();
                }

            }
        });
        Toolbar toolbar = findViewById(R.id.data_toolbar);
        toolbar.setTitle("注册");
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
    /**
     *
     检查应用程序是否允许写入存储设备

     *
     *
     如果应用程序不允许那么会提示用户授予权限

     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void initData() {
        String fileName = "log.txt";

        writeTxtToFile("Username password", filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
}
