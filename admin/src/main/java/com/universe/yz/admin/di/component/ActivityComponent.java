package com.universe.yz.admin.di.component;

import android.app.Activity;

import com.universe.yz.admin.ui.activitys.CacheActivity;
import com.universe.yz.admin.ui.activitys.EditorActivity;
import com.universe.yz.admin.ui.activitys.HistoryActivity;
import com.universe.yz.admin.ui.activitys.SearchActivity;
import com.universe.yz.admin.ui.activitys.StreamInfoActivity;
import com.universe.yz.admin.ui.activitys.VideoInfoActivity;
import com.universe.yz.admin.ui.activitys.WelcomeActivity;
import com.universe.yz.admin.di.scope.ActivityScope;
import com.universe.yz.admin.ui.activitys.CollectionActivity;
import com.universe.yz.admin.ui.activitys.MmImageActivity;
import com.universe.yz.admin.di.module.ActivityModule;
import com.universe.yz.admin.ui.activitys.VideoListActivity;
import com.universe.yz.admin.ui.activitys.WelfareActivity;

import dagger.Component;

/**
 * Description:
 * Creator: yxc
 * date: $date $time
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {
    Activity getActivity();

    void inject(VideoInfoActivity videoInfoActivity);

    void inject(WelcomeActivity welcomeActivity);

    void inject(CollectionActivity collectionActivity);

    void inject(HistoryActivity historyActivity);

    void inject(SearchActivity searchActivity);

    void inject(VideoListActivity videoListActivity);

    void inject(WelfareActivity welfareActivity);

    void inject(MmImageActivity mmImageActivity);

    void inject(StreamInfoActivity streamInfoActivity);

    void inject(CacheActivity cacheActivity);

    void inject(EditorActivity editorActivity);
}
