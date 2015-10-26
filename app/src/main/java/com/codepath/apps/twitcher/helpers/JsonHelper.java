package com.codepath.apps.twitcher.helpers;

import android.util.Log;

import com.codepath.apps.twitcher.TwitcherApp;
import com.codepath.apps.twitcher.models.Tweet;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by dmaskev on 10/24/15.
 */
public class JsonHelper {

    public static <T> List<T> parseJsonArray(byte[] data, Type type) {
        List<T> tweets = Collections.emptyList();
        String jsonString = new String(data);
        try {
            tweets = TwitcherApp.gson.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            Log.e(Tweet.class.getSimpleName(), "Failed to parse json " + jsonString, e);
        }

        return tweets;
    }

    public static <T> T parseJsonObject(byte[] data, Class<T> clazz) {
        T tweet = null;
        String jsonString = new String(data);
        try {
            tweet = TwitcherApp.gson.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            Log.e(Tweet.class.getSimpleName(), "Failed to parse json " + jsonString, e);
        }
        return tweet;
    }


}
