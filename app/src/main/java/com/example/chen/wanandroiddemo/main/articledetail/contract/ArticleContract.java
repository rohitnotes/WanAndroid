package com.example.chen.wanandroiddemo.main.articledetail.contract;

import com.example.chen.wanandroiddemo.base.presenter.IPresenter;
import com.example.chen.wanandroiddemo.base.view.BaseView;

public interface ArticleContract {
    interface Presenter extends IPresenter<View> {
        void collectActicle(int id);
    }

    interface View extends BaseView {

    }
}
