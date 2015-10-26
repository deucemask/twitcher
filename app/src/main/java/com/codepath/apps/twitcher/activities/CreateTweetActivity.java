package com.codepath.apps.twitcher.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitcher.R;
import com.codepath.apps.twitcher.TwitcherApp;
import com.codepath.apps.twitcher.models.Tweet;
import com.codepath.apps.twitcher.net.TwitterClient;
import com.squareup.picasso.Picasso;

import static com.google.common.base.Strings.isNullOrEmpty;

public class CreateTweetActivity extends AppCompatActivity {

    private static final int TWEET_MAX_LENGTH = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tweet);

        String avatarUrl = getIntent().getStringExtra("avatar");

        if(!isNullOrEmpty(avatarUrl)) {
            ImageView ivAvatar = (ImageView) findViewById(R.id.ivMyAvatar);
            Picasso.with(this).load(avatarUrl).into(ivAvatar);

        }

        final EditText etPost = (EditText) findViewById(R.id.etPost);
        final TextView tvCharsLeft = (TextView) findViewById(R.id.tvCharsLeft);
        etPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharsLeft.setText(String.valueOf(TWEET_MAX_LENGTH - etPost.getText().length()));
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void onTweet(View view) {
        EditText etPost = (EditText) findViewById(R.id.etPost);
        String post = etPost.getText().toString();
        if(post.length() > 140) {
            Toast.makeText(this, "Only 140 characters allowed in the post", Toast.LENGTH_LONG).show();
            return;
        }

        TwitcherApp.getTwitterClient().postTweet(post, new TwitterClient.Callback() {
            @Override
            public void onSuccess(Tweet tweet) {
                Intent intent = new Intent(CreateTweetActivity.this, FeedActivity.class);
                intent.putExtra("tweet", tweet);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, String error, Throwable t) {
                Log.e(CreateTweetActivity.class.getSimpleName(), "Failed to get feed. " + error, t);
                Toast.makeText(CreateTweetActivity.this, "Network error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onCancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
