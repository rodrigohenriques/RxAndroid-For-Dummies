package com.github.rodrigohenriques.rx.infrastructure.di.module;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

import com.github.rodrigohenriques.rx.api.OmdbApi;
import com.github.rodrigohenriques.rx.infrastructure.di.qualifier.Background;
import com.github.rodrigohenriques.rx.infrastructure.di.qualifier.OmdbApiHost;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

@Module
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides public Context provideContext() {
        return context;
    }

    @Provides @Background public Scheduler provideBackgroundScheduler() {
        HandlerThread handlerThread = new HandlerThread("background-handler-thread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        return AndroidSchedulers.from(looper);
    }

    @Provides public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides @OmdbApiHost public String provideMarvelApiHost() {
        return "http://www.omdbapi.com";
    }

    @Provides public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides public Converter.Factory provideConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides public OmdbApi provideMarvelApi(@OmdbApiHost String omdbApiHost, OkHttpClient okHttpClient, Converter.Factory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(omdbApiHost)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build()
                .create(OmdbApi.class);
    }

    @Provides public ReactiveLocationProvider provideReactiveLocationProvider(Context applicationContext) {
        return new ReactiveLocationProvider(applicationContext);
    }
}
