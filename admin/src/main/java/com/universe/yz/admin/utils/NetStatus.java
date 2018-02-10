package com.universe.yz.admin.utils;

public enum NetStatus {
    WIFI_MOBILE_AVAILABLE(100, "WIFI和移动网络都可用"),
    MOBILE_AVAILABLE_ONLY(1001, "仅移动网络可用"),
    WIFI_AVAILABLE_ONLY(1002, "仅WIFI可用"),
    NET_STATUS_UNKNOWN(1003, "未知网络状态"),
    EITHER_AVAILABLE(1004, "无网络可用"),
    NO_NET_AVAILABLE(1005, "无网络可用");

    private int code;
    private String message;

    NetStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
