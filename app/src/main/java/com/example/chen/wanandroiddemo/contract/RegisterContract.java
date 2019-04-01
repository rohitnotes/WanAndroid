package com.example.chen.wanandroiddemo.contract;

import com.example.chen.wanandroiddemo.base.presenter.IPresenter;
import com.example.chen.wanandroiddemo.base.view.BaseView;

/**
 * Coder : chenshuaiyu
 * Time : 2019/3/25 22:23
 */
public interface RegisterContract {
    interface Presenter extends IPresenter<View> {
        void getRegisterData(String useranme, String password, String repassword);
    }

    interface View extends BaseView {
        void showErrorMesssage(String error);
        void showSuccessfulMesssage();
    }
}
