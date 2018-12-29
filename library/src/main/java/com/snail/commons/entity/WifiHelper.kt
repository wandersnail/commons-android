package com.snail.commons.entity

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.snail.commons.utils.NetworkUtils
import java.util.*

class WifiHelper(context: Context) {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var isScanning: Boolean = false
        private set
    var isConnecting: Boolean = false
        private set
    private val context = context.applicationContext
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 判断wifi是否开启的状态
     */
    val isWifiEnable: Boolean
        get() = wifiManager.isWifiEnabled

    val wifiConfigurations: List<WifiConfiguration>
        get() = wifiManager.configuredNetworks

    /**
     * 获取当前WifiInfo
     */
    val wifiInfo: WifiInfo
        get() = wifiManager.connectionInfo

    /**
     * 获取当前Wifi所分配的Ip地址
     */
    val currentIpAddress: String
        get() {
            val address = wifiManager.dhcpInfo.ipAddress
            return (address and 0xFF).toString() + "." + (address shr 8 and 0xFF) + "." + (address shr 16 and 0xFF) + "." + (address shr 24 and 0xFF)
        }

    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     */
    val ipAddressFromHotspot: String
        get() {
            val dhcpInfo = wifiManager.dhcpInfo
            val address = dhcpInfo.gateway
            return (address and 0xFF).toString() + "." + (address shr 8 and 0xFF) + "." + (address shr 16 and 0xFF) + "." + (address shr 24 and 0xFF)
        }

    /**
     * 开启热点之后，获取自身热点的IP地址
     */
    val hotspotLocalIpAddress: String
        get() {
            val dhcpInfo = wifiManager.dhcpInfo
            val address = dhcpInfo.serverAddress
            return (address and 0xFF).toString() + "." + (address shr 8 and 0xFF) + "." + (address shr 16 and 0xFF) + "." + (address shr 24 and 0xFF)
        }

    interface OnScanCallback {
        fun onScanResult(scanResult: ScanResult)

        fun onScanStop()
    }

    interface OnConnectCallback {
        fun onSuccess()

        fun onFail()
    }

    /**
     * 打开wifi
     */
    fun openWifi() {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
    }

