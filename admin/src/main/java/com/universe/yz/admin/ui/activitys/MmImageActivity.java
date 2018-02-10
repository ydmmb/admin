package com.universe.yz.admin.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.model.bean.DataInfo;
import com.universe.yz.admin.presenter.MmPresenter;
import com.universe.yz.admin.presenter.contract.MmContract;
import com.universe.yz.admin.ui.adapter.MmAdapter;
import com.universe.yz.admin.widget.Image;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.utils.EventUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MmImageActivity extends SwipeBackActivity<MmPresenter> implements MmContract.View, SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {
    @BindView(R.id.mm_recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.title_name)
    ColorTextView titleName;
    DataInfo dataInfo;
    MmAdapter adapter;

    @Override
    protected void initView() {
        titleName.setText("美女福利");
        recyclerView.setAdapter(adapter=new MmAdapter(mContext));
        recyclerView.setErrorView(R.layout.view_error);
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPresenter.getTotalSize(dataInfo);

        onRefresh();
    }

    @Override
    protected void initEvent() {
        titleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EventUtil.isFastDoubleClick()) {
                    recyclerView.scrollToPosition(0);
                }
            }
        });
        recyclerView.setRefreshListener(this);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("MmImageActivity", "You are clicking " + adapter.getItem(position).toString());
            }
        });

        adapter.setError(R.layout.view_error_footer, new MmAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });

        recyclerView.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.showProgress();
                onRefresh();
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_list_image;
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        if (mContext instanceof MmImageActivity) {
            finish();
        }
    }

    @Override
    public void refreshFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        recyclerView.showError();
    }

    @Override
    public void loadMoreFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        adapter.pauseMore();
    }

    public void clearFooter() {
        adapter.setMore(new View(mContext), this);
        adapter.setError(new View(mContext));
        adapter.setNoMore(new View(mContext));
    }

    @Override
    public void showContent(List<Image> list) {
        adapter.clear();

        if (list != null && list.size() < MmPresenter.NUM_PER_PAGE) {
            clearFooter();
        }

        adapter.addAll(list);
    }

    @Override
    public void showMoreContent(List<Image> list) {
        adapter.addAll(list);
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMore();
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    protected void getIntentData() {
        dataInfo = new DataInfo();
        dataInfo.dir = getIntent().getStringExtra("dir");
        dataInfo.style_q = getIntent().getStringExtra("style_q");
        dataInfo.style_h = getIntent().getStringExtra("style_h");
        dataInfo.from_ = getIntent().getStringExtra("from");
        dataInfo.to = getIntent().getStringExtra("to");
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    public static void start(Context context, String dir,String style_q, String style_h, String from, String to) {
        Intent starter = new Intent(context, MmImageActivity.class);
        starter.putExtra("dir", dir);
        starter.putExtra("style_q", style_q);
        starter.putExtra("style_h", style_h);
        starter.putExtra("from", from);
        starter.putExtra("to", to);
        context.startActivity(starter);
    }
}
