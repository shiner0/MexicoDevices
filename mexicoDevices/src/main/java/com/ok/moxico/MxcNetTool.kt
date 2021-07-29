package com.ok.moxico


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build.VERSION
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.RequiresPermission

import java.net.*
import java.util.*

/**
 * @author tamsiree
 * @date 2016/1/29
 */
object MxcNetTool {
    /**
     * no network
     */
    const val NETWORK_NO = -1

    /**
     * wifi network
     */
    const val NETWORK_WIFI = 1

    /**
     * "2G" networks
     */
    const val NETWORK_2G = 2

    /**
     * "3G" networks
     */
    const val NETWORK_3G = 3

    /**
     * "4G" networks
     */
    const val NETWORK_4G = 4

    /**
     * unknown network
     */
    const val NETWORK_UNKNOWN = 5
    private const val NETWORK_TYPE_GSM = 16
    private const val NETWORK_TYPE_TD_SCDMA = 17
    private const val NETWORK_TYPE_IWLAN = 18

    /**
     * 需添加权限
     *
     * @param context 上下文
     * @return 网络类型
     * @code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     *
     *
     * 它主要负责的是
     * 1 监视网络连接状态 包括（Wi-Fi, 2G, 3G, 4G）
     * 2 当网络状态改变时发送广播通知
     * 3 网络连接失败尝试连接其他网络
     * 4 提供API，允许应用程序获取可用的网络状态
     *
     *
     * netTyped 的结果
     * @link #NETWORK_NO      = -1; 当前无网络连接
     * @link #NETWORK_WIFI    =  1; wifi的情况下
     * @link #NETWORK_2G      =  2; 切换到2G环境下
     * @link #NETWORK_3G      =  3; 切换到3G环境下
     * @link #NETWORK_4G      =  4; 切换到4G环境下
     * @link #NETWORK_UNKNOWN =  5; 未知网络
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getNetWorkType(context: Context): String {
        // 获取ConnectivityManager
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo // 获取当前网络状态
        var netType = ""
        if (ni != null && ni.isConnectedOrConnecting) {
            when (ni.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    netType = "NETWORK_WIFI"
                   // RxToast.success("切换到wifi环境下")
                }
                ConnectivityManager.TYPE_MOBILE -> when (ni.subtype) {
                    NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> {
                        netType = "NETWORK_2G"
                   //     RxToast.info("切换到2G环境下")
                    }
                    TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, NETWORK_TYPE_TD_SCDMA -> {
                        netType = "NETWORK_3G"
                    //    RxToast.info("切换到3G环境下")
                    }
                    TelephonyManager.NETWORK_TYPE_LTE, NETWORK_TYPE_IWLAN -> {
                        netType = "NETWORK_4G"
                   //     RxToast.info("切换到4G环境下")
                    }
                    TelephonyManager.NETWORK_TYPE_NR -> {
                        netType = "NETWORK_5G"
                        //     RxToast.info("切换到4G环境下")
                    }
                    else -> {
                        val subtypeName = ni.subtypeName
                        netType = if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                            || subtypeName.equals("WCDMA", ignoreCase = true)
                            || subtypeName.equals("CDMA2000", ignoreCase = true)
                        ) {
                            "NETWORK_3G"
                        } else {
                            "NETWORK_UNKNOWN"
                        }
                     //   RxToast.normal("未知网络")
                    }
                }
                else -> {
                    netType = "NETWORK_UNKNOWN"
                 //   RxToast.normal("未知网络")
                }
            }
        } else {
            netType = "NETWORK_NO"
        //    RxToast.error(context, "当前无网络连接")?.show()
        }
        return netType
    }


    @RequiresPermission("android.permission.INTERNET")
    @JvmStatic
    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val nis =
                NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<Any?>()
            label64@ while (true) {
                var ni: NetworkInterface
                do {
                    do {
                        if (!nis.hasMoreElements()) {
                            val var9: Iterator<*> = adds.iterator()
                            while (var9.hasNext()) {
                                val add = var9.next() as InetAddress
                                if (!add.isLoopbackAddress) {
                                    val hostAddress = add.hostAddress
                                    val isIPv4 =
                                        hostAddress.indexOf(58.toChar()) < 0
                                    if (useIPv4) {
                                        if (isIPv4) {
                                            return hostAddress
                                        }
                                    } else if (!isIPv4) {
                                        val index = hostAddress.indexOf(37.toChar())
                                        return if (index < 0) hostAddress.toUpperCase() else hostAddress.substring(
                                            0,
                                            index
                                        ).toUpperCase()
                                    }
                                }
                            }
                            break@label64
                        }
                        ni = nis.nextElement() as NetworkInterface
                    } while (!ni.isUp)
                } while (ni.isLoopback)
                val addresses: Enumeration<*> = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
        } catch (var8: SocketException) {
            var8.printStackTrace()
        }
        return ""
    }

    @RequiresPermission("android.permission.ACCESS_WIFI_STATE")
    @JvmStatic
    fun getWifiInfo(context: Context,type:String): String? {
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wm == null) {
            ""
        } else {
            return if (type.equals("netmask")){
                Formatter.formatIpAddress(wm.dhcpInfo.netmask)
            }else if (type.equals("gateway")){
                Formatter.formatIpAddress(wm.dhcpInfo.gateway)
            }else if (type.equals("ipAddress")){
                Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
            }else {
                Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
            }
        }
    }

    @JvmStatic
    fun getBroadcastIpAddress(): String? {
        try {
            val nis =
                NetworkInterface.getNetworkInterfaces()
            LinkedList<Any?>()
            while (true) {
                var ni: NetworkInterface
                do {
                    do {
                        if (!nis.hasMoreElements()) {
                            return ""
                        }
                        ni = nis.nextElement() as NetworkInterface
                    } while (!ni.isUp)
                } while (ni.isLoopback)
                val ias =
                    ni.interfaceAddresses
                var i = 0
                val size = ias.size
                while (i < size) {
                    val ia = ias[i] as InterfaceAddress
                    val broadcast = ia.broadcast
                    if (broadcast != null) {
                        return broadcast.hostAddress
                    }
                    ++i
                }
            }
        } catch (var8: SocketException) {
            var8.printStackTrace()
            return ""
        }
    }

    @RequiresPermission("android.permission.ACCESS_WIFI_STATE")
    @JvmStatic
    fun getSSID(context: Context): String? {
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wm == null) {
            ""
        } else {
            val wi = wm.connectionInfo
            if (wi == null) {
                ""
            } else {
                val ssid = wi.ssid
                if (TextUtils.isEmpty(ssid)) {
                    ""
                } else {
                    if (ssid.length > 2 && ssid[0] == '"' && ssid[ssid.length - 1] == '"') ssid.substring(
                        1,
                        ssid.length - 1
                    ) else ssid
                }
            }
        }
    }


    /**
     * 判断网络连接是否可用
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm == null) {
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            val info = cm.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 判断网络是否可用
     * 需添加权限
     *
     * @code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     */
    @JvmStatic
    fun isAvailable(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable
    }


