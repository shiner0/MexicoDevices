package com.ok.mexico

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.location.*
import android.net.Proxy
import android.net.wifi.WifiManager
import android.os.Build.VERSION
import android.os.Bundle
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.util.*


object MxcHardwareUtil {
    var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("Location", "onLocationChanged")
            Log.d("Location", "onLocationChanged Latitude" + location.latitude)
            Log.d("Location", "onLocationChanged location" + location.longitude)
        }

        override fun onProviderDisabled(provider: String) {
            Log.d("Location", "onProviderDisabled")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d("Location", "onProviderEnabled")
        }

        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
            Log.d("Location", "onStatusChanged")
        }
    }
    var ml: ArrayList<Any> = ArrayList<Any>()

    @JvmStatic
    fun getuptime(): Long {
        var uptime = 0L
        try {
            if (VERSION.SDK_INT >= 17) {
                uptime = SystemClock.elapsedRealtime()
            }
        } catch (var3: Exception) {
        }
        return uptime
    }
    @JvmStatic
    val timezone: String
        get() {
            var timezone = ""
            try {
                val tz = TimeZone.getDefault()
                timezone = tz.getDisplayName(false, 0) + ", " + tz.id
            } catch (var2: Exception) {
            }
            return timezone
        }
    @JvmStatic
    fun isGpsEnabled(context: Context?): Boolean {
        val lm =context!!
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled("gps")
    }
    @JvmStatic
    val boottime: Long
        get() {
            var bootTime = 0L
            try {
                if (VERSION.SDK_INT >= 17) {
                    bootTime =
                        System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000L
                }
            } catch (var3: Exception) {
            }
            return bootTime
        }
    @JvmStatic
    fun getBatterytemp(context: Context?): Int {
        var temperature = 0
        try {
            val batteryInfoIntent = context!!
                .registerReceiver(
                    null as BroadcastReceiver?,
                    IntentFilter("android.intent.action.BATTERY_CHANGED")
                )
            temperature = batteryInfoIntent!!.getIntExtra("temperature", -1)
        } catch (var2: Exception) {
        }
        return temperature
    }
    @JvmStatic
    fun getBatteryStatus(context: Context?): Int {
        var temperature = 0
        try {
            val batteryInfoIntent = context!!
                .registerReceiver(
                    null as BroadcastReceiver?,
                    IntentFilter("android.intent.action.BATTERY_CHANGED")
                )
            temperature = batteryInfoIntent!!.getIntExtra("status", -1)
        } catch (var2: Exception) {
        }
        return temperature
    }
    @JvmStatic
    fun isPlugged(context: Context?): Boolean {
        var acPlugged = false
        var usbPlugged = false
        var wirePlugged = false
        try {
            val intentFilter = IntentFilter("android.intent.action.BATTERY_CHANGED")
            val intent = context!!
                .registerReceiver(null as BroadcastReceiver?, intentFilter)
            val isPlugged = intent!!.getIntExtra("plugged", -1)
            acPlugged = 1 == isPlugged
            usbPlugged = 2 == isPlugged
            wirePlugged = 4 == isPlugged
        } catch (var6: Exception) {
        }
        return acPlugged || usbPlugged || wirePlugged
    }

    @JvmStatic
    fun isAcCharge(context: Context?): Int {
        var acPlugged = false
        var acCharge = 0
        try {
            val intentFilter = IntentFilter("android.intent.action.BATTERY_CHANGED")
            val intent = context!!
                    .registerReceiver(null as BroadcastReceiver?, intentFilter)
            val isPlugged = intent!!.getIntExtra("plugged", -1)
             acPlugged = 2 == isPlugged
            if (acPlugged){
                acCharge = 1
            }else{
                acCharge = 0
            }
        } catch (var6: Exception) {
        }
        return acCharge
    }
  @JvmStatic
    fun isUSBCharge(context: Context?): Int {
        var acPlugged = false
        var acCharge = 0
        try {
            val intentFilter = IntentFilter("android.intent.action.BATTERY_CHANGED")
            val intent = context!!
                    .registerReceiver(null as BroadcastReceiver?, intentFilter)
            val isPlugged = intent!!.getIntExtra("plugged", -1)
            acPlugged = 1 == isPlugged
            if (acPlugged){
                acCharge = 1
            }else{
                acCharge = 0
            }
        } catch (var6: Exception) {
        }
        return acCharge
    }
    @JvmStatic
    fun getWifiBSSID(context: Context?): String? {
        try {
            val wm =
               context!!
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wm != null) {
                val winfo = wm.connectionInfo
                return winfo.bssid
            }
        } catch (var2: Exception) {
        }
        return null
    }
    @JvmStatic
    fun readArp(context: Context?): String {
        ml.clear()
        try {
            val br =
                BufferedReader(FileReader("/proc/net/arp"))
            var line = ""
            var ip = ""
            var flag = ""
            var mac = ""
            while (br.readLine().also { line = it } != null) {
                try {
                    line = line.trim { it <= ' ' }
                    if (line.length >= 63 && !line.toUpperCase(Locale.US)
                            .contains("IP")
                    ) {
                        ip = line.substring(0, 17).trim { it <= ' ' }
                        flag = line.substring(29, 32).trim { it <= ' ' }
                        mac = line.substring(41, 63).trim { it <= ' ' }
                        if (!mac.contains("00:00:00:00:00:00")) {
                            ml.add("mac=$mac ; ip= $ip ;flag= $flag")
                        }
                    }
                } catch (var6: Exception) {
                }
            }
            br.close()
        } catch (var7: Exception) {
        }
        return ml.toString()
    }
    @JvmStatic
    fun getBluetoothAddress(context: Context?): String? {
        return if (VERSION.RELEASE == "10") {
            ""
        } else {
            try {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val field =
                    bluetoothAdapter.javaClass.getDeclaredField("mService")
                field.isAccessible = true
                val bluetoothManagerService = field[bluetoothAdapter]
                if (bluetoothManagerService == null) {
                    null
                } else {
                    val method =
                        bluetoothManagerService.javaClass.getMethod("getAddress")
                    val address = method.invoke(bluetoothManagerService)
                    if (address != null && address is String) address else null
                }
            } catch (var5: Exception) {
                var5.printStackTrace()
                ""
            }
        }
    }
    @JvmStatic
    fun getCountryZipCode(context: Context?): String {
        var CountryZipCode = ""
        try {
            val locale =
               context!!.resources
                    .configuration.locale
            CountryZipCode = locale.isO3Country
        } catch (var2: Exception) {
        }
        return CountryZipCode
    }

    fun getSimCountryIso(context: Context?): String {
        var simCountryIso = ""
        try {
            val telManager = context!!
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            simCountryIso = telManager.simCountryIso
        } catch (var2: Exception) {
        }
        return simCountryIso
    }
    @JvmStatic
    fun getNetworkCountryIso(context: Context?): String {
        var networkCountryIso = ""
        try {
            val tel = context!!
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            networkCountryIso = tel.networkCountryIso
        } catch (var2: Exception) {
        }
        return networkCountryIso
    }
    @JvmStatic
    fun getSimOperator(context: Context?): String {
        var simOperator = ""
        try {
            val tel = context!!
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            simOperator = tel.simOperator
        } catch (var2: Exception) {
        }
        return simOperator
    }
    @JvmStatic
    fun getNetworkOperator(context: Context?): String {
        var networkOperator = ""
        try {
            val tel = context!!
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            networkOperator = tel.networkOperator
        } catch (var2: Exception) {
        }
        return networkOperator
    }

    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getCellLocation(context: Context?): String {
        var cellLocation: GsmCellLocation? = null
        try {
            val tel = context!!
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            cellLocation = tel.cellLocation as GsmCellLocation
        } catch (var2: Exception) {
        }
        return cellLocation?.toString() ?: ""
    }
    @JvmStatic
    fun getDefaultHost(context: Context?): String {
        var proHost = ""
        var proPort = 0
        try {
            proHost = Proxy.getDefaultHost()
            proPort = Proxy.getDefaultPort()
        } catch (var3: Exception) {
            Log.i("","sss")
        }
       // return "$proHost $proPort"
        return proHost +" "+proPort
    }

    @SuppressLint("MissingPermission")
    @JvmStatic
    fun getVoiceMailNumber(context: Context?): String {
        var voiceMailNumber = ""
        try {
            val tel = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            voiceMailNumber = tel.voiceMailNumber
        } catch (var2: Exception) {
            return ""
        }
       if(TextUtils.isEmpty( return voiceMailNumber)){
           return ""
       }else{
           return voiceMailNumber
       }
    }
}

