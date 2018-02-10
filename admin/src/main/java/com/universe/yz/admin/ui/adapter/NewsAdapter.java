package com.universe.yz.admin.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NewsAdapter extends RecyclerArrayAdapter<NewsItem> {

    public NewsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsViewHolder(parent);
    }

    class NewsViewHolder extends BaseViewHolder<NewsItem> {
        ImageView imgPicture;
        TextView tv_title;
        TextView tv_size;

        public NewsViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_cache);

            imgPicture = $(R.id.item_face);
            tv_title = $(R.id.item_name);
            tv_size = $(R.id.item_size);
        }

        @Override
        public void setData(NewsItem data) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            tv_title.setText(data.getTitle());
            tv_title.setTextColor(getContext().getResources().getColor(R.color.black));
            Date s = new Date();
            s.setTime(Long.valueOf(data.getTime()));
            tv_size.setText(data.getName() + " 发布时间: " + formatter.format(s));
            Glide.with(getContext())
                    .load(R.drawable.default_image)
                    .placeholder(R.drawable.default_image)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(imgPicture);
        }
    }

}
