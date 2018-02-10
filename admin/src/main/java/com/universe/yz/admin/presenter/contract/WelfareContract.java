package com.universe.yz.admin.presenter.contract;

import com.universe.yz.admin.model.bean.GankItemBean;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;

import java.util.List;

/**
 * Description: WelfareContract
 * Creator: yxc
 * date: 2016/10/24 12:34
 */
public interface WelfareContract {
    interface View extends BaseView {


        void refreshFaild(String msg);

        void loadMoreFaild(String msg);

        void showContent(List<GankItemBean> list, List<NewsItem> list1);

        void showMoreContent(List<GankItemBean> list, List<NewsItem> list1);
    }

    interface Presenter extends BasePresenter<View> {
        void onRefresh();

        void loadMore();
    }
}
