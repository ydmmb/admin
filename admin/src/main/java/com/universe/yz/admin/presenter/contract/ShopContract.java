package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.ShopItem;

import java.util.List;

public interface ShopContract {

    interface View extends BaseView {
        void showContent(List<ShopItem> shopItems);

        void refreshFaild(String msg);

        void loadMoreFaild(String msg);

        void showMoreContent(List<ShopItem> list);
    }

    interface Presenter extends BasePresenter<View> {
        void onRefresh(String query);

        void loadMore(String query);
    }
}
