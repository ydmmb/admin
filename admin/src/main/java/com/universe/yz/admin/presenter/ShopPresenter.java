package com.universe.yz.admin.presenter;

import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.presenter.contract.ShopContract;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.UserRet;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.utils.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ShopPresenter extends RxPresenter<ShopContract.View> implements ShopContract.Presenter {
    public final static String TAG = ShopPresenter.class.getSimpleName();

    public static final int NUM_OF_PAGE = 10;

    public static int currentPage = 0;

    @Inject
    public ShopPresenter() {
    }

    @Override
    public void onRefresh(String query) {
        currentPage = 0;

        if(query == null || "".equals(query)) {
            getShopHomeInfo(query);
        }else {
            getSearchResult(query);
        }
    }

    @Override
    public void loadMore(String query) {
        currentPage++;
        if(query == null || "".equals(query)) {
            getShopHomeInfo(query);
        }else {
            getSearchResult(query);
        }
    }

    private void getSearchResult(String query) {
        Subscription rxSubscription = RetrofitHelper.getMemberApi().fuzzyByName(query, currentPage, NUM_OF_PAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<CommonHttpResponse<List<ShopItem>>, List<ShopItem>>() {
                    @Override
                    public List<ShopItem> call(CommonHttpResponse<List<ShopItem>> listCommonHttpResponse) {
                        if (listCommonHttpResponse.getCode() == 100)
                            return listCommonHttpResponse.getRet();
                        return null;
                    }
                })
                .subscribe(new Action1<List<ShopItem>>() {
                    @Override
                    public void call(List<ShopItem> shopItems) {
                        if (shopItems != null) {
                            if (currentPage == 0) mView.showContent(shopItems);
                            else mView.showMoreContent(shopItems);
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

    private void getShopHomeInfo(String query) {
        if(query == null || "".equals(query)) {
            Subscription rxSubscription = RetrofitHelper.getMemberApi().findBycat("all", currentPage, NUM_OF_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<CommonHttpResponse<List<ShopItem>>, List<ShopItem>>() {
                        @Override
                        public List<ShopItem> call(CommonHttpResponse<List<ShopItem>> listCommonHttpResponse) {
                            if (listCommonHttpResponse.getCode() == 100)
                                return listCommonHttpResponse.getRet();
                            return null;
                        }
                    })
                    .subscribe(new Action1<List<ShopItem>>() {
                        @Override
                        public void call(List<ShopItem> res) {
                            if (res != null) {
                                if (currentPage == 0) mView.showContent(res);
                                else mView.showMoreContent(res);
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

    public void buyDaoju(String username, int ckey, int needpoint, String bitemid) {
        Subscription rxSubscription = RetrofitHelper.getMemberApi().buyGoods(username,ckey,needpoint,bitemid)
                .compose(RxUtil.<CommonHttpResponse<UserRet>>rxSchedulerHelper())
                .compose(RxUtil.<UserRet>handleUserResult())
                .subscribe(new Action1<UserRet>() {
                    @Override
                    public void call( final UserRet res) {
                        if (res != null) {
                           // mView.refreshFaild(StringUtils.getErrorMsg("购买成功!"));
                            onRefresh("");
                        }
                    }
                },new Action1<Throwable>() {
                        @Override
                        public void call (Throwable throwable){
                            mView.refreshFaild(StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }
}