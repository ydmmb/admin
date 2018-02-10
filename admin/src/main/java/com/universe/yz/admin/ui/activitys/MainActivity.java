package com.universe.yz.admin.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.views.PgyerDialog;
import com.universe.yz.admin.app.App;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.ui.fragments.DiscoverFragment;
import com.universe.yz.admin.ui.fragments.MallFragment;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.utils.ThemeUtils;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.BaseActivity;
import com.universe.yz.admin.ui.adapter.ContentPagerAdapter;
import com.universe.yz.admin.ui.fragments.ClassificationFragment;
import com.universe.yz.admin.ui.fragments.MineFragment;
import com.universe.yz.admin.ui.fragments.RecommendFragment;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.M3u8Server;
import com.universe.yz.admin.utils.PreUtils;
import com.universe.yz.admin.utils.ThemeUtil;
import com.universe.yz.admin.widget.ResideLayout;
import com.universe.yz.admin.widget.UnScrollViewPager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ColorChooserDialog.ColorCallback {

    public static final String Set_Theme_Color = "Set_Theme_Color";
    public final static String Banner_Stop = "Banner_Stop";
    private Long firstTime = 0L;
    final int WAIT_TIME = 200;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.tv_mydown)
    TextView tvMydown;
    @BindView(R.id.tv_fuli)
    TextView tvFuli;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.tv_feedback)
    TextView tvFeedback;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.about)
    TextView about;
    @BindView(R.id.theme)
    TextView theme;
    @BindView(R.id.tab_rg_menu)
    RadioGroup tabRgMenu;
    @BindView(R.id.vp_content)
    UnScrollViewPager vpContent;
    @BindView(R.id.resideLayout)
    ResideLayout mResideLayout;
    ContentPagerAdapter mPagerAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        List<Fragment> fragments = initFragments();
        vpContent.setScrollable(false);
        mPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager(), fragments);
        vpContent.setAdapter(mPagerAdapter);
        vpContent.setOffscreenPageLimit(fragments.size());
        StringUtils.setIconDrawable(mContext, tvCollect, MaterialDesignIconic.Icon.gmi_collection_bookmark, 16, 10);
        StringUtils.setIconDrawable(mContext, tvMydown, MaterialDesignIconic.Icon.gmi_download, 16, 10);
        StringUtils.setIconDrawable(mContext, tvFuli, MaterialDesignIconic.Icon.gmi_mood, 16, 10);
        StringUtils.setIconDrawable(mContext, tvShare, MaterialDesignIconic.Icon.gmi_share, 16, 10);
        StringUtils.setIconDrawable(mContext, tvFeedback, MaterialDesignIconic.Icon.gmi_android, 16, 10);
        StringUtils.setIconDrawable(mContext, tvSetting, MaterialDesignIconic.Icon.gmi_settings, 16, 10);
        StringUtils.setIconDrawable(mContext, about, MaterialDesignIconic.Icon.gmi_account, 16, 10);
        StringUtils.setIconDrawable(mContext, theme, MaterialDesignIconic.Icon.gmi_palette, 16, 10);

        M3u8Server.finish();

        M3u8Server.execute();
    }

    @Override
    protected void initEvent() {
        tabRgMenu.setOnCheckedChangeListener(this);
        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) tabRgMenu.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mResideLayout.setPanelSlideListener(new ResideLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                postBannerState(true);
            }

            @Override
            public void onPanelOpened(View panel) {
                postBannerState(true);
            }

            @Override
            public void onPanelClosed(View panel) {
                postBannerState(false);
            }
        });
    }

    private void postBannerState(final boolean stop) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(stop, Banner_Stop);
            }
        }, WAIT_TIME);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.tab_rb_1:
                vpContent.setCurrentItem(0, false);
                break;
            case R.id.tab_rb_2:
                vpContent.setCurrentItem(1, false);
                break;
            case R.id.tab_rb_5:
                vpContent.setCurrentItem(2, false);
                break;
            case R.id.tab_rb_3:
                vpContent.setCurrentItem(3, false);
                break;
            case R.id.tab_rb_4:
                vpContent.setCurrentItem(4, false);
                break;
        }
    }

    private List<Fragment> initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        Fragment fragment1 = new RecommendFragment();
        Fragment fragment2 = new ClassificationFragment();
        Fragment fragment4 = new MallFragment();
        Fragment fragment3 = new DiscoverFragment();
        Fragment mineFragment = new MineFragment();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment4);
        fragments.add(fragment3);
        fragments.add(mineFragment);
        return fragments;
    }

    @Subscriber(tag = MineFragment.SET_THEME)
    public void setTheme(String content) {
        new ColorChooserDialog.Builder(this, R.string.theme)
                .customColors(R.array.colors, null)
                .doneButton(R.string.done)
                .cancelButton(R.string.cancel)
                .allowUserColorInput(false)
                .allowUserColorInputAlpha(false)
                .show(this);
                //.show();
    }

    @OnClick({R.id.tv_collect, R.id.tv_mydown, R.id.tv_fuli, R.id.tv_share, R.id.tv_feedback, R.id.tv_setting, R.id.about, R.id.theme})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_collect:
                mContext.startActivity(new Intent(mContext, CollectionActivity.class));
                break;
            case R.id.tv_mydown:
                //EventUtil.showToast(mContext, "敬请期待");
                mContext.startActivity(new Intent(mContext, CacheActivity.class));
                break;
            case R.id.tv_fuli:
                mContext.startActivity(new Intent(mContext, WelfareActivity.class));
                break;
            case R.id.tv_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.setting_recommend_content));
                shareIntent.setType("text/plain");

                //设置分享列表的标题，并且每次都显示分享列表
                mContext.startActivity(Intent.createChooser(shareIntent, "分享到"));
                break;
            case R.id.tv_feedback:
                // 以对话框的形式弹出
                PgyerDialog.setDialogTitleBackgroundColor(PreUtils.getString(mContext, Constants.PRIMARYCOLOR, "#000000"));
                PgyerDialog.setDialogTitleTextColor(PreUtils.getString(mContext, Constants.TITLECOLOR, "#0aa485"));
                PgyFeedback.getInstance().showDialog(mContext).d().setChecked(false);
                break;
            case R.id.tv_setting:
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.about:
                new MaterialDialog.Builder(mContext)
                        .title(R.string.about)
                        .titleColor(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary))
                        .icon(new IconicsDrawable(mContext)
                                .color(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary))
                                .icon(MaterialDesignIconic.Icon.gmi_account)
                                .sizeDp(20))
                        .content(R.string.about_me)
                        .contentColor(ThemeUtils.getThemeColor(mContext, R.attr.colorPrimary))
                        .positiveText(R.string.close)
                        .show();
                break;
            case R.id.theme:
                setTheme("");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        M3u8Server.finish();
    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        ThemeUtil.onColorSelection(this, dialog, selectedColor);
        EventBus.getDefault().post("", Set_Theme_Color);
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }

    @Override
    public void onBackPressed() {
        if (mResideLayout.isOpen()) {
            mResideLayout.closePane();
        } else {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 1500) {
                EventUtil.showToast(this, "再按一次退出");
                firstTime = secondTime;
            } else {
                App.getInstance().exitApp();
            }
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

}