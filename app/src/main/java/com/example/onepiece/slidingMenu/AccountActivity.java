package com.example.onepiece.slidingMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;

public class AccountActivity extends AppCompatActivity {

    private TextView showname;
    private TextView showpwd;
    private ImageView showimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        showname = findViewById(R.id.username);
        showname.setText(getIntent().getStringExtra("getusername"));
        showpwd = findViewById(R.id.password);
        showpwd.setText(getIntent().getStringExtra("getpwd"));

        Button button1 = findViewById(R.id.login);
        Button button2 = findViewById(R.id.regi);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(AccountActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                TextView textView = findViewById(R.id.username);
                String s = textView.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("getusername" , s);
                setResult(4,intent);
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent_data = new Intent(AccountActivity.this,DataActivity.class);
                startActivityForResult(intent_data,5);
            }
        });
        ImageButton imageButton1 = findViewById(R.id.comeback);
        imageButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }
    public void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode){
            case 5:
                showname = findViewById(R.id.username);
                showname.setText(intent.getStringExtra("getusername"));
                showpwd = findViewById(R.id.password);
                showpwd.setText(intent.getStringExtra("getpwd"));
                break;
        }
    }
}
