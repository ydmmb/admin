package com.universe.yz.admin.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoleData {
    public CharacterPack characterPack;
    public CharacterBottom characterBottom;
    public DataBean dataBean;
    public
    @SerializedName("list")
    List<InventoryBean> inventoryBeans;
/*
    public RoleData(CharacterPack characterPack) {
        this.characterPack = characterPack;
    }

    public CharacterPack getCharacterPack() {
        return characterPack;
    }

    public void setCharacterPack(CharacterPack characterPack) {
        this.characterPack = characterPack;
    }

    public CharacterBottom getCharacterBottom() {
        return characterBottom;
    }

    public void setCharacterBottom(CharacterBottom characterBottom) {
        this.characterBottom = characterBottom;
    }

    public DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public List<InventoryBean> getInventoryBeans() {
        return inventoryBeans;
    }

    public void setInventoryBeans(List<InventoryBean> inventoryBeans) {
        this.inventoryBeans = inventoryBeans;
    }
    */
}
