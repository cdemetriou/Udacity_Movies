package com.android.popularmovies.dataModel;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {

    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185//";

    private String id;
    private String title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;


    public Movie(String id, String title, String poster_path, String vote_average, String overview, String release_date) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public String getId() {
        return id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTitle() {return title;}

    public String getVote_average() {return vote_average;}


    private Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster_path = in.readString();
        vote_average = in.readString();
        overview = in.readString();
        release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(vote_average);
        dest.writeString(overview);
        dest.writeString(release_date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}