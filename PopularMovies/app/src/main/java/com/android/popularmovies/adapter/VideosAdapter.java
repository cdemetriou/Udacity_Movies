package com.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.popularmovies.R;
import com.android.popularmovies.dataModel.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christos on 18/12/2017.
 */

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String YOUTUBE_BASE = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_CLOSINGTAG = "/hqdefault.jpg";

    private Context mContext;
    private List<Video> videoList;
    private final VideoAdapterOnClickHandler mClickHandler;

    public interface VideoAdapterOnClickHandler {
        void onClick(Video video);
    }
    public VideosAdapter(Context context, VideoAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.videoList = new ArrayList<>();
        this.mClickHandler = clickHandler;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.video_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String video_key = videoList.get(position).getKey();

        String thumbnailURL = YOUTUBE_BASE.concat(video_key).concat(YOUTUBE_CLOSINGTAG);

        Picasso.with(mContext)
                .load(thumbnailURL)
                .placeholder(R.drawable.video_placeholder)
                .into(((VideoViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        if (null == videoList) return 0;
        return videoList.size();
    }

    public void setNewReviewData(List<Video> newData) {
        videoList.clear();
        videoList.addAll(newData);
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;

        VideoViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.video_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video video = videoList.get(adapterPosition);
            mClickHandler.onClick(video);
        }

    }
}
