package com.universe.yz.admin.ui.fragments;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.rollviewpager.Util;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.presenter.NewsPresenter;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.BaseMvpFragment;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.presenter.contract.NewsContract;
import com.universe.yz.admin.ui.activitys.EditorActivity;
import com.universe.yz.admin.ui.adapter.NewsAdapter;
import com.universe.yz.admin.utils.EventUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import butterknife.BindView;

public class NewsFragment extends BaseMvpFragment<NewsPresenter> implements NewsContract.View,
        SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {
    private final String TAG = NewsFragment.class.getSimpleName();
    private String mLastQuery = "";
    private NewsAdapter mNewsSearchAdapter;
    private boolean mIsDarkSearchTheme = false;
    public static boolean viewType = true;
/*
    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;*/
    @BindView(R.id.search_results_list)
    EasyRecyclerView recyclerView;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    private String query = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView(LayoutInflater inflater) {
       // toolbar.setTitle("");
       // titleName.setText(getResources().getString(R.string.news_title));

        recyclerView.setAdapterWithProgress(mNewsSearchAdapter = new NewsAdapter(getContext()));
        recyclerView.setErrorView(R.layout.view_error);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY,
                Util.dip2px(mContext, 0.2f), Util.dip2px(mContext, 72), 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);

        setupFloatingSearch();
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        recyclerView.setRefreshListener(this);
        mNewsSearchAdapter.setMore(R.layout.view_more, this);
        mNewsSearchAdapter.setNoMore(R.layout.view_nomore);
        mNewsSearchAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                final int  pos = position;
                final NewsItem newsItem = mNewsSearchAdapter.getItem(pos);
                CharSequence charSequence = Html.fromHtml(newsItem.getInfo());
                final CharSequence charSequence1 = newsItem.getTitle();

                new MaterialDialog.Builder(mContext)
                        .title(newsItem.getTitle())
                        .content(charSequence)
                        .backgroundColor(mContext.getResources().getColor(R.color.white))
                        .contentColor(mContext.getResources().getColor(R.color.bel_red_text))
                        .titleColor(mContext.getResources().getColor(R.color.blue_green))
                        .limitIconToDefaultSize()
                        .positiveText("编辑")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                EditorActivity.start(mContext,newsItem.getId(),charSequence1,newsItem.getInfo());
                            }
                        })
                        .show();

            }
        });
        mNewsSearchAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {

                final NewsItem newsItem = mNewsSearchAdapter.getItem(position);

                new MaterialDialog.Builder(mContext)
                        .title(newsItem.getTitle())
                        .content("确认是否删除?")
                        .backgroundColor(mContext.getResources().getColor(R.color.white))
                        .contentColor(mContext.getResources().getColor(R.color.bel_red_text))
                        .titleColor(mContext.getResources().getColor(R.color.blue_green))
                        .limitIconToDefaultSize()
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                mPresenter.delNews(Integer.valueOf(newsItem.getId()));
                            }
                        })
                        .show();

                return false;
            }
        });
        recyclerView.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.showProgress();
                onRefresh();
            }
        });

        mPresenter.onRefresh(query);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search;
    }

    private void setupFloatingSearch() {
        mSearchView.setQueryTextSize(14);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                    query = "";
                } else {
                    mSearchView.showProgress();
                    query = newQuery;
                    mPresenter.onRefresh(newQuery);
                    mSearchView.hideProgress();
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                mPresenter.onRefresh(query);
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {
                mSearchView.setSearchBarTitle(mLastQuery);
                Log.d(TAG, "onFocusCleared()");
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {}
        });

        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {}
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                NewsItem newsItem = (NewsItem) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

                leftIcon.setAlpha(0.0f);
                leftIcon.setImageDrawable(null);


                textView.setTextColor(Color.parseColor(textColor));
                String text = newsItem.getTitle()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });

        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                recyclerView.setTranslationY(newHeight);
            }
        });

        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                Log.d(TAG, "onClearSearchClicked()");
            }
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh(query);
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }

    @Override
    public void showContent(List<NewsItem> newsItems) {
        if (newsItems != null) {
            mNewsSearchAdapter.clear();
            mNewsSearchAdapter.addAll(newsItems);
        }
    }

    public void clearFooter() {
        mNewsSearchAdapter.setMore(new View(mContext), this);
        mNewsSearchAdapter.setError(new View(mContext));
        mNewsSearchAdapter.setNoMore(new View(mContext));
    }

    @Override
    public void showMoreContent(List<NewsItem> list1) {
        //mNewsSearchAdapter.clear();
        if (list1 != null && list1.size() < NewsPresenter.NUM_OF_PAGE) {
            clearFooter();
        }
        mNewsSearchAdapter.addAll(list1);
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
        mNewsSearchAdapter.pauseMore();
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMore(query);
    }

    @Subscriber(tag= Constants.NEWS_UPDATE_FLAG)
    public void newslistUpdate(String flag) {
        mPresenter.onRefresh("");
        if("DEL_NEWS".equals(flag)) {
            EventUtil.showToast(mContext, "成功删除!");
        }
    }
}