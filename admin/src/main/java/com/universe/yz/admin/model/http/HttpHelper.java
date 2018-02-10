package com.universe.yz.admin.model.http;

import com.universe.yz.admin.model.bean.GankHttpResponse;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.bean.GankItemBean;

import java.util.List;

import rx.Observable;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @description:
 */

public interface HttpHelper {

    Observable<CommonHttpResponse<VideoRes>> fetchHomePage();

    Observable<CommonHttpResponse<VideoRes>> fetchVideoInfo(String mediaId);

    Observable<CommonHttpResponse<VideoRes>> fetchVideoList(String catalogId, String pnum);

    Observable<CommonHttpResponse<VideoRes>> fetchVideoListByKeyWord(String keyword, String pnum);

    Observable<CommonHttpResponse<VideoRes>> fetchCommentList(String mediaId, String pnum);

    Observable<GankHttpResponse<List<GankItemBean>>> fetchGirlList(int num, int page);
}
