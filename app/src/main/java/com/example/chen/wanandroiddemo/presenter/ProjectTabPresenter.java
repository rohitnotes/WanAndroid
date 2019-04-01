package com.example.chen.wanandroiddemo.presenter;

import com.example.chen.wanandroiddemo.base.presenter.BasePresenter;
import com.example.chen.wanandroiddemo.contract.ProjectTabContract;
import com.example.chen.wanandroiddemo.core.DataManager;
import com.example.chen.wanandroiddemo.core.bean.Articles;
import com.example.chen.wanandroiddemo.core.bean.BaseResponse;
import com.example.chen.wanandroiddemo.utils.RxUtils;

import javax.inject.Inject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Coder : chenshuaiyu
 * Time : 2019/3/21 8:48
 */
public class ProjectTabPresenter extends BasePresenter<ProjectTabContract.View> implements ProjectTabContract.Presenter {
    @Inject
    public ProjectTabPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void getProjectTabArticles(int page, int cid) {
        mDataManager.getProjectTabArticles(page, cid)
                .compose(RxUtils.switchSchedulers())
                .subscribe(new Observer<BaseResponse<Articles>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(BaseResponse<Articles> articlesBaseResponse) {
                        mView.showProjectTabArticles(articlesBaseResponse.getData().getDatas());
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onComplete() {
                    }
                });
    }
}
