package com.universe.yz.admin.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.shizhefei.view.largeimage.LargeImageView;
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.net.OkHttpProgressGlideModule;
import com.universe.yz.admin.utils.ProgressTarget;
import com.universe.yz.admin.widget.Image;

import java.io.File;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MmAdapter extends RecyclerArrayAdapter<Image> {
    Context context;

    public MmAdapter(Context context) {
        super(context);

        this.context = context;

        final Glide glide = Glide.get(context);
        OkHttpProgressGlideModule a = new OkHttpProgressGlideModule();
        a.registerComponents(context, glide);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MmAdapter.MmViewHolder(parent);
    }

    class MmViewHolder extends BaseViewHolder<Image> {
        private LargeImageView largeImageView;
        private Image data;
        private RingProgressBar ringProgressBar;

        public MmViewHolder(ViewGroup parent) {
            super(parent,R.layout.item_image);

            largeImageView = $(R.id.imageView);
            ringProgressBar = $(R.id.ringProgressBar);
        }

        @Override
        public void setData(Image data) {
            this.data = data;

            int width = context.getResources().getDisplayMetrics().widthPixels;
            ViewGroup.LayoutParams layoutParams = largeImageView.getLayoutParams();
            layoutParams.height = width/2;
            largeImageView.setLayoutParams(layoutParams);

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
                    int width = context.getResources().getDisplayMetrics().widthPixels;
                    ViewGroup.LayoutParams layoutParams = largeImageView.getLayoutParams();
                    layoutParams.height = width * nHeight / nWidth;
                    largeImageView.setLayoutParams(layoutParams);
                    largeImageView.setImage(new FileBitmapDecoderFactory(resource));

                    largeImageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            return false;
                        }
                    });

                }

                @Override
                public void getSize(SizeReadyCallback cb) {
                    cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                }
            });
        }
    }
}
