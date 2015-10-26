package com.codepath.apps.twitcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.codepath.apps.twitcher.R;
import com.codepath.apps.twitcher.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.codepath.apps.twitcher.helpers.DateHelper.getRelDateFormatted;

/**
 * Created by dmaskev on 10/24/15.
 */
public class FeedListAdapter extends ArrayAdapter<Tweet> {

    private class ViewHolder {
        public ImageView ivAvatar;
        public TextView tvUsername;
        public TextView tvHandle;
        public TextView tvMessage;
        public TextView tvDate;
    }

    public FeedListAdapter(Context ctx, List<Tweet> tweets) {
        super(ctx, 0, tweets);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
            vh = new ViewHolder();
            vh.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
            vh.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            vh.tvHandle = (TextView) convertView.findViewById(R.id.tvHandle);
            vh.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            vh.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(vh);
        }

        vh = (ViewHolder)convertView.getTag();
        Tweet tweet = getItem(position);
        vh.tvMessage.setText(tweet.text);
        Picasso.with(getContext()).load(tweet.user.avatarUrl).into(vh.ivAvatar);
        vh.tvUsername.setText(tweet.user.name);
        vh.tvHandle.setText('@' + tweet.user.handle);
        vh.tvDate.setText(getRelDateFormatted(tweet.getCreatedAtAsDate()));

        return convertView;
    }
}
