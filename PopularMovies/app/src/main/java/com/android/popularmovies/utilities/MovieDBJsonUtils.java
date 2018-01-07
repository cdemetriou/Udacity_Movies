package com.android.popularmovies.utilities;

import com.android.popularmovies.Constants;
import com.android.popularmovies.dataModel.Movie;
import com.android.popularmovies.dataModel.Review;
import com.android.popularmovies.dataModel.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.popularmovies.Constants.*;


public class MovieDBJsonUtils {

    public static  List<Movie> getMovieListFromJsonString(String JsonStr) throws JSONException {

        JSONObject forecastJson = new JSONObject(JsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(RESULTS_STRING);

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < weatherArray.length(); i++) {

            /* Get the JSON object representing each movie */
            JSONObject movieObject = weatherArray.getJSONObject(i);

            String id = movieObject.optString(MOVIE_ID);
            String title = movieObject.optString(MOVIE_TITLE);
            String poster_path = movieObject.optString(MOVIE_POSTER_PATH);
            String vote_average = movieObject.optString(MOVIE_VOTE_AVERAGE);
            String overview = movieObject.optString(MOVIE_OVERVIEW);
            String release_date = movieObject.optString(MOVIE_RELEASE_DATE);

            Movie movie = new Movie(id, title, poster_path, vote_average, overview, release_date);
            movies.add(movie);
        }
        return movies;
    }

    public static  List<Review> getMovieReviewsFromJsonString(String JsonStr) throws JSONException {

        JSONObject forecastJson = new JSONObject(JsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(RESULTS_STRING);

        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < weatherArray.length(); i++) {

            /* Get the JSON object representing each movie */
            JSONObject movieObject = weatherArray.getJSONObject(i);

            String id = movieObject.optString(Constants.REVIEW_ID);
            String author = movieObject.optString(REVIEW_AUTHOR);
            String content = movieObject.optString(REVIEW_CONTENT);

            Review review = new Review(id, author, content);
            reviews.add(review);
        }
        return reviews;
    }


    public static  List<Video> getMovieVideosFromJsonString(String JsonStr) throws JSONException {

        JSONObject forecastJson = new JSONObject(JsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(RESULTS_STRING);

        List<Video> videos = new ArrayList<>();
        for (int i = 0; i < weatherArray.length(); i++) {

            /* Get the JSON object representing each movie */
            JSONObject movieObject = weatherArray.getJSONObject(i);

            String id = movieObject.optString(VIDEO_ID);
            String iso = movieObject.optString(VIDEO_ISO);
            String key = movieObject.optString(VIDEO_KEY);
            String name = movieObject.optString(VIDEO_NAME);
            String site = movieObject.optString(VIDEO_SITE);
            String size = movieObject.optString(VIDEO_SIZE);
            String type = movieObject.optString(VIDEO_TYPE);

            Video video = new Video(id, iso, key, name, site, size, type);
            videos.add(video);
        }
        return videos;
    }

}
