package com.android.popularmovies.utilities;

import com.android.popularmovies.dataModel.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MovieDBJsonUtils {

    public static  List<Movie> getSimpleMovieStringsFromJson(String forecastJsonStr) throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray("results");

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < weatherArray.length(); i++) {

            /* Get the JSON object representing each movie */
            JSONObject movieObject = weatherArray.getJSONObject(i);

            String id = movieObject.optString("id");
            String title = movieObject.optString("title");
            String poster_path = movieObject.optString("poster_path");
            String vote_average = movieObject.optString("vote_average");
            String overview = movieObject.optString("overview");
            String release_date = movieObject.optString("release_date");

            Movie movie = new Movie(id, title, poster_path, vote_average, overview, release_date);
            movies.add(movie);
        }
        return movies;
    }

}
