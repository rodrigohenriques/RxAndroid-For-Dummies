package com.github.rodrigohenriques.rx.infrastructure.di.component;

import com.github.rodrigohenriques.rx.view.activities.LocationActivity;
import com.github.rodrigohenriques.rx.infrastructure.di.module.ApplicationModule;
import com.github.rodrigohenriques.rx.view.activities.SearchTvShowActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component( modules = { ApplicationModule.class } )
public interface ApplicationComponent {
    void inject(LocationActivity locationActivity);
    void inject(SearchTvShowActivity searchTvShowActivity);
}
