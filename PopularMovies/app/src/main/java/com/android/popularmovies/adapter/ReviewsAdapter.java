package com.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.android.popularmovies.dataModel.Review;

import java.util.List;

/**
 * Created by Christos on 18/12/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {



    private List<Review> reviewList;

    public ReviewsAdapter (List<Review> reviewList){
        this.reviewList = reviewList;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.authorTView.setText(review.getAuthor());
        holder.contentTView.setText(review.getContent());
        holder.contentTView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public int getItemCount() {
        if (null == reviewList) return 0;
        return reviewList.size();
    }

    public void setNewReviewData(List<Review> newData) {
        reviewList.clear();
        reviewList.addAll(newData);
        notifyDataSetChanged();
    }

    class ReviewHolder  extends RecyclerView.ViewHolder {

        private final TextView authorTView;
        private final TextView contentTView;

        private ReviewHolder(View view) {
            super(view);
            authorTView = view.findViewById(R.id.author);
            contentTView = view.findViewById(R.id.content);
        }
    }
}
