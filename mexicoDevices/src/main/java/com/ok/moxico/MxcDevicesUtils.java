package com.ok.moxico;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.input.InputManager;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MxcDevicesUtils {

    static Map<String, Object> map = new HashMap<>();

    @SuppressLint("MissingPermission")
    public static Map<String, Object> getDevicesInfo(final Activity activity) throws JSONException {
        map.clear();
        map.put("albs", "");
        map.put("idfv", "");
        map.put("idfa", "");
        map.put("productionDate", Build.TIME);
        map.put("audio_external", getAudioExternalNumber(activity));
        map.put("audio_internal", getAudioInternalNumber(activity));
        map.put("video_external", getVideoExternalNumber(activity));
        map.put("video_internal", getVideoInternalNumber(activity));
        map.put("images_external", getImagesExternalNumber(activity));
        map.put("images_internal", getImagesInternalNumber(activity));
        map.put("download_files", getDownloadFileNumber());
        map.put("contact_group", getContactsGroupNumber(activity));
        map.put("pic_count", Integer.parseInt((String) map.get("images_external")) + Integer.parseInt((String) map.get("images_internal")) + "");

        map.put("batteryLevelMa", (Double.parseDouble(MxcBatteryUtils.getBatteryCapacity(activity)) / 100) * MxcBatteryUtils.getSystemBatteryLevel(activity));
        map.put("batteryMaxMa", MxcBatteryUtils.getBatteryCapacity(activity) + "mAh");
        map.put("isAcCharge", getBatteryStatus(activity).getString("is_ac_charge"));
        map.put("isUsbCharge", getBatteryStatus(activity).getString("is_usb_charge"));
        map.put("currentSystemTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()));
        map.put("isUsingProxyPort", isWifiProxy(activity) + "");
        map.put("isUsingVpn", isDeviceInVPN() + "");
        map.put("locale_display_language", Locale.getDefault().getLanguage());
        map.put("locale_iso_3_country", getCountry());
        map.put("locale_iso_3_language", getLanguage());
        map.put("sensor_list", getSensorList(activity).toString());
        map.put("keyboard", getKeyboard(activity));

        map.put("appScreenWidth", MxcDeviceTool.getScreenWidth(activity) + "");
        map.put("appScreenHeight", MxcDeviceTool.getScreenHeight(activity) + "");
        map.put("screenDensity", MxcDeviceTool.getScreenDensity(activity, ""));
        map.put("screenDensityDpi", MxcDeviceTool.getScreenDensity(activity, "dpi"));
        map.put("fullScreen", MxcDeviceTool.isFullScreen(activity) + "");
        map.put("landscape", MxcDeviceTool.isLandscape(activity) + "");

        map.put("lastUpdateTime", MxcAppTool.getLastUpdateTime(activity));
        map.put("appPath", MxcAppTool.getAppPath(activity));
        map.put("sha1", MxcAppTool.getAppSignatureSHA1(activity, "SHA-1"));
        map.put("sha256", MxcAppTool.getAppSignatureSHA1(activity, "SHA256"));
        map.put("md5", MxcAppTool.getAppSignatureSHA1(activity, "MD5"));
        map.put("uid", MxcAppTool.getAppUid(activity) + "");
        map.put("screenWidth", MxcDeviceTool.getScreenWidths(activity) + "");
        map.put("screenHeight", MxcDeviceTool.getScreenHeights(activity) + "");
        map.put("debug", MxcAppTool.isAppDebug(activity) + "");
        map.put("sleepDuration", MxcDeviceTool.getSleepDuration(activity) + "");
        map.put("autoBrightnessEnabled", MxcDeviceTool.isAutoBrightnessEnabled(activity) + "");
        map.put("brightness", MxcDeviceTool.getBrightness(activity) + "");
        map.put("isPhone", MxcDeviceTool.isPhone(activity) + "");
        map.put("phoneType", MxcDeviceTool.getPhoneType(activity) + "");
        map.put("simCardReady", MxcDeviceTool.getSimState(activity) + "");
        map.put("simOperatorName", MxcDeviceTool.getSimOperatorName(activity));
        map.put("simOperatorByMnc", MxcDeviceTool.getSimOperator(activity));
        map.put("simCountryIso", MxcDeviceTool.getSimCountryIso(activity));
        map.put("networkCountryIso", MxcHardwareUtil.getNetworkCountryIso(activity));
        map.put("systemApp", MxcAppTool.isSystemApp(activity) + "");
        map.put("foreground", !MxcAppTool.isAppBackground(activity) + "");
        map.put("running", "true");
        map.put("packageName", MxcAppTool.getAppPackageName(activity));
        map.put("name", MxcAppTool.getAppName(activity));
        map.put("versionName", MxcAppTool.getAppVersionName(activity));
        map.put("versionCode", MxcAppTool.getAppVersionCode(activity) + "");
        map.put("firstInstallTime", MxcAppTool.getFirstInstallTime(activity));
        map.put("portrait", MxcDeviceTool.isPortrait(activity) + "");
        map.put("screenRotation", MxcDeviceTool.getScreenRotation(activity) + "");
        map.put("screenLock", MxcDeviceTool.isScreenLock(activity) + "");
        map.put("networkOperator", MxcHardwareUtil.getNetworkOperator(activity));
        map.put("simSerialNumber", MxcDeviceTool.getSimSerialNumber(activity));
        map.put("networkOperatorName", MxcNetTool.getNetworkOperatorName(activity));
        map.put("deviceId", MxcDeviceTool.getDeviceId(activity));
        map.put("serial", MxcDeviceTool.getSerial());
        String imei = (MxcDeviceTool.getIMEIOne(activity) + "," + MxcDeviceTool.getIMEITwo(activity)).equals(",") ? MxcDeviceTool.getIMEI(activity) : MxcDeviceTool.getIMEIOne(activity) + "," + MxcDeviceTool.getIMEITwo(activity);
        if (TextUtils.equals(",", imei)) {
            imei = "";
        }
        if (!TextUtils.isEmpty(imei)) {
            Log.e("imei", "imei = " + imei);
            if (imei.endsWith(",")) {
                imei = imei.substring(0, imei.length() - 1);
            }

            if (imei.startsWith(",")) {
                imei = imei.substring(1);
            }
        }
        map.put("imei", imei);
        map.put("meid", MxcDeviceTool.getMEID(activity));
        map.put("imsi", MxcDeviceTool.getIMSI(activity));
        map.put("board", Build.BOARD);
        map.put("buildId", Build.ID);
        map.put("host", Build.HOST);
        map.put("display", Build.DISPLAY);
        map.put("radioVersion", Build.getRadioVersion());
        map.put("fingerprint", Build.FINGERPRINT);
        map.put("device", Build.DEVICE);
        map.put("product", Build.PRODUCT);
        map.put("type", Build.TYPE);
        map.put("buildUser", Build.USER);
        map.put("cpuAbi", Build.CPU_ABI);
        map.put("cpuAbi2", Build.CPU_ABI2);
        map.put("baseOS", Build.VERSION.BASE_OS);
        map.put("bootloader", Build.BOOTLOADER);
        map.put("brand", Build.BRAND);
        map.put("time", Build.TIME);
        map.put("hardware", Build.HARDWARE);
        map.put("language", MxcDeviceTool.getCountryByLanguage());
        map.put("country", MxcDeviceTool.getCountryCodeByLanguage("Default"));
        map.put("sdkVersionName", Build.VERSION.RELEASE);
        map.put("sdkVersionCode", Build.VERSION.SDK_INT + "");
        map.put("androidID", MxcDeviceTool.getAndroidId(activity));
        map.put("macAddress", MxcDeviceTool.getMacAddress(activity));
        map.put("manufacturer", MxcDeviceTool.getBuildMANUFACTURER());
        map.put("model", MxcDeviceTool.getBuildBrandModel());
        map.put("abis", Arrays.asList(MxcDeviceTool.getABIs()) + "");
        map.put("isTablet", MxcDeviceTool.isTablet() + "");
        map.put("isEmulator", MxcDeviceTool.isEmulator(activity) + "");
        map.put("sameDevice", "true");
        map.put("connected", MxcNetTool.isNetworkAvailable(activity) + "");
        map.put("mobileDataEnabled", MxcNetTool.getMobileDataEnabled(activity) + "");
        String type = MxcNetTool.getNetWorkType(activity);
        map.put("mobileData", (type.equals("NETWORK_2G") || type.equals("NETWORK_3G") || type.equals("NETWORK_4G") || type.equals("NETWORK_5G")) + "");
        map.put("is4G", MxcNetTool.is4G(activity) + "");
        map.put("is5G", MxcNetTool.is5G(activity) + "");
        map.put("wifiConnected", MxcNetTool.isWifiConnected(activity) + "");
        map.put("networkType", MxcNetTool.getNetWorkType(activity) + "");
        map.put("ipAddress", MxcNetTool.getIPAddress(true) + "");
        map.put("ipv6Address", MxcNetTool.getIPAddress(false));
        map.put("ipAddressByWifi", MxcNetTool.getWifiInfo(activity, "ipAddress"));
        map.put("gatewayByWifi", MxcNetTool.getWifiInfo(activity, "gateway"));
        map.put("netMaskByWifi", MxcNetTool.getWifiInfo(activity, "netmask"));
        map.put("serverAddressByWifi", MxcNetTool.getWifiInfo(activity, "serverAddress"));
        map.put("broadcastIpAddress", MxcNetTool.getBroadcastIpAddress());
        map.put("ssid", MxcNetTool.getSSID(activity));
        map.put("root", MxcAppTool.isAppRoot() + "");
        if (Build.VERSION.SDK_INT >= 17) {
            map.put("adbEnabled", MxcDeviceTool.isAdbEnabled(activity) + "");
        }
        map.put("sdCardEnableByEnvironment", MxcFileTool.sdCardIsAvailable() + "");
        map.put("sdCardPathByEnvironment", MxcFileTool.getSDCardPath());
        map.put("sdCardInfo", MxcDeviceTool.getSDCardInfo(activity).toString());
        map.put("mountedSdCardPath", MxcDeviceTool.getMountedSDCardPath(activity).toString());
        map.put("externalTotalSize", MxcFileTool.byte2FitMemorySize(MxcDeviceTool.getExternalTotalSize(), 2));
        map.put("externalAvailableSize", MxcFileTool.byte2FitMemorySize(MxcDeviceTool.getExternalAvailableSize(), 2));
        map.put("internalTotalSize", MxcFileTool.byte2FitMemorySize(MxcDeviceTool.getInternalTotalSize(), 2));
        map.put("internalAvailableSize", MxcFileTool.byte2FitMemorySize(MxcDeviceTool.getInternalAvailableSize()));
        map.put("batteryLevel", MxcBatteryUtils.getSystemBatteryLevel(activity));
        map.put("batterySum", MxcBatteryUtils.getSystemBatterySum(activity));
        map.put("batteryPercent", MxcBatteryUtils.getSystemBattery(activity) + "%");
        map.put("percentValue", MxcDeviceTool.getUsedPercentValue(activity));
        map.put("availableMemory", MxcDeviceTool.getAvailableMemory(activity));
        map.put("processCpuRate", MxcDeviceTool.getCurProcessCpuRate());
        map.put("cpuRate", MxcDeviceTool.getTotalCpuRate());
        map.put("time", MxcHardwareUtil.getuptime());
        map.put("timezone", MxcHardwareUtil.getTimezone());
        map.put("gpsEnabled", MxcHardwareUtil.isGpsEnabled(activity));
        map.put("bootTime", MxcHardwareUtil.getBoottime());
        map.put("batteryStatus", MxcHardwareUtil.getBatteryStatus(activity));
        map.put("batterytemp", MxcHardwareUtil.getBatterytemp(activity));
        map.put("isPlugged", MxcHardwareUtil.isPlugged(activity));
        map.put("wifiBSSID", MxcHardwareUtil.getWifiBSSID(activity));
        map.put("arpList", MxcHardwareUtil.readArp(activity));
        map.put("bluetoothAddress", MxcHardwareUtil.getBluetoothAddress(activity));
        map.put("countryZipCode", MxcHardwareUtil.getCountryZipCode(activity));
        map.put("cellLocation", MxcHardwareUtil.getCellLocation(activity));
        map.put("defaultHost", getDefaultHost());
        map.put("voiceMailNumber", MxcHardwareUtil.getVoiceMailNumber(activity));
        map.put("available", MxcNetTool.isAvailable(activity) + "");
        map.put("availableByPing", MxcNetTool.isAvailableByPing() + "");
        map.put("availableByDns", MxcNetTool.isAvailableByDns() + "");
        map.put("wifiAvailable", MxcNetTool.isWifiAvailable(activity) + "");
        map.put("wifiSignal", getWifiRssi(activity) + "");
        map.put("cellularSignal", getMobileDbm(activity) + "");

        map.put("ramTotalSize", CPUUtil.getRAMTotalMemorySize(activity));
        map.put("ramUsableSize", CPUUtil.getRAMUsableMemorySize(activity));
        map.put("memoryCardSize", CPUUtil.getTotalExternalMemorySize() + "");
        map.put("memoryCardUsableSize", CPUUtil.getAvailableExternalMemorySize() + "");
        map.put("memoryCardSizeUse", CPUUtil.getTotalExternalMemorySize() - CPUUtil.getAvailableExternalMemorySize() + "");
        map.put("internalStorageUsable", CPUUtil.getAvailableInternalMemorySize() + "");
        map.put("internalStorageTotal", CPUUtil.getTotalInternalMemorySize() + "");
        map.put("network", CPUUtil.getNetworkData(activity) + "");
        map.put("cpuNum", CPUUtil.getNumberOfCPUCores());
        map.put("appMaxMemory", CPUUtil.getAPPMaxMemory(activity) + "M");
        map.put("appAvailableMemory", CPUUtil.getAPPAvailableMemory(activity) + "M");
        map.put("appFreeMemory", CPUUtil.getAPPFreeMemory(activity) + "M");
        map.put("physicalSize", CPUUtil.getScreenSizeOfDevice(activity));
        map.put("totalBootTimeWake", SystemClock.uptimeMillis());
        map.put("totalBootTime", SystemClock.elapsedRealtime());
//                map.put("memory", CPUUtil.getMemorySize(this));
        return map;
    }

    /**
     * 获取手机信号强度，需添加权限 android.permission.ACCESS_COARSE_LOCATION <br>
     * API要求不低于17 <br>
     *
     * @return 当前手机主卡信号强度, 单位 dBm（-1是默认值，表示获取失败）
     */
    @SuppressLint("MissingPermission")
    public static int getMobileDbm(Context context) {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cellInfoList = tm.getAllCellInfo();
            if (null != cellInfoList) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma =
                                ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma =
                                    ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();
                        }
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                    }
                }
            }
        }
        return dbm;
    }

    public static String getDefaultHost() {
        String proHost = "";
        int proPort = 0;
        try {
            proHost = Proxy.getDefaultHost();
            proPort = Proxy.getDefaultPort();
        } catch (Exception var3) {
        }
        return proHost + " " + proPort;
    }

    public static int getWifiRssi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                return info.getRssi();
            }
        }
        return 0;
    }

    public static String getAudioExternalNumber(Context context) {
        int result = 0;
        Cursor cursor;
        for (cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }

    public static String getAudioInternalNumber(Context context) {
        int result = 0;

        Cursor cursor;
        for (cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, new String[]{"date_added", "date_modified", "duration", "mime_type", "is_music", "year", "is_notification", "is_ringtone", "is_alarm"}, (String) null, (String[]) null, "title_key"); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }

    public static String getVideoExternalNumber(Context context) {
        int result = 0;
        String[] arrayOfString = new String[]{"date_added"};
        Cursor cursor;
        for (cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOfString, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return String.valueOf(result);
    }

    public static String getVideoInternalNumber(Context context) {
        int result = 0;
        String[] arrayOfString = new String[]{"date_added"};

        Cursor cursor;
        for (cursor = context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, arrayOfString, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }

    public static String getImagesExternalNumber(Context context) {
        int result = 0;

        Cursor cursor;
        for (cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return String.valueOf(result);
    }

    public static String getImagesInternalNumber(Context context) {
        int result = 0;

        Cursor cursor;
        for (cursor = context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new String[]{"datetaken", "date_added", "date_modified", "height", "width", "latitude", "longitude", "mime_type", "title", "_size"}, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return String.valueOf(result);
    }


    public static String getDownloadFileNumber() {
        int result = 0;
        File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        if (files != null) {
            result = files.length;
        }

        return String.valueOf(result);
    }

    public static String getContactsGroupNumber(Context context) {
        try {
            int result = 0;
            Uri uri = ContactsContract.Groups.CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor;
            for (cursor = contentResolver.query(uri, (String[]) null, (String) null, (String[]) null, (String) null); cursor != null && cursor.moveToNext(); ++result) {
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return String.valueOf(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static JSONObject getBatteryStatus(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            Intent intent = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            int k = intent.getIntExtra("plugged", -1);
            switch (k) {
                case 1:
                    jSONObject.put("is_usb_charge", "false");
                    jSONObject.put("is_ac_charge", "true");
                    jSONObject.put("is_charging", "true");
                    return jSONObject;
                case 2:
                    jSONObject.put("is_usb_charge", "true");
                    jSONObject.put("is_ac_charge", "false");
                    jSONObject.put("is_charging", "true");
                    return jSONObject;
                default:
                    jSONObject.put("is_usb_charge", "false");
                    jSONObject.put("is_ac_charge", "false");
                    jSONObject.put("is_charging", "false");
                    return jSONObject;
            }
        } catch (JSONException e) {
            Log.i("异常", e.toString());
        }
        return jSONObject;
    }

    private static boolean isWifiProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = Proxy.getHost(context);
            proxyPort = Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    //判断网络接口名字包含 ppp0 或 tun0
    public static boolean isDeviceInVPN() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equals("tun0") || nif.getName().equals("ppp0")) {
                    Log.i("TAG", "isDeviceInVPN  current device is in VPN.");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("NewApi")
    private static JSONArray getSensorList(Context context) {
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        JSONArray jsonArray = new JSONArray();
        for (Sensor item : sensors) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", item.getType());
                jsonObject.put("name", item.getName());
                jsonObject.put("version", item.getVersion());
                jsonObject.put("vendor", item.getVendor());
                jsonObject.put("maxRange", item.getMaximumRange());
                jsonObject.put("minDelay", item.getMinDelay());
                jsonObject.put("power", item.getPower());
                jsonObject.put("resolution", item.getResolution());
            } catch (JSONException e) {
                Log.i("json异常", e.toString());
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static String getCountry() {
        String country = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleListCompat listCompat = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
            for (int i = 0; i < listCompat.size(); i++) {
                country = listCompat.get(i).getCountry();
            }
        } else {
            Locale locale = Locale.getDefault();
            country = locale.getCountry();
        }
        return country;
    }

    public static String getLanguage() {
        String language = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleListCompat listCompat = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
            for (int i = 0; i < listCompat.size(); i++) {
                language = listCompat.get(i).getLanguage();
            }
        } else {
            Locale locale = Locale.getDefault();
            language = locale.getLanguage();
        }
        return language;
    }

    public static int getKeyboard(Context context) {
        InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        int[] inputDeviceIds = inputManager.getInputDeviceIds();
        return inputDeviceIds.length;
    }

}
