package com.ap.project.webrtcmobile.interactors;


import com.ap.project.webrtcmobile.models.ClientInfo;
import com.ap.project.webrtcmobile.utils.ServerInfoInteractor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;


public class ApiInteractor {

    private static ApiService apiService;


    public static synchronized ApiService getHelloApiService() {
        if (apiService == null) {
            apiService = createApiService();
        }
        return apiService;
    }


    private static ApiService createApiService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(ServerInfoInteractor.getServerURL())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    public interface ApiService {

        @GET("onlineUsers")
        Observable<List<ClientInfo>> getOnlineUsers();
    }

}
