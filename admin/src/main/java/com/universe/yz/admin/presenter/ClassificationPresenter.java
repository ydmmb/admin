package com.universe.yz.admin.presenter;

import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.exception.ApiException;
import com.universe.yz.admin.model.net.HttpMethods;
import com.universe.yz.admin.model.net.MyObserver;
import com.universe.yz.admin.presenter.contract.ClassificationContract;

import javax.inject.Inject;

/**
 * Description: ClassificationPresenter
 * Creator: yxc
 * date: 2016/9/21 17:55
 */
public class ClassificationPresenter extends RxPresenter<ClassificationContract.View> implements ClassificationContract.Presenter {
    int page = 0;

    @Inject
    public ClassificationPresenter() {
    }

    @Override
    public void onRefresh() {
        page = 0;
        getPageHomeInfo();
    }

    private void getPageHomeInfo() {
        HttpMethods.getInstance().queryClassification()
                .subscribe(new MyObserver<VideoRes>() {
                    @Override
                    protected void onError(ApiException ex) {
                        mView.refreshFaild(ex.getDisplayMessage());
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(VideoRes res) {
                        if (res != null) {
                            mView.showContent(res);
                        }
                    }
                });
    }
}
