package com.github.rodrigohenriques.rx.model;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("Title") public String title;
    @SerializedName("Released") public String released;
    @SerializedName("Episode") public String episode;
    @SerializedName("imdbRating") public String imdbRating;
    @SerializedName("imdbID") public String imdbID;
}
