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

import com.afollestad.materialdialogs.MaterialDialog;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.universe.yz.admin.app.App;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.base.BaseMvpFragment;
import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.presenter.ShopPresenter;
import com.universe.yz.admin.presenter.contract.ShopContract;
import com.universe.yz.admin.ui.adapter.SearchResultsListAdapter;
import com.universe.yz.admin.R;
import com.universe.yz.admin.model.bean.RoleData;
import com.universe.yz.admin.utils.EventUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class SearchFragment extends BaseMvpFragment<ShopPresenter> implements ShopContract.View,
        SwipeRefreshLayout.OnRefreshListener,RecyclerArrayAdapter.OnLoadMoreListener {
    private final String TAG = SearchFragment.class.getSimpleName();
    private String mLastQuery = "";
    private SearchResultsListAdapter mSearchResultsAdapter;
    private boolean mIsDarkSearchTheme = false;
    public static boolean viewType = true;

    @BindView(R.id.search_results_list)
    EasyRecyclerView recyclerView;
    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;
   /* @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    */
   private List<String> stringList = null;
    private Map<String,Integer> name2key;
    private String query = null;

    @Override
    protected void initView(LayoutInflater inflater) {
       // toolbar.setTitle("");
       // titleName.setText(getResources().getString(R.string.daoju_title));
        recyclerView.setAdapterWithProgress(mSearchResultsAdapter = new SearchResultsListAdapter(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setErrorView(R.layout.view_error);
        getRoleList();
        setupFloatingSearch();
    }

    private void getRoleList() {
        List<RoleData> roleDataList = App.getInstance().getRoleDataList();
        stringList = new ArrayList<>();
        name2key = new HashMap<>();

        if (roleDataList!=null) {
            for(RoleData roleData: roleDataList) {
                stringList.add(roleData.characterPack.getCharactername().trim());
                name2key.put(roleData.characterPack.getCharactername().trim(),roleData.characterPack.getCkey());
            }
        }
    }

    @Override
    protected void initEvent() {
        recyclerView.setRefreshListener(this);
        mSearchResultsAdapter.setMore(R.layout.view_more, this);
        mSearchResultsAdapter.setNoMore(R.layout.view_nomore);
        mSearchResultsAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if ("".equals(Constants.theServer)) {
                    new MaterialDialog.Builder(mContext)
                            .title("请先选择服务区")
                            .backgroundColor(mContext.getResources().getColor(R.color.colorHint))
                            .contentColor(mContext.getResources().getColor(R.color.white))
                            .titleColor(mContext.getResources().getColor(R.color.white))
                            .items("神恩之地", "国研国战")
                            .limitIconToDefaultSize()
                            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    Log.e(TAG, "You are clicking " + which + "server:" + text);
                                    Constants.theServer = text.toString();
                                    return true;
                                }
                            })
                            .positiveText("选择")
                            .negativeText("取消")
                            .show();
                } else {
                    final ShopItem shopItem = mSearchResultsAdapter.getItem(position);
                    new MaterialDialog.Builder(mContext)
                            .title(Constants.theServer)
                            .backgroundColor(mContext.getResources().getColor(R.color.colorHint))
                            .contentColor(mContext.getResources().getColor(R.color.white))
                            .titleColor(mContext.getResources().getColor(R.color.white))
                            .items(stringList)
                            .limitIconToDefaultSize()
                            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    mPresenter.buyDaoju(RealmHelper.getInstance().queryUser().getName(), name2key.get(text.toString()), shopItem.needCroPoint,shopItem.bitemid);
                                    Log.e(TAG, "Post->" + Constants.theServer + ";" + text.toString() + ";" + shopItem.bitemid + ";" + shopItem.needCroPoint);
                                    return true;
                                }
                            })
                            .positiveText("购买")
                            .negativeText("取消")
                            .show();
                }
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

                ShopPresenter.currentPage = 0;
                mPresenter.onRefresh(query);

                mSearchView.hideProgress();
            }
        }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {}

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
            public void onActionMenuItemSelected(MenuItem item) {
            }
        });

        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                ShopItem shopItem = (ShopItem) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";
                leftIcon.setAlpha(0.0f);
                leftIcon.setImageDrawable(null);

                textView.setTextColor(Color.parseColor(textColor));
                String text = shopItem.getBody()
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
    public void showContent(List<ShopItem> shopItems) {
        mSearchResultsAdapter.clear();

        if (shopItems.size() < ShopPresenter.NUM_OF_PAGE) {
            clearFooter();
        }

        mSearchResultsAdapter.addAll(shopItems);
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
        mSearchResultsAdapter.pauseMore();
    }

    public void clearFooter() {
        mSearchResultsAdapter.setMore(new View(mContext), this);
        mSearchResultsAdapter.setError(new View(mContext));
        mSearchResultsAdapter.setNoMore(new View(mContext));
    }

    @Override
    public void showMoreContent(List<ShopItem> list) {
        mSearchResultsAdapter.addAll(list);
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMore(query);
    }
}