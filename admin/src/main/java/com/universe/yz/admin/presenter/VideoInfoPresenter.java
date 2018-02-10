package com.universe.yz.admin.presenter;

import android.os.Environment;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.model.bean.M3U8;
import com.universe.yz.admin.model.bean.M3U8Ts;
import com.universe.yz.admin.model.bean.Record;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.utils.BeanUtil;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.Caesar;
import com.universe.yz.admin.model.bean.Collection;
import com.universe.yz.admin.model.bean.OnM3U8InfoListener;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.presenter.contract.VideoInfoContract;
import com.universe.yz.admin.utils.M3U8InfoManger;
import com.universe.yz.admin.utils.RxUtil;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

public class VideoInfoPresenter extends RxPresenter<VideoInfoContract.View> implements VideoInfoContract.Presenter {
    public static String TAG= VideoInfoPresenter.class.getSimpleName();
    public final static String Refresh_Video_Info = "Refresh_Video_Info";
    public final static String Put_DataId = "Put_DataId";
    public static final String Refresh_Collection_List = "Refresh_Collection_List";
    public static final String Refresh_History_List = "Refresh_History_List";
    private final int WAIT_TIME = 200;
    private VideoRes result;
    private String dataId = "";
    private String pic = "";
    private static M3U8 m3U8;

    @Inject
    public VideoInfoPresenter() {
    }

    public void prepareInfo(VideoInfo videoInfo) {
        mView.showContent(BeanUtil.VideoInfo2VideoRes(videoInfo, null));
        this.dataId = videoInfo.dataId;
        this.pic = videoInfo.pic;

        if (videoInfo.dataId.length()<= 3) {
            result = new VideoRes();
            result.HDURL = videoInfo.moreURL;
            result.title = videoInfo.title;
            result.pic = videoInfo.pic;
            result.actors="精品视频";
            result.description="";
            result.director="Henry Fifth";
            result.list = new ArrayList<>();

            if(m3U8 == null || m3U8.getTsList()== null || m3U8.getTsList().size()<=0) {
                getM3u8();
            }else {
                download();
            }

            mView.showContent(result);
            postData();
            insertRecord();
        }else {
            getDetailData(videoInfo.dataId);
        }
        setCollectState();
        putMediaId();
    }


