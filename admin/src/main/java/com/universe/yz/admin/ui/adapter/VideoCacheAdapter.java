package com.universe.yz.admin.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.bean.VideoType;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class VideoCacheAdapter extends RecyclerArrayAdapter<VideoType> {

    public VideoCacheAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoListViewHolder(parent);
    }

    class VideoListViewHolder extends BaseViewHolder<VideoType> {
        ImageView imgPicture;
        TextView tv_title;
        TextView tv_size;

        public VideoListViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_cache);

            imgPicture = $(R.id.item_face);
            tv_title = $(R.id.item_name);
            tv_size = $(R.id.item_size);
        }

        @Override
        public void setData(VideoType data) {/*
            tv_title.setText(data.title);
            ViewGroup.LayoutParams params = imgPicture.getLayoutParams();

            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            int width = dm.widthPixels / 3;
            params.height = (int) (width * 1.1);
            imgPicture.setLayoutParams(params);
            ImageLoader.load(getContext(), data.pic, imgPicture); */

            Log.i("ViewHolder","position"+ data.title);
            tv_title.setText(data.title);
            tv_size.setText(data.phoneNumber);
            Glide.with(getContext())
                    .load(data.pic)
                    .placeholder(R.drawable.default_image)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(imgPicture);
        }
    }
}
