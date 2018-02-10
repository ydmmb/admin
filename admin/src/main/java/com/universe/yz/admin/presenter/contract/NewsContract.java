package com.universe.yz.admin.presenter.contract;

import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.NewsItem;

import java.util.List;

public interface NewsContract {
    interface View extends BaseView {

        void refreshFaild(String msg);

        void loadMoreFaild(String msg);

        void showContent(List<NewsItem> list);

        void showMoreContent(List<NewsItem> list);
    }

    interface Presenter extends BasePresenter<View> {

        void onRefresh(String query);

        void loadMore(String query);
    }
}
