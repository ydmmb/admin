package com.universe.yz.admin.presenter;

import android.util.Log;

import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.presenter.contract.NewsContract;
import com.universe.yz.admin.utils.RxUtil;

import org.simple.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NewsPresenter extends RxPresenter<NewsContract.View> implements NewsContract.Presenter {
    public static final int NUM_OF_PAGE = 20;

    private int currentPage = 1;

    @Inject
    public NewsPresenter() {
    }

    @Override
    public void onRefresh(String query) {
        currentPage = 1;
        if(query == null || "".equals(query)) {
            Subscription rxSubscription = RetrofitHelper.getMemberApi().findNewsByType("系统公告", currentPage - 1, NUM_OF_PAGE)
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
                                mView.showContent(newsItems);
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
            Subscription rxSubscription = RetrofitHelper.getMemberApi().fuzzyNewsHint(query,currentPage - 1, NUM_OF_PAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<CommonHttpResponse<List<NewsItem>>, List<NewsItem>>() {
                        @Override
                        public List<NewsItem> call(CommonHttpResponse<List<NewsItem>> listCommonHttpResponse) {
                            if (listCommonHttpResponse.getCode()==100 )
                                return listCommonHttpResponse.getRet();
                            return null;
                        }
                    })
                    .subscribe(new Action1<List<NewsItem>>() {
                        @Override
                        public void call(List<NewsItem> newsItems) {
                            if (newsItems != null) {
                                mView.showContent(newsItems);
                            }
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
    public void loadMore(String query) {
        if(query == null || "".equals(query)) {
            Subscription rxSubscription = RetrofitHelper.getMemberApi().findNewsByType("系统公告", (++currentPage) - 1, NUM_OF_PAGE)
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
                                mView.showMoreContent(newsItems);
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
            Subscription rxSubscription = RetrofitHelper.getMemberApi().fuzzyNewsHint(query, (++currentPage) - 1, NUM_OF_PAGE)
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
                                mView.showMoreContent(newsItems);
                            }
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

    public void delNews(Integer id) {
        RetrofitHelper.getMemberApi().delNews(id)
                .compose(RxUtil.<CommonHttpResponse<String>>rxSchedulerHelper())
                .subscribe(new Action1<CommonHttpResponse<String>>() {
                    @Override
                    public void call(CommonHttpResponse<String> stringCommonHttpResponse) {
                        if(stringCommonHttpResponse.getCode()==100) {
                            EventBus.getDefault().post("DEL_NEWS", Constants.NEWS_UPDATE_FLAG);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("hint", "delete failed! please try again!");
                        return;
                    }
                });
    }
}
