package com.universe.yz.admin.utils;

import android.os.Handler;
import android.os.Message;

import com.universe.yz.admin.model.bean.M3U8;
import com.universe.yz.admin.model.bean.M3U8Ts;
import com.universe.yz.admin.model.bean.OnM3U8InfoListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class M3U8InfoManger {
    private static M3U8InfoManger mM3U8InfoManger;
    private OnM3U8InfoListener onM3U8InfoListener;
    private static final int WHAT_ON_ERROR = 1101;
    private static final int WHAT_ON_SUCCESS = 1102;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ON_ERROR:
                    onM3U8InfoListener.onError((Throwable) msg.obj);
                    break;
                case WHAT_ON_SUCCESS:
                    onM3U8InfoListener.onSuccess((M3U8) msg.obj);
                    break;
            }
        }
    };

    private M3U8InfoManger() {
    }

    public static M3U8InfoManger getInstance() {
        synchronized (M3U8InfoManger.class) {
            if (mM3U8InfoManger == null) {
                mM3U8InfoManger = new M3U8InfoManger();
            }
        }
        return mM3U8InfoManger;
    }

    public synchronized void getM3U8Info(final String url, OnM3U8InfoListener onM3U8InfoListener) {
        this.onM3U8InfoListener = onM3U8InfoListener;
        onM3U8InfoListener.onStart();
        new Thread() {
            @Override
            public void run() {
                try {
                    //M3U8 m3u8 = MUtils.parseIndex(url);
                    M3U8 m3u8 = parseIndex(url);
                    handlerSuccess(m3u8);
                } catch (IOException e) {
                    handlerError(e);
                }
            }
        }.start();

    }

    private void handlerError(Throwable e) {
        Message msg = mHandler.obtainMessage();
        msg.obj = e;
        msg.what = WHAT_ON_ERROR;
        mHandler.sendMessage(msg);
    }

    private void handlerSuccess(M3U8 m3u8) {
        Message msg = mHandler.obtainMessage();
        msg.obj = m3u8;
        msg.what = WHAT_ON_SUCCESS;
        mHandler.sendMessage(msg);
    }

    public static M3U8 parseIndex(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        if (conn.getResponseCode() == 200) {
            String realUrl = conn.getURL().toString();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String basepath = realUrl.substring(0, realUrl.lastIndexOf("/") + 1);
            M3U8 ret = new M3U8();
            ret.setBasepath(basepath);

            String line;
            float seconds = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    if (line.startsWith("#EXTINF:")) {
                        line = line.substring(8);
                        if (line.endsWith(",")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        seconds = Float.parseFloat(line);
                    }
                    continue;
                }
                if (line.endsWith("m3u8")) {
                    return parseIndex(basepath + line);
                }
                ret.addTs(new M3U8Ts(line, seconds));
                seconds = 0;
            }
            reader.close();

            return ret;
        } else {
            return null;
        }
    }
}
