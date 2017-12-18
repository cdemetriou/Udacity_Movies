package com.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Christos on 09/12/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Context mContext;
    private List<Movie> movieList;
    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(Context context, MovieAdapterOnClickHandler clickHandler, List<Movie> dataList) {
        movieList = dataList;
        mContext = context;
        mClickHandler = clickHandler;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        Picasso.with(mContext)
                .load(Movie.POSTER_BASE_URL + movie.getPoster_path())
                .into(holder.movieImageView);
    }

    @Override
    public int getItemCount() {
        if (null == movieList) return 0;
        return movieList.size();
    }


    public void setNewData(List<Movie> newData) {
        movieList.clear();
        movieList.addAll(newData);
        notifyDataSetChanged();
    }


    class MovieViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView movieImageView;

        public MovieViewHolder (View view) {
            super(view);
            movieImageView = view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }
}
