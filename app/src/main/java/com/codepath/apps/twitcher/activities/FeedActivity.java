package com.codepath.apps.twitcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.twitcher.R;
import com.codepath.apps.twitcher.adapters.FeedListAdapter;
import com.codepath.apps.twitcher.helpers.EndlessScrollListener;
import com.codepath.apps.twitcher.models.Tweet;
import com.codepath.apps.twitcher.models.User;
import com.codepath.apps.twitcher.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.color.holo_blue_bright;
import static android.R.color.holo_green_light;
import static android.R.color.holo_orange_light;
import static android.R.color.holo_red_light;
import static com.codepath.apps.twitcher.TwitcherApp.getTwitterClient;

public class FeedActivity extends AppCompatActivity {
    private static final int ACTIVITY_CODE_CREATE_TWEET = 999;
    private static final int TWEETS_PER_PAGE = 25;
    private static final int SCROLL_MAX_PAGES = 14;
    private static final int SCROLL_BUFFER = 12;

    private ListView lvTweets;
    private List<Tweet> tweets;
    private FeedListAdapter aTweets;
    private User profile = new User("", "");
    private SwipeRefreshLayout swipeContainer;

    private long lastTweetId = 0L;
    private int nextPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new FeedListAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener(SCROLL_BUFFER) {
            @Override
            public boolean onLoadMore() {
                if (nextPage > SCROLL_MAX_PAGES) {
                    return false;
                }

                retrieveTweets(false);

                return true;
            }
        });

        this.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadTweets();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(holo_blue_bright, holo_green_light, holo_orange_light, holo_red_light);


        retrieveTwitterRateLimit();
//        retrieveTweets();
        retrieveProfile();

    }

    private void retrieveProfile() {
        getTwitterClient().getProfile(new TwitterClient.Callback() {
            @Override
            public void onSuccess(User profile) {
                if (profile != null) {
                    FeedActivity.this.profile = profile;
                }

            }

            @Override
            public void onFailure(int statusCode, String error, Throwable t) {
                Log.e(FeedActivity.class.getSimpleName(), "Failed to get feed. " + error, t);
                Toast.makeText(FeedActivity.this, "Network error during profile load", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void reloadTweets() {
        this.nextPage = 0;
        this.lastTweetId = 0L;
        retrieveTweets(true);
    }

    private void retrieveTweets(final boolean clearOld) {
        Log.d(FeedActivity.class.getSimpleName(), "getting twitter feed");
        getTwitterClient().getFeed(lastTweetId, TWEETS_PER_PAGE, new TwitterClient.Callback() {
            @Override
            public void onSuccess(List<Tweet> tweets) {
                if (tweets != null && !tweets.isEmpty()) {
                    if(clearOld) {
                        aTweets.clear();
                    }
                    aTweets.addAll(tweets);
                    lastTweetId = tweets.get(tweets.size() - 1).tweetId;
                }
                nextPage++;
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, String error, Throwable t) {
                Log.e(FeedActivity.class.getSimpleName(), "Failed to get feed. " + error, t);
                Toast.makeText(FeedActivity.this, "Network error during feed load", Toast.LENGTH_LONG).show();
                swipeContainer.setRefreshing(false);
            }

        });
    }

    private void retrieveTwitterRateLimit() {
//        https://api.twitter.com/1.1/application/rate_limit_status.json
        getTwitterClient().getRateLimit(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(FeedActivity.class.getSimpleName(), "Twitter rate limit " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(FeedActivity.class.getSimpleName(), "Failed to get rate limit " + responseString);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.miWrite) {
            Intent intent = new Intent(this, CreateTweetActivity.class);
            if(profile != null) {
                intent.putExtra("avatar", profile.avatarUrl);
            }

            startActivityForResult(intent, ACTIVITY_CODE_CREATE_TWEET);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ACTIVITY_CODE_CREATE_TWEET) {
            Tweet tweet = data.getParcelableExtra("tweet");
            if(tweet != null) {
                aTweets.insert(tweet, 0);
            }
        }
    }
}
