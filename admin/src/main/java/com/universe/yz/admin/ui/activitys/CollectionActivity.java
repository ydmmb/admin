package com.universe.yz.admin.ui.activitys;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.presenter.CollectionPresenter;
import com.universe.yz.admin.presenter.VideoInfoPresenter;
import com.universe.yz.admin.presenter.contract.CollectionContract;
import com.universe.yz.admin.ui.adapter.VideoListAdapter;
import com.universe.yz.admin.utils.BeanUtil;
import com.universe.yz.admin.utils.ScreenUtil;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.bean.VideoType;
import com.universe.yz.admin.utils.EventUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * Description: 收藏
 * Creator: yxc
 * date: 2017/9/6 14:57
 */
public class CollectionActivity extends SwipeBackActivity<CollectionPresenter> implements CollectionContract.View {

    @BindView(R.id.rl_collect_clear)
    RelativeLayout rlCollectClear;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.recyclerView)
    EasyRecyclerView mRecyclerView;
    VideoListAdapter mAdapter;
    VideoInfo videoInfo;

    @Override
    protected int getLayout() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        setTitle();
        rlCollectClear.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapterWithProgress(mAdapter = new VideoListAdapter(mContext));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(mAdapter.obtainGridSpanSizeLookUp(3));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        SpaceDecoration itemDecoration = new SpaceDecoration(ScreenUtil.dip2px(mContext, 8));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        mRecyclerView.addItemDecoration(itemDecoration);

        mPresenter.getData(0);
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                videoInfo = BeanUtil.VideoType2VideoInfo(mAdapter.getItem(position), videoInfo);
                VideoInfoActivity.start(mContext, videoInfo);
            }
        });
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }


    @Override
    public void showContent(List<VideoType> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
    }

    @OnClick({R.id.rl_back, R.id.rl_collect_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                if (mContext instanceof CollectionActivity) {
                    finish();
                } else if (mContext instanceof HistoryActivity) {
                    finish();
                }
                break;
            case R.id.rl_collect_clear:
                mAdapter.clear();
                mPresenter.delAllDatas();
                break;
        }
    }

    private void setTitle() {
        if (mContext instanceof CollectionActivity) {
            titleName.setText("收藏");
        } else if (mContext instanceof HistoryActivity) {
            titleName.setText("历史");
        }
    }

    @Subscriber(tag = VideoInfoPresenter.Refresh_Collection_List)
    public void setData(String tag) {
        mPresenter.getCollectData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }
}
