package com.example.onepiece.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/5/12 0012.
 */

public class Utility {
    private static final String TAG = DebugMessage.TAG;
    public static final int REQUEST_PERMISSION = 0;
    public static final int REQUEST_CODE_PICK_IMAGE = 1;
    public static final int REQUEST_UPDATE_INFO = 2;
    public static final int MY_PERMISSIONS_REQUEST_CHOOSE_PHOTO = 3;

    public static void showSoftKeyboard(View view, Context mContext) {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void closeSoftKeyboard(View view, Context mContext) {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static AlertDialog getTitleEditDialog(final Activity activity, String title, final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        final LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.simple_alert_dialog, null);
        builder.setView(linearLayout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText new_title = linearLayout.findViewById(R.id.playlist_title_edit);
                textView.setText(new_title.getText().toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(activity, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }

    public static String getSDPath(Context context) {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return context.getExternalFilesDir(null) + "/playlist_picture";
        } else {
            return context.getFilesDir().getPath() + "/playlist_picture";
        }
    }

    static public String getTimeNow() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}

