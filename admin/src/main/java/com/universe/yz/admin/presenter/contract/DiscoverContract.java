package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.VideoRes;

/**
 * Description: RecommendContract
 * Creator: yxc
 * date: 2016/9/21 15:53
 */
public interface DiscoverContract {

    interface View extends BaseView {

        void showContent(VideoRes videoRes);

        void refreshFaild(String msg);

        void hidLoading();

        int getLastPage();

        void setLastPage(int page);
    }

    interface Presenter extends BasePresenter<View> {
        void getData();
    }
}
