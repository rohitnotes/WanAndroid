package com.example.chen.wanandroiddemo.di.component;

import com.example.chen.wanandroiddemo.di.module.HomeModule;
import com.example.chen.wanandroiddemo.ui.homepage.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author : chenshuaiyu
 * @date : 2019/3/19 14:30
 */
@Singleton
@Component(modules = HomeModule.class)
public interface HomeComponent {
    void inject(HomeFragment homeFragment);

}
