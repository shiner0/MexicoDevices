package com.ok.moxico;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class CPUUtil {
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 1;
        } catch (NullPointerException e) {
            cores = 1;
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    /**
     * 获取系统存储空间（ROM）大小
     */
    public static long getROMTotalSize(final Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        String availMemStr = formateFileSize(context, blockSize * totalBlocks);
        return blockSize * totalBlocks;
    }

    /**
     * 获取系统可用存储空间（ROM）
     */
    public static long getROMAvailableSize(final Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        String availMemStr = formateFileSize(context, blockSize * availableBlocks);
        return blockSize * availableBlocks;

    }

    public static String formateFileSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    //新增 storage 字段
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSize();
        long availableBlocks = (long) stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = (long) stat.getBlockSize();
        long totalBlocks = (long) stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = (long) stat.getBlockSize();
            long availableBlocks = (long) stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1L;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = (long) stat.getBlockSize();
            long totalBlocks = (long) stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1L;
        }
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static double getScreenSizeOfDevice(Activity context) {
        Point point = new Point();
        context.getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }

    public static long getMemory() {
        long totalMemorySize = 0L;
        String dir = "/proc/meminfo";

        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            totalMemorySize = (long) Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return totalMemorySize;
    }

    public static String getFileSizeDescription(long size) {
        //定义GB/MB/KB的计算常量
        double GB = 1024.0 * 1024.0 * 1024.0;
        double MB = 1024.0 * 1024.0;
        double KB = 1024.0;
        StringBuffer bytes = new StringBuffer();
        DecimalFormat df = new DecimalFormat("###.00");
        if (size >= GB) {
            double i = (size / GB);
            bytes.append(df.format(i)).append("GB");
        } else if (size >= MB) {
            double i = (size / MB);
            bytes.append(df.format(i)).append("MB");
        } else if (size >= KB) {
            double i = (size / KB);
            bytes.append(df.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    public static float getAPPMaxMemory(Context context) {
        //最大分配内存获取方法
        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        return maxMemory;
    }

    public static float getAPPAvailableMemory(Context context) {
        //最大分配内存获取方法
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        return totalMemory;
    }

    public static float getAPPFreeMemory(Context context) {
        //最大分配内存获取方法
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        return freeMemory;
    }

    public static long getRAMTotalMemorySize(final Context context) {
        //获得ActivityManager服务的对象
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.totalMem;
        //字符类型转换
//        String availMemStr = formateFileSize(context, memSize);
        return memSize;

    }

    public static String getMemorySize(final Context context) {
        //获得ActivityManager服务的对象
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.totalMem;
        //字符类型转换
        String availMemStr = formateFileSize(context, memSize);
        return availMemStr;

    }

    public static long getRAMUsableMemorySize(final Context context) {
        //获得ActivityManager服务的对象
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        //字符类型转换
        String availMemStr = formateFileSize(context, memSize);
        return memSize;

    }

    public static JSONObject getNetworkData(Context context) {
        JSONObject network = new JSONObject();
        JSONObject currentNetwork = new JSONObject();
        JSONArray configNetwork = new JSONArray();

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                currentNetwork.put("bssid", wifiInfo.getBSSID());
                currentNetwork.put("ssid", wifiInfo.getSSID());
                currentNetwork.put("mac", wifiInfo.getMacAddress());
                currentNetwork.put("name", getWifiName(context));

                List<ScanResult> configs = wifiManager.getScanResults();
                Iterator var6 = configs.iterator();
                while (var6.hasNext()) {
                    ScanResult scanResult = (ScanResult) var6.next();
                    JSONObject config = new JSONObject();
                    config.put("bssid", scanResult.BSSID);
                    config.put("ssid", scanResult.SSID);
                    config.put("mac", scanResult.BSSID);
                    config.put("name", scanResult.SSID);
                    configNetwork.put(config);
                }
                network.put("currentWifi", currentNetwork);
                network.put("ip", getWifiIP(context));
                network.put("wifiCount", configs.size());
                network.put("configuredWifi", configNetwork);
            }
        } catch (Exception var9) {
        }

        return network;
    }
    private static String getWifiIP(Context context) {
        String ip = null;

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                ip = (i & 255) + "." + (i >> 8 & 255) + "." + (i >> 16 & 255) + "." + (i >> 24 & 255);
            }
        } catch (Exception var4) {
        }

        return ip;
    }
    private static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static String getWifiName(Context context) {
        if (isOnline(context) && getNetworkState(context).equals("WIFI")) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if (!TextUtils.isEmpty(ssid) && ssid.contains("\"")) {
                ssid = ssid.replaceAll("\"", "");
            }

            return ssid;
        } else {
            return "";
        }
    }
    private static String getNetworkState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (null == connManager) {
            return "none";
        } else {
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                NetworkInfo wifiInfo = connManager.getNetworkInfo(1);
                if (null != wifiInfo) {
                    NetworkInfo.State state = wifiInfo.getState();
                    if (null != state && (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING)) {
                        return "WIFI";
                    }
                }

                NetworkInfo networkInfo = connManager.getNetworkInfo(0);
                if (null != networkInfo) {
                    NetworkInfo.State state = networkInfo.getState();
                    String strSubTypeName = networkInfo.getSubtypeName();
                    if (null != state && (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING)) {
                        switch (activeNetInfo.getSubtype()) {
                            case 1:
                            case 2:
                            case 4:
                            case 7:
                            case 11:
                                return "2G";
                            case 3:
                            case 5:
                            case 6:
                            case 8:
                            case 9:
                            case 10:
                            case 12:
                            case 14:
                            case 15:
                                return "3G";
                            case 13:
                                return "4G";
                            default:
                                return !strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !strSubTypeName.equalsIgnoreCase("WCDMA") && !strSubTypeName.equalsIgnoreCase("CDMA2000") ? "other" : "3G";
                        }
                    }
                }

                return "none";
            } else {
                return "none";
            }
        }
    }
}
