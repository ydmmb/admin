package com.universe.yz.admin.model.bean;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by thomas on 2017/12/18.
 */

public class NewsItem implements SearchSuggestion {
    private int id;
    private String name;
    private String title;
    private String info;
    private String time;
    private String type;
    private int top;
    private int yn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getYn() {
        return yn;
    }

    public void setYn(int yn) {
        this.yn = yn;
    }

    public NewsItem(Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
        this.title = source.readString();
        this.info = source.readString();
        this.time = source.readString();
        this.type = source.readString();
        this.top = source.readInt();
        this.yn = source.readInt();
    }

    @Override
    public String getBody() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(title);
        parcel.writeString(info);
        parcel.writeString(time);
        parcel.writeString(type);
        parcel.writeInt(top);
        parcel.writeInt(yn);
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}