package com.universe.yz.admin.presenter;

import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.presenter.contract.JuanContract;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class JuanPresenter extends RxPresenter<JuanContract.View> implements JuanContract.Presenter {
    public final static String TAG = JuanPresenter.class.getSimpleName();
    int page = 0;

    @Inject
    public JuanPresenter() {
    }

    @Override
    public void onRefresh() {
        page = 0;
        getShopHomeInfo();
    }

    private void getShopHomeInfo() {
        Subscription rxSubscription = RetrofitHelper.getMemberApi().findBycat("baoshi",0,20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<CommonHttpResponse<List<ShopItem>>, List<ShopItem>>() {
                    @Override
                    public List<ShopItem> call(CommonHttpResponse<List<ShopItem>> listCommonHttpResponse) {
                        if (listCommonHttpResponse.getCode()==100 )
                            return listCommonHttpResponse.getRet();
                        return null;
                    }
                })
                .subscribe(new Action1<List<ShopItem>>() {
                    @Override
                    public void call(List<ShopItem> res) {
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