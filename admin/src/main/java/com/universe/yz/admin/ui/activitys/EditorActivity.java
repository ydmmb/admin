package com.universe.yz.admin.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mr5.icarus.Callback;
import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.Toolbar;
import com.github.mr5.icarus.button.Button;
import com.github.mr5.icarus.button.FontScaleButton;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;
import com.github.mr5.icarus.popover.FontScalePopoverImpl;
import com.github.mr5.icarus.popover.HtmlPopoverImpl;
import com.github.mr5.icarus.popover.ImagePopoverImpl;
import com.github.mr5.icarus.popover.LinkPopoverImpl;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.R;
import com.universe.yz.admin.base.SwipeBackActivity;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.model.bean.UserRet;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.presenter.MmPresenter;
import com.universe.yz.admin.utils.EventUtil;
import com.universe.yz.admin.utils.RxUtil;
import com.universe.yz.admin.widget.theme.ColorTextView;

import org.simple.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class EditorActivity extends SwipeBackActivity<MmPresenter> {
  //  @BindView(R.id.editor)
    WebView webView;

    @BindView(R.id.title_name)
    ColorTextView titleName;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.preview)
    TextView titlePreview;
    @BindView(R.id.publish)
    TextView titlePublish;
    @BindView(R.id.ll_container)
    LinearLayout rlContainer;
    @BindView(R.id.news_title)
    EditText title;

    protected Icarus icarus;
    private String original;
    private int id;
    private CharSequence newsTitle;

    @Override
    protected int getLayout() {
        return R.layout.activity_editor;
    }

    @Override
    protected void initView() {
        titleName.setText("活动编辑器");
        title.setText(newsTitle);

        // Dynamic create WebView
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 400);
        lp.gravity = Gravity.CENTER;
        lp.weight = 99;
        webView = new WebView(getApplicationContext());
        webView.setLayoutParams(lp);
        rlContainer.addView(webView);

        TextViewToolbar toolbar = new TextViewToolbar();
        Options options = new Options();
        options.setPlaceholder("Input something...");
        //  img: ['src', 'alt', 'width', 'height', 'data-non-image']
        // a: ['href', 'target']
        options.addAllowedAttributes("img", Arrays.asList("data-type", "data-id", "class", "src", "alt", "width", "height", "data-non-image"));
        options.addAllowedAttributes("iframe", Arrays.asList("data-type", "data-id", "class", "src", "width", "height"));
        options.addAllowedAttributes("a", Arrays.asList("data-type", "data-id", "class", "href", "target", "title"));

        icarus = new Icarus(toolbar, options, webView);
        prepareToolbar(toolbar, icarus);
        icarus.loadCSS("file:///android_asset/editor.css");
        icarus.loadJs("file:///android_asset/test.js");

        icarus.setContent(original);

        icarus.render();
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titlePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (icarus == null) {
                    EventUtil.showToast(mContext, "内容不能为空!");
                    return;
                }

                icarus.getContent(new Callback() {
                    @Override
                    public void run(String params) {
                        String rq = params.substring(params.indexOf(":")+2);
                        String content = rq.substring(0,rq.lastIndexOf("}")-1);
                        Log.e("content",content);

                        RetrofitHelper.getMemberApi().updateNews(content,String.valueOf(title.getText()),id)
                                .compose(RxUtil.<CommonHttpResponse<UserRet>>rxSchedulerHelper())
                                .compose(RxUtil.<UserRet>handleUserResult())
                                .subscribe(new Action1<UserRet>() {
                                    @Override
                                    public void call( final UserRet res) {
                                        if (res != null) {
                                            EventUtil.showToast(mContext, "更新成功!");
                                            finish();
                                            EventBus.getDefault().post("", Constants.NEWS_UPDATE_FLAG);
                                        }
                                    }
                                },new Action1<Throwable>() {
                                    @Override
                                    public void call (Throwable throwable){
                                      //  EventUtil.showToast(mContext, StringUtils.getErrorMsg(throwable.getMessage()));
                                        EventUtil.showToast(mContext, "更新成功!");
                                        finish();
                                        EventBus.getDefault().post("", Constants.NEWS_UPDATE_FLAG);
                                    }
                                });
                    }
                });
            }
        });
        titlePublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (icarus == null) {
                    EventUtil.showToast(mContext, "内容不能为空!");
                    return;
                }

                icarus.getContent(new Callback() {
                    @Override
                    public void run(String params) {
                        String rq = params.substring(params.indexOf(":")+2);
                        String content = rq.substring(0,rq.lastIndexOf("}")-1);
                        Log.e("content",content);

                        RetrofitHelper.getMemberApi().addNews("乐游科洛斯团队", String.valueOf(title.getText()), content, "系统公告",1,1)
                                .compose(RxUtil.<CommonHttpResponse<List<NewsItem>>>rxSchedulerHelper())
                                .compose(RxUtil.<List<NewsItem>>handleUserResult())
                                .subscribe(new Action1<List<NewsItem>>() {
                                    @Override
                                    public void call(final List<NewsItem> res) {
                                        if (res != null) {
                                            EventUtil.showToast(mContext, "添加成功!");
                                            finish();
                                            EventBus.getDefault().post("", Constants.NEWS_UPDATE_FLAG);
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        EventUtil.showToast(mContext, "添加失败!");
                                        finish();
                                        EventBus.getDefault().post("", Constants.NEWS_UPDATE_FLAG);
                                    }
                                });
                    }
                });
            }
        });
    }

    @OnClick({R.id.rl_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                if (mContext instanceof EditorActivity) {
                    finish();
                }
                break;
        }
    }

    private Toolbar prepareToolbar(TextViewToolbar toolbar, Icarus icarus) {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "Simditor.ttf");
        HashMap<String, Integer> generalButtons = new HashMap<>();
        generalButtons.put(Button.NAME_BOLD, R.id.button_bold);
        generalButtons.put(Button.NAME_OL, R.id.button_list_ol);
        generalButtons.put(Button.NAME_BLOCKQUOTE, R.id.button_blockquote);
        generalButtons.put(Button.NAME_HR, R.id.button_hr);
        generalButtons.put(Button.NAME_UL, R.id.button_list_ul);
        generalButtons.put(Button.NAME_ALIGN_LEFT, R.id.button_align_left);
        generalButtons.put(Button.NAME_ALIGN_CENTER, R.id.button_align_center);
        generalButtons.put(Button.NAME_ALIGN_RIGHT, R.id.button_align_right);
        generalButtons.put(Button.NAME_ITALIC, R.id.button_italic);
        generalButtons.put(Button.NAME_INDENT, R.id.button_indent);
        generalButtons.put(Button.NAME_OUTDENT, R.id.button_outdent);
        generalButtons.put(Button.NAME_CODE, R.id.button_math);
        generalButtons.put(Button.NAME_UNDERLINE, R.id.button_underline);
        generalButtons.put(Button.NAME_STRIKETHROUGH, R.id.button_strike_through);

        for (String name : generalButtons.keySet()) {
            TextView textView = findViewById(generalButtons.get(name));
            if (textView == null) {
                continue;
            }
            textView.setTypeface(iconfont);
            TextViewButton button = new TextViewButton(textView, icarus);
            button.setName(name);
            toolbar.addButton(button);
        }
        TextView linkButtonTextView = findViewById(R.id.button_link);
        linkButtonTextView.setTypeface(iconfont);
        TextViewButton linkButton = new TextViewButton(linkButtonTextView, icarus);
        linkButton.setName(Button.NAME_LINK);
        linkButton.setPopover(new LinkPopoverImpl(linkButtonTextView, icarus));
        toolbar.addButton(linkButton);

        TextView imageButtonTextView = findViewById(R.id.button_image);
        imageButtonTextView.setTypeface(iconfont);
        TextViewButton imageButton = new TextViewButton(imageButtonTextView, icarus);
        imageButton.setName(Button.NAME_IMAGE);
        imageButton.setPopover(new ImagePopoverImpl(imageButtonTextView, icarus));
        toolbar.addButton(imageButton);

        TextView htmlButtonTextView = findViewById(R.id.button_html5);
        htmlButtonTextView.setTypeface(iconfont);
        TextViewButton htmlButton = new TextViewButton(htmlButtonTextView, icarus);
        htmlButton.setName(Button.NAME_HTML);
        htmlButton.setPopover(new HtmlPopoverImpl(htmlButtonTextView, icarus));
        toolbar.addButton(htmlButton);

        TextView fontScaleTextView = findViewById(R.id.button_font_scale);
        fontScaleTextView.setTypeface(iconfont);
        TextViewButton fontScaleButton = new FontScaleButton(fontScaleTextView, icarus);
        fontScaleButton.setPopover(new FontScalePopoverImpl(fontScaleTextView, icarus));
        toolbar.addButton(fontScaleButton);

        return toolbar;
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        webView.destroy();
        super.onDestroy();
    }



    @Override
    protected void getIntentData() {
        this.id = getIntent().getExtras().getInt("id");
        this.newsTitle =  getIntent().getExtras().getCharSequence("title");
        this.original = getIntent().getExtras().getString("original");
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    public static void start(Context context, int id, CharSequence title, String info) {
        Intent starter = new Intent(context, EditorActivity.class);

        starter.putExtra("id", id);
        starter.putExtra("title",title);
        starter.putExtra("original",info);

        context.startActivity(starter);
    }

    @Override
    public void showError(String msg) {
        EventUtil.showToast(mContext, msg);
    }
}