package com.universe.yz.admin.model.bean;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Description: 收藏
 * Creator: yxc
 * date: 2016/9/23 11:29
 */
public class User extends RealmObject implements Serializable {
    int id;
    String name;
    String token;
    String type;
    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
