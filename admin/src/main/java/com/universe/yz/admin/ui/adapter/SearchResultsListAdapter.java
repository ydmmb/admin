package com.universe.yz.admin.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.universe.yz.admin.app.App;
import com.universe.yz.admin.component.ImageLoader;
import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.R;
import com.universe.yz.admin.ui.fragments.SearchFragment;

public class SearchResultsListAdapter extends RecyclerArrayAdapter<ShopItem> {
    public static final String TAG = SearchResultsListAdapter.class.getSimpleName();
    int[] bg_colors={R.color.blue_green,R.color.black,R.color.cardview_light_background,R.color.cardview_dark_background};

    public SearchResultsListAdapter(Context context) {
        super(context);
    }
    
    public interface OnItemClickListener{
        void onClick(ShopItem shopItem);
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType1) {
        if (!SearchFragment.viewType) return new ShopViewHolder1(parent);
        else return new SearchViewHolder(parent);
    }

    public static class ShopViewHolder1 extends BaseViewHolder<ShopItem> {
        ImageView imgPicture;
        TextView tv_title;

        public ShopViewHolder1(ViewGroup parent) {
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
            params.height = (int) (width / 1.8);
            imgPicture.setLayoutParams(params);
            ImageLoader.load(getContext(), "http://p.fgame.com/api/ShopPic/"+shopItem.itemPic, imgPicture);
        }
    }

    class SearchViewHolder extends BaseViewHolder<ShopItem> {
        private final TextView mItemName;
        private final TextView mCroPoint;
        private final ImageView imgPicture;
        private final View mTextContainer;
        private final TextView option0;
        private final TextView option1;
        private final TextView option2;
        private final TextView option3;
        private final TextView option4;
        private final TextView option5;
        private final TextView option6;
        private final TextView option7;
        private final TextView mColor;
        private final TextView mNeedVip;
        private final TextView mGrade;
        private final TextView mDurability;
        private final CardView mCardview;
        private SearchViewHolder(ViewGroup parent) {

            super(parent,R.layout.search_results_list_item);
            mCardview = $(R.id.card_view);
            mColor = $(R.id.color);
            mNeedVip = $(R.id.needVip);
            mGrade = $(R.id.grade);
            mDurability = $(R.id.durability);
            imgPicture =  $(R.id.img_Item);
            mItemName =  $(R.id.item_name);
            mCroPoint =  $(R.id.item_cro);
            mTextContainer = $(R.id.text_container);

            option0 =  $(R.id.option0);
            option1 =  $(R.id.option1);
            option2 =  $(R.id.option2);
            option3 =  $(R.id.option3);
            option4 =  $(R.id.option4);
            option5 =  $(R.id.option5);
            option6 =  $(R.id.option6);
            option7 =  $(R.id.option7);

            imgPicture.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override
        public void setData(ShopItem shopItem) {
            int color = ContextCompat.getColor(getContext(), R.color.black);//bg_colors[new Random().nextInt(3)]);
            mCardview.setCardBackgroundColor(color);

            mItemName.setText(shopItem.name);
            mCroPoint.setText("道具点: " + String.valueOf(shopItem.needCroPoint));
            mColor.setText("颜色: "+shopItem.color);
            mGrade.setText("等级: "+shopItem.grade);
            mNeedVip.setText("VIP装备: "+shopItem.needVip);
            mDurability.setText("耐力: " + shopItem.durability);

            if(shopItem.optionId0 == null ||shopItem.optionId0.trim().equals("") || shopItem.optionId0.trim().equals("0"))
                option0.setText("");
            else option0.setText("属性1: "+ shopItem.optionId0 + " 值:"+ shopItem.damage_values0);

            if(shopItem.optionId1 == null ||shopItem.optionId1.trim().equals("") || shopItem.optionId1.trim().equals("0"))
                option1.setText("");
            else option1.setText("属性2: "+ shopItem.optionId1 + " 值:"+ shopItem.damage_values1);

            if(shopItem.optionId2 == null ||shopItem.optionId2.trim().equals("") || shopItem.optionId2.trim().equals("0"))
                option2.setText("");
            else option2.setText("属性3: "+ shopItem.optionId2 + " 值:"+ shopItem.damage_values2);

            if(shopItem.optionId3 == null ||shopItem.optionId3.trim().equals("") || shopItem.optionId3.trim().equals("0"))
                option3.setText("");
            else option3.setText("属性4: "+ shopItem.optionId3 + " 值:"+ shopItem.damage_values3);

            if(shopItem.optionId4 == null ||shopItem.optionId4.trim().equals("") || shopItem.optionId4.trim().equals("0"))
                option4.setText("");
            else option4.setText("属性5: "+ shopItem.optionId4 + " 值:"+ shopItem.damage_values4);

            if(shopItem.optionId5 == null ||shopItem.optionId5.trim().equals("") || shopItem.optionId5.trim().equals("0"))
                option5.setText("");
            else option5.setText("属性6: "+ shopItem.optionId5 + " 值:"+ shopItem.damage_values5);

            if(shopItem.optionId6 == null ||shopItem.optionId6.trim().equals("") || shopItem.optionId6.trim().equals("0"))
                option6.setText("");
            else option6.setText("属性7: "+ shopItem.optionId6 + " 值:"+ shopItem.damage_values6);

            if(shopItem.optionId7 == null ||shopItem.optionId7.trim().equals("") || shopItem.optionId7.trim().equals("0"))
                option7.setText("");
            else option7.setText("属性8: "+ shopItem.optionId7 + " 值:"+ shopItem.damage_values7);

            ViewGroup.LayoutParams params = imgPicture.getLayoutParams();
            DisplayMetrics dm = App.getInstance().getApplicationContext().getResources().getDisplayMetrics();
            int width = dm.widthPixels / 2;
            params.height = (int) (width / 1.8);
            imgPicture.setLayoutParams(params);
            ImageLoader.load(imgPicture.getContext(), "http://p.fgame.com/api/ShopPic/"+shopItem.itemPic, imgPicture);
        }
    }
}
