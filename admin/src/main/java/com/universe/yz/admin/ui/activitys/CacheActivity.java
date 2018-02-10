package com.universe.yz.admin.ui.activitys;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.easyrecyclerview.decoration.StickyHeaderDecoration;
import com.jude.rollviewpager.Util;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.CachePresenter;
import com.universe.yz.admin.presenter.VideoInfoPresenter;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.model.bean.VideoType;
import com.universe.yz.admin.presenter.contract.CacheContract;
import com.universe.yz.admin.ui.adapter.StickyHeaderAdapter;
import com.universe.yz.admin.ui.adapter.VideoCacheAdapter;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.Utils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class CacheActivity extends SwipeBackActivity<CachePresenter> implements CacheContract.View {

    @BindView(R.id.rl_collect_clear)
    RelativeLayout rlCollectClear;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.recyclerView)
    EasyRecyclerView mRecyclerView;
    VideoCacheAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_collection;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        setTitle();
        rlCollectClear.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapterWithProgress(mAdapter = new VideoCacheAdapter(mContext));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(this, 0.2f), Util.dip2px(this, 72), 0);
        itemDecoration.setDrawLastItem(false);
        mRecyclerView.addItemDecoration(itemDecoration);

        // StickyHeader
        StickyHeaderDecoration decoration = new StickyHeaderDecoration(new StickyHeaderAdapter(this));
        decoration.setIncludeHeader(false);
        mRecyclerView.addItemDecoration(decoration);

        mPresenter.getData(2);
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final int  pos = position;
                new MaterialDialog.Builder(mContext)
                        .title("请确认是否删除")
                        .content("注意，删除后不能恢复！")
                        .backgroundColor(mContext.getResources().getColor(R.color.white))
                        .contentColor(mContext.getResources().getColor(R.color.bel_red_text))
                        .titleColor(mContext.getResources().getColor(R.color.blue_green))
                        .limitIconToDefaultSize()
                        .positiveText("删除")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                VideoType videoType = mAdapter.getItem(pos);
                                Log.d("CacheActivity","id:"+ videoType.title);
                                EventUtil.showToast(mContext, "开始清除缓存");
                                final String rDir = videoType.moreURL;
                                final String id = videoType.title;
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        Utils.deleteFolderFile(rDir,true);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                RealmHelper.getInstance().deleteCaesar(id);
                                                EventUtil.showToast(getApplicationContext(), "清除缓存成功");
                                                mAdapter.remove(pos);
                                            }
                                        });
                                    }
                                }.start();
                            }
                        })
                        .show();
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
                if (mContext instanceof CacheActivity) {
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
        if (mContext instanceof CacheActivity) {
            titleName.setText("缓存");
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
