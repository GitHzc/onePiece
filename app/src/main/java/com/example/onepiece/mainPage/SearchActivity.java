package com.example.onepiece.mainPage;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.model.Query;
import com.example.onepiece.model.SearchResultBean;
import com.example.onepiece.model.SearchResultItem;
import com.example.onepiece.util.DebugMessage;
import com.example.onepiece.util.HttpUtils;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.activity_search_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_search_fragment_container);
        if (fragment == null) {
            fragment = new HistoryFragment();
            fm.beginTransaction().add(R.id.activity_search_fragment_container, fragment, HistoryFragment.TAG).commit();
        }

        final SearchView searchView = findViewById(R.id.activity_search_search_view);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
                int pointer = sharedPreferences.getInt("pointer", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (int i = pointer + 1; i > 1; i--) {
                    String temp = sharedPreferences.getString(String.valueOf(i - 1), null);
                    editor.putString(String.valueOf(i), temp);
                }
                editor.putString(String.valueOf(1), query);
                pointer = Math.min(pointer + 1, 20);
                editor.putInt("pointer", pointer).apply();

                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.activity_search_fragment_container);
                if (fragment.getTag().equals(HistoryFragment.TAG)) {
                    fm.beginTransaction().replace(R.id.activity_search_fragment_container, new SearchResultFragment(), SearchResultFragment.TAG).commit();
                } else {
                    ((SearchResultFragment)fragment).updateUI(searchView.getQuery().toString());
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
