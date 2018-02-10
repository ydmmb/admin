package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.base.BasePresenter;

/**
 * Description: ClassificationContract
 * Creator: yxc
 * date: 2016/9/21 17:55
 */
public interface ClassificationContract {

    interface View extends BaseView {

        void showContent(VideoRes videoRes);

        void refreshFaild(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void onRefresh();
    }
}
