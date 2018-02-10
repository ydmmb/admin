package com.universe.yz.admin.presenter;

import android.util.Log;

import com.universe.yz.admin.app.App;
import com.universe.yz.admin.app.Constants;
import com.universe.yz.admin.base.RxPresenter;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.bean.UserPoint;
import com.universe.yz.admin.model.db.RealmHelper;
import com.universe.yz.admin.model.net.RetrofitHelper;
import com.universe.yz.admin.presenter.contract.WelcomeContract;
import com.universe.yz.admin.utils.RxUtil;
import com.universe.yz.admin.utils.StringUtils;
import com.universe.yz.admin.model.bean.RoleData;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WelcomePresenter extends RxPresenter<WelcomeContract.View> implements WelcomeContract.Presenter {

    private static final int COUNT_DOWN_TIME = 2200;

    @Inject
    public WelcomePresenter() {

    }

    @Override
    public void getWelcomeData() {
        mView.showContent(getImgData());
        User user = RealmHelper.getInstance().queryUser();

        if (user != null) {
            getIsVIP(user);
            getRoleData(user.getName());
        }
        startCountDown();
    }

    private void startCountDown() {
        Subscription rxSubscription = Observable.timer(COUNT_DOWN_TIME, TimeUnit.MILLISECONDS)
                .compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d("WelcomePresenter","to main");
                        mView.jumpToMain();
                    }
                });
        addSubscribe(rxSubscription);
    }

    private List<String> getImgData() {
        List<String> imgs = new ArrayList<>();
        imgs.add("file:///android_asset/a.jpg");
        imgs.add("file:///android_asset/b.jpg");
        imgs.add("file:///android_asset/c.jpg");
        imgs.add("file:///android_asset/d.jpg");
        imgs.add("file:///android_asset/e.jpg");
        imgs.add("file:///android_asset/f.jpg");
        imgs.add("file:///android_asset/g.jpg");

        return imgs;
    }

    public void getIsVIP(final User user) {
        Subscription rxSubscription = RetrofitHelper.getMemberApi().findPoint(user.getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<CommonHttpResponse<List<UserPoint>>, List<UserPoint>>() {
                    @Override
                    public List<UserPoint> call(CommonHttpResponse<List<UserPoint>> listCommonHttpResponse) {
                        if (listCommonHttpResponse.getCode()==100 ) {
                            return listCommonHttpResponse.getRet();
                        }
                        return null;
                    }
                })
                .subscribe(new Action1<List<UserPoint>>() {
                    @Override
                    public void call(List<UserPoint> res) {
                        if (res != null) {
                            User user1 = new User();
                            user1.setType(Constants.common);

                            if(res.get(0).point >=1500 && res.get(0).point<3000)
                                user1.setType(Constants.copper);
                            if(res.get(0).point >=3000 && res.get(0).point<6000)
                                user1.setType(Constants.silver);
                            if(res.get(0).point >=6000 && res.get(0).point<15000)
                                user1.setType(Constants.gold);
                            if(res.get(0).point >=15000) user1.setType(Constants.extreme);

                            if(! user.getType().equals(user1.getType())) {
                                user1.setId(user.getId());
                                user1.setName(user.getName());
                                user1.setPassword(user.getPassword());
                                user1.setToken(user.getToken());
                                RealmHelper.getInstance().deleteUserById(user.getId());
                                RealmHelper.getInstance().insertUser(user1);
                                Log.e("WelcomePresenter","Type changed！");
                            } else {
                                Log.e("WelcomePresenter","No Type changed！");
                            }
                        }else {
                            Log.e("WelcomePresenter","Your current points res==null");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("welcome", StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }

    private void getRoleData(String username) {
        Subscription rxSubscription = RetrofitHelper.getMemberApi().getRoleData(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<CommonHttpResponse<List<RoleData>>, List<RoleData>>() {
                    @Override
                    public List<RoleData> call(CommonHttpResponse<List<RoleData>> listCommonHttpResponse) {
                        if (listCommonHttpResponse.getCode()==100 ) {
                            return listCommonHttpResponse.getRet();
                        }
                        return null;
                    }
                })
                .subscribe(new Action1<List<RoleData>>() {
                    @Override
                    public void call(List<RoleData> res) {
                        if (res != null) {
                            App.getInstance().setRoleDataList(res);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        App.getInstance().setRoleDataList(null);
                        Log.e("welcome",StringUtils.getErrorMsg(throwable.getMessage()));
                    }
                });
        addSubscribe(rxSubscription);
    }
}
