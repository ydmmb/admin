package com.universe.yz.admin.model.http;

import com.universe.yz.admin.model.bean.GankHttpResponse;
import com.universe.yz.admin.model.http.api.VideoApis;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.bean.GankItemBean;
import com.universe.yz.admin.model.http.api.GankApis;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;


/**
 * Created by codeest on 2016/8/3.
 */
public class RetrofitHelper1 implements HttpHelper {

    private VideoApis mVideoApis;
    private GankApis mGankApis;
    
    @Inject
    public RetrofitHelper1(VideoApis videoApis, GankApis gankApis) {
        this.mVideoApis = videoApis;
        this.mGankApis = gankApis;
    }


    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchHomePage() {
        return mVideoApis.getHomePage();
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchVideoInfo(String mediaId) {
        return mVideoApis.getVideoInfo(mediaId);
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchVideoList(String catalogId, String pnum) {
        return mVideoApis.getVideoList(catalogId, pnum);
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchVideoListByKeyWord(String keyword, String pnum) {
        return mVideoApis.getVideoListByKeyWord(keyword, pnum);
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchCommentList(String mediaId, String pnum) {
        return mVideoApis.getCommentList(mediaId, pnum);
    }

    @Override
    public Observable<GankHttpResponse<List<GankItemBean>>> fetchGirlList(int num, int page) {
        return mGankApis.getGirlList(num, page);
    }
}
