package com.universe.yz.admin.presenter;

import com.universe.yz.admin.model.bean.Record;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.Caesar;
import com.universe.yz.admin.model.bean.Collection;
import com.universe.yz.admin.model.bean.VideoType;
import com.universe.yz.admin.presenter.contract.CacheContract;
import com.universe.yz.admin.utils.Utils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Description: CollectionPresenter
 * Creator: yxc
 * date: 2016/9/29 12:15
 */
public class CachePresenter extends RxPresenter<CacheContract.View> implements CacheContract.Presenter {
    int type = 0;//收藏:0; 历史:1:

    @Inject
    public CachePresenter() {
    }

    @Override
    public void getData(int type) {
        this.type = type;
        if (type == 0) {
            getCollectData();
        } else if (type == 2) {
            getCaesarData();
        }else {
            getRecordData();
        }
    }

    @Override
    public void getCollectData() {
        List<Collection> collections = RealmHelper.getInstance().getCollectionList();
        List<VideoType> list = new ArrayList<>();
        VideoType videoType;
        for (Collection collection : collections) {
            videoType = new VideoType();
            videoType.title = collection.title;
            videoType.pic = collection.pic;
            videoType.dataId = collection.getId();
            videoType.score = collection.getScore();
            videoType.airTime = collection.getAirTime();
            list.add(videoType);
        }
        mView.showContent(list);
    }

    @Override
    public void delAllDatas() {
        if (type == 0) {
            RealmHelper.getInstance().deleteAllCollection();
        } else {
            RealmHelper.getInstance().deleteAllRecord();
            EventBus.getDefault().post("", VideoInfoPresenter.Refresh_History_List);
        }
    }

    @Override
    public void getRecordData() {
        List<Record> records = RealmHelper.getInstance().getRecordList();
        List<VideoType> list = new ArrayList<>();
        VideoType videoType;
        for (Record record : records) {
            videoType = new VideoType();
            videoType.title = record.title;
            videoType.pic = record.pic;
            videoType.dataId = record.getId();
            list.add(videoType);
        }
        mView.showContent(list);
    }

    @Override
    public void getCaesarData() {
        List<Caesar> records = RealmHelper.getInstance().getCaesarList();
        List<VideoType> list = new ArrayList<>();
        VideoType videoType;
        for (Caesar record : records) {
            videoType = new VideoType();
            //videoType.title = record.rpath;
            videoType.pic = record.pic;
            videoType.title = record.getId();
            double f = Utils.getFolderSize(new File(record.rpath));
            videoType.phoneNumber = Utils.getFormatSize(f);
            videoType.moreURL = record.rpath;
            list.add(videoType);
        }
        mView.showContent(list);
    }

    @Override
    public int getType() {
        return type;
    }
}
