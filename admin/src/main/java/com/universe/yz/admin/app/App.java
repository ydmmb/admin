package com.universe.yz.admin.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.universe.yz.admin.utils.NetStatus;
import com.universe.yz.admin.di.component.AppComponent;
import com.universe.yz.admin.di.component.DaggerAppComponent;
import com.universe.yz.admin.di.module.AppModule;
import com.universe.yz.admin.di.module.HttpModule;
import com.universe.yz.admin.model.bean.RoleData;
import com.universe.yz.admin.model.db.RealmHelper;

import java.net.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.ittiger.player.Config;
import cn.ittiger.player.PlayerManager;
import cn.ittiger.player.factory.ExoPlayerFactory;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    private static App instance;
    private Set<Activity> allActivities;
    private static List<RoleData> roleDataList = null;

    public static App getInstance() {
        return instance;
    }

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //蒲公英crash上报
//        PgyCrashManager.register(this);
        //初始化内存泄漏检测
//        LeakCanary.install(this);
        //初始化过度绘制检测
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
//        初始化realm
        initRealm();
        Realm.init(getApplicationContext());

        PlayerManager.loadConfig(
                new Config.Builder(this)
                        .buildPlayerFactory(new ExoPlayerFactory(this))
                        .enableSmallWindowPlay()
                        .cache(false)
                        .build()
        );

        // just for open the log in this demo project.
        //  FileDownloadLog.NEED_LOG = BuildConfig.DOWNLOAD_NEED_LOG;

        /**
         * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
         * by below code, so please do not worry about performance.
         * @see FileDownloader#init(Context)
         */
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                        .proxy(Proxy.NO_PROXY) // set proxy
                ))
                .commit();

    }

    public void registerActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<Activity>();
        }
        allActivities.add(act);
    }

    public void unregisterActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    if (act != null && !act.isFinishing())
                        act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(RealmHelper.DB_NAME)
                .schemaVersion(1)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(instance))
                    .httpModule(new HttpModule())
                    .build();
        }
        return appComponent;
    }

    public void setRoleDataList(List<RoleData> roleDataList) {
        this.roleDataList = roleDataList;
    }

    public List<RoleData> getRoleDataList() {
        return roleDataList;
    }

    // Judging whether WIFI is available.
    public static boolean isWifi(Context mContext) {
        NetworkInfo activeNetInfo = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        }

        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static NetStatus checkNetworkConnection(Context context)
    {
        NetworkInfo mobile;
        NetworkInfo wifi;
        int i = 0;
        int ni = 0;

        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        } else return NetStatus.NET_STATUS_UNKNOWN;

        if(wifi == null) {
            ni++;
        }else{
            if (wifi.isAvailable()) {
                Log.d("wifi","wifi is ok");
                i++;
            }
        }
        if(mobile == null) {
            ni++;
        }else {
            if(mobile.isAvailable()) {
                Log.d("Mobile","mobile is ok");
                i++;
            }
        }

        if (ni==2) return NetStatus.NO_NET_AVAILABLE;
        if (i == 2) return NetStatus.WIFI_MOBILE_AVAILABLE;
        if (i==1) {
            return NetStatus.EITHER_AVAILABLE;
        }else {
            return NetStatus.NET_STATUS_UNKNOWN;
        }
    }
}