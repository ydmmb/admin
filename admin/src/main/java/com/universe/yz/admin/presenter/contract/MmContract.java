package com.universe.yz.admin.presenter.contract;

import com.universe.yz.admin.widget.Image;
import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;
import com.universe.yz.admin.model.bean.DataInfo;

import java.util.List;

/**
 * Description: WelfareContract
 * Creator: yxc
 * date: 2016/10/24 12:34
 */
public interface MmContract {
    interface View extends BaseView {

        void refreshFaild(String msg);

        void loadMoreFaild(String msg);

        void showContent(List<Image> list);

        void showMoreContent(List<Image> list);
    }

    interface Presenter extends BasePresenter<View> {

        void onRefresh();

        void getTotalSize(DataInfo dataInfo);

        void loadMore();
    }
}
