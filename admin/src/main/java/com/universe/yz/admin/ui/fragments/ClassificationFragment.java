package com.universe.yz.admin.ui.fragments;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.universe.yz.admin.base.BaseMvpFragment;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.contract.ClassificationContract;
import com.universe.yz.admin.ui.activitys.LoginActivity;
import com.universe.yz.admin.ui.adapter.ClassificationAdapter;
import com.universe.yz.admin.utils.ScreenUtil;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.presenter.ClassificationPresenter;
import com.universe.yz.admin.ui.activitys.VideoListActivity;
import com.universe.yz.admin.utils.EventUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ClassificationFragment extends BaseMvpFragment<ClassificationPresenter> implements ClassificationContract.View, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "ClassificationFragment";

    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    ClassificationAdapter adapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        titleName.setText("专题");
        EventBus.getDefault().register(this);
        recyclerView.setAdapterWithProgress(adapter = new ClassificationAdapter(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setErrorView(R.layout.view_error);
        SpaceDecoration itemDecoration = new SpaceDecoration(ScreenUtil.dip2px(getContext(), 8));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    protected void initEvent() {
        recyclerView.setRefreshListener(this);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VideoListActivity.start(mContext, StringUtils.getCatalogId(adapter.getItem(position).moreURL), adapter.getItem(position).title);
            }
        });
        recyclerView.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.showProgress();
                onRefresh();
            }
        });

        mPresenter.onRefresh();
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void showContent(VideoRes videoRes) {
        if (videoRes != null) {
            adapter.clear();
            List<VideoInfo> videoInfos = new ArrayList<>();
            for (int i = 1; i < videoRes.list.size(); i++) {
                if (!TextUtils.isEmpty(videoRes.list.get(i).moreURL) && !TextUtils.isEmpty(videoRes.list.get(i).title)) {
                    VideoInfo videoInfo = videoRes.list.get(i).childList.get(0);
                    videoInfo.title = videoRes.list.get(i).title;
                    videoInfo.moreURL = videoRes.list.get(i).moreURL;
                    videoInfos.add(videoInfo);
                }
            }

            VideoInfo videoInfo = new VideoInfo();
            videoInfo.title = "福利美图";
            videoInfo.pic = "http://p.fgame.com/api/ShopPic/17_10_23_12_30_16.jpg";
            videoInfo.moreURL = "http://p.fgame.com/api/columns/getVideoList.do?catalogId=福利美图&information=null";
            videoInfos.add(videoInfo);
            // 对于注册登陆用户追加以下内容
            User user = RealmHelper.getInstance().queryUser();
            if (user != null ) {
                // Add a classification by inserting data to results
                videoInfo = new VideoInfo();
                videoInfo.title = "推女郎";
                videoInfo.pic = "http://p.fgame.com/api/ShopPic/yy20171019220020.png";
                videoInfo.moreURL = "http://p.fgame.com/api/columns/getVideoList.do?catalogId=推女郎&information=null";
                videoInfos.add(videoInfo);
            }

            adapter.addAll(videoInfos);
        }
    }


    @Subscriber(tag= LoginActivity.EXTRA_DATA)
    public void refreshData(String username) {
        Log.d(TAG, "refreshData: " + username);
        mPresenter.onRefresh();
    }

    @Override
    public void refreshFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        recyclerView.showError();
    }


    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_classification;
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }
}
