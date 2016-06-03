package com.github.rodrigohenriques.rx.api;

import com.github.rodrigohenriques.rx.model.Episode;
import com.github.rodrigohenriques.rx.model.QueryResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface OmdbApi {
    @GET("?plot=short&r=json&type=episode")
    Observable<List<Episode>> findEpisodeInfoByImdbId(@Query("i") String imdbId);

    @GET("/")
    Observable<QueryResult> queryByNameAndSeason(@Query("t") String name, @Query("season") String season);
}
