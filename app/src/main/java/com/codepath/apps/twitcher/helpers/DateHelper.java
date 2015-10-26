package com.codepath.apps.twitcher.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dmaskev on 10/25/15.
 */
public class DateHelper {

    public static final SimpleDateFormat TWITTER_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");


    public static String getRelDateFormatted(Date date) {
        if(date == null) {
            return "";
        }

        return formatMillis(getRelDate(date));
    }

    public static long getRelDate(Date date) {
        if(date == null) {
            return 0L;
        }

        return System.currentTimeMillis() - date.getTime();
    }

    public static String formatMillis(long millisPassed) {
        int sec = 1000;
        int min = 60 * sec;
        int hour = 60 * min;
        int day = 24 * hour;
        int week = 7 * day;

        int delim = sec;
        String unit = "s";
        if(millisPassed >= week) {
            delim = week;
            unit = "w";
        } else if(millisPassed >= day) {
            delim = day;
            unit = "d";
        } else if(millisPassed >= hour) {
            delim = hour;
            unit = "h";
        } else if(millisPassed >= min) {
            delim = min;
            unit = "m";
        }

        int v = Double.valueOf(Math.ceil(millisPassed / delim)).intValue();
        return v + unit;
    }

    public static Date parseTwitterDate(String dateStr) {
        Date result = null;
        try {
            result = TWITTER_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            Log.e(DateHelper.class.getSimpleName(), "Failed to parse date " + dateStr, e);
        }

        return result;
    }
}
