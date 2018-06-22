package com.example.onepiece.slidingMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onepiece.R;
import com.example.onepiece.mainPage.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.os.Environment.DIRECTORY_PICTURES;

public class EditActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;
    private Uri tempUri;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private View mNightView = null;
    private WindowManager mWindowManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        textView1 = findViewById(R.id.edt_register_account);
        textView2 = findViewById(R.id.edt_register_pwd);
        imageView = findViewById(R.id.img_upload_img);
        
        Button button = findViewById(R.id.register_btn_sure);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String s1 = textView1.getText().toString();
                String s2 = textView2.getText().toString();

                if(null == s1 || "".equals(s1)){
                    Toast.makeText(EditActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(null == s2 || "".equals(s2)){
                    Toast.makeText(EditActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("getusername", s1);
                    intent.putExtra("getpwd",s2);
                    intent.setData(tempUri);
                    setResult(3, intent);
                    finish();
                }

            }
        });
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        toolbar.setTitle("编辑资料");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showChoosePicDialog();
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
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStoragePublicDirectory(DIRECTORY_PICTURES),"image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }

        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    Glide.with(EditActivity.this).load(tempUri).into(imageView);
                    break;
                case CHOOSE_PICTURE:
                    tempUri = data.getData();
                    Glide.with(EditActivity.this).load(tempUri).into(imageView);
                    break;
            }
        }
    }
}

