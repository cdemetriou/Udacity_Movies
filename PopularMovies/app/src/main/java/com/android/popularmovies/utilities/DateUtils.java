package com.android.popularmovies.utilities;

/**
 * Created by Christos on 18/12/2017.
 */

public class DateUtils {

    public static String getYear(String date) {
        String str[] = date.split("-");
        return str[0];
    }
}
