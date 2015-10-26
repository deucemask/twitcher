package com.codepath.apps.twitcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.codepath.apps.twitcher.helpers.DateHelper.parseTwitterDate;
import static com.codepath.apps.twitcher.helpers.JsonHelper.parseJsonArray;
import static com.codepath.apps.twitcher.helpers.JsonHelper.parseJsonObject;

/**
 * Created by dmaskev on 10/24/15.
 */
@Table(name="tweets")
public class Tweet extends Model implements Parcelable{

    //Tue Aug 28 21:16:23 +0000 2012
    @SerializedName("created_at")
    public String createdAtStr;

//    @Column
//    public Date createdAt;
    @Column
    public String text;
    @Column
    public User user;

    @SerializedName("id")
    @Column
    public long tweetId;

    public Date getCreatedAtAsDate() {
        return parseTwitterDate(createdAtStr);
    }

    public static List<Tweet> fromJsonArray(byte[] data) {
        Type classType = new TypeToken<ArrayList<Tweet>>() {}.getType();
        return parseJsonArray(data, classType);
    }

    public static Tweet fromJson(byte[] data) {
        return parseJsonObject(data, Tweet.class);
    }

    public Tweet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createdAtStr);
        dest.writeString(this.text);
        dest.writeParcelable(this.user, 0);
    }

    protected Tweet(Parcel in) {
        this.createdAtStr = in.readString();
        this.text = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
