package com.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.popularmovies.adapter.MoviesAdapter;
import com.android.popularmovies.dataModel.Movie;
import com.android.popularmovies.database.Contract;
import com.android.popularmovies.utilities.MovieDBJsonUtils;
import com.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterOnClickHandler {

    //private static final String TAG = MainActivity.class.getSimpleName();

    private Bundle savedInstance;

    private List<Movie> dataList = new ArrayList<>();
    private MoviesAdapter movieAdapter;

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private String sort_by = NetworkUtils.MOVIE_POPULAR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else gridLayoutManager.setSpanCount(3);

        movieAdapter = new MoviesAdapter(this, this, dataList);
        mRecyclerView.setAdapter(movieAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(savedInstance == null || !savedInstance.containsKey(getString(R.string.movie_list_identifier))) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                loadData();
            }
            else {
                sort_by = NetworkUtils.MOVIE_FAVORITES;
                new FetchFavorites().execute();

                mErrorMessageDisplay.setText(R.string.network_error_message);
                showErrorMessage();
            }
        }
        else {
            dataList = savedInstance.getParcelableArrayList(getString(R.string.movie_list_identifier));
            sort_by = savedInstance.getString(getString(R.string.sort_option_identifier));
            movieAdapter.setNewData(dataList);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString(getString(R.string.sort_option_identifier), sort_by);
        bundle.putParcelableArrayList(getString(R.string.movie_list_identifier), (ArrayList<? extends Parcelable>) dataList);
        super.onSaveInstanceState(bundle);
    }


    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        sort_by = bundle.getString(getString(R.string.sort_option_identifier));
        super.onRestoreInstanceState(bundle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list_activity, menu);

        switch (sort_by) {
            case NetworkUtils.MOVIE_POPULAR:
                menu.findItem(R.id.sort_by_most_popular).setChecked(true);
                break;
            case NetworkUtils.MOVIE_TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case NetworkUtils.MOVIE_FAVORITES:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                sort_by = NetworkUtils.MOVIE_TOP_RATED;
                if (NetworkUtils.isNetworkAvailable(this)) loadData();
                item.setChecked(true);
                break;
            case R.id.sort_by_most_popular:
                sort_by = NetworkUtils.MOVIE_POPULAR;
                if (NetworkUtils.isNetworkAvailable(this)) loadData();
                item.setChecked(true);
                break;
            case R.id.sort_by_favorites:
                sort_by = NetworkUtils.MOVIE_FAVORITES;
                loadData();
                item.setChecked(true);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadData() {
        showDataView();
        if (Objects.equals(sort_by, NetworkUtils.MOVIE_FAVORITES)) {
            new FetchFavorites().execute();
        }
        else new FetchMovies().execute(sort_by);
    }


    private void showDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra(getString(R.string.movie_identifier), movie);
        startActivity(intentToStartDetailActivity);
    }


    @SuppressLint("StaticFieldLeak")
    private class FetchMovies extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String movie_type = params[0];
            URL requestUrl = NetworkUtils.buildUrl(movie_type);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
                return MovieDBJsonUtils.getMovieListFromJsonString(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieList == null) {
                showErrorMessage();
                return;
            }
            dataList = movieList;
            showDataView();
            movieAdapter.setNewData(dataList);
        }
    }



    @SuppressLint("StaticFieldLeak")
    private class FetchFavorites extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            Cursor cursor = getContentResolver().query(Contract.MovieEntry.CONTENT_URI, null, null,null, null);

            List<Movie> movies = new ArrayList<>();

            try {
                assert cursor != null;
                if (cursor.moveToFirst()) {
                    do {
                        Movie movie = new Movie(cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(5),
                                cursor.getString(4),
                                cursor.getString(6));

                        movies.add(movie);
                    } while (cursor.moveToNext());
                }

            } finally {
                try {
                    assert cursor != null;
                    cursor.close(); } catch (Exception ignore) {}
            }

            return movies;

        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieList == null) {
                showErrorMessage();
                return;
            }
            dataList = movieList;
            showDataView();
            movieAdapter.setNewData(dataList);
        }
    }

}
