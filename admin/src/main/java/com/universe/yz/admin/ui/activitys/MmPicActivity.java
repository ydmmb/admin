package com.universe.yz.admin.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;
import com.universe.yz.admin.utils.ProgressTarget;
import com.universe.yz.admin.widget.Image;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.net.OkHttpProgressGlideModule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MmPicActivity extends FragmentActivity {
    private List<Image> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);

       // recyclerView.setErrorView(R.layout.view_error);

        // Display Progress
        final Glide glide = Glide.get(this);
        OkHttpProgressGlideModule a = new OkHttpProgressGlideModule();
        a.registerComponents(this, glide);

        loadData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mm_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
    }

    private void loadData() {
        for(int i=1; i<35; i++) {
           // if (i<10) list.add(new Image("http://p.fgame.com/api/025/000"+ i + ".jpg", 3600, 5400,null));
           // if(i>=10) list.add(new Image("http://p.fgame.com/api/025/00"+ i + ".jpg", 3600, 5400,null));
            if (i<10) list.add(new Image("http://p.fgame.com/api/025/000"+ i + ".jpg",null));
            if(i>=10) list.add(new Image("http://p.fgame.com/api/025/00"+ i + ".jpg",null));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(getLayoutInflater().inflate(R.layout.item_image, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.setData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {
            private LargeImageView largeImageView;
            private Image data;
            private RingProgressBar ringProgressBar;

            public ItemViewHolder(View itemView) {
                super(itemView);
                largeImageView = (LargeImageView) itemView.findViewById(R.id.imageView);
                ringProgressBar = (RingProgressBar) itemView.findViewById(R.id.ringProgressBar);
            }

            public void setData(Image data) {
                this.data = data;
                Glide.with(largeImageView.getContext()).load(data.url).downloadOnly(new ProgressTarget<String, File>(data.url, null) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        ringProgressBar.setVisibility(View.VISIBLE);
                        ringProgressBar.setProgress(0);
                    }

                    @Override
                    public void onProgress(long bytesRead, long expectedLength) {
                        int p = 0;
                        if (expectedLength >= 0) {
                            p = (int) (100 * bytesRead / expectedLength);
                        }
                        ringProgressBar.setProgress(p);
                    }

                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        ringProgressBar.setVisibility(View.GONE);
                        FileBitmapDecoderFactory fileBitmapDecoderFactory = new FileBitmapDecoderFactory(resource);
                        int nWidth = fileBitmapDecoderFactory.getImageInfo()[0];
                        int nHeight = fileBitmapDecoderFactory.getImageInfo()[1];
                        int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = largeImageView.getLayoutParams();
                        if(nWidth < nHeight) {
                            layoutParams.height = width * nHeight / nWidth;
                        } else {
                            layoutParams.height = nHeight;
                            layoutParams.width = nHeight;
                        }
                        Log.e("TAG","Width:" + nWidth + " Height:" + nHeight);
                        largeImageView.setLayoutParams(layoutParams);
                        largeImageView.setImage(new FileBitmapDecoderFactory(resource));
                    }

                    @Override
                    public void getSize(SizeReadyCallback cb) {
                        cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    }
                });
            }
        }
    }

    public static void start(Context context, String catalogId, String title) {
        Intent starter = new Intent(context, VideoListActivity.class);
        starter.putExtra("catalogId", catalogId);
        starter.putExtra("title", title);
        context.startActivity(starter);
    }
}
