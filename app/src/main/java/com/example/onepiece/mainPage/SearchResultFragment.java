package com.example.onepiece.mainPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.db.MyDataBaseHelper;
import com.example.onepiece.model.DownloadFile;
import com.example.onepiece.model.Playlist;
import com.example.onepiece.model.Query;
import com.example.onepiece.model.SearchResultBean;
import com.example.onepiece.model.SearchResultItem;
import com.example.onepiece.model.Song;
import com.example.onepiece.player.PlayerActivity;
import com.example.onepiece.util.FileUtils;
import com.example.onepiece.util.HttpUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Environment.DIRECTORY_MUSIC;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class SearchResultFragment extends Fragment {
    public final static String TAG = "SearchResultFragment";
    private RecyclerView mRecyclerView;
    private ResultAdapter mResultAdapter;
    private SearchView mSearchView;
    private List<SearchResultItem> mResults;
    private List<SearchResultBean> mSearchResultBeans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.search_result_fragment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchView = getActivity().findViewById(R.id.activity_search_search_view);
        updateUI(mSearchView.getQuery().toString());
        return view;
    }

    private class ResultHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mSongTitle;
        public ImageView mSongToken;

        public ResultHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mSongTitle = itemView.findViewById(R.id.search_result_fragment_item_title);
            mSongToken = itemView.findViewById(R.id.search_result_fragment_item_token);
        }
    }

    private class ResultAdapter extends RecyclerView.Adapter<ResultHolder> {
        private List<SearchResultItem> mResults;

        public ResultAdapter(List<SearchResultItem> results) {
            mResults = results;
        }

        @NonNull
        @Override
        public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.search_result_recycler_view_item, parent, false);
            return new ResultHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
            final SearchResultItem searchResultItem = mResults.get(position);
            holder.mSongTitle.setText(searchResultItem.getTitle());
            holder.mSongToken.setImageResource(searchResultItem.getToken() == 1 ? R.drawable.play_icon : R.drawable.download);
            holder.mView.setTag(searchResultItem.getToken());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int token = (int)v.getTag();
                    if (token == 0) {
                        String musicDirectory = FileUtils.getMusicDirectory();
                        String fileName = musicDirectory + searchResultItem.getTitle() + ".mp3";
                        resourceDownload(fileName, "audio", searchResultItem.getAudio());
                        fileName = FileUtils.getLyricDirectory() + searchResultItem.getTitle() + ".lrc";
                        resourceDownload(fileName, "lyric", searchResultItem.getLyric());
                    } else {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
                        intent.putExtra("playlistTitle", "本地音乐");
                        intent.putExtra("index", Playlist.get(getActivity(), "本地音乐").getSongIndexByTitle(searchResultItem.getTitle()));
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }
    }

    public void updateUI(final String query) {
        Observable<List<SearchResultItem>> observable = Observable.create(new ObservableOnSubscribe<List<SearchResultItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchResultItem>> emitter) throws Exception {
                List<SearchResultItem> localResult = MyDataBaseHelper.get(getActivity(), "OnePiece", 1).querySong(getContext(), query);
                emitter.onNext(localResult);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<List<SearchResultItem>> observer = new Observer<List<SearchResultItem>>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(List<SearchResultItem> searchResultItems) {
                mResults = searchResultItems;
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {
                mResultAdapter = new ResultAdapter(mResults);
                mRecyclerView.setAdapter(mResultAdapter);
                searchForQuery(query);
            }
        };

        observable.subscribe(observer);
    }

    void searchForQuery(String query) {
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
        Query q = new Query();
        q.setQuery(query);

        api.searchForQuery(q)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SearchResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(List<SearchResultBean> searchResultBeans) {
                        mSearchResultBeans = searchResultBeans;
                        for (SearchResultBean searchResultBean : searchResultBeans) {
                            String title = searchResultBean.getTitle();
                            SearchResultItem searchResultItem = new SearchResultItem(title, null, 0);
                            searchResultItem.setAudio(searchResultBean.getAudio());
                            searchResultItem.setLyric(searchResultBean.getLyric());
                            mResults.add(searchResultItem);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                        mResultAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void resourceDownload(final String fileName, final String requestType, final int fileId) {
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
        DownloadFile df = new DownloadFile();
        df.setFileId(fileId);
        df.setRequestType(requestType);
        api.downloadFile(df)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(final ResponseBody responseBody) {
                        FileUtils.writeResponseBodyToFile(fileName, responseBody);
                        if (requestType.equals("audio")) {
                            MediaScannerConnection.scanFile(getActivity(), new String[]{fileName}, null, null);
                            String id = getSongIdByPath(getActivity(), fileName);
                            mapId(getActivity(), id, fileId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    public static String getSongIdByPath(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DATA + "=?",
                new String[]{filePath},
                null);
        cursor.moveToNext();
        String id = cursor.getString(0);
        cursor.close();
        return id;
    }

    public static void mapId(Context context, String fromId, int toId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(fromId, toId).apply();
    }
}
