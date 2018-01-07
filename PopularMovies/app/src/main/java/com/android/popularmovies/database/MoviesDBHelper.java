package com.android.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Christos on 05/01/2018.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 2;

    MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + Contract.MovieEntry.TABLE_NAME
                + " (" +
                Contract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                Contract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                Contract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                Contract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                Contract.MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                Contract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, " +
                Contract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
