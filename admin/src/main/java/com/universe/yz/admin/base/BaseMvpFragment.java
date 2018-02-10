package com.universe.yz.admin.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.universe.yz.admin.di.component.FragmentComponent;
import com.universe.yz.admin.app.App;
import com.universe.yz.admin.di.module.FragmentModule;
import com.universe.yz.admin.di.component.DaggerFragmentComponent;

import javax.inject.Inject;

/**
 * Created by yuexingchuan on 17/9/14.
 */

public abstract class BaseMvpFragment<T extends BasePresenter> extends BaseFragment implements BaseView {

    @Inject
    protected T mPresenter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initInject();
        if (mPresenter != null) mPresenter.attachView(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) mPresenter.detachView();
        mPresenter = null;
        super.onDestroy();
    }

    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(App.getAppComponent())
                .fragmentModule(getFragmentModule())
                .build();
    }

    protected FragmentModule getFragmentModule() {
        return new FragmentModule(this);
    }

    protected abstract void initInject();
}
