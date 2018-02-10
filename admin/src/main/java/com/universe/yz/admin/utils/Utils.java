package com.universe.yz.admin.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meiji on 2017/9/30.
 */

public class Utils {

    /**
     * 获取手机内所有支持分享的应用列表
     */
    public static ArrayList<AppInfo> getShareAppList(Context context, Intent intent) {
        ArrayList<AppInfo> shareAppInfos = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = getShareApps(context, intent);
        if (null == resolveInfos) {
            return null;
        } else {
            for (ResolveInfo resolveInfo : resolveInfos) {
                AppInfo appInfo = new AppInfo();
                appInfo.setPkgName(resolveInfo.activityInfo.packageName);
                appInfo.setLaunchClassName(resolveInfo.activityInfo.name);
                appInfo.setAppName(resolveInfo.loadLabel(packageManager).toString());
                appInfo.setAppIcon(resolveInfo.loadIcon(packageManager));
                shareAppInfos.add(appInfo);
            }
        }
        return shareAppInfos;
    }

    /**
     * 查询手机内所有支持分享的应用列表
     */
    public static List<ResolveInfo> getShareApps(Context context, Intent intent) {
        List<ResolveInfo> resolveInfoList;
        PackageManager pm = context.getPackageManager();
        resolveInfoList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);//.COMPONENT_ENABLED_STATE_DEFAULT);
        // 按名称排序
//        ResolveInfo.DisplayNameComparator comparator = new ResolveInfo.DisplayNameComparator(pm);
//        Collections.sort(resolveInfoList, comparator);
        return resolveInfoList;
    }


    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(java.io.File file){
        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return size/1048576;
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     * @param deleteThisPath
     * @param filePath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size/1024;
        if(kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
