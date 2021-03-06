package com.example.chen.wanandroiddemo.main.articledetail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.chen.wanandroiddemo.R;
import com.example.chen.wanandroiddemo.base.fragment.BaseFragment;
import com.example.chen.wanandroiddemo.core.DataManager;
import com.example.chen.wanandroiddemo.main.articledetail.contract.ArticleDetailContract;
import com.example.chen.wanandroiddemo.main.articledetail.presenter.ArticleDetailPresenter;
import com.example.chen.wanandroiddemo.utils.NetUtil;
import com.example.statelayout_lib.StateLayoutManager;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author chenshuaiyu
 */
public class ArticleDetailFragment extends BaseFragment<ArticleDetailPresenter> implements ArticleDetailContract.View {

    public static final String BUNDLE_ARTICLE_DETAIL_URL = "article_detail_url";

    @BindView(R.id.fl_container)
    FrameLayout mFrameLayout;

    @Override
    protected ArticleDetailPresenter getPresenter() {
        return new ArticleDetailPresenter(DataManager.getInstance());
    }

    @Override
    protected StateLayoutManager getStateLayoutManager() {
        return new StateLayoutManager.Builder()
                .setContentLayoutResId(R.layout.fragment_article_detail)
                .setOnReLoadListener(this::showContentView)
                .build();
    }

    public static ArticleDetailFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ArticleDetailFragment.BUNDLE_ARTICLE_DETAIL_URL, url);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        assert getArguments() != null;
        String url = getArguments().getString(BUNDLE_ARTICLE_DETAIL_URL);

        AgentWeb agentWeb = AgentWeb.with(this)
                .setAgentWebParent(mFrameLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(getResources().getColor(R.color.colorPrimary))
                .setMainFrameErrorView(R.layout.default_error, -1)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .createAgentWeb()
                .ready()
                .go(url);

        WebView mWebView = agentWeb.getWebCreator().getWebView();
        WebSettings mSettings = mWebView.getSettings();

        mSettings.setBlockNetworkImage(mPresenter.getNoImageMode());
        if (mPresenter.getAutoCache()) {
            mSettings.setAppCacheEnabled(true);
            mSettings.setDatabaseEnabled(true);
            if (NetUtil.isNetworkConnected()) {
                //默认从网络获取
                mSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                //若有缓存，使用缓存，否则从网络获取
                mSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
//            setWithRxJava(mSettings);
        } else {
            mSettings.setAppCacheEnabled(false);
            mSettings.setDatabaseEnabled(false);
            //不使用缓存，只从网络获取
            mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        //支持缩放
        mSettings.setJavaScriptEnabled(true);
        mSettings.setSupportZoom(true);
        mSettings.setBuiltInZoomControls(true);
        //不显示缩放按钮
        mSettings.setDisplayZoomControls(false);
        //设置自适应屏幕，两者合用
        //将图片调整到适合WebView的大小
        mSettings.setUseWideViewPort(true);
        //缩放至屏幕大小
        mSettings.setLoadWithOverviewMode(true);
        //自适应屏幕
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    private void setWithRxJava(WebSettings mSettings) {
        //使用RxJava有点繁琐
        Observable<Integer> net = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                if (NetUtil.isNetworkConnected()) {
                    //默认从网络获取
                    emitter.onNext(WebSettings.LOAD_DEFAULT);
                }
            }
        });

        Observable<Integer> cache = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                //若有缓存，使用缓存，否则从网络获取
                emitter.onNext(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        });

        mPresenter.addSubcriber(
                Observable.concat(net, cache)
                        .firstElement()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mSettings::setCacheMode)
        );
    }
}
