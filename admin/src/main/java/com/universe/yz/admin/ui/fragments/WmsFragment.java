package com.universe.yz.admin.ui.fragments;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.universe.yz.admin.utils.ScreenUtil;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.BaseMvpFragment;
import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.presenter.ShopPresenter;
import com.universe.yz.admin.presenter.contract.ShopContract;
import com.universe.yz.admin.ui.adapter.ShopAdapter;
import com.universe.yz.admin.utils.EventUtil;

import java.util.List;

import butterknife.BindView;

public class WmsFragment extends BaseMvpFragment<ShopPresenter> implements ShopContract.View, SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnLoadMoreListener {
   // @BindView(R.id.title_name)
   // ColorTextView titleName;
    @BindView(R.id.recyclerView1)
    EasyRecyclerView recyclerView;
    ShopAdapter adapter;
    public String shopCat;

    public void WmsFragment(String shopCat) {
        this.shopCat = shopCat;
    }

    @Override
    protected void initView(LayoutInflater inflater) {
        //titleName.setText("商城");
        recyclerView.setAdapterWithProgress(adapter = new ShopAdapter(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setErrorView(R.layout.view_error);
        SpaceDecoration itemDecoration = new SpaceDecoration(ScreenUtil.dip2px(getContext(), 8));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    protected void initEvent() {
        recyclerView.setRefreshListener(this);
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
               // VideoListActivity.start(mContext, StringUtils.getCatalogId(adapter.getItem(position).moreURL), adapter.getItem(position).title);
            }
        });
        recyclerView.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.showProgress();
                onRefresh();
            }
        });

        mPresenter.onRefresh("");
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    public void showContent(List<ShopItem> shopItems) {
        if (shopItems != null) {
            adapter.clear();
            adapter.addAll(shopItems);
        }
    }

    @Override
    public void refreshFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        recyclerView.showError();
    }

    @Override
    public void loadMoreFaild(String msg) {
        if (!TextUtils.isEmpty(msg))
            showError(msg);
        adapter.pauseMore();
    }

    public void clearFooter() {
        adapter.setMore(new View(mContext), this);
        adapter.setError(new View(mContext));
        adapter.setNoMore(new View(mContext));
    }

    @Override
    public void showMoreContent(List<ShopItem> list) {
        adapter.clear();
        if (list != null && list.size() < ShopPresenter.NUM_OF_PAGE) {
            clearFooter();
        }
        adapter.addAll(list);
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh("");
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMore("");
    }
}
