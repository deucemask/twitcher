package com.codepath.apps.twitcher.net;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.twitcher.models.Tweet;
import com.codepath.apps.twitcher.models.User;
import com.codepath.oauth.OAuthBaseClient;
import com.google.common.collect.ImmutableMap;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static abstract class Callback {
		public void onSuccess(List<Tweet> tweets) {
			throw new IllegalStateException("Callback.onSuccess(List<Tweet>) was called but not overridden.");
		}
		public void onSuccess(Tweet tweet) {
			throw new IllegalStateException("Callback.onSuccess(Tweet) was called but not overridden.");
		}
		public void onSuccess(User user) {
			throw new IllegalStateException("Callback.onSuccess(User) was called but not overridden.");
		}

		abstract public void onFailure(int statusCode, String error, Throwable t);
	}

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "HXTBZtHVYUWwb0M7FQByeufFC";       // Change this
	public static final String REST_CONSUMER_SECRET = "RNuDFabazhMyBgqxi5UL0tRLfF2G6sW98wSMoSsV42Wi5PfPXV"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://twitcherdm"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getFeed(long lastTweetId, int count, final Callback callback) {
		final String url = getApiUrl("statuses/home_timeline.json");
		final Map<String, String> paramMap = new HashMap<>(ImmutableMap.of("count", String.valueOf(count), "since_id", "1"));
		if(lastTweetId > 0 ) {
			paramMap.put("max_id", String.valueOf(lastTweetId - 1));
		}

		Log.d(TwitterClient.class.getSimpleName(), "Calling " + url + " with " + paramMap);

		RequestParams params = new RequestParams(paramMap);
		client.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.d(TwitterClient.class.getSimpleName(), "Result for " + url + " with " + paramMap + ": " + new String(responseBody));
				callback.onSuccess(Tweet.fromJsonArray(responseBody));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				callback.onFailure(statusCode, new String(responseBody), error);
			}
		});
	}

	public void getRateLimit(JsonHttpResponseHandler callback) {
		String url = getApiUrl("application/rate_limit_status.json");
		RequestParams params = new RequestParams();
		client.get(url, params, callback);
	}

	public void getProfile(final Callback callback) {
		String url = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams(ImmutableMap.of("include_entities", "false", "skip_status", "true"));
		client.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				callback.onSuccess(User.fromJson(responseBody));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				callback.onFailure(statusCode, new String(responseBody), error);
			}
		});
	}

	public void postTweet(final String message, final Callback callback) {
		String url = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams(ImmutableMap.of("status", message));
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				callback.onSuccess(Tweet.fromJson(responseBody));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				callback.onFailure(statusCode, new String(responseBody), error);
			}
		});
	}
}