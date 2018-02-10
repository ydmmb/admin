package com.universe.yz.admin.model.bean;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class ShopItem implements SearchSuggestion {
    public String type; //电影分类
    public String limitation;
    public int id;
    public String color;
    public String damage_values0;
    public String damage_values1;
    public String damage_values2;
    public String damage_values3;
    public String damage_values4;
    public String damage_values5;
    public String damage_values6;
    public String damage_values7;
    public int needGloryPoint;
    public String amount;
    public String bitemid;
    public String durability;
    public String serialt;
    public String serialc;
    public String optionId0;
    public String optionId1;
    public String optionId2;
    public String optionId3;
    public String optionId4;
    public String optionId5;
    public String optionId6;
    public String optionId7;
    public String name;
    public int needMaoDou;
    public int needCroPoint;
    public int needVip;
    public int lastConut;
    public String itemPic;
    public String itemText;
    public String itemTime;
    public String upgradePoint;
    public int isTop;
    public int yn;
    public String grade;

    public ShopItem(Parcel source) {
        this.type = source.readString();
        this.limitation = source.readString();
        this.id = source.readInt();
        this.color = source.readString();
        this.damage_values0 = source.readString();
        this.damage_values1 = source.readString();
        this.damage_values2 = source.readString();
        this.damage_values3 = source.readString();
        this.damage_values4 = source.readString();
        this.damage_values5 = source.readString();
        this.damage_values6 = source.readString();
        this.needGloryPoint = source.readInt();
        this.amount = source.readString();
        this.bitemid = source.readString();
        this.durability = source.readString();
        this.serialt = source.readString();
        this.serialc = source.readString();
        this.optionId0 = source.readString();
        this.optionId1 = source.readString();
        this.optionId2 = source.readString();
        this.optionId3 = source.readString();
        this.optionId4 = source.readString();
        this.optionId5 = source.readString();
        this.optionId6 = source.readString();
        this.optionId7 = source.readString();
        this.name = source.readString();
        this.needMaoDou = source.readInt();
        this.needCroPoint = source.readInt();
        this.needVip = source.readInt();
        this.lastConut = source.readInt();
        this.itemPic = source.readString();
        this.itemText = source.readString();
        this.itemTime = source.readString();
        this.upgradePoint = source.readString();
        this.isTop = source.readInt();
        this.yn = source.readInt();
        this.grade = source.readString();
    }

    @Override
    public String getBody() {
            return name;
    }

    @Override
    public int describeContents() {
            return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(limitation);
        parcel.writeInt(id);
        parcel.writeString(color);
        parcel.writeString(damage_values0);
        parcel.writeString(damage_values1);
        parcel.writeString(damage_values2);
        parcel.writeString(damage_values3);
        parcel.writeString(damage_values4);
        parcel.writeString(damage_values5);
        parcel.writeString(damage_values6);
        parcel.writeInt(needGloryPoint);
        parcel.writeString(amount);
        parcel.writeString(bitemid);
        parcel.writeString(durability);
        parcel.writeString(serialt);
        parcel.writeString(serialc);
        parcel.writeString(optionId0);
        parcel.writeString(optionId1);
        parcel.writeString(optionId2);
        parcel.writeString(optionId3);
        parcel.writeString(optionId4);
        parcel.writeString(optionId5);
        parcel.writeString(optionId6);
        parcel.writeString(optionId7);
        parcel.writeString(name);
        parcel.writeInt(needMaoDou);
        parcel.writeInt(needCroPoint);
        parcel.writeInt(needVip);
        parcel.writeInt(lastConut);
        parcel.writeString(itemPic);
        parcel.writeString(itemText);
        parcel.writeString(itemTime);
        parcel.writeString(upgradePoint);
        parcel.writeInt(isTop);
        parcel.writeInt(yn);
        parcel.writeString(grade);
    }

    public static final Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @Override
        public ShopItem createFromParcel(Parcel in) {
                return new ShopItem(in);
        }

        @Override
        public ShopItem[] newArray(int size) {
                return new ShopItem[size];
        }
    };
}
