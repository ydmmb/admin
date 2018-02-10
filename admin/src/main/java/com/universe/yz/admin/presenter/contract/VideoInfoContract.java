package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.VideoRes;

public interface VideoInfoContract {

    interface View extends BaseView {

        void showContent(VideoRes videoRes);

        void hidLoading();

        void collected();

        void disCollect();
    }

    interface Presenter extends BasePresenter<View> {
        void getDetailData(String dataId);

        void collect();

        void insertRecord();

        void download();
    }
}
