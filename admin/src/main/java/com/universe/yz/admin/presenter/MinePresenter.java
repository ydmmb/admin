package com.universe.yz.admin.presenter;

import com.universe.yz.admin.model.bean.Record;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.contract.MineContract;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.VideoType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Description: CollectionPresenter
 * Creator: yxc
 * date: 2016/9/29 12:19
 */
public class MinePresenter extends RxPresenter<MineContract.View> implements MineContract.Presenter {
    public static final int maxSize = 30;

    @Inject
    public MinePresenter() {
    }

    @Override
    public void getHistoryData() {
        List<Record> records = RealmHelper.getInstance().getRecordList();
        List<VideoType> list = new ArrayList<>();
        VideoType videoType;
        int maxlinth = records.size() <= 3 ? records.size() : 3;
        for (int i = 0; i < maxlinth; i++) {
            Record record = records.get(i);
            videoType = new VideoType();
            videoType.title = record.title;
            videoType.pic = record.pic;
            videoType.dataId = record.getId();
            list.add(videoType);
        }
        mView.showContent(list);
    }

    @Override
    public void delAllHistory() {
        RealmHelper.getInstance().deleteAllRecord();
    }

}
