package com.universe.yz.admin.model.net;

import android.util.Log;

import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.model.http.api.GankApis;
import com.universe.yz.admin.model.http.api.UserApis;
import com.universe.yz.admin.model.http.api.VideoApis;
import com.universe.yz.admin.utils.KL;
import com.universe.yz.admin.utils.SystemUtils;
import com.universe.yz.admin.model.http.api.CronousApis;
import com.universe.yz.admin.ui.fragments.MineFragment;
import com.universe.yz.admin.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description: RetrofitHelper1
 * Creator: yxc
 * date: 2016/9/21 10:03
 */
public class RetrofitHelper {
    private static final String TAG = RetrofitHelper.class.getSimpleName();
    private static OkHttpClient okHttpClient = null;
    private static VideoApis videoApi;
    private static GankApis gankApis;
    private static UserApis userApis;
    private static CronousApis cronousApis;

    public static CronousApis getMemberApi() {
        initOkHttp();
        if (cronousApis == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(CronousApis.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            cronousApis = retrofit.create(CronousApis.class);
        }
        return cronousApis;
    }

    public static UserApis getUserApi() {
        initOkHttp();
        if (userApis == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(UserApis.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            userApis = retrofit.create(UserApis.class);
        }
        return userApis;
    }

    public static VideoApis getVideoApi() {
        initOkHttp();
        if (videoApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(VideoApis.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            videoApi = retrofit.create(VideoApis.class);
        }
        return videoApi;
    }
    public static GankApis getGankApis() {
        initOkHttp();
        if (gankApis == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(GankApis.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            gankApis = retrofit.create(GankApis.class);
        }
        return gankApis;
    }

    private static void initOkHttp() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                builder.addInterceptor(loggingInterceptor);
            }
            File cacheFile = new File(Constants.PATH_CACHE);
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);

            Interceptor cacheInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request;

                    if("DELETE".equals(original.method())) {
                        request= original.newBuilder()
                                .addHeader("authorization", MineFragment.uuid)
                                .build();
                        Log.e(TAG, "Request Header(uuid):" + MineFragment.uuid);
                    }else {
                        request = original;
                    }

                    if (!SystemUtils.isNetworkConnected()) {
                        if(!"DELETE".equals(original.method())) {
                            request= original.newBuilder()
                                    .cacheControl(CacheControl.FORCE_CACHE)
                                    .build();
                        }else {
                            request= original.newBuilder()
                                    .addHeader("authorization", MineFragment.uuid)
                                    .build();
                            Log.e(TAG, "Request Header:" + MineFragment.uuid);
                        }
                    }

                    int tryCount = 0;
                    Response response = chain.proceed(request);

                    while (!response.isSuccessful() && tryCount < 3) {
                        KL.d(RetrofitHelper.class, "interceptRequest is not successful - :{}", tryCount);
                        tryCount++;
                        // retry the request
                        response = chain.proceed(request);
                    }

                    if (SystemUtils.isNetworkConnected()) {
                        int maxAge = 0;
                        // 有网络时, 不缓存, 最大保存时长为0
                        response.newBuilder()
                                .header("Cache-Control", "public, max-age=" + maxAge)
                                .removeHeader("Pragma")
                                .build();
                    } else {
                        // 无网络时，设置超时为4周
                        int maxStale = 60 * 60 * 24 * 28;
                        response.newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .removeHeader("Pragma")
                                .build();
                    }

                    return response;
                }
            };

            //设置缓存
            builder.addNetworkInterceptor(cacheInterceptor);
            builder.addInterceptor(cacheInterceptor);
            builder.cache(cache);

            //设置超时
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(20, TimeUnit.SECONDS);

            //错误重连
            builder.retryOnConnectionFailure(true);
            okHttpClient = builder.build();
        }
    }
}
