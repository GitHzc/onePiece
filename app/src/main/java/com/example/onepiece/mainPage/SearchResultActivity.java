package com.example.onepiece.mainPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.onepiece.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayAdapter<String> mAdatper;
    private List<String> titles = new ArrayList<>();
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init_search();
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");
        mSearchView.setQuery(query, false);
        mAdatper.getFilter().filter(query);
    }

    //初始化搜索框
    private void init_search() {
        // 获取权限
        if (ContextCompat.checkSelfPermission(SearchResultActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchResultActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getSearchHint();
        }

        mImageView = findViewById(R.id.result_back);
        mSearchView = findViewById(R.id.result_search_view);
        mListView = findViewById(R.id.result_search_list);
        mAdatper = new ArrayAdapter<String>(SearchResultActivity.this, android.R.layout.simple_list_item_1, titles);
        mListView.setAdapter(mAdatper);
        mListView.setTextFilterEnabled(true);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    mListView.setVisibility(View.INVISIBLE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    mAdatper.getFilter().filter(newText);
                    mAdatper.notifyDataSetChanged();
                }
                return false;
            }
        });

        // 搜索结果列表项点击回调
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchResultActivity.this, mAdatper.getItem(i), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 权限请求结果回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getSearchHint();
        } else {
            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
        }
    }

    // 获取搜索提示
    private void getSearchHint() {
        if (titles.size() != 0) {
            titles.clear();
        }

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null
                , null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            titles.add(title);
        }

        cursor.close();
    }
}
