package com.universe.yz.admin.model.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * M3U8实体类
 * Created by HDL on 2017/7/24.
 */

public class M3U8 {
    private String localsavepath;
    private String basepath;
    private List<M3U8Ts> tsList = new ArrayList<>();
    private long startTime;//开始时间
    private long endTime;//结束时间
    private long startDownloadTime;//开始下载时间
    private long endDownloadTime;//结束下载时间

    public String getLocalsavepath() {
        return localsavepath;
    }

    public void setLocalsavepath(String localsavepath) {
        this.localsavepath = localsavepath;
    }
    public String getBasepath() {
        return basepath;
    }

    public void setBasepath(String basepath) {
        this.basepath = basepath;
    }

    public List<M3U8Ts> getTsList() {
        return tsList;
    }

    public void setTsList(List<M3U8Ts> tsList) {
        this.tsList = tsList;
    }

    public void addTs(M3U8Ts ts) {
        this.tsList.add(ts);
    }

    public long getStartDownloadTime() {
        return startDownloadTime;
    }

    public void setStartDownloadTime(long startDownloadTime) {
        this.startDownloadTime = startDownloadTime;
    }

    public long getEndDownloadTime() {
        return endDownloadTime;
    }

    public void setEndDownloadTime(long endDownloadTime) {
        this.endDownloadTime = endDownloadTime;
    }

    /**
     * 获取开始时间
     *
     * @return
     */
    public long getStartTime() {
        if (tsList.size()>0) {
            Collections.sort(tsList);
            startTime = tsList.get(0).getLongDate();
            return startTime;
        }
        return 0;
    }

    /**
     * 获取结束时间(加上了最后一段时间的持续时间)
     *
     * @return
     */
    public long getEndTime() {
        if (tsList.size()>0) {
            M3U8Ts m3U8Ts = tsList.get(tsList.size() - 1);
            endTime = m3U8Ts.getLongDate() + (long) (m3U8Ts.getSeconds() * 1000);
            return endTime;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("basepath: " + basepath);
        for (M3U8Ts ts : tsList) {
            sb.append("\nts_file_name = " + ts);
        }
        sb.append("\n\nstartTime = " + startTime);
        sb.append("\n\nendTime = " + endTime);
        sb.append("\n\nstartDownloadTime = " + startDownloadTime);
        sb.append("\n\nendDownloadTime = " + endDownloadTime);
        return sb.toString();
    }
}
