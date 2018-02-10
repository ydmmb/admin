package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.VideoType;

import java.util.List;

/**
 * Description: CollectionContract
 * Creator: yxc
 * date: 2016/9/29 12:06
 */
public interface CacheContract {

    interface View extends BaseView {


        void showContent(List<VideoType> list);

    }

    interface Presenter extends BasePresenter<View> {
        void getData(int type);

        void getCollectData();

        void delAllDatas();

        void getRecordData();

        void getCaesarData();

        int getType();
    }
}
