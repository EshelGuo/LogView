/*
 * Copyright (c) 2019 Eshel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package open.log.core.util;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {

    private static final String TAG = "LogView";
    private static final String pageage_name = "open.log.plugins";
    private static BufferedWriter sWriter;
    /**
     * 打印日志至文件
     * @return
     */
    public static boolean nativeLogIsOpen() {
        return true;
    }

    /**
     * 打印 方法堆栈 类堆栈, 可在方法中调用 静态代码块中调用 代码块中调用, 打印被调用时堆栈信息
     */
    public static void logMethodStack() {
            new Exception(TAG).printStackTrace(System.out);
    }

    public static void log(Object msg) {
        String msgS = objToString(msg);
        saveLogToNative("D", TAG, msgS);
    }

    public static void logD(Object msg) {
        log(msg);
    }

    public static void logI(Object msg) {
        String msgS = objToString(msg);
        saveLogToNative("I", TAG, msgS);
    }

    public static void logW(Object msg) {
        String msgS = objToString(msg);
        saveLogToNative("W", TAG, msgS);
    }

    public static void logE(Object msg) {
        String msgS = objToString(msg);
        saveLogToNative("E", TAG, msgS);
    }

    public static void d(String tag, String msg) {
        logD(tag + " : " + msg);
    }

    public static void i(String tag, String msg) {
        logI(tag + " : " + msg);
    }

    /**
     * 打印日志 并拼接堆栈信息
     */
    public static void logAndPrintMethodStack(String tag, String msg){
        logI(tag + " : " + msg);
        logMethodStack();
    }

    public static void w(String tag, String msg) {
        logW(tag + " : " + msg);
    }

    public static void e(String tag, String msg) {
        logE(tag + " : " + msg);
    }

    public static void logE(String msg, Throwable throwable) {
        saveLogToNative("E", TAG, msg);
        printError(throwable);
    }

    public static String objToString(Object msg) {
        try {
            String msg2 = "";
            if (msg == null)
                msg2 = "null";
            else if (msg instanceof String)
                msg2 = (String) msg;
            else if (msg instanceof Integer)
                msg2 = "" + ((int) msg);
            else if (msg instanceof Float)
                msg2 = "" + ((float) msg);
            else {
                msg2 = msg.toString();
            }
            return msg2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printStackTrace(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
            saveException("W", "System.err", throwable);
        }
    }

    public static void printError(Throwable throwable) {
        if (throwable != null) {
            saveException("E", "AndroidRuntime", throwable);
        }
    }

    static SimpleDateFormat format = new SimpleDateFormat("MM-dd hh:mm:ss.SSS", Locale.getDefault());

    public static void saveException(String level, String TAG, Throwable throwable) {
        if (!nativeLogIsOpen())
            return;

        if (throwable != null) {
            saveLogToNative(level, TAG,getStackTraceString(throwable));
        }
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, false);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static void saveLogToNative(String level, String TAG, String msg) {
        try {
            if (nativeLogIsOpen()) {
                String content = String.format(Locale.getDefault(), "%s %s-%d/%s %s/%s: %s",
                        format.format(new Date()),
                        ManagementFactory.getRuntimeMXBean().getName().split("@")[0],
                        Thread.currentThread().getId(),
                        Log.pageage_name,
                        level == null ? "NULL" : level,
                        TAG == null ? "null" : TAG,
                        msg == null ? "null" : msg
                );
                if (sWriter == null) {
                    File desktop = FileSystemView.getFileSystemView() .getHomeDirectory();
                    File appDir;
                    appDir = new File(desktop, "AnnFormatLog");
                    if(!appDir.exists() || !appDir.isDirectory()) {
                        boolean mkdir = appDir.mkdir();
                        if(!mkdir){
                            appDir = desktop;
                        }
                    }else {
                        if(appDir == null)
                            appDir = desktop;
                    }
                    if (appDir != null && appDir.exists()) {
                        File logFile = new File(appDir, Log.TAG + ".log");
                        if (!logFile.exists() || !logFile.isFile()) {
                            try {
                                logFile.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            sWriter = new BufferedWriter(new FileWriter(logFile, true));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    sWriter.write(content);
                    sWriter.newLine();
                    sWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    /**
     * 设置全局异常捕获并保存至文件
     */
    public static void setDefaultUncaughtExceptionHandler() {
        //请继承 ErrorReportSubmitter
        //在 plugin.xml 中配置以下代码:
        // <extensions defaultExtensionNs="com.intellij">
        //      <!-- Add your extensions here -->
        //      <errorHandler implementation="com.eshel.ann.format.MyErrorReportSubmitter"/>
        // </extensions>
        /*Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.logE(t.toString());
                printError(e);
            }
        });
        Thread.currentThread().setUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());*/
    }
}
