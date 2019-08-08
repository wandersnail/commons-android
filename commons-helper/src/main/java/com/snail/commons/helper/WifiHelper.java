package com.snail.commons.helper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.snail.commons.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * date: 2019/8/6 16:57
 * author: zengfansheng
 */
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

    private final Context context;
    private final Handler handler;
    private final WifiManager wifiManager;
    private boolean isScanning;
    private boolean isConnecting;
    private BroadcastReceiver resultReceiver;
    private ScanTimeoutRunnable scanTimeoutRunnable;

    public WifiHelper(@NonNull Context context) {
        this.context = context.getApplicationContext();
        handler = new Handler(Looper.getMainLooper());
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 判断wifi是否开启的状态
     */
    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    public List<WifiConfiguration> getWifiConfigurations() {
        return wifiManager.getConfiguredNetworks();
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
    @SuppressLint("DefaultLocale")
    public String getCurrentIpAddress() {
        int address = wifiManager.getDhcpInfo().ipAddress;
        return NetworkUtils.toAddressString(address);
    }

    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     */
    public String getIpAddressFromHotspot() {
        int address = wifiManager.getDhcpInfo().gateway;
        return NetworkUtils.toAddressString(address);
    }

    /**
     * 开启热点之后，获取自身热点的IP地址
     */
    public String getHotspotLocalIpAddress() {
        int address = wifiManager.getDhcpInfo().serverAddress;
        return NetworkUtils.toAddressString(address);
    }

    /**
     * 打开wifi
     */
    public void openWifi() {
        if (!isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * wifi扫描
     */
    public void startScan(int timeoutMillis, ScanCallback callback) {
        synchronized (this) {
            if (isScanning) {
                return;
            }
            isScanning = true;
        }
        scanTimeoutRunnable = new ScanTimeoutRunnable(callback);
        resultReceiver = new ResultBroadcastReceiver(callback);
        IntentFilter intent = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(resultReceiver, intent);
        handler.postDelayed(scanTimeoutRunnable, timeoutMillis);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            wifiManager.startScan();
        }
    }

    private class ScanTimeoutRunnable implements Runnable {
        private ScanCallback callback;

        ScanTimeoutRunnable(ScanCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            if (resultReceiver != null) {
                context.unregisterReceiver(resultReceiver);
                resultReceiver = null;
            }
            isScanning = false;
            handleScanResults(callback);
        }
    }

    private class ResultBroadcastReceiver extends BroadcastReceiver {
        private ScanCallback callback;

        ResultBroadcastReceiver(ScanCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isScanning && WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                if (scanTimeoutRunnable != null) {
                    handler.removeCallbacks(scanTimeoutRunnable);
                }
                if (resultReceiver != null) {
                    WifiHelper.this.context.unregisterReceiver(resultReceiver);
                    resultReceiver = null;
                }
                handleScanResults(callback);
            }
        }
    }

    private void handleScanResults(ScanCallback callback) {
        isScanning = false;
        List<ScanResult> list = new ArrayList<>();
        List<ScanResult> results = wifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                if (!TextUtils.isEmpty(result.SSID) && !"\"\"".equals(result.SSID)) {
                    boolean contains = false;
                    for (ScanResult sr : list) {
                        if (result.SSID.equals(sr.SSID)) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        list.add(result);
                    }
                }
            }
        }
        if (callback != null) {
            callback.onComplete(list);
        }
    }

    public void addNetwork(final WifiConfiguration wf, final int timeoutMillis, final ConnectCallback callback) {
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
                String ssid = wf.SSID;
                //判断是否已保存
                WifiConfiguration savedCfg = null;
                List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
                if (networks != null) {
                    for (WifiConfiguration network : networks) {
                        if (network.SSID.equals(ssid)) {
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
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeoutMillis) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) {
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

    private void handleConnectCallback(final ConnectCallback callback, final boolean result) {
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
        if (isWifiEnabled()) {
            return wifiManager.disconnect();
        }
        return false;
    }

    /**
     * 清除当前wifi
     */
    public void clearCurrentNetwork() {
        if (isWifiEnabled()) {
            int netId = wifiManager.getConnectionInfo().getNetworkId();
            wifiManager.removeNetwork(netId);
            wifiManager.saveConfiguration();
        }
    }

    /**
     * 清除指定wifi
     */
    public void clearNetwork(WifiConfiguration conf) {
        wifiManager.removeNetwork(conf.networkId);
        wifiManager.saveConfiguration();
    }

    /**
     * 关闭Wifi
     */
    public void disableWifi() {
        wifiManager.setWifiEnabled(false);
    }

    /**
     * 创建WifiConfiguration
     *
     * @param cipher {@link #WIFICIPHER_NOPASS},{@link #WIFICIPHER_WEP},{@link #WIFICIPHER_WPA},{@link #WIFICIPHER_WPA2}
     */
    public static WifiConfiguration createWifiConfiguration(@NonNull String ssid, String password, int cipher) {
        Objects.requireNonNull(ssid, "ssid is null");
        password = password == null ? "" : password;
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        switch (cipher) {
            case WIFICIPHER_NOPASS:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case WIFICIPHER_WEP:
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFICIPHER_WPA:
                config.preSharedKey = "\"" + password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            case WIFICIPHER_WPA2:
                config.preSharedKey = "\"" + password + "\"";
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return config;
    }

    public static int getSecurity(@NonNull WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK) || config.allowedKeyManagement.get(4)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return config.wepKeys[0] != null ? SECURITY_WEP : SECURITY_NONE;
    }

    public static int getSecurity(@NonNull ScanResult result) {
        if (result.capabilities != null) {
            String capabilities = result.capabilities.toUpperCase(Locale.ENGLISH);
            if (capabilities.contains("WEP")) {
                return SECURITY_WEP;
            } else if (capabilities.contains("PSK")) {
                return SECURITY_PSK;
            } else if (capabilities.contains("EAP")) {
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
            String capabilities = result.capabilities.toUpperCase(Locale.ENGLISH);
            if (capabilities.contains("WEP")) {
                return WIFICIPHER_WEP;
            } else if (capabilities.contains("WPA2-PSK")) {
                return WIFICIPHER_WPA2;
            } else if (capabilities.contains("WPA-PSK")) {
                return WIFICIPHER_WPA;
            }
        }
        return WIFICIPHER_NOPASS;
    }

    public interface ScanCallback {
        void onComplete(@NonNull List<ScanResult> scanResults);
    }

    public interface ConnectCallback {
        void onSuccess();

        void onFail();
    }
}
