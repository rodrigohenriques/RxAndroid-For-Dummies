package com.github.rodrigohenriques.rx.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.github.rodrigohenriques.rx.R;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.buttonOpenLocationSample) Button buttonOpenLocationSample;
    @BindView(R.id.buttonOpenHttpRequestSample) Button buttonOpenHttpRequestSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        RxView.clicks(buttonOpenLocationSample)
                .compose(bindToLifecycle())
                .subscribe(this::showLocationSample);

        RxView.clicks(buttonOpenHttpRequestSample)
                .compose(bindToLifecycle())
                .subscribe(this::showHttpRequestSample);
    }

    public void showLocationSample(Void aVoid) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void showHttpRequestSample(Void aVoid) {
        Intent intent = new Intent(this, SearchTvShowActivity.class);
        startActivity(intent);
    }
}
