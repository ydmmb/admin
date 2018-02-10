package com.universe.yz.admin.di.component;


import com.universe.yz.admin.app.App;
import com.universe.yz.admin.di.module.AppModule;
import com.universe.yz.admin.model.DataManager;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.model.http.RetrofitHelper1;
import com.universe.yz.admin.di.module.HttpModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by codeest on 16/8/7.
 */

@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {

    App getContext();  // 提供App的Context

    DataManager getDataManager(); //数据中心

    RetrofitHelper1 retrofitHelper();  //提供http的帮助类

    RealmHelper realmHelper();    //提供数据库帮助类

}
