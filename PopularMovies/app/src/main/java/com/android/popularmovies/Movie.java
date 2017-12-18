package com.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Christos on 09/12/2017.
 */

public class Movie implements Parcelable {

    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185//";

    String id;
    String title;
    String poster_path;
    String vote_average;
    String overview;
    String release_date;

    public Movie() {
    }

    public Movie(String id, String title, String poster_path, String vote_average, String overview, String release_date) {
        this.id = id;
        this.title = title;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.overview = overview;
        this.release_date = release_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getVote_average() {return vote_average;}

    public void setVote_average(String vote_average) {this.vote_average = vote_average;}

    protected Movie(Parcel in) {
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