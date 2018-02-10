package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.base.BaseView;

/**
 * Description: RecommendContract
 * Creator: yxc
 * date: 2016/9/21 15:53
 */
public interface RecommendContract {

    interface View extends BaseView {

        void showContent(VideoRes videoRes);

        void refreshFaild(String msg);

    }

    interface Presenter extends BasePresenter<View> {
        void onRefresh();
    }
}
