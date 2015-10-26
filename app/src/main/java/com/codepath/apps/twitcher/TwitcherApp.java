package com.codepath.apps.twitcher;

import android.content.Context;

import com.codepath.apps.twitcher.net.TwitterClient;
import com.google.gson.Gson;


public class TwitcherApp extends com.activeandroid.app.Application {
	private static Context context;
	public static final Gson gson = new Gson();

	@Override
	public void onCreate() {
		super.onCreate();
		TwitcherApp.context = this;
	}

	public static TwitterClient getTwitterClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitcherApp.context);
	}
}