package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.model.bean.VideoType;

import java.util.List;

/**
 * Description: CollectionContract
 * Creator: cp
 * date: 2016/9/29 12:19
 */
public interface MineContract {

    interface View extends BaseView {

        void showContent(List<VideoType> list);

    }

    interface Presenter extends BasePresenter<View> {
        void getHistoryData();

        void delAllHistory();
    }
}
