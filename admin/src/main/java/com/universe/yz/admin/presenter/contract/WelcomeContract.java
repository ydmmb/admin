package com.universe.yz.admin.presenter.contract;


import com.universe.yz.admin.base.BasePresenter;
import com.universe.yz.admin.base.BaseView;

import java.util.List;

/**
 * Description: WelcomeContract
 * Creator: yxc
 * date: 2016/9/22 13:16
 */
public interface WelcomeContract {

    interface View extends BaseView {

        void showContent(List<String> list);

        void jumpToMain();
    }

    interface Presenter extends BasePresenter<View> {
        void getWelcomeData();
    }
}
