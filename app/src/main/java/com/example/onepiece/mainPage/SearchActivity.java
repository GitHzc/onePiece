package com.example.onepiece.mainPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.onepiece.R;
import com.example.onepiece.util.Utility;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private SearchView msearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        msearchView = findViewById(R.id.search_view);

        msearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Utility.showSoftKeyboard(view, SearchActivity.this);
                }
            }
        });

        // 搜索按钮点击回调
        msearchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                Utility.closeSoftKeyboard(msearchView, SearchActivity.this);
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("query", string);
                startActivity(intent);
                finish();
            }
        });

        // 返回按钮点击回调
        msearchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });
    }


}
