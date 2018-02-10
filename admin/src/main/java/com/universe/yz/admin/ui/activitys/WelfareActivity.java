package com.universe.yz.admin.ui.activitys;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.Util;
import com.universe.yz.admin.model.bean.GankItemBean;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.presenter.WelfarePresenter;
import com.universe.yz.admin.ui.adapter.WelfareAdapter;
import com.universe.yz.admin.utils.ScreenUtil;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.presenter.contract.WelfareContract;
import com.universe.yz.admin.ui.adapter.NewsAdapter;
import com.universe.yz.admin.utils.EventUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description: 福利墙
 * Creator: yxc
 * date: 2017/9/6 14:57
 */
public class WelfareActivity extends SwipeBackActivity<WelfarePresenter> implements WelfareContract.View,
        SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {
    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.recyclerView)
    EasyRecyclerView mRecyclerView;

    NewsAdapter mAdapter1;
    WelfareAdapter mAdapter;

    public static boolean isNews = true;

    @Override
    protected int getLayout() {
        return R.layout.activity_welfare;
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    protected void initView() {

        if(isNews) {
            titleName.setText("游戏活动");
            mRecyclerView.setAdapterWithProgress(mAdapter1 = new NewsAdapter(mContext));
            mRecyclerView.setErrorView(R.layout.view_error);
            mAdapter1.setMore(R.layout.view_more, this);
            mAdapter1.setNoMore(R.layout.view_nomore);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY,
                    Util.dip2px(this, 0.2f), Util.dip2px(this, 72), 0);
            itemDecoration.setDrawLastItem(false);
            mRecyclerView.addItemDecoration(itemDecoration);
        }else {
            titleName.setText("福利");
            mRecyclerView.setAdapterWithProgress(mAdapter = new WelfareAdapter(mContext));
            mRecyclerView.setErrorView(R.layout.view_error);
            mAdapter.setMore(R.layout.view_more, this);
            mAdapter.setNoMore(R.layout.view_nomore);
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            SpaceDecoration itemDecoration = new SpaceDecoration(ScreenUtil.dip2px(mContext, 8));
            itemDecoration.setPaddingEdgeSide(true);
            itemDecoration.setPaddingStart(true);
            itemDecoration.setPaddingHeaderFooter(false);
            mRecyclerView.addItemDecoration(itemDecoration);
        }

        onRefresh();
    }

    @Override
    protected void initEvent() {
        mRecyclerView.setRefreshListener(this);
        if(isNews) {
            mAdapter1.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    final int  pos = position;
                    if(isNews) {
                        NewsItem newsItem = mAdapter1.getItem(pos);
                        CharSequence charSequence = Html.fromHtml(newsItem.getInfo());
                        new MaterialDialog.Builder(mContext)
                                .title(newsItem.getTitle())
                                .content(charSequence)
                                .backgroundColor(mContext.getResources().getColor(R.color.white))
                                .contentColor(mContext.getResources().getColor(R.color.bel_red_text))
                                .titleColor(mContext.getResources().getColor(R.color.blue_green))
                                .limitIconToDefaultSize()
                                .negativeText("取消")
                                .show();
                    }

                }
            });
            mAdapter1.setError(R.layout.view_error_footer, new NewsAdapter.OnErrorListener() {
                @Override
                public void onErrorShow() {
                    mAdapter1.resumeMore();
                }

                @Override
                public void onErrorClick() {
                    mAdapter1.resumeMore();
                }
            });
        } else {
            mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                }
            });
            mAdapter.setError(R.layout.view_error_footer, new WelfareAdapter.OnErrorListener() {
                @Override
                public void onErrorShow() {
                    mAdapter.resumeMore();
                }

                @Override
                public void onErrorClick() {
                    mAdapter.resumeMore();
                }
            });
        }
        mRecyclerView.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.showProgress();
                onRefresh();
            }
        });
    }


    @Override
    public void refreshFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        mRecyclerView.showError();
    }

    @Override
    public void loadMoreFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        if(isNews) {
            mAdapter1.pauseMore();
        } else {
            mAdapter.pauseMore();
        }
    }

    public void clearFooter() {
        if(isNews) {
            mAdapter1.setMore(new View(mContext), this);
            mAdapter1.setError(new View(mContext));
            mAdapter1.setNoMore(new View(mContext));
        } else {
            mAdapter.setMore(new View(mContext), this);
            mAdapter.setError(new View(mContext));
            mAdapter.setNoMore(new View(mContext));
        }
    }

    @Override
    public void showContent(List<GankItemBean> list, List<NewsItem> list1) {
        if(isNews) {
            mAdapter1.clear();
            if (list1 != null && list1.size() < WelfarePresenter.NUM_OF_PAGE) {
                clearFooter();
            }
            mAdapter1.addAll(list1);
        } else {
            mAdapter.clear();
            if (list != null && list.size() < WelfarePresenter.NUM_OF_PAGE) {
                clearFooter();
            }
            mAdapter.addAll(list);
        }
    }

    @Override
    public void showMoreContent(List<GankItemBean> list, List<NewsItem> list1) {
        if(isNews) mAdapter1.addAll(list1);
        else mAdapter.addAll(list);
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        if (mContext instanceof WelfareActivity) {
            finish();
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMore();
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }
}
