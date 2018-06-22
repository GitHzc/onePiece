package com.example.onepiece.mainPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.slidingMenu.AccountActivity;
import com.example.onepiece.slidingMenu.EditActivity;
import com.example.onepiece.slidingMenu.LightActivity;
import com.example.onepiece.slidingMenu.LoginActivity;
import com.example.onepiece.util.DebugMessage;
import com.example.onepiece.util.Utility;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_MUSIC;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = DebugMessage.TAG;
    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyViewPageAdapter mMyViewPageAdapter;
    private String[] titles = new String[] {"One", "音乐", "发现"};
    private List<Fragment> fragments = new ArrayList<>();
    public static boolean mode = false;
    private View mNightView = null;
    private WindowManager mWindowManager;
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        getPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    //夜间模式
    public static void switch_Mode(){
        if(mode == false){
            mode = true;
        }
        else{
            mode = true;
        }
    }
    private void init() {
        if(mode){
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

        toolbar = findViewById(R.id.main_activity_toolbar);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        ImageView equalizerImageView = findViewById(R.id.equalizer_image_view);

        equalizerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EqualizerActivity.class);
                startActivity(intent);
            }
        });

        for (String tab : titles) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tab));
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fragments.add(new OneFragment());
        fragments.add(new MusicFragment());
        fragments.add(new DiscoverFragment());
        mMyViewPageAdapter = new MyViewPageAdapter(getSupportFragmentManager(), titles, fragments);
        mViewPager.setAdapter(mMyViewPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // 初始化侧滑菜单
        init_drawer();
    }

    void init_drawer() {
        /*控制侧滑菜单*/
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*设定NavigationView菜单的选择事件*/
        NavigationView navigation =  findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(this);
    }

    /*后退键*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //菜单点击事件
        if(id == R.id.nav_swtich){
            Intent intent_switch = new Intent(MainActivity.this,AccountActivity.class);
            startActivityForResult(intent_switch,4);
        }
        else if(id == R.id.nav_quit){
            //创建退出对话框
            AlertDialog.Builder isExit = new AlertDialog.Builder(MainActivity.this);
            //设置对话框标题
            isExit.setTitle("消息提醒");
            //设置对话框消息
            isExit.setMessage("确定退出登录？");
            // 添加选择按钮并注册监听
            isExit.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    MainActivity.this.finish();
                }
            });
            isExit.setNegativeButton("取消",null);
            //对话框显示
            isExit.show();
            //super.onBackPressed();
        }
        else if(id == R.id.nav_edit){
            Intent intent_edit = new Intent(MainActivity.this,EditActivity.class);
            startActivityForResult(intent_edit,3);
        }
        else if(id == R.id.nav_light){
            Intent intent_light = new Intent(MainActivity.this,LightActivity.class);
            startActivityForResult(intent_light,6);
        }
        else if(id == R.id.nav_clear){
            Toast.makeText(this, "已清除本地缓存", Toast.LENGTH_SHORT).show();
            //这里还需要添加代码，删除本地缓存的文件夹内的内容
        }
        else if(id == R.id.nav_version){
            Intent intent_version = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent_version);
        }

        /*关闭侧滑菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    void getPermission() {
        permissions = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WAKE_LOCK
        };
        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {
            Toast.makeText(MainActivity.this, "已经授权", Toast.LENGTH_SHORT).show();
        } else {
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, Utility.REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Utility.REQUEST_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        finish();
                    }
                }
            }
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        TextView myID;
        CircleImageView myimage;
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headview = navigationView.getHeaderView(0);
        switch (requestCode){
            case 3:
                if(resultCode == 3){
                    myID = headview.findViewById(R.id.show_username);
                    myID.setText(intent.getStringExtra("getusername"));
                }
                break;
            case 4:
                if(resultCode == 4){
                    myID = headview.findViewById(R.id.show_username);
                    myID.setText(intent.getStringExtra("getusername"));
                }
                break;
            case 6:
                if(resultCode == 6){
                    mode = true;
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
                else if(resultCode == 7){
                    mode = false;
                    try{
                        mWindowManager.removeView(mNightView);
                    }catch(Exception ex){}
                }
                break;
        }
    }
}