    @Override
    public void getDetailData(String dataId) {
        Subscription rxSubscription = RetrofitHelper.getVideoApi().getVideoInfo(dataId)
                .compose(RxUtil.<CommonHttpResponse<VideoRes>>rxSchedulerHelper())
                .compose(RxUtil.<VideoRes>handleResult())
                .subscribe(new Action1<VideoRes>() {
                    @Override
                    public void call(final VideoRes res) {
                        if (res != null) {
                            mView.showContent(res);
                            result = res;
                            postData();
                            insertRecord();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.hidLoading();
                        mView.showError("数据加载失败");
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mView.hidLoading();
                    }
                });
        addSubscribe(rxSubscription);
    }

    private void postData() {
        Subscription rxSubscription = Observable.timer(WAIT_TIME, TimeUnit.MILLISECONDS)
                .compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        EventBus.getDefault().post(result, Refresh_Video_Info);
                    }
                });
        addSubscribe(rxSubscription);
    }

    private void putMediaId() {
        Subscription rxSubscription = Observable.timer(WAIT_TIME, TimeUnit.MILLISECONDS)
                .compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        EventBus.getDefault().post(dataId, Put_DataId);
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void collect() {
        if (RealmHelper.getInstance().queryCollectionId(dataId)) {
            RealmHelper.getInstance().deleteCollection(dataId);
            mView.disCollect();
        } else {
            if (result != null) {
                Collection bean = new Collection();
                bean.setId(String.valueOf(dataId));
                bean.setPic(pic);
                bean.setTitle(result.title);
                bean.setAirTime(result.airTime);
                bean.setScore(result.score);
                bean.setTime(System.currentTimeMillis());
                RealmHelper.getInstance().insertCollection(bean);
                mView.collected();
            }
        }
        //刷新收藏列表
        Subscription rxSubscription = Observable.timer(WAIT_TIME, TimeUnit.MILLISECONDS)
                .compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        EventBus.getDefault().post("", Refresh_Collection_List);
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void insertRecord() {
        if (!RealmHelper.getInstance().queryRecordId(dataId)) {
            if (result != null) {
                Record bean = new Record();
                bean.setId(String.valueOf(dataId));
                bean.setPic(pic);
                bean.setTitle(result.title);
                bean.setTime(System.currentTimeMillis());
                RealmHelper.getInstance().insertRecord(bean, MinePresenter.maxSize);
                //刷新收藏列表
                Subscription rxSubscription = Observable.timer(WAIT_TIME, TimeUnit.MILLISECONDS)
                        .compose(RxUtil.<Long>rxSchedulerHelper())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                EventBus.getDefault().post("", Refresh_History_List);
                            }
                        });
                addSubscribe(rxSubscription);
            }
        }
    }

    @Override
    public void download() {

        downloadListener = createLis();

        String rURL = result.HDURL.substring(0, result.HDURL.lastIndexOf("/"));
        String rPath = result.HDURL.substring(result.HDURL.indexOf("com/")+3);
        rPath = Environment.getExternalStorageDirectory().getPath() + rPath.substring(0, rPath.lastIndexOf("/"));

        Log.e(TAG,"rPath:" + rPath);
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);

        final List<BaseDownloadTask> tasks = new ArrayList<>();

        tasks.add(FileDownloader
                .getImpl()
                .create(result.HDURL)
                .setPath(rPath + File.separator + "index.m3u8",false)
                .setTag(0));
        Log.e(TAG,"index:" + rPath + File.separator + "index.m3u8");
        List<M3U8Ts> m3U8Ts = m3U8.getTsList();
        for (int i = 0; i <m3U8Ts.size(); i++) {
            M3U8Ts m3U8Ts1 = m3U8Ts.get(i);
            String m3u8file = rPath + File.separator + m3U8Ts1.getFile();
            Log.e(TAG,"Segment:" + m3u8file);
            Log.e(TAG,"URL:" + rURL + File.separator + m3U8Ts1.getFile());
            tasks.add(FileDownloader
                    .getImpl()
                    .create(rURL + File.separator + m3U8Ts1.getFile())
                    .setPath(m3u8file,false)
                    .setTag(i + 1));
        }

        queueSet.disableCallbackProgressTimes();
        queueSet.setAutoRetryTimes(2);
        queueSet.downloadSequentially(tasks);

        if(!RealmHelper.getInstance().queryCaesarId(result.title)) {
            Caesar caesar = new Caesar();
            caesar.setRpath(rPath);
            caesar.setPic(result.pic);
            caesar.setId(result.title);
            caesar.setTime(new Date().getTime()/1000);
            Log.e(TAG,"Caesar:" + result.title +"|"+result.pic+"|"+rPath);
            RealmHelper.getInstance().insertCaesar(caesar);
        }

        queueSet.start();
    }


    private void setCollectState() {
        if (RealmHelper.getInstance().queryCollectionId(dataId)) {
            mView.collected();
        } else {
            mView.disCollect();
        }
    }

    private void getM3u8() {
        M3U8InfoManger.getInstance().getM3U8Info(result.HDURL, new OnM3U8InfoListener() {
            @Override
            public void onSuccess(M3U8 m3U8) {
                VideoInfoPresenter.m3U8 = m3U8;
                download();
            }

            @Override
            public void onStart() {
                Log.e(TAG,"开始获取信息" );
            }

            @Override
            public void onError(Throwable errorMsg) {
                Log.e(TAG,"出错了" + errorMsg);
            }
        });
    }

    private FileDownloadListener downloadListener;
    private FileDownloadListener createLis() {
        return new FileDownloadListener() {

            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                // 之所以加这句判断，是因为有些异步任务在pause以后，会持续回调pause回来，而有些任务在pause之前已经完成，
                // 但是通知消息还在线程池中还未回调回来，这里可以优化
                // 后面所有在回调中加这句都是这个原因
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                     int soFarBytes, int totalBytes) {

                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                if (task.getListener() != downloadListener) {
                    return;
                }

                // trigger play event when two segments are downloaded.
                if ((int)task.getTag() == Constants.startfrom) {
                    // Notify the video can be played.
                    EventBus.getDefault().post("VIDEO", Constants.PLAY_VIDEO_FLAG);
                    mView.hidLoading();
                    Log.e(TAG,"Send Event:" + task.getTag());
                }
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                if (task.getListener() != downloadListener) {
                    return;
                }

                if (task.isReusedOldFile() && (int)task.getTag() == Constants.startfrom) {
                    EventBus.getDefault().post("VIDEO", Constants.PLAY_VIDEO_FLAG);
                    mView.hidLoading();
                    Log.e(TAG,"Send Event(re-used):" + task.getTag());
                }
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                if (task.getListener() != downloadListener) {
                    return;
                }
            }
        };
    }
}