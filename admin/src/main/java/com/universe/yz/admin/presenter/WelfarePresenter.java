package com.universe.yz.admin.presenter;

import android.util.DisplayMetrics;

import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.app.App;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.GankHttpResponse;
import com.universe.yz.admin.model.bean.GankItemBean;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.presenter.contract.WelfareContract;
import com.universe.yz.admin.ui.activitys.WelfareActivity;
import com.universe.yz.admin.utils.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Description:
 * Creator: yxc
 * date: $date $time
 */

public class WelfarePresenter extends RxPresenter<WelfareContract.View> implements WelfareContract.Presenter {
    public static final int NUM_OF_PAGE = 20;

    private int currentPage = 1;

    @Inject
    public WelfarePresenter() {
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        if(WelfareActivity.isNews) {
            Subscription rxSubscription = RetrofitHelper.getMemberApi().findNewsByType("系统公告",currentPage-1,NUM_OF_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<CommonHttpResponse<List<NewsItem>>, List<NewsItem>>() {
                        @Override
                        public List<NewsItem> call(CommonHttpResponse<List<NewsItem>> listCommonHttpResponse) {
                            if (listCommonHttpResponse.getCode() == 100)
                                return listCommonHttpResponse.getRet();
                            return null;
                        }
                    })
                    .subscribe(new Action1<List<NewsItem>>() {
                        @Override
                        public void call(List<NewsItem> newsItems) {
                            if (newsItems != null) {
                                mView.showContent(null,newsItems);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                        }
                    });
            addSubscribe(rxSubscription);
        } else {
            Subscription rxSubscription = RetrofitHelper.getGankApis().getGirlList(NUM_OF_PAGE, currentPage)
                    .compose(RxUtil.<GankHttpResponse<List<GankItemBean>>>rxSchedulerHelper())
                    .compose(RxUtil.<List<GankItemBean>>handleGankResult())
                    .subscribe(new Action1<List<GankItemBean>>() {
                        @Override
                        public void call(List<GankItemBean> gankItemBeen) {
                            setHeight(gankItemBeen);
                            mView.showContent(gankItemBeen,null);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                        }
                    });
            addSubscribe(rxSubscription);
        }

    }

    @Override
    public void loadMore() {
        if(WelfareActivity.isNews) {
            Subscription rxSubscription = RetrofitHelper.getMemberApi().findNewsByType("系统公告",(++currentPage)-1,NUM_OF_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<CommonHttpResponse<List<NewsItem>>, List<NewsItem>>() {
                        @Override
                        public List<NewsItem> call(CommonHttpResponse<List<NewsItem>> listCommonHttpResponse) {
                            if (listCommonHttpResponse.getCode() == 100)
                                return listCommonHttpResponse.getRet();
                            return null;
                        }
                    })
                    .subscribe(new Action1<List<NewsItem>>() {
                        @Override
                        public void call(List<NewsItem> newsItems) {
                            if (newsItems != null) {
                                mView.showMoreContent(null,newsItems);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mView.showError("数据加载失败ヽ(≧Д≦)ノ");
                        }
                    });
            addSubscribe(rxSubscription);
        }else {
            Subscription rxSubscription = RetrofitHelper.getGankApis().getGirlList(NUM_OF_PAGE, ++currentPage)
                    .compose(RxUtil.<GankHttpResponse<List<GankItemBean>>>rxSchedulerHelper())
                    .compose(RxUtil.<List<GankItemBean>>handleGankResult())
                    .subscribe(new Action1<List<GankItemBean>>() {
                        @Override
                        public void call(List<GankItemBean> gankItemBeen) {
                            setHeight(gankItemBeen);
                            mView.showMoreContent(gankItemBeen,null);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            mView.showError("加载更多数据失败ヽ(≧Д≦)ノ");
                        }
                    });
            addSubscribe(rxSubscription);
        }
    }

    private void setHeight(List<GankItemBean> list) {
        DisplayMetrics dm = App.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels / 2;//宽度为屏幕宽度一半
        for (GankItemBean gankItemBean : list) {
            gankItemBean.setHeight(width * StringUtils.getRandomNumber(3, 6) / 3);//随机的高度
        }
    }
}
