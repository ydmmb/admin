package com.universe.yz.admin.presenter;

import com.universe.yz.admin.presenter.contract.DiscoverContract;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.utils.RxUtil;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.utils.SystemUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Description: DiscoverPresenter
 * Creator: yxc
 * date: 2016/9/21 17:55
 */
public class DiscoverPresenter extends RxPresenter<DiscoverContract.View> implements DiscoverContract.Presenter {
    final String catalogId = "402834815584e463015584e539330016";

    int max = 108;
    int min = 1;

    @Inject
    public DiscoverPresenter() {
    }

    @Override
    public void getData() {
        getNextVideos();
    }

    private void getNextVideos() {
        Subscription rxSubscription = RetrofitHelper.getVideoApi().getVideoList(catalogId, getNextPage() + "")
                .compose(RxUtil.<CommonHttpResponse<VideoRes>>rxSchedulerHelper())
                .compose(RxUtil.<VideoRes>handleResult())
                .subscribe(new Action1<VideoRes>() {
                               @Override
                               public void call(final VideoRes res) {
                                   if (res != null) {
                                       mView.showContent(res);
                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {

                                   mView.refreshFaild(StringUtils.getErrorMsg(throwable.getMessage()));
                               }
                           }, new Action0() {
                               @Override
                               public void call() {
                                   mView.hidLoading();
                               }
                           }
                );

        addSubscribe(rxSubscription);
    }


    private int getNextPage() {
        int page = mView.getLastPage();
        if (SystemUtils.isNetworkConnected()) {
            page = StringUtils.getRandomNumber(min, max);
            mView.setLastPage(page);
        }
        return page;
    }
}
