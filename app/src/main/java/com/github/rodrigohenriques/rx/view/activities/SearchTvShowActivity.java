package com.github.rodrigohenriques.rx.view.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.github.rodrigohenriques.rx.R;
import com.github.rodrigohenriques.rx.api.OmdbApi;
import com.github.rodrigohenriques.rx.infrastructure.di.qualifier.Background;
import com.github.rodrigohenriques.rx.model.QueryRequest;
import com.github.rodrigohenriques.rx.model.QueryResult;
import com.github.rodrigohenriques.rx.view.adapter.DividerItemDecoration;
import com.github.rodrigohenriques.rx.view.adapter.EpisodeAdapter;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class SearchTvShowActivity extends BaseActivity {

    @BindView(R.id.edittext_tv_show) EditText editTextTvShow;
    @BindView(R.id.edittext_season) EditText editTextSeason;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.button_query) Button buttonQuery;

    @Inject OmdbApi omdbApi;
    @Inject @Background Scheduler backgroundScheduler;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tv_show);

        getApplicationComponent().inject(this);

        ButterKnife.bind(this);

        RxTextView.textChanges(editTextTvShow)
                .compose(bindToLifecycle())
                .subscribe(this::toastChange, this::handleError);

        RxView.clicks(buttonQuery)
                .compose(bindToLifecycle())
                .map(this::createQueryRequest)
                .filter(QueryRequest::isValid)
                .subscribe(this::performQuery);
    }

    private void toastChange(CharSequence charSequence) {
        Snackbar.make(recyclerView, "Text Changed: " + charSequence, Snackbar.LENGTH_SHORT).show();
    }

    private QueryRequest createQueryRequest(Void aVoid) {
        return new QueryRequest(getTvShow(), getSeason());
    }

    @NonNull
    private String getSeason() {
        return editTextSeason.getText().toString();
    }

    @NonNull
    private String getTvShow() {
        return editTextTvShow.getText().toString();
    }

    private void performQuery(QueryRequest queryRequest) {
        omdbApi.queryByNameAndSeason(queryRequest.getTvShow(), queryRequest.getSeason())
                .subscribeOn(backgroundScheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::showLoading)
                .doOnCompleted(this::hideLoading)
                .doOnError(t -> hideLoading())
                .subscribe(this::showQueryResult, this::handleError);
    }

    private void handleError(Throwable throwable) {
        Log.e("Retrofit", throwable.getMessage(), throwable);
        Snackbar.make(recyclerView, "shit happened: " + throwable.getMessage(), Snackbar.LENGTH_LONG).show();
        recyclerView.setAdapter(null);
    }

    private void showQueryResult(QueryResult queryResult) {
        recyclerView.setAdapter(new EpisodeAdapter(queryResult.episodes));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void showLoading() {
        progressDialog = ProgressDialog.show(this, "Aguarde", "Carregando lista de episódios...");
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
