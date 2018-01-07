package com.android.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Christos on 05/01/2018.
 */

public class MoviesContentProvider extends ContentProvider {

    private MoviesDBHelper dbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static final int MOVIES = 100;


    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_MOVIE, MOVIES);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MoviesDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match) {
            case MOVIES:
                cursor = myDB.query(
                        Contract.MovieEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = myDB.insert(Contract.MovieEntry.TABLE_NAME, null, contentValues);
                if ( id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.MovieEntry.CONTENT_URI, id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int rowsDeleted;

        if (null == s) {
            s = "1";
        }
        switch (match) {
            case MOVIES:
                rowsDeleted = myDB.delete(
                        Contract.MovieEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
