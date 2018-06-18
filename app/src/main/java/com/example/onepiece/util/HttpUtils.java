package com.example.onepiece.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.onepiece.model.DiscoveryBean;
import com.example.onepiece.model.DownloadFile;
import com.example.onepiece.model.OneBean;
import com.example.onepiece.model.Query;
import com.example.onepiece.model.SearchResultBean;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2018/6/11 0011.
 */

public class HttpUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface MyApi {
        @Headers("Content-Type:application/json")
        @POST("music/query")
        Observable<List<SearchResultBean>> searchForQuery(@Body Query query);

        @Headers("Content-Type:application/json")
        @POST("music/files")
        Observable<ResponseBody> downloadFile(@Body DownloadFile df);

        @Headers("Content-Type:application/json")
        @GET("one/discovery/get")
        Observable<List<DiscoveryBean>> fetchDiscovery();

        @GET("one/daily")
        Observable<OneBean> fetchOne();

        @GET("media/one/{picName}")
        Observable<ResponseBody> getOnePicture(@Path("picName") String picName);
    }

    public static Retrofit getRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient =  new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://172.18.157.244:55555/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
