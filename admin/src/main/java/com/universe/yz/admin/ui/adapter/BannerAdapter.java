package com.universe.yz.admin.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.component.ImageLoader;
import com.universe.yz.admin.ui.activitys.VideoInfoActivity;
import com.universe.yz.admin.R;

import java.util.List;

/**
 * Description: BannerAdapter
 * Creator: yxc
 * date: 2016/9/30 11:09
 */
public class BannerAdapter extends StaticPagerAdapter {

    private Context ctx;
    private List<VideoInfo> list;

    public BannerAdapter(Context ctx, List<VideoInfo> list) {
        this.ctx = ctx;
        this.list = list;
        removeEmpty(this.list);
    }

    private void removeEmpty(List<VideoInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).loadType.equals("video")) {
                list.remove(i);
                i--;
            }
        }
    }

    @Override
    public View getView(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(ctx);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(R.mipmap.default_320);
        //加载图片
        ImageLoader.load(ctx, list.get(position).pic, imageView);
        //点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoInfoActivity.start(ctx, list.get(position));
            }
        });
        return imageView;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}