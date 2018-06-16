package com.example.onepiece.mainPage;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onepiece.R;
import com.example.onepiece.db.MyDataBaseHelper;
import com.example.onepiece.model.DownloadFile;
import com.example.onepiece.model.Query;
import com.example.onepiece.model.SearchResultBean;
import com.example.onepiece.model.SearchResultItem;
import com.example.onepiece.model.Song;
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
        public TextView mSongTitle;
        public ImageView mSongToken;

        public ResultHolder(View itemView) {
            super(itemView);
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
            holder.mSongToken.setImageResource(searchResultItem.getToken() == 1 ? R.drawable.play : R.drawable.download);
            if (searchResultItem.getToken() == 0) {
                holder.mSongToken.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resourceDownload(searchResultItem.getTitle() + ".mp3", "audio", searchResultItem.getAudio());
                    }
                });
            }
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

    void resourceDownload(final String fileName, String requestType, int fileId) {
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi api = retrofit.create(HttpUtils.MyApi.class);
        DownloadFile df = new DownloadFile();
        df.setFileId(fileId);
        df.setRequestType(requestType);
        api.downloadFile(df)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        FileUtils.writeResponseBodyToFile(fileName, responseBody);
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {
                        Toast.makeText(getActivity(), "download completed", Toast.LENGTH_SHORT).show();
                        String filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC) + File.separator + fileName;
                        MediaScannerConnection.scanFile(getActivity(), new String[]{filePath}, null, null);
                    }
                });
    }
}
