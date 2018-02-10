package com.universe.yz.admin.presenter;

import android.util.Log;

import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.presenter.contract.MmContract;
import com.universe.yz.admin.widget.Image;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.DataInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class MmPresenter extends RxPresenter<MmContract.View> implements MmContract.Presenter {
    public static final int NUM_PER_PAGE = 5;
    private int currentPage = 0;
    private int pageSize = 0;
    private DataInfo dataInfo;

    @Override
    public void getTotalSize(DataInfo dataInfo) {
        this.dataInfo = dataInfo;
        int from = Integer.valueOf(dataInfo.from_);
        int to = Integer.valueOf(dataInfo.to);

        pageSize = (int) Math.ceil((to - from) / NUM_PER_PAGE);
    }

    @Inject
    public MmPresenter() {}

    @Override
    public void onRefresh() {
        currentPage = 0;
        getData();
    }

    private void  getData() {
        int from = Integer.valueOf(dataInfo.from_);
        int to = Integer.valueOf(dataInfo.to);

        String base_url = Constants.PIC_BASE_URL+ dataInfo.dir+"/";
        String url ="";
        from = from + 1;

        if(currentPage <= pageSize) {
            List<Image> imageList = new ArrayList<>();
            for (int i = from + (currentPage * NUM_PER_PAGE); i < from + (currentPage + 1) * NUM_PER_PAGE; i++) {
                if(i <= to) {
                    if (i < 10)
                        url = base_url + dataInfo.style_q + "0" + i + dataInfo.style_h + ".jpg";
                    if (i >= 10) url = base_url + dataInfo.style_q + i + dataInfo.style_h + ".jpg";
                    Log.d("MmPresenter", "url:" + url);
                    imageList.add(new Image(url, 3600, 5400, null));
                }
            }

            if(imageList.size()>0) {
                if (currentPage == 0) mView.showContent(imageList);
                else mView.showMoreContent(imageList);
            }
        } else {
            if (currentPage>0)  {
                mView.showMoreContent(null);
            }
        }
    }


    @Override
    public void loadMore() {
        currentPage++;

        getData();
    }
}
