package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;

import java.util.List;

/**
 * Description: ClassificationContract
 * Creator: yxc
 * date: 2016/9/21 17:55
 */
public interface JuanContract {

    interface View extends BaseView {
        void showContent(List<ShopItem> shopItems);
        void refreshFaild(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void onRefresh();
    }
}
