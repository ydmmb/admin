package com.universe.yz.admin.presenter;

import android.util.Log;

import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.presenter.contract.VideoListContract;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.bean.VideoType;
import com.universe.yz.admin.utils.RxUtil;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class VideoListPresenter extends RxPresenter<VideoListContract.View> implements VideoListContract.Presenter {
    public static String TAG= VideoListPresenter.class.getSimpleName();
    int page = 1;
    String catalogId = "";

    @Inject
    public VideoListPresenter() {
    }

    @Override
    public void onRefresh(String catalogId) {
        this.catalogId = catalogId;
        page = 1;
        Log.e(TAG,"Cat is : "+ catalogId);
        if(catalogId.equals("推女郎")) {
            getVideoTGList(catalogId);
        }else if(catalogId.equals("福利美图")) {
            getPicTGList(catalogId);
        }else{
            getVideoList(catalogId);
        }
    }

    private void getPicTGList(String catalogID) {
        Subscription rxSubscription = RetrofitHelper.getMemberApi().findBycat(catalogID,0,20)
                .compose(RxUtil.<CommonHttpResponse<List<ShopItem>>>rxSchedulerHelper())
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
                    public void call(List<ShopItem> results) {
                        if (results != null) {
                            List<VideoType> lists = new ArrayList<>();

                            for(ShopItem result: results) {
                                VideoType videoType = new VideoType();

                                //头图
                                videoType.pic = StringUtils.getPicURL(result.bitemid.trim() +"/" + result.itemPic.trim());
                                Log.e(TAG,"pic->" + videoType.pic);
                                //Title
                                videoType.title = result.name;
                                videoType.airTime = result.itemTime;
                                Log.e(TAG,"title->" + videoType.title);
                                // 目录
                                videoType.score = result.bitemid;
                                videoType.dataId = String.valueOf(result.id);

                                //样式-前半部分
                                videoType.moreURL= result.itemText;
                                videoType.time  = result.amount;
                                Log.e(TAG,"moreURL" + videoType.moreURL);

                                // from ..to
                                videoType.phoneNumber = String.valueOf(result.isTop);
                                videoType.likeNum = String.valueOf(result.yn);
                                videoType.msg = result.type;
                                Log.e(TAG,"type->" + videoType.msg);

                                lists.add(videoType);
                            }

                            mView.showContent(lists);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //Log.d(TAG, "Get Data failed!");
                        mView.refreshFaild(StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }

    private void getVideoTGList(String catalogID) {
        SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
        Subscription rxSubscription = RetrofitHelper.getMemberApi().getTui(catalogID,sdf.format(new Date()))
                .compose(RxUtil.<CommonHttpResponse<List<ShopItem>>>rxSchedulerHelper())
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
                    public void call(List<ShopItem> results) {
                        if (results != null) {
                            List<VideoType> lists = new ArrayList<>();

                            for(ShopItem result: results) {
                                VideoType videoType = new VideoType();
                                //videoType.pic = StringUtils.getImgURL(result.itemPic);
                                videoType.pic = StringUtils.getVideoURL(result.itemText +"/"+ result.itemPic);
                                videoType.title = result.name;
                                videoType.airTime = result.itemTime;
                                videoType.score = result.bitemid;
                                videoType.dataId = String.valueOf(result.id);
                                videoType.moreURL= StringUtils.getVideoURL(result.itemText+"/index.m3u8");
                                Log.e(TAG,"moreURL->" + videoType.moreURL);
                                lists.add(videoType);
                            }

                            mView.showContent(lists);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //Log.d(TAG, "Get Data failed!");
                        mView.refreshFaild(StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }

    private void getVideoList(String catalogID) {
        Subscription rxSubscription = RetrofitHelper.getVideoApi().getVideoList(catalogID, page + "")
                .compose(RxUtil.<CommonHttpResponse<VideoRes>>rxSchedulerHelper())
                .compose(RxUtil.<VideoRes>handleResult())
                .subscribe(new Action1<VideoRes>() {
                    @Override
                    public void call(VideoRes res) {
                        if (res != null) {
                            if (page == 1) {
                                mView.showContent(res.list);
                            } else {
                                mView.showMoreContent(res.list);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (page > 1) {
                            page--;
                        }
                        mView.refreshFaild(StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }

    /**
     * 搜索电影
     *
     * @param searchStr
     */
    private void getSearchVideoList(String searchStr) {
        Subscription rxSubscription = RetrofitHelper.getVideoApi().getVideoListByKeyWord(searchStr, page + "")
                .compose(RxUtil.<CommonHttpResponse<VideoRes>>rxSchedulerHelper())
                .compose(RxUtil.<VideoRes>handleResult())
                .subscribe(new Action1<VideoRes>() {
                    @Override
                    public void call(VideoRes res) {
                        if (res != null) {
                            if (page == 1) {
                                mView.showContent(res.list);
                            } else {
                                mView.showMoreContent(res.list);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (page > 1) {
                            page--;
                        }
                        mView.refreshFaild(StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void loadMore() {
        page++;

        if(!catalogId.equals("推女郎") && !catalogId.equals("福利美图") ) {
            getVideoList(catalogId);
        }
    }

}
