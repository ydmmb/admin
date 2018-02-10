package com.universe.yz.admin.ui.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;

import com.universe.yz.admin.R;
import com.universe.yz.admin.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MallFragment extends BaseFragment {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tab)
    TabLayout tab;

    private List<Pair<String, Fragment>> items;

    @Override
    protected void initView(LayoutInflater inflater) {
        super.initView(inflater);

        items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("游戏活动", new NewsFragment()));
        items.add(new Pair<String, Fragment>("游戏商城", new SearchFragment()));
        items.add(new Pair<String, Fragment>("角色", new RoleFragment()));
        items.add(new Pair<String, Fragment>("仓库", new WmsFragment()));

        viewPager.setAdapter(new MainAdapter(getActivity().getSupportFragmentManager()));
        tab.setupWithViewPager(viewPager);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mall;
    }

    private class MainAdapter extends FragmentPagerAdapter {

        MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position).second;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items.get(position).first;
        }
    }
}