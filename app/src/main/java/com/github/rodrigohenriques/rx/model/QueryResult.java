package com.github.rodrigohenriques.rx.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    @SerializedName("Title") public String title;
    @SerializedName("Season") public String season;
    @SerializedName("Episodes") public List<Episode> episodes = new ArrayList<>();
    @SerializedName("Response") public boolean response;
}
