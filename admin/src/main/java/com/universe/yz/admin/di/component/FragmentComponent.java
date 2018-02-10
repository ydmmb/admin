package com.universe.yz.admin.di.component;

import android.app.Activity;

import com.universe.yz.admin.ui.fragments.NewsFragment;
import com.universe.yz.admin.ui.fragments.RoleFragment;
import com.universe.yz.admin.di.module.FragmentModule;
import com.universe.yz.admin.di.scope.FragmentScope;
import com.universe.yz.admin.ui.fragments.ClassificationFragment;
import com.universe.yz.admin.ui.fragments.CommentFragment;
import com.universe.yz.admin.ui.fragments.DiscoverFragment;
import com.universe.yz.admin.ui.fragments.MineFragment;
import com.universe.yz.admin.ui.fragments.RecommendFragment;
import com.universe.yz.admin.ui.fragments.SearchFragment;
import com.universe.yz.admin.ui.fragments.WmsFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();

    void inject(DiscoverFragment dailyFragment);

    void inject(ClassificationFragment dailyFragment);

    void inject(RecommendFragment recommendFragment);

    void inject(MineFragment mineFragment);

    void inject(CommentFragment commentFragment);

    void inject(WmsFragment wmsFragment);

    void inject(RoleFragment roleFragment);

    void inject(SearchFragment searchFragment);

    void inject(NewsFragment newsFragment);

}