    /**
     * wifi扫描
     */
    fun startScan(scanPeriod: Int, callback: OnScanCallback) {
        synchronized(this) {
            if (isScanning) {
                return
            }
            isScanning = true
        }
        wifiManager.startScan()
        val list = ArrayList<ScanResult>()
        val startTime = System.currentTimeMillis()
        Thread(Runnable {
            while (System.currentTimeMillis() - startTime < scanPeriod) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                val results = wifiManager.scanResults
                if (results != null) {
                    for (result in results) {
                        if (!TextUtils.isEmpty(result.SSID) && "\"\"" != result.SSID) {
                            var contains = false
                            for (s in list) {
                                if (result.SSID == s.SSID) {
                                    contains = true
                                    break
                                }
                            }
                            if (!contains) {
                                list.add(result)
                                handleScanCallback(callback, result)
                            }
                        }
                    }
                }
            }
            isScanning = false
            handleScanCallback(callback, null)
        }).start()
    }

    private fun handleScanCallback(callback: OnScanCallback?, result: ScanResult?) {
        if (callback != null) {
            handler.post {
                if (result == null) {
                    callback.onScanStop()
                } else {
                    callback.onScanResult(result)
                }
            }
        }
    }

    /**
     * 添加到指定Wifi网络 /切换到指定Wifi网络
     *
     * @param timeoutMillis 超时时间
     * @param callback      连接回调
     */
    fun addNetwork(wf: WifiConfiguration, timeoutMillis: Int, callback: OnConnectCallback?) {
        synchronized(this) {
            if (isConnecting) {
                return
            }
            isConnecting = true
        }
        //断开当前
        disconnectCurrentNetwork()
        Thread(Runnable {
            val ssid = wf.SSID
            //判断是否已保存
            var savedCfg: WifiConfiguration? = null
            val networks = wifiManager.configuredNetworks
            if (networks != null) {
                for (network in networks) {
                    if (wf.SSID == network.SSID) {
                        savedCfg = network
                        break
                    }
                }
            }
            val netid = savedCfg?.networkId ?: wifiManager.addNetwork(wf)
            //连接新的连接
            wifiManager.enableNetwork(netid, true)
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (NetworkUtils.isCurrentNetworkWifi(context) && ssid != null && ssid == wifiInfo.ssid) {
                    handleConnectCallback(callback, true)
                    return@Runnable
                }
            }
            handleConnectCallback(callback, false)
        }).start()
    }

    private fun handleConnectCallback(callback: OnConnectCallback?, result: Boolean) {
        if (callback != null) {
            handler.post {
                isConnecting = false
                if (result) {
                    callback.onSuccess()
                } else {
                    callback.onFail()
                }
            }
        }
    }

    /**
     * 关闭当前的Wifi网络
     */
    fun disconnectCurrentNetwork(): Boolean {
        return if (wifiManager.isWifiEnabled) {
            wifiManager.disconnect()
        } else false
    }

    /**
     * 清除当前wifi
     */
    fun clearCurrentNetwork() {
        if (wifiManager.isWifiEnabled) {
            val netId = wifiManager.connectionInfo.networkId
            wifiManager.removeNetwork(netId)
            wifiManager.saveConfiguration()
        }
    }

    /**
     * 清除指定wifi
     */
    fun clearNetwork(conf: WifiConfiguration) {
        wifiManager.removeNetwork(conf.networkId)
        wifiManager.saveConfiguration()
    }

    /**
     * 关闭Wifi
     */
    fun disableWifi() {
        wifiManager.isWifiEnabled = false
    }

    companion object {

        //创建WifiConfiguration的类型
        const val WIFICIPHER_NOPASS = 1
        const val WIFICIPHER_WEP = 2
        const val WIFICIPHER_WPA = 3
        const val WIFICIPHER_WPA2 = 4
        //WIFI的安全性
        const val SECURITY_NONE = 1
        const val SECURITY_EAP = 2
        const val SECURITY_WEP = 3
        const val SECURITY_PSK = 4

        /**
         * 创建WifiConfiguration
         *
         * @param type [WIFICIPHER_NOPASS],[WIFICIPHER_WEP],[WIFICIPHER_WPA],[WIFICIPHER_WPA2]
         */
        fun createWifiConfiguration(ssid: String, password: String, type: Int): WifiConfiguration {
            val config = WifiConfiguration()
            config.allowedAuthAlgorithms.clear()
            config.allowedGroupCiphers.clear()
            config.allowedKeyManagement.clear()
            config.allowedPairwiseCiphers.clear()
            config.allowedProtocols.clear()

            config.SSID = "\"" + ssid + "\""

            when (type) {
                WIFICIPHER_NOPASS -> config.allowedKeyManagement.set(KeyMgmt.NONE)
                WIFICIPHER_WEP -> {
                    config.hiddenSSID = true
                    config.wepKeys[0] = "\"" + password + "\""
                    config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
                    config.allowedKeyManagement.set(KeyMgmt.NONE)
                    config.wepTxKeyIndex = 0
                }
                WIFICIPHER_WPA -> {
                    config.preSharedKey = "\"" + password + "\""
                    config.hiddenSSID = true
                    config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    config.allowedKeyManagement.set(KeyMgmt.WPA_PSK)
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    config.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                    config.status = WifiConfiguration.Status.ENABLED
                }
                WIFICIPHER_WPA2 -> {
                    config.preSharedKey = "\"" + password + "\""
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    config.allowedKeyManagement.set(KeyMgmt.WPA_PSK)
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                    config.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                    config.status = WifiConfiguration.Status.ENABLED
                }
            }
            return config
        }

        fun getSecurity(config: WifiConfiguration): Int {
            if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK) || config.allowedKeyManagement.get(4)) {
                return SECURITY_PSK
            }
            if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
                return SECURITY_EAP
            }
            return if (config.wepKeys[0] != null) SECURITY_WEP else SECURITY_NONE
        }

        fun getSecurity(result: ScanResult): Int {
            if (result.capabilities != null) {
                when {
                    result.capabilities.toUpperCase(Locale.ENGLISH).contains("WEP") -> return SECURITY_WEP
                    result.capabilities.toUpperCase(Locale.ENGLISH).contains("PSK") -> return SECURITY_PSK
                    result.capabilities.toUpperCase(Locale.ENGLISH).contains("EAP") -> return SECURITY_EAP
                }
            }
            return SECURITY_NONE
        }

        /**
         * @return [WIFICIPHER_NOPASS],[WIFICIPHER_WEP],[WIFICIPHER_WPA],[WIFICIPHER_WPA2]
         */
        fun getWificipher(result: ScanResult): Int {
            if (result.capabilities != null) {
                when {
                    result.capabilities.toUpperCase(Locale.ENGLISH).contains("WEP") -> return WIFICIPHER_WEP
                    result.capabilities.toUpperCase(Locale.ENGLISH).contains("WPA2-PSK") -> return WIFICIPHER_WPA2
                    result.capabilities.toUpperCase(Locale.ENGLISH).contains("WPA-PSK") -> return WIFICIPHER_WPA
                }
            }
            return WIFICIPHER_NOPASS
        }

        /**
         * 热点是否开启
         */
        fun isApOn(context: Context): Boolean {
            try {
                val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val method = manager.javaClass.getDeclaredMethod("isWifiApEnabled")
                method.isAccessible = true
                return method.invoke(manager) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        /**
         * 获取热点SSID
         */
        fun getApSsid(context: Context): String? {
            try {
                val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val method = manager.javaClass.getDeclaredMethod("getWifiApConfiguration")
                val configuration = method.invoke(manager) as WifiConfiguration
                return configuration.SSID
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 关闭热点
         */
        fun disableAp(context: Context) {
            try {
                val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val method = manager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
                method.invoke(manager, null, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 配置热点
         */
        @JvmOverloads
        fun configApState(context: Context, ssid: String? = null): Boolean {
            try {
                val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val config = WifiConfiguration()
                config.SSID = ssid
                config.preSharedKey = "12345678"
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedKeyManagement.set(4)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                // if WiFi is on, turn it off
                if (isApOn(context)) {
                    manager.isWifiEnabled = false
                    // if ap is on and then disable ap
                    disableAp(context)
                }
                val method = manager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
                method.invoke(manager, config, !isApOn(context))
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }
}
