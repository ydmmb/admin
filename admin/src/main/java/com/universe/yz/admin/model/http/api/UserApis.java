package com.universe.yz.admin.model.http.api;

import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.bean.UserRet;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by codeest on 16/8/19.
 */

public interface UserApis {
    String HOST = "http://p.fgame.com/api/tokens/";

    @GET("{username}/{password}")
    Observable<CommonHttpResponse<UserRet>> verifyUser(@Path("username") String username, @Path("password") String password);

    @GET("regUser/{username}/{password}/{email}")
    Observable<CommonHttpResponse<List<User>>> regUser(@Path("username") String username, @Path("password") String password, @Path("email") String email);

    @GET("logout/{id}")
    Observable<CommonHttpResponse<String>> logout(@Path("id") String id);
}
