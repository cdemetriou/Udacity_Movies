package com.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.popularmovies.dataModel.Movie;
import com.android.popularmovies.utilities.DateUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    // private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.detail_activity_title);
        }

        Movie movie = getIntent().getParcelableExtra(getString(R.string.movie_identifier));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
