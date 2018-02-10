package com.universe.yz.admin.model;


import com.universe.yz.admin.model.bean.Record;
import com.universe.yz.admin.model.bean.Caesar;
import com.universe.yz.admin.model.bean.GankHttpResponse;
import com.universe.yz.admin.model.bean.SearchKey;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.bean.VideoRes;
import com.universe.yz.admin.model.http.HttpHelper;
import com.universe.yz.admin.model.db.DBHelper;
import com.universe.yz.admin.model.http.response.CommonHttpResponse;
import com.universe.yz.admin.model.bean.Collection;
import com.universe.yz.admin.model.bean.GankItemBean;

import java.util.List;

import io.realm.Realm;
import rx.Observable;

/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @desciption:
 */

public class DataManager implements HttpHelper, DBHelper {

    private HttpHelper mHttpHelper;
    private DBHelper mDbHelper;

    public DataManager(HttpHelper httpHelper, DBHelper dbHelper) {
        mHttpHelper = httpHelper;
        mDbHelper = dbHelper;
    }


    @Override
    public Realm getRealm() {
        return mDbHelper.getRealm();
    }

    @Override
    public void insertCollection(Collection bean) {
        mDbHelper.insertCollection(bean);
    }

    @Override
    public void deleteCollection(String id) {
        mDbHelper.deleteCollection(id);
    }

    @Override
    public void deleteAllCollection() {
        mDbHelper.deleteAllCollection();
    }

    @Override
    public boolean queryCollectionId(String id) {
        return mDbHelper.queryCollectionId(id);
    }

    @Override
    public List<Collection> getCollectionList() {
        return mDbHelper.getCollectionList();
    }

    @Override
    public void insertRecord(Record bean, int maxSize) {
        mDbHelper.insertRecord(bean, maxSize);
    }

    @Override
    public void deleteRecord(String id) {
        mDbHelper.deleteRecord(id);
    }

    @Override
    public boolean queryRecordId(String id) {
        return mDbHelper.queryRecordId(id);
    }

    @Override
    public List<Record> getRecordList() {
        return mDbHelper.getRecordList();
    }

    @Override
    public void deleteAllRecord() {
        mDbHelper.deleteAllRecord();
    }

    @Override
    public void insertSearchHistory(SearchKey bean) {
        mDbHelper.insertSearchHistory(bean);
    }

    @Override
    public List<SearchKey> getSearchHistoryList(String value) {
        return mDbHelper.getSearchHistoryList(value);
    }

    @Override
    public void deleteSearchHistoryList(String value) {
        mDbHelper.deleteSearchHistoryList(value);
    }

    @Override
    public void deleteSearchHistoryAll() {
        mDbHelper.deleteSearchHistoryAll();
    }

    @Override
    public List<SearchKey> getSearchHistoryListAll() {
        return mDbHelper.getSearchHistoryListAll();
    }

    @Override
    public void insertUser(User bean) {
        mDbHelper.insertUser(bean);
    }

    @Override
    public User queryUser() {
        return mDbHelper.queryUser();
    }

    @Override
    public void deleteUserById(int id) {
        mDbHelper.deleteUserById(id);
    }

    @Override
    public void insertCaesar(Caesar bean) {
        mDbHelper.insertCaesar(bean);
    }

    @Override
    public boolean queryCaesarId(String id) {
        return mDbHelper.queryCaesarId(id);
    }

    @Override
    public void deleteCaesar(String id) {
        mDbHelper.deleteCaesar(id);
    }

    @Override
    public void deleteAllCaesar() {
        mDbHelper.deleteAllCaesar();
    }

    @Override
    public List<Caesar> getCaesarList() {
        return mDbHelper.getCaesarList();
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchHomePage() {
        return mHttpHelper.fetchHomePage();
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchVideoInfo(String mediaId) {
        return mHttpHelper.fetchVideoInfo(mediaId);
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchVideoList(String catalogId, String pnum) {
        return mHttpHelper.fetchVideoList(catalogId, pnum);
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchVideoListByKeyWord(String keyword, String pnum) {
        return mHttpHelper.fetchVideoListByKeyWord(keyword, pnum);
    }

    @Override
    public Observable<CommonHttpResponse<VideoRes>> fetchCommentList(String mediaId, String pnum) {
        return mHttpHelper.fetchCommentList(mediaId, pnum);
    }

    @Override
    public Observable<GankHttpResponse<List<GankItemBean>>> fetchGirlList(int num, int page) {
        return mHttpHelper.fetchGirlList(num, page);
    }
}
