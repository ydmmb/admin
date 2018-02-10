package com.universe.yz.admin.presenter;

import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.presenter.contract.RecommendContract;
import com.universe.yz.admin.utils.RxUtil;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.net.RetrofitHelper;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Description: RecommendPresenter
 * Creator: yxc
 * date: 2016/9/21 16:26
 */
public class RecommendPresenter extends RxPresenter<RecommendContract.View> implements RecommendContract.Presenter {

    int page = 0;

    @Inject
    public RecommendPresenter() {}

    @Override
    public void onRefresh() {
        page = 0;
        getPageHomeInfo();
    }

    private void getPageHomeInfo() {
        Subscription rxSubscription = RetrofitHelper.getVideoApi().getHomePage()
               // .compose(RxUtil.<VideoHttpResponse<VideoRes>>rxSchedulerHelper())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                });
        addSubscribe(rxSubscription);
    }

}
