package com.universe.yz.admin.app;

import android.os.Environment;
import java.io.File;

/**
 * Description: Constants
 * Creator: yxc
 * date: 2016/9/21 10:05
 */
public class Constants {
    //================= PATH ====================
    public static final String PATH_DATA = App.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + File.separator + "NetCache";

    public static final String PRIMARYCOLOR = "PRIMARYCOLOR";
    public static final String TITLECOLOR = "TITLECOLOR";
    public static final String DISCOVERlASTpAGE = "DISCOVERlASTpAGE";

    public static final String PIC_BASE_URL = "http://p.fgame.com/api/";
    public static final String IMG_BASE_URL = "http://p.fgame.com/api/ShopPic/";
    public static final String VIDEO_BASE_URL = "http://p.fgame.com/api/Videos/";
    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "codeest" + File.separator + "GeekNews";
    public static final String PLAY_VIDEO_FLAG = "PLAY_VIDEO_FLAG";
    public static final String NEWS_UPDATE_FLAG = "NEWS_UPDATE_FLAG";
    public static final int    startfrom = 6;

    // Alipay pay parameters
    public static final String partner="2088102450620488";
    public static final String seller_id ="thomas.x.wang@gmail.com";
    public static String out_trade_no="0819145412-6177";
    public static String subject="会员";
    public static String body="黄金会员";
    public static String total_fee="38.0";
    public static final String notify_url="http://notify.msp.hk/notify.htm";
    public static final String service="mobile.securitypay.pay";
    public static final String payment_type="1";
    public static final String _input_charset="utf-8";
    public static final String it_b_pay="30m";
    public static String sign="lBBK%2F0w5LOajrMrji7DUgEqNjIhQbidR13GovA5r3TgIbNqv231yC1NksLdw%2Ba3JnfHXoXuet6XNNHtn7VE%2BeCoRO1O%2BR1KugLrQEZMtG5jmJIe2pbjm%2F3kb%2FuGkpG%2BwYQYI51%2BhA3YBbvZHVQBYveBqK%2Bh8mUyb7GM1HxWs9k4%3D";
    public static final String sign_type="RSA";

    public static final String common="普通会员";
    public static final String copper="铜牌会员";
    public static final String silver="银牌会员";
    public static final String gold="金牌会员";
    public static final String extreme="至尊会员";

    public static String theServer = "";
}