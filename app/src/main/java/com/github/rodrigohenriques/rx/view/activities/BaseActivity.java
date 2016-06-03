package com.github.rodrigohenriques.rx.view.activities;

import com.github.rodrigohenriques.rx.RxMarvelApplication;
import com.github.rodrigohenriques.rx.infrastructure.di.component.ApplicationComponent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public abstract class BaseActivity extends RxAppCompatActivity {
    public ApplicationComponent getApplicationComponent() {
        RxMarvelApplication rxMarvelApplication = (RxMarvelApplication) getApplication();
        return rxMarvelApplication.getApplicationComponent();
    }
}
