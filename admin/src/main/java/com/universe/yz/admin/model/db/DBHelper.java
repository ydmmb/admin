package com.universe.yz.admin.model.db;

import com.universe.yz.admin.model.bean.Record;
import com.universe.yz.admin.model.bean.User;
import com.universe.yz.admin.model.bean.Caesar;
import com.universe.yz.admin.model.bean.SearchKey;
import com.universe.yz.admin.model.bean.Collection;

import java.util.List;

import io.realm.Realm;


/**
 * @author: Est <codeest.dev@gmail.com>
 * @date: 2017/4/21
 * @description:
 */

public interface DBHelper {
    Realm getRealm();

    void insertCollection(Collection bean);

    void deleteCollection(String id);

    void deleteAllCollection();

    boolean queryCollectionId(String id);

    List<Collection> getCollectionList();

    void insertRecord(Record bean, int maxSize);

    void deleteRecord(String id);

    boolean queryRecordId(String id);

    List<Record> getRecordList();

    void deleteAllRecord();

    void insertSearchHistory(SearchKey bean);

    List<SearchKey> getSearchHistoryList(String value);

    void deleteSearchHistoryList(String value);

    void deleteSearchHistoryAll();

    List<SearchKey> getSearchHistoryListAll();

    void insertUser(User bean);

    User queryUser();

    void deleteUserById(int id);

    void insertCaesar(Caesar bean);

    boolean queryCaesarId(String id);

    void deleteCaesar(String id);

    void deleteAllCaesar();

    List<Caesar> getCaesarList();
}
