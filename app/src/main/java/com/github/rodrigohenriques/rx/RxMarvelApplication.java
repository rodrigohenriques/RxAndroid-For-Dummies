package com.github.rodrigohenriques.rx;

import android.app.Application;

import com.github.rodrigohenriques.rx.infrastructure.di.component.ApplicationComponent;
import com.github.rodrigohenriques.rx.infrastructure.di.component.DaggerApplicationComponent;
import com.github.rodrigohenriques.rx.infrastructure.di.module.ApplicationModule;

public class RxMarvelApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
