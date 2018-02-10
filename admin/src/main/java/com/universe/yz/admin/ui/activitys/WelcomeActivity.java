package com.universe.yz.admin.ui.activitys;

import android.util.Log;
import android.widget.ImageView;

import com.universe.yz.admin.app.App;
import com.universe.yz.admin.base.BaseMvpActivity;
import com.universe.yz.admin.utils.NetStatus;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.R;
import com.universe.yz.admin.component.ImageLoader;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.WelcomePresenter;
import com.universe.yz.admin.presenter.contract.WelcomeContract;
import com.universe.yz.admin.utils.EventUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import butterknife.BindView;

public class WelcomeActivity extends BaseMvpActivity<WelcomePresenter> implements WelcomeContract.View {
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @BindView(R.id.iv_welcome_bg)
    ImageView ivWelcomeBg;

    @Override
    protected int getLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        if (App.checkNetworkConnection(mContext) == NetStatus.NO_NET_AVAILABLE) {
            EventUtil.showToast(mContext, NetStatus.NO_NET_AVAILABLE.getMessage());
        }
        mPresenter.getWelcomeData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    public void showContent(List<String> list) {
        if (list != null) {
            int page = StringUtils.getRandomNumber(0, list.size() - 1);
            ImageLoader.load(mContext, list.get(page), ivWelcomeBg);
            ivWelcomeBg.animate().scaleX(1.12f).scaleY(1.12f).setDuration(2000).setStartDelay(100).start();
        }
    }

    @Override
    public void jumpToMain() {
        if (RealmHelper.getInstance().queryUser() != null) {
            MainActivity.start(this);
        }else {
            LoginActivity.start(this,"welcome");
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Subscriber(tag=LoginActivity.EXTRA_DATA)
    public void refreshVIP(User user) {
        if (user != null) {
            mPresenter.getIsVIP(user);
        }else {
            Log.d(TAG, "refreshVIP: user = null");
        }
    }
}