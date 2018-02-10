package com.universe.yz.admin.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.ui.fragments.CommentFragment;
import com.universe.yz.admin.ui.fragments.VideoIntroFragment;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.model.bean.VideoInfo;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.VideoInfoPresenter;
import com.universe.yz.admin.presenter.contract.VideoInfoContract;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.M3u8Server;
import com.universe.yz.admin.widget.LVGhost;
import com.universe.yz.admin.widget.SwipeViewPager;
import com.universe.yz.admin.widget.theme.ColorTextView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ittiger.player.PlayerManager;
import cn.ittiger.player.VideoPlayerView;

public class StreamInfoActivity extends SwipeBackActivity<VideoInfoPresenter> implements VideoInfoContract.View {
    public static String TAG= StreamInfoActivity.class.getSimpleName();
    VideoInfo videoInfo;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.videoView)
    VideoPlayerView videoplayer;
    @BindView(R.id.title_name)
    ColorTextView mTitleName;
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewpagertab;
    @BindView(R.id.viewpager)
    SwipeViewPager mViewpager;
    @BindView(R.id.circle_loading)
    LVGhost mLoading;
    @BindView(R.id.rl_collect)
    RelativeLayout rlCollect;
    @BindView(R.id.ll_about)
    View wrapper;

    VideoRes videoRes;
    private Animation animation;

    @Override
    protected void initView() {
        animation = AnimationUtils.loadAnimation(mContext, R.anim.view_hand);
        rlCollect.setVisibility(View.VISIBLE);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(mContext)
                .add(R.string.video_intro, VideoIntroFragment.class)
                .add(R.string.video_comment, CommentFragment.class)
                .create());
        mViewpager.setAdapter(adapter);
        mViewpagertab.setViewPager(mViewpager);

        EventBus.getDefault().register(this);

        mPresenter.prepareInfo(videoInfo);
    }

    @Override
    protected void getIntentData() {
        videoInfo = (VideoInfo) getIntent().getSerializableExtra("videoInfo");
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_stream_info;
    }

    @OnClick(R.id.rl_back)
    public void back() {
        finish();
    }

    @Override
    public void hidLoading() {
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public void collected() {
        ivCollect.setBackgroundResource(R.mipmap.collection_select);
    }

    @Override
    public void disCollect() {
        ivCollect.setBackgroundResource(R.mipmap.collection);
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    public void showContent(VideoRes videoRes) {
        this.videoRes = videoRes;
        mTitleName.setText(videoRes.title);

        String m3u8file = Environment.getExternalStorageDirectory().getPath() +
                videoRes.getVideoUrl().substring(videoRes.getVideoUrl().indexOf("com/")+3);
        String url = String.format("http://127.0.0.1:%d%s", M3u8Server.PORT, m3u8file);

        Log.e(TAG,"videoRes.getVideoUrl()->　" + url);

        videoplayer.bind(url, videoRes.title);
        videoplayer.getThumbImageView().setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this).load(url).into(videoplayer.getThumbImageView());
    }

    @OnClick(R.id.iv_collect)
    public void onClick() {
        if (videoRes != null) {
            ivCollect.startAnimation(animation);
            mPresenter.collect();
        }
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    public static void start(Context context, VideoInfo videoInfo) {
        if(RealmHelper.getInstance().queryUser().getType().equals("普通会员")) {
            EventUtil.showToast(context, "请通过微信或支付宝捐助50元成为铜牌会员!");
        } else {
            Intent starter = new Intent(context, StreamInfoActivity.class);
            starter.putExtra("videoInfo", videoInfo);
            context.startActivity(starter);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        PlayerManager.getInstance().resume();
    }

    @Override
    protected void onPause() {

        super.onPause();
        PlayerManager.getInstance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PlayerManager.getInstance().release();
    }

    @Override
    public void onBackPressed() {

        if(!PlayerManager.getInstance().onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Subscriber(tag= Constants.PLAY_VIDEO_FLAG)
    public void playVideoNow(String flag) {
        if (flag != null) {
            videoplayer.startPlayVideo();
            Log.d(TAG, "playVideoNow: " + flag);
        }else {
            Log.d(TAG, "playVideoNow: No video available.");
        }
    }
}