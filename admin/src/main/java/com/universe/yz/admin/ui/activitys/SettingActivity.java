package com.universe.yz.admin.ui.activitys;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.views.PgyerDialog;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.ui.adapter.AppInfoAdapter;
import com.universe.yz.admin.utils.PreUtils;
import com.universe.yz.admin.utils.RxUtil;
import com.universe.yz.admin.utils.ThemeUtils;
import com.universe.yz.admin.widget.theme.ColorTextView;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.helper.SessionManager;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.utils.AppInfo;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.Utils;
import com.universe.yz.admin.widget.RoundCornerButton;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Description: 设置
 * Creator: yxc
 * date: 2017/9/6 14:57
 */
public class SettingActivity extends SwipeBackActivity {
    public static final String TAG = "SettingActivity";
    public static final String LOGOUT_NOTIFY="LOGOUT_NOTIFY";
    private ArrayList<AppInfo> mAppinfoList;
    private AppInfoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Intent shareIntent;

    {
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "p.fgame.com");
    }

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.rl_recommend)
    RelativeLayout rlRecommend;
    @BindView(R.id.rl_about)
    RelativeLayout rlAbout;
    @BindView(R.id.rl_feedback)
    RelativeLayout rlFeedback;
    @BindView(R.id.tv_cache)
    TextView tvCache;
    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.tv_logout)
    RoundCornerButton btnRound;
    User user;

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        titleName.setText("设置");
        tvCache.setText(EventUtil.getFormatSize(Glide.getPhotoCacheDir(this).length()));

        btnRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = RealmHelper.getInstance().queryUser();
                Log.e(TAG, "id: " + (user != null? user.getId():""));
                if(user != null) {
                    RetrofitHelper.getMemberApi().logout()
                            .compose(RxUtil.<CommonHttpResponse<String>>rxSchedulerHelper())
                            .subscribe(new Action1<CommonHttpResponse<String>>() {
                                @Override
                                public void call(CommonHttpResponse<String> stringCommonHttpResponse) {
                                    if(stringCommonHttpResponse.getCode()==100) {
                                        SessionManager.getInstance(getApplicationContext()).setLogin(false);
                                        RealmHelper.getInstance().deleteUserById(user.getId());
                                        //App.getInstance().setUser(null);
                                        EventBus.getDefault().post("Logout",LOGOUT_NOTIFY);
                                        EventUtil.showToast(getApplicationContext(),"已经退出当前用户");
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "Logout failed! please try again!");
                                    return;
                                }
                            });
                }else {
                    EventUtil.showToast(getApplicationContext(),"已经退出当前用户");
                }
            }
        });
    }

    @OnClick({R.id.rl_back, R.id.rl_recommend, R.id.rl_about, R.id.rl_feedback, R.id.rl_clearcache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_recommend:
                /*
                new MaterialDialog.Builder(this)
                        .content(R.string.setting_recommend_content)
                        .contentColor(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                        .positiveText(R.string.close)
                        .negativeText(R.string.setting_recommend_copy).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(getResources().getString(R.string.setting_recommend_url));
                        EventUtil.showToast(SettingActivity.this, "已复制到粘贴板");
                    }
                }).show();*/
                showBottomDialog();
                break;
            case R.id.rl_about:
                new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .titleColor(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                        .icon(new IconicsDrawable(this)
                                .color(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                                .icon(MaterialDesignIconic.Icon.gmi_account)
                                .sizeDp(20))
                        .content(R.string.about_me)
                        .contentColor(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                        .positiveText(R.string.close)
                        .show();
                break;
            case R.id.rl_feedback:
                PgyerDialog.setDialogTitleBackgroundColor(PreUtils.getString(this, Constants.PRIMARYCOLOR, "#000000"));
                PgyerDialog.setDialogTitleTextColor(PreUtils.getString(this, Constants.TITLECOLOR, "#0aa485"));
                PgyFeedback.getInstance().showDialog(this);
                PgyFeedback.getInstance().showDialog(this).d().setChecked(false);
                break;
            case R.id.rl_clearcache:
                EventUtil.showToast(this, "开始清除缓存");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Glide.get(getApplicationContext()).clearDiskCache();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                EventUtil.showToast(getApplicationContext(), "清除缓存成功");
                                tvCache.setText("0kb");
                            }
                        });
                    }
                }.start();
                break;
        }
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppinfoList = Utils.getShareAppList(this, shareIntent);
        for(int i=0; i < mAppinfoList.size(); i++) {
            if  (!mAppinfoList.get(i).getAppName().equals("发送给朋友") ||
                    !mAppinfoList.get(i).getAppName().equals("发送给好友") ||
                    !mAppinfoList.get(i).getAppName().equals("知乎首页") ||
                    !mAppinfoList.get(i).getAppName().equals("知乎私信") ||
                    !mAppinfoList.get(i).getAppName().equals("通过邮件发送"))
                mAppinfoList.remove(i);
        }
    }

    /*
        @Subscriber(tag=LoginActivity.EXTRA_DATA)
        public void refreshSetting(String username) {
            Log.d(TAG, "refreshSettingActivity: " + username);
            btnRound.setVisibility(View.GONE);
        }
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initInject() {

    }

    private void showBottomDialog() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(R.layout.dialog_bottom_sheet);
        initBottomDialog(mBottomSheetDialog);
        mBottomSheetDialog.show();
       // for(int i=0; i< mAppinfoList.size(); i++) {
         //   Log.e(TAG,mAppinfoList.get(i).getAppName() +"|"+ mAppinfoList.get(i).getPkgName()+"|"+mAppinfoList.get(i).getLaunchClassName());
        //}
    }

    private void initBottomDialog(final Dialog dialog) {
        mAdapter = new AppInfoAdapter(this, mAppinfoList);

        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.list_view_fen);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickLitener(new AppInfoAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(AppInfo appInfo, View view, int position) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setComponent(new ComponentName(appInfo.getPkgName(), appInfo.getLaunchClassName()));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "http://p.fgame.com/api/app-release-1.3.0.apk");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(AppInfo appInfo, View view, int position) {
                // 打开应用信息界面
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + appInfo.getPkgName()));
                startActivity(intent);
            }
        });
    }
}