package com.universe.yz.admin.ui.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.universe.yz.admin.component.ImageLoader;
import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.R;

public class ShopAdapter extends RecyclerArrayAdapter<ShopItem> {

    public ShopAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShopViewHolder(parent);
    }

    class ShopViewHolder extends BaseViewHolder<ShopItem> {
        ImageView imgPicture;
        TextView tv_title;

        public ShopViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_shop);
            imgPicture = $(R.id.img_shop);
            tv_title = $(R.id.shop_title);
            imgPicture.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        public void setData(ShopItem shopItem) {
            tv_title.setText(shopItem.name);
            ViewGroup.LayoutParams params = imgPicture.getLayoutParams();
            DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
            int width = dm.widthPixels / 2;//宽度为屏幕宽度一半
//        int height = data.getHeight()*width/data.getWidth();//计算View的高度
            params.height = (int) (width / 1.8);
            imgPicture.setLayoutParams(params);
            ImageLoader.load(getContext(), "http://p.fgame.com/api/ShopPic/"+shopItem.itemPic, imgPicture);
        }
    }

}
