package com.github.rodrigohenriques.rx.model;

public class QueryRequest {
    private final String tvShow;
    private final String season;

    public QueryRequest(String tvShow, String season) {
        this.tvShow = tvShow;
        this.season = season;
    }

    public String getTvShow() {
        return tvShow;
    }

    public String getSeason() {
        return season;
    }

    public boolean isValid() {
        return tvShow != null && season != null && tvShow.length() > 0 && season.length() > 0;
    }
}
