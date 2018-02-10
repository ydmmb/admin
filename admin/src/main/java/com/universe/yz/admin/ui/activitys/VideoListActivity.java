package com.universe.yz.admin.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.universe.yz.admin.app.App;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.contract.VideoListContract;
import com.universe.yz.admin.ui.adapter.VideoListAdapter;
import com.universe.yz.admin.utils.BeanUtil;
import com.universe.yz.admin.utils.ScreenUtil;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.bean.VideoType;
import com.universe.yz.admin.presenter.VideoListPresenter;
import com.universe.yz.admin.utils.EventUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Description: 影片列表
 * Creator: yxc
 * date: 2017/9/6 14:57
 */
public class VideoListActivity extends SwipeBackActivity<VideoListPresenter> implements VideoListContract.View, SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    String mTitle = "";
    String mCatalogId = "";
    @BindView(R.id.title_name)
    ColorTextView mTitleName;

    @BindView(R.id.recyclerView)
    EasyRecyclerView mRecyclerView;
    VideoListAdapter mAdapter;
    VideoType videoType;
    VideoInfo videoInfo;
    int pageSize = 30;

    @Override
    protected int getLayout() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        mTitleName.setText(mTitle);
        mRecyclerView.setAdapterWithProgress(mAdapter = new VideoListAdapter(mContext));
        mRecyclerView.setErrorView(R.layout.view_error);
        mAdapter.setMore(R.layout.view_more, this);
        mAdapter.setNoMore(R.layout.view_nomore);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(mAdapter.obtainGridSpanSizeLookUp(3));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        SpaceDecoration itemDecoration = new SpaceDecoration(ScreenUtil.dip2px(mContext, 8));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        mRecyclerView.addItemDecoration(itemDecoration);
        onRefresh();
    }

    @Override
    protected void initEvent() {
        mTitleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EventUtil.isFastDoubleClick()) {
                    mRecyclerView.scrollToPosition(0);
                }
            }
        });
        mRecyclerView.setRefreshListener(this);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(alertMobile()) {
                    videoType = mAdapter.getItem(position);
                    if (videoType.msg != null && videoType.msg.equals("福利美图")) {
                        if (RealmHelper.getInstance().queryUser().getType().equals("银牌会员") ||
                                RealmHelper.getInstance().queryUser().getType().equals("金牌会员") ||
                                RealmHelper.getInstance().queryUser().getType().equals("至尊会员")) {
                            MmImageActivity.start(mContext, videoType.score, videoType.moreURL, videoType.time, videoType.phoneNumber, videoType.likeNum);
                        } else {
                            //EventUtil.showToast(mContext, "请通过微信或支付宝捐助100元成为银牌会员!");
                            alertPay("100", "银牌");
                        }
                    } else {
                        if ("推女郎".equals(mCatalogId)) {
                            if (RealmHelper.getInstance().queryUser().getType().equals("金牌会员") ||
                                    RealmHelper.getInstance().queryUser().getType().equals("至尊会员")) {
                                videoInfo = BeanUtil.VideoType2VideoInfo(mAdapter.getItem(position), videoInfo);
                                StreamInfoActivity.start(mContext, videoInfo);
                            } else {
                                //EventUtil.showToast(mContext, "请通过微信或支付宝捐助200元成为金牌会员!");
                                alertPay("200", "金牌");
                            }
                        } else {
                            videoInfo = BeanUtil.VideoType2VideoInfo(mAdapter.getItem(position), videoInfo);
                            if (RealmHelper.getInstance().queryUser().getType().equals("普通会员")) {
                                alertPay("50", "铜牌");
                            } else {
                                VideoInfoActivity.start(mContext, videoInfo);
                            }
                        }
                    }
                }
            }
        });
        mAdapter.setError(R.layout.view_error_footer, new VideoListAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                mAdapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                mAdapter.resumeMore();
            }
        });
        mRecyclerView.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.showProgress();
                onRefresh();
            }
        });
    }

    private boolean alertMobile() {
        final boolean[] bSug = {false};
        if (!App.isWifi(mContext)) {
            new MaterialDialog.Builder(mContext)
                    .title("提醒")
                    .content("会产生大流量,建议使用WIFI.")
                    .backgroundColor(mContext.getResources().getColor(R.color.white))
                    .contentColor(mContext.getResources().getColor(R.color.bel_red_text))
                    .titleColor(mContext.getResources().getColor(R.color.blue_green))
                    .limitIconToDefaultSize()
                    .positiveText("土豪继续")
                    .negativeText("等WIFI")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Log.d("VideoListActivity:", "Click: " + which);
                            bSug[0] =  true;
                        }
                    })
                    .show();

            return bSug[0];
        } else {
            return true;
        }
    }

    private void alertPay(String pay_num,String band) {
        new MaterialDialog.Builder(mContext)
                .title("打赏")
                .content("捐助" + pay_num +"元成为" + band + "会员！")
                .backgroundColor(mContext.getResources().getColor(R.color.white))
                .contentColor(mContext.getResources().getColor(R.color.bel_red_text))
                .titleColor(mContext.getResources().getColor(R.color.blue_green))
                .limitIconToDefaultSize()
                .positiveText("支付")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Log.d("VideoListActivity:","Click: "+ which);
                    }
                })
                .show();
    }

    @OnClick(R.id.rl_back)
    public void back() {
        if (mContext instanceof VideoListActivity) {
            finish();
        }
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
        mAdapter.pauseMore();
    }

    public void clearFooter() {
        mAdapter.setMore(new View(mContext), this);
        mAdapter.setError(new View(mContext));
        mAdapter.setNoMore(new View(mContext));
    }

    @Override
    public void showContent(List<VideoType> list) {
        mAdapter.clear();
        if (list != null && list.size() < pageSize) {
            clearFooter();
        }
        mAdapter.addAll(list);
    }

    @Override
    public void showMoreContent(List<VideoType> list) {
        mAdapter.addAll(list);
    }


    @Override
    public void onLoadMore() {
        mPresenter.loadMore();
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh(mCatalogId);
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    protected void getIntentData() {
        mCatalogId = getIntent().getStringExtra("catalogId");
        mTitle = getIntent().getStringExtra("title");
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    public static void start(Context context, String catalogId, String title) {
        Intent starter = new Intent(context, VideoListActivity.class);
        starter.putExtra("catalogId", catalogId);
        starter.putExtra("title", title);
        context.startActivity(starter);
    }
}