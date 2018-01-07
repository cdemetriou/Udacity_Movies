package com.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.popularmovies.adapter.ReviewsAdapter;
import com.android.popularmovies.adapter.VideosAdapter;
import com.android.popularmovies.dataModel.Movie;
import com.android.popularmovies.dataModel.Review;
import com.android.popularmovies.dataModel.Video;
import com.android.popularmovies.database.Contract;
import com.android.popularmovies.utilities.DateUtils;
import com.android.popularmovies.utilities.MovieDBJsonUtils;
import com.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements VideosAdapter.VideoAdapterOnClickHandler {

    //private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String YOUTUBE_VIDEO_BASE = "https://www.youtube.com/watch?v=";

    private Movie movie;

    private List<Review> reviewsList = new ArrayList<>();
    private ReviewsAdapter reviewsAdapter;

    private List<Video> videosList = new ArrayList<>();
    private VideosAdapter videosAdapter;

    private boolean isFavorite = false;

    private TextView videosErrorMessage, reviewsErrorMessage;
    private ProgressBar videosLoadingIndicator, reviewsLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.detail_activity_title);
        }

        movie = getIntent().getParcelableExtra(getString(R.string.movie_identifier));

        setUpExistingContent();
        setUpReviews();
        setUpVideos();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkUtils.isNetworkAvailable(this)) {
            reviewsLoadingIndicator.setVisibility(View.VISIBLE);
            videosLoadingIndicator.setVisibility(View.VISIBLE);
            loadTrailers();
            loadReviews();
        }
        else {
            reviewsErrorMessage.setText(R.string.network_error_message);
            videosErrorMessage.setText(R.string.network_error_message);
            showErrorMessage();
        }

        new isFavorite().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                sharePressed();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(Video video) {
        String url = YOUTUBE_VIDEO_BASE.concat(video.getKey());
        Intent youtube_intent = new Intent(Intent.ACTION_VIEW);
        youtube_intent.setData(Uri.parse(url));
        startActivity(youtube_intent);
    }


    private void setUpExistingContent() {
        videosErrorMessage = findViewById(R.id.videos_error_message_display);
        reviewsErrorMessage = findViewById(R.id.reviews_error_message_display);

        videosLoadingIndicator = findViewById(R.id.videos_loading_indicator);
        reviewsLoadingIndicator = findViewById(R.id.reviews_loading_indicator);

        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText(movie.getTitle());

        ImageView ivPoster = findViewById(R.id.image);
        Picasso.with(this)
                .load(Movie.POSTER_BASE_URL + movie.getPoster_path())
                .into(ivPoster);

        TextView tvDescription = findViewById(R.id.description);
        tvDescription.setText(movie.getOverview());

        TextView tvDate = findViewById(R.id.date);
        tvDate.setText(DateUtils.getYear(movie.getRelease_date()));

        TextView tvRating = findViewById(R.id.rating);
        tvRating.setText(String.format("%s/%s", movie.getVote_average(), getString(R.string.top_rating)));
    }


    private void setUpVideos() {
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(this);
        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView videosRecyclerView = findViewById(R.id.trailers_recyclerview);
        videosRecyclerView.setLayoutManager(videosLayoutManager);

        videosAdapter = new VideosAdapter(this, this);
        videosRecyclerView.setAdapter(videosAdapter);
    }


    private void setUpReviews() {
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView reviewsRecyclerView = findViewById(R.id.reviews_recyclerview);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

        reviewsAdapter = new ReviewsAdapter(reviewsList);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
    }


    private void showErrorMessage() {
        reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
        videosLoadingIndicator.setVisibility(View.INVISIBLE);
        videosErrorMessage.setVisibility(View.VISIBLE);
        reviewsErrorMessage.setVisibility(View.VISIBLE);
    }


    private void showDataView() {
        reviewsLoadingIndicator.setVisibility(View.INVISIBLE);
        videosLoadingIndicator.setVisibility(View.INVISIBLE);
        videosErrorMessage.setVisibility(View.INVISIBLE);
        reviewsErrorMessage.setVisibility(View.INVISIBLE);
    }


    private void loadReviews() {
        String url = NetworkUtils.MOVIE_REVIEWS;
        url = url.replace("{id}", movie.getId());
        new FetchReviews().execute(url);
    }


    private void loadTrailers() {
        String url = NetworkUtils.MOVIE_VIDEOS;
        url = url.replace("{id}", movie.getId());
        new FetchVideos().execute(url);
    }


    private void sharePressed() {
        Intent share_intent = new Intent(android.content.Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, movie.getTitle());
        share_intent.putExtra(Intent.EXTRA_TEXT, movie.getOverview());
        startActivity(Intent.createChooser(share_intent, getString(R.string.share_title)));
    }


    public void favoritePressed(View v) {
        if (isFavorite) {
            new RemoveFavorite().execute();
        }
        else {
            new AddFavorite().execute();
        }
    }


    private void changeFavoriteIcon() {
        ImageButton favoriteButton = findViewById(R.id.favorite_icon);
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.heart_icon_filled);
        }
        else {
            favoriteButton.setImageResource(R.drawable.heart_icon_empty);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchReviews extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Review> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String url = params[0];
            URL requestUrl = NetworkUtils.buildUrl(url);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return MovieDBJsonUtils.getMovieReviewsFromJsonString(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviewList) {
            if (reviewList == null) {
                showErrorMessage();
                return;
            }
            reviewsList = reviewList;
            showDataView();
            reviewsAdapter.setNewReviewData(reviewsList);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchVideos extends AsyncTask<String, Void, List<Video>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Video> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String url = params[0];
            URL requestUrl = NetworkUtils.buildUrl(url);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return MovieDBJsonUtils.getMovieVideosFromJsonString(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Video> reviewList) {
            if (reviewList == null) {
                showErrorMessage();
                return;
            }
            videosList = reviewList;

            showDataView();
            videosAdapter.setNewReviewData(videosList);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class isFavorite extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Cursor cursor = getContentResolver().query(Contract.MovieEntry.CONTENT_URI,
                    new String[]{Contract.MovieEntry.COLUMN_MOVIE_ID},
                    Contract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(),
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isFavorited) {
            isFavorite = isFavorited;
            changeFavoriteIcon();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AddFavorite extends AsyncTask<String, Void, Uri> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(String... params) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(Contract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            contentValues.put(Contract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPoster_path());
            contentValues.put(Contract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            contentValues.put(Contract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVote_average());
            contentValues.put(Contract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getRelease_date());

            return getContentResolver().insert(Contract.MovieEntry.CONTENT_URI, contentValues);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            isFavorite= true;
            changeFavoriteIcon();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RemoveFavorite extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            return getContentResolver().delete(Contract.MovieEntry.CONTENT_URI, Contract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null) > 0;
        }

        @Override
        protected void onPostExecute(Boolean removed) {
            if (removed) {
                isFavorite = false;
                changeFavoriteIcon();
            }
        }
    }


}
