package com.android.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
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

import com.android.popularmovies.utilities.NetworkUtils;
import com.android.popularmovies.utilities.MovieDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Movie> dataList;
    private RecyclerView mRecyclerView;
    private MoviesAdapter movieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private String sort_by = NetworkUtils.MOVIE_POPULAR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager.setSpanCount(2);
        else gridLayoutManager.setSpanCount(3);

        dataList = new ArrayList<>();
        movieAdapter = new MoviesAdapter(this, this, dataList);
        mRecyclerView.setAdapter(movieAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            if (NetworkUtils.isNetworkAvailable(this)) loadData();
            else showErrorMessage();
        }
        else {
            dataList = savedInstanceState.getParcelableArrayList("movies");
            sort_by = savedInstanceState.getString("sort_by");
            movieAdapter.setNewData(dataList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("sort_by", sort_by);
        bundle.putParcelableArrayList("movies", (ArrayList<? extends Parcelable>) dataList);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        sort_by = bundle.getString("sort_by");
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        showDataView();
        new FetchMovies().execute(sort_by);
    }

    /* First, make sure the error is invisible
    Then, make sure the weather data is visible */
    private void showDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /* First, hide the currently visible data
    Then, show the error */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {

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
                return MovieDBJsonUtils.getSimpleMovieStringsFromJson(MainActivity.this, jsonResponse);

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


}