    @RequiresPermission("android.permission.INTERNET")
    @JvmStatic
    fun isAvailableByPing(): Boolean {
        return isAvailableByPing("")
    }

    @RequiresPermission("android.permission.INTERNET")
    fun isAvailableByPing(ip: String?): Boolean {
        val realIp = if (TextUtils.isEmpty(ip)) "223.5.5.5" else ip!!
        val result =
                MxcAdbUtils.execCmd(
                String.format(
                    "ping -c 1 %s",
                    realIp
                ), false
            )
        return result.result == 0
    }


    @RequiresPermission("android.permission.INTERNET")
    @JvmStatic
    fun isAvailableByDns(): Boolean {
        return isAvailableByDns("")
    }

    @RequiresPermission("android.permission.INTERNET")
    fun isAvailableByDns(domain: String?): Boolean {
        val realDomain = if (TextUtils.isEmpty(domain)) "www.baidu.com" else domain!!
        return try {
            val inetAddress = InetAddress.getByName(realDomain)
            inetAddress != null
        } catch (var4: UnknownHostException) {
            var4.printStackTrace()
            false
        }
    }


    @RequiresPermission(allOf = ["android.permission.ACCESS_WIFI_STATE", "android.permission.INTERNET"])
    @JvmStatic
    fun isWifiAvailable(context: Context): Boolean {
        return getWifiEnabled(context) && isAvailable(context)
    }

    @RequiresPermission("android.permission.ACCESS_WIFI_STATE")
    fun getWifiEnabled(context: Context): Boolean {
        val manager =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return manager?.isWifiEnabled ?: false
    }


    /**
     * 判断wifi是否连接状态
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @param context 上下文
     * @return `true`: 连接<br></br>`false`: 未连接
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun isWifiConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm != null && cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI
    }



    /**
     * 判断网络是否是4G
     * 需添加权限
     *
     * @code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     */
    @JvmStatic
    fun is4G(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_LTE
    }

    @JvmStatic
    fun is5G(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info != null && info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_NR
    }


    /**
     * 获取活动网络信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getActiveNetworkInfo(context: Context): NetworkInfo? {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo!!
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getMobileDataEnabled(context: Context): Boolean {
        try {
            val tm = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                ?: return false
            if (VERSION.SDK_INT >= 26) {
                return tm.isDataEnabled
            }
            val getMobileDataEnabledMethod =
                tm.javaClass.getDeclaredMethod("getDataEnabled")
            if (null != getMobileDataEnabledMethod) {
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            }
        } catch (var2: Exception) {
            Log.e("NetworkUtils", "getMobileDataEnabled: ", var2)
        }
        return false
    }
    /**
     * 获取移动网络运营商名称
     *
     * 如中国联通、中国移动、中国电信
     *
     * @param context 上下文
     * @return 移动网络运营商名称
     */
    @JvmStatic
    fun getNetworkOperatorName(context: Context): String? {
        val tm = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName
    }


}