package com.example.onepiece.mainPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onepiece.R;
import com.example.onepiece.util.DebugMessage;
import com.example.onepiece.util.Utility;


public class SongListEditActivity extends AppCompatActivity {
    private static final String TAG = DebugMessage.TAG;
    private TextView mTitle;
    private ImageView mPicture;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        final Button mConfirm = findViewById(R.id.confirm);
        mTitle = findViewById(R.id.new_title);
        mPicture = findViewById(R.id.new_picture);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.getTitleEditDialog(SongListEditActivity.this, "编辑标题", mTitle).show();
            }
        });

        mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SongListEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SongListEditActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Utility.MY_PERMISSIONS_REQUEST_CHOOSE_PHOTO);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, Utility.REQUEST_CODE_PICK_IMAGE);
                }
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SongListEditActivity.this, MainActivity.class);
                if (uri != null) {
                    intent.setData(uri);
                }
                if (mTitle.getText() != null) {
                    intent.putExtra("title", mTitle.getText().toString());
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST_CHOOSE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Utility.REQUEST_CODE_PICK_IMAGE);
            } else {
                Toast.makeText(SongListEditActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utility.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                uri = data.getData();
                Glide.with(SongListEditActivity.this).load(uri).into(mPicture);
            } else {
                Toast.makeText(SongListEditActivity.this, "未选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
