package com.snail.commons.entity;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.snail.commons.utils.NetworkUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiHelper {

    //创建WifiConfiguration的类型
    public static final int WIFICIPHER_NOPASS = 1;
    public static final int WIFICIPHER_WEP = 2;
    public static final int WIFICIPHER_WPA = 3;
    public static final int WIFICIPHER_WPA2 = 4;
    //WIFI的安全性
    public static final int SECURITY_NONE = 1;
    public static final int SECURITY_EAP = 2;
    public static final int SECURITY_WEP = 3;
    public static final int SECURITY_PSK = 4;

    private WifiManager wifiManager;
    private boolean isScanning;
    private boolean isConnecting;
    private Context context;
    private Handler handler;

    public interface OnScanCallback {
        void onScanResult(@NonNull ScanResult scanResult);

        void onScanStop();
    }

    public interface OnConnectCallback {
        void onSuccess();

        void onFail();
    }

    public WifiHelper(@NonNull Context context) {
        this.context = context.getApplicationContext();
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        handler = new Handler(Looper.getMainLooper());
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public boolean isScanning() {
        return isScanning;
    }

    /**
     * 打开wifi
     */
    public void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 判断wifi是否开启的状态
     */
    public boolean isWifiEnable() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    /**
     * wifi扫描
     */
    public void startScan(final int scanPeriod, final OnScanCallback callback) {
        synchronized (this) {
            if (isScanning) {
                return;
            }
            isScanning = true;
        }
        wifiManager.startScan();
        final List<ScanResult> list = new ArrayList<>();
        final long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (System.currentTimeMillis() - startTime < scanPeriod) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<ScanResult> results = wifiManager.getScanResults();
                    if (results != null) {
                        for (final ScanResult result : results) {
                            if (!TextUtils.isEmpty(result.SSID) && !"\"\"".equals(result.SSID)) {
                                boolean contains = false;
                                for (ScanResult s : list) {
                                    if (result.SSID.equals(s.SSID)) {
                                        contains = true;
                                        break;
                                    }
                                }
                                if (!contains) {
                                    list.add(result);
                                    handleScanCallback(callback, result);
                                }
                            }
                        }
                    }
                }
                isScanning = false;
                handleScanCallback(callback, null);
            }
        }).start();
    }

    private void handleScanCallback(final OnScanCallback callback, final ScanResult result) {
        if (callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (result == null) {
                        callback.onScanStop();
                    } else {
                        callback.onScanResult(result);
                    }
                }
            });
        }
    }

    public List<WifiConfiguration> getWifiConfigurations() {
        return wifiManager.getConfiguredNetworks();
    }

    /**
     * 添加到指定Wifi网络 /切换到指定Wifi网络
     *
     * @param timeoutMillis 超时时间
     * @param callback      连接回调
     */
    public void addNetwork(@NonNull final WifiConfiguration wf, final int timeoutMillis, final OnConnectCallback callback) {
        synchronized (this) {
            if (isConnecting) {
                return;
            }
            isConnecting = true;
        }
        //断开当前
        disconnectCurrentNetwork();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String ssid = wf.SSID;
                //判断是否已保存
                WifiConfiguration savedCfg = null;
                List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
                if (networks != null) {
                    for (WifiConfiguration network : networks) {
                        if (wf.SSID.equals(network.SSID)) {
                            savedCfg = network;
                            break;
                        }
                    }
                }
                int netid;
                if (savedCfg == null) {
                    netid = wifiManager.addNetwork(wf);
                } else {
                    netid = savedCfg.networkId;
                }                
                //连接新的连接
                wifiManager.enableNetwork(netid, true);
                final long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeoutMillis) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (NetworkUtils.isCurrentNetworkWifi(context) && ssid != null && ssid.equals(getWifiInfo().getSSID())) {
                        handleConnectCallback(callback, true);
                        return;
                    }
                }
                handleConnectCallback(callback, false);
            }
        }).start();
    }

    private void handleConnectCallback(final OnConnectCallback callback, final boolean result) {
        if (callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    isConnecting = false;
                    if (result) {
                        callback.onSuccess();
                    } else {
                        callback.onFail();
                    }
                }
            });
        }
    }

    /**
     * 关闭当前的Wifi网络
     */
    public boolean disconnectCurrentNetwork() {
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            return wifiManager.disconnect();
        }
        return false;
    }

    /**
     * 清除当前wifi
     */
    public void clearCurrentNetwork() {
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            int netId = wifiManager.getConnectionInfo().getNetworkId();
            wifiManager.removeNetwork(netId);
            wifiManager.saveConfiguration();
        }
    }

    /**
     * 清除指定wifi
     */
    public void clearNetwork(@NonNull WifiConfiguration conf) {
        if (wifiManager != null) {
            wifiManager.removeNetwork(conf.networkId);
            wifiManager.saveConfiguration();
        }
    }

    /**
     * 创建WifiConfiguration
     *
     * @param type {@link #WIFICIPHER_NOPASS},{@link #WIFICIPHER_WEP},{@link #WIFICIPHER_WPA},{@link #WIFICIPHER_WPA2}
     */
    public static WifiConfiguration createWifiConfiguration(@NonNull String ssid, @NonNull String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        } else if (type == WIFICIPHER_WPA2) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 获取当前WifiInfo
     */
    public WifiInfo getWifiInfo() {
        return wifiManager.getConnectionInfo();
    }

    /**
     * 获取当前Wifi所分配的Ip地址
     */
    public String getCurrentIpAddress() {
        int address = wifiManager.getDhcpInfo().ipAddress;
        return ((address & 0xFF) + "." + ((address >> 8) & 0xFF) + "." + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF));
    }

    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     */
    public String getIpAddressFromHotspot() {
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int address = dhcpInfo.gateway;
        return ((address & 0xFF) + "." + ((address >> 8) & 0xFF) + "." + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF));
    }

    /**
     * 开启热点之后，获取自身热点的IP地址
     */
    public String getHotspotLocalIpAddress() {
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int address = dhcpInfo.serverAddress;
        return ((address & 0xFF) + "." + ((address >> 8) & 0xFF) + "." + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF));
    }

    public static int getSecurity(@NonNull WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK) || config.allowedKeyManagement.get(4)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    public static int getSecurity(@NonNull ScanResult result) {
        if (result.capabilities != null) {
            if (result.capabilities.toUpperCase().contains("WEP")) {
                return SECURITY_WEP;
            } else if (result.capabilities.toUpperCase().contains("PSK")) {
                return SECURITY_PSK;
            } else if (result.capabilities.toUpperCase().contains("EAP")) {
                return SECURITY_EAP;
            }
        }
        return SECURITY_NONE;
    }

    /**
     * @return {@link #WIFICIPHER_NOPASS},{@link #WIFICIPHER_WEP},{@link #WIFICIPHER_WPA},{@link #WIFICIPHER_WPA2}
     */
    public static int getWificipher(@NonNull ScanResult result) {
        if (result.capabilities != null) {
            if (result.capabilities.toUpperCase().contains("WEP")) {
                return WIFICIPHER_WEP;
            } else if (result.capabilities.toUpperCase().contains("WPA2-PSK")) {
                return WIFICIPHER_WPA2;
            } else if (result.capabilities.toUpperCase().contains("WPA-PSK")) {
                return WIFICIPHER_WPA;
            }
        }
        return WIFICIPHER_NOPASS;
    }

    /**
     * 关闭Wifi
     */
    public void disableWifi() {
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 热点是否开启
     */
    public static boolean isApOn(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取热点SSID
     */
    public static String getApSsid(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getDeclaredMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(manager);
            return configuration.SSID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭热点
     */
    public static void disableAp(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(manager, null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 配置热点
     */
    public static boolean configApState(@NonNull Context context) {
        return configApState(context, null);
    }

    /**
     * 配置热点
     */
    public static boolean configApState(@NonNull Context context, String ssid) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = ssid;
            config.preSharedKey = "12345678";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(4);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            // if WiFi is on, turn it off
            if (isApOn(context)) {
                manager.setWifiEnabled(false);
                // if ap is on and then disable ap
                disableAp(context);
            }
            Method method = manager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(manager, config, !isApOn(context));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
