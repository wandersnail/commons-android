package com.snail.commons.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.snail.commons.helper.WifiHelper
import java.net.NetworkInterface
import java.util.*

/**
 * 描述:
 * 时间: 2018/6/12 13:01
 * 作者: zengfansheng
 */
object NetworkUtils {
    class NetInfo {
        var type = ""
        var ip = ""
        var isWifi: Boolean = false
        var isAp: Boolean = false
        var ssid = ""
        var mac = ""
    }

    @JvmStatic
    fun getLocalNetInfos(context: Context): List<NetInfo> {
        val list = ArrayList<NetInfo>()
        //获取连接信息
        try {
            val wifiHelper = WifiHelper(context)
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                if (intf.name.toLowerCase(Locale.ENGLISH) == "eth0" || intf.name.toLowerCase(Locale.ENGLISH) == "wlan0") {
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val ipaddress = inetAddress.hostAddress
                            if (ipaddress.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}".toRegex())) { //符合规则才行
                                val info = NetInfo()
                                info.ip = ipaddress
                                info.type = intf.name.toLowerCase(Locale.ENGLISH)
                                info.mac = StringUtils.bytesToHexString(intf.hardwareAddress, ":")
                                if (info.type == "wlan0") {
                                    if (isCurrentNetworkWifi(context)) {
                                        info.isWifi = info.ip == wifiHelper.currentIpAddress
                                        if (info.isWifi) {
                                            info.ssid = wifiHelper.wifiInfo.ssid
                                            list.add(info)
                                        }
                                    } else if (WifiHelper.isApOn(context)) {
                                        info.ssid = WifiHelper.getApSsid(context) ?: ""
                                        info.isAp = true
                                        list.add(info)
                                    }
                                } else {
                                    list.add(info)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    /**
     * 判断网络是否可用，在6.0以上可判断出连通性，否则只判断是否连接，不确定连通性
     */
    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                if (network != null) {
                    val capabilities = cm.getNetworkCapabilities(network)
                    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            } else {
                val netInfo = cm.activeNetworkInfo
                if (netInfo != null) {
                    return netInfo.isAvailable
                }
            }
        }
        return false
    }

    /**
     * 判断是否是当前是否WIFI
     */
    @JvmStatic
    fun isCurrentNetworkWifi(context: Context): Boolean {
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                if (network != null) {
                    val capabilities = cm.getNetworkCapabilities(network)
                    return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
            } else {
                val netInfo = cm.activeNetworkInfo
                if (netInfo != null) {
                    return netInfo.type == ConnectivityManager.TYPE_WIFI
                }
            }
        }
        return false
    }
}
