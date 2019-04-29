/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.raistone.wallet.sealwallet.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogUtils {
    private static Boolean LOG_SWITCH = true;
    private static Boolean LOG_TO_FILE = false;
    private static String LOG_TAG = "BookReader";
    private static char LOG_TYPE = 'v';
    private static int LOG_SAVE_DAYS = 7;

    private final static SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat FILE_SUFFIX = new SimpleDateFormat("yyyy-MM-dd");
    private static String LOG_FILE_PATH;
    private static String LOG_FILE_NAME;

    public static void w(Object msg) {
        w(LOG_TAG, msg);
    }

    public static void w(String tag, Object msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'w');
    }


    public static void e(Object msg) {
        e(LOG_TAG, msg);
    }

    public static void e(String tag, Object msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'e');
    }


    public static void d(Object msg) {
        d(LOG_TAG, msg);
    }

    public static void d(String tag, Object msg) {// 调试信息
        d(tag, msg, null);
    }

    public static void d(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'd');
    }

    public static void i(Object msg) {
        i(LOG_TAG, msg);
    }

    public static void i(String tag, Object msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'i');
    }

    public static void v(Object msg) {
        v(LOG_TAG, msg);
    }

    public static void v(String tag, Object msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'v');
    }

    private static void log(String tag, String msg, Throwable tr, char level) {
        if (LOG_SWITCH) {
            if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) { // 输出错误信息
                Log.e(tag, createMessage(msg), tr);
            } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.w(tag, createMessage(msg), tr);
            } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.d(tag, createMessage(msg), tr);
            } else if ('i' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.i(tag, createMessage(msg), tr);
            } else {
                Log.v(tag, createMessage(msg), tr);
            }
            if (LOG_TO_FILE)
                log2File(String.valueOf(level), tag, msg + tr == null ? "" : "\n" + Log.getStackTraceString(tr));
        }
    }

    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getFileName().equals("LogUtils.java")) {
                continue;
            }
            return "[" + Thread.currentThread().getName() + "("
                    + Thread.currentThread().getId() + "): " + st.getFileName()
                    + ":" + st.getLineNumber() + "]";
        }
        return null;
    }

    private static String createMessage(String msg) {
        String functionName = getFunctionName();
        String message = (functionName == null ? msg
                : (functionName + " - " + msg));
        return message;
    }

    private synchronized static void log2File(String mylogtype, String tag, String text) {
        Date nowtime = new Date();
        String date = FILE_SUFFIX.format(nowtime);
        String dateLogContent = LOG_FORMAT.format(nowtime) + ":" + mylogtype + ":" + tag + ":" + text; // 日志输出格式
        File destDir = new File(LOG_FILE_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file = new File(LOG_FILE_PATH, LOG_FILE_NAME + date);
        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(dateLogContent);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delFile() {// 删除日志文件
        String needDelFiel = FILE_SUFFIX.format(getDateBefore());
        File file = new File(LOG_FILE_PATH, needDelFiel + LOG_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }


    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - LOG_SAVE_DAYS);
        return now.getTime();
    }
}
