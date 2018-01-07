package com.android.popularmovies.utilities;


public class DateUtils {

    public static String getYear(String date) {
        String str[] = date.split("-");
        return str[0];
    }
}
