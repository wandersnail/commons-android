package cn.wandersnail.commons.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * date: 2019/8/7 21:59
 * author: zengfansheng
 */
public class NetworkUtils {
    public static class NetInfo {
        public String type = "";
        public String ip = "";
        public boolean isWifi;
        public boolean isAp;
        public String ssid = "";
        public String mac = "";
    }

    /**
     * 获取当前设备的网络信息
     */
    public static List<NetInfo> getLocalNetInfos(@NonNull Context context) {
        List<NetInfo> list = new ArrayList<>();
        //获取连接信息
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                if ("eth0".equals(intf.getName().toLowerCase(Locale.ENGLISH)) || "wlan0".equals(intf.getName().toLowerCase(Locale.ENGLISH))) {
                    Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (ipaddress.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) { //符合规则才行
                                NetInfo info = new NetInfo();
                                info.ip = ipaddress;
                                info.type = intf.getName().toLowerCase(Locale.ENGLISH);
                                info.mac = StringUtils.toHex(intf.getHardwareAddress(), ":");
                                if ("wlan0".equals(info.type)) {
                                    if (isCurrentNetworkWifi(context)) {
                                        int ipAddress = Objects.requireNonNull(wifiManager).getDhcpInfo().ipAddress;
                                        info.isWifi = info.ip.equals(toAddressString(ipAddress));
                                        if (info.isWifi) {
                                            info.ssid = wifiManager.getConnectionInfo().getSSID();
                                            list.add(info);
                                        }
                                    } else if (isApOn(context)) {
                                        String apSsid = getApSsid(context);
                                        info.ssid = apSsid == null ? "" : apSsid;
                                        info.isAp = true;
                                        list.add(info);
                                    }
                                } else {
                                    list.add(info);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return list;
    }

    @SuppressLint("DefaultLocale")
    public static String toAddressString(int address) {
        return String.format("%d.%d.%d.%d", address & 0xff, (address >> 8) & 0xff, (address >> 16) & 0xff, (address >> 24) & 0xff);
    }

    /**
     * 热点是否开启
     */
    @SuppressWarnings("all")
    public static boolean isApOn(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(manager);
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * 获取热点SSID
     */
    @SuppressWarnings("all")
    public static String getApSsid(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getDeclaredMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(manager);
            return configuration.SSID;
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 关闭热点
     */
    @SuppressWarnings("all")
    public static void disableAp(@NonNull Context context) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(manager, null, false);
        } catch (Exception ignore) {
        }
    }

    /**
     * 配置热点
     */
    @SuppressWarnings("all")
    public static boolean configApState(@NonNull Context context, String ssid, String preSharedKey) {
        try {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = ssid;
            config.preSharedKey = preSharedKey;
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

    /**
     * 判断网络是否可用，在6.0以上可判断出连通性，否则只判断是否连接，不确定连通性
     */
    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                }
            } else {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {
                    return netInfo.isAvailable();
                }
            }
        }
        return false;
    }

    /**
     * 判断当前是否使用WIFI网络
     */
    public static boolean isCurrentNetworkWifi(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                    return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            } else {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {
                    return netInfo.getType() == ConnectivityManager.TYPE_WIFI;
                }
            }
        }
        return false;
    }

    /**
     * wifi是否已连接，不保证是正在使用的网络
     */
    public static boolean isWifiConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network[] networks = cm.getAllNetworks();
                for (Network network : networks) {
                    NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                    if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {
                    return netInfo.getType() == ConnectivityManager.TYPE_WIFI;
                }
            }
        }
        return false;
    }
}
