package com.universe.yz.admin.model.http.api;

import com.universe.yz.admin.model.bean.Member;
import com.universe.yz.admin.model.bean.NewsItem;
import com.universe.yz.admin.model.bean.ShopItem;
import com.universe.yz.admin.model.bean.UserPoint;
import com.universe.yz.admin.model.bean.UserRet;
import com.universe.yz.admin.model.bean.RoleData;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;

import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface CronousApis {
    String HOST = "http://p.fgame.com/api/";

    @GET("cro/find/{username}")
    Observable<CommonHttpResponse<Member>> findById(@Path("username") String username);

    @FormUrlEncoded
    @POST("cro/reg")
    Observable<CommonHttpResponse<List<Member>>> reg(@Field("username") String username, @Field("password") String password, @Field("email") String email);

    @FormUrlEncoded
    @POST("cro/login")
    Observable<CommonHttpResponse<UserRet>> login(@Field("username") String username, @Field("password") String password);

    @DELETE("cro/logout")
    Observable<CommonHttpResponse<String>> logout();

    @GET("cro/findPoint/{username}")
    Observable<CommonHttpResponse<List<UserPoint>>> findPoint(@Path("username") String username);

    @FormUrlEncoded
    @POST("cro/buyGoods")
    Observable<CommonHttpResponse<UserRet>> buyGoods(@Field("username") String username, @Field("ckey") int ckey,
                                                     @Field("needpoint") int needpoint, @Field("bitemid")String bitemid);

    @GET("cro/role/{username}")
    Observable<CommonHttpResponse<List<RoleData>>> getRoleData(@Path("username") String username);

    @GET("cro/role2/{username}")
    Observable<CommonHttpResponse<List<RoleData>>> getRoleData2(@Path("username") String username);

    @GET("shop/all")
    Observable<CommonHttpResponse<List<ShopItem>>> getAllGoods();

    @GET("shop/find/{cat}/{page}/{size}")
    Observable<CommonHttpResponse<List<ShopItem>>> findBycat(@Path("cat") String cat, @Path("page") int page, @Path("size") int size);

    @GET("shop/getTui/{type}/{time}")
    Observable<CommonHttpResponse<List<ShopItem>>> getTui(@Path("type") String cat, @Path("time") String time);

    @GET("shop/find/{username}")
    Observable<CommonHttpResponse<Member>> findByUsername(@Path("username") String username);

    @GET("shop/fuzzy/{itemname}/{page}/{size}")
    Observable<CommonHttpResponse<List<ShopItem>>> fuzzyByName(@Path("itemname") String itemname, @Path("page") int page, @Path("size") int size);

    @GET("shop/fuzzyNews/{hint}/{page}/{size}")
    Observable<CommonHttpResponse<List<NewsItem>>> fuzzyNewsHint(@Path("hint") String hint, @Path("page") int page, @Path("size") int size);

    @GET("shop/findNewsPerPage/{type}/{page}/{size}")
    Observable<CommonHttpResponse<List<NewsItem>>> findNewsByType(@Path("type") String type,@Path("page") int page, @Path("size") int size);

    @FormUrlEncoded
    @POST("shop/updateNews")
    Observable<CommonHttpResponse<UserRet>> updateNews(@Field("content") String content, @Field("title") String title, @Field("id") int id);

    @FormUrlEncoded
    @POST("shop/addNews")
    Observable<CommonHttpResponse<List<NewsItem>>> addNews(@Field("name") String name, @Field("title") String title, @Field("info") String info,
                                                           @Field("type") String type, @Field("top") int top, @Field("yn") int yn);
    @FormUrlEncoded
    @POST("shop/delNews")
    Observable<CommonHttpResponse<String>> delNews(@Field("id") Integer id);
}