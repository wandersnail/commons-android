package com.snail.commons.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import com.snail.commons.entity.WifiHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * 描述:
 * 时间: 2018/6/12 13:01
 * 作者: zengfansheng
 */
public class NetworkUtils {
    public static class NetInfo {
        public String type;
        public String ip;
        public boolean isWifi;
        public boolean isAp;
        public String ssid;
        public String mac;
    }
    
    public static List<NetInfo> getLocalNetInfos(@NonNull Context context) {
        List<NetInfo> list = new ArrayList<>();
        //获取连接信息
        try {
            WifiHelper wifiHelper = new WifiHelper(context);
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().toLowerCase(Locale.ENGLISH).equals("eth0") || intf.getName().toLowerCase(Locale.ENGLISH).equals("wlan0")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (ipaddress.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {//符合规则才行
                                NetInfo info = new NetInfo();
                                info.ip = ipaddress;
                                info.type = intf.getName().toLowerCase(Locale.ENGLISH);
                                info.mac = StringUtils.bytesToHexString(intf.getHardwareAddress(), ":");
                                if (info.type.equals("wlan0")) {
                                    if (isCurrentNetworkWifi(context)) {
                                        info.isWifi = info.ip.equals(wifiHelper.getCurrentIpAddress());
                                        if (info.isWifi) {
                                            info.ssid = wifiHelper.getWifiInfo().getSSID();
                                            list.add(info);
                                        }
                                    } else if (WifiHelper.isApOn(context)) {
                                        info.ssid = WifiHelper.getApSsid(context);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 判断网络是否可用，在6.0以上可判断出连通性，否则只判断是否连接，不确定连通性
     */
    public static boolean isNetworkAvailable(Context context) {
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
     * 判断是否是当前是否WIFI
     */
    public static boolean isCurrentNetworkWifi(Context context) {
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

    @SuppressWarnings("unchecked")
    public static void setStaticIpConfiguration(WifiManager manager, WifiConfiguration config, InetAddress ipAddress, int prefixLength, InetAddress gateway, InetAddress[] dns) {
        try {
            // First set up IpAssignment to STATIC.
            Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
            callMethod(config, "setIpAssignment", new String[] { "android.net.IpConfiguration$IpAssignment" }, new Object[] {ipAssignment});

            // Then set properties in StaticIpConfiguration.
            Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");

            Object linkAddress = newInstance("android.net.LinkAddress", new Class[] { InetAddress.class, int.class }, new Object[] {ipAddress, prefixLength});
            setField(staticIpConfig, "ipAddress", linkAddress);
            setField(staticIpConfig, "gateway", gateway);
            ArrayList<Object> aa = (ArrayList<Object>) getField(staticIpConfig, "dnsServers");
            aa.clear();
            Collections.addAll(aa, dns);
            callMethod(config, "setStaticIpConfiguration", new String[] { "android.net.StaticIpConfiguration" }, new Object[] { staticIpConfig });
            manager.updateNetwork(config);
            manager.saveConfiguration();

            int netId = manager.addNetwork(config);
            manager.disableNetwork(netId);
            manager.enableNetwork(netId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object newInstance(String className) {
        try {
            return newInstance(className, new Class[0], new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object newInstance(String className, Class[] parameterClasses, Object[] parameterValues) {
        try {
            Class clz = Class.forName(className);
            Constructor constructor = clz.getConstructor(parameterClasses);
            return constructor.newInstance(parameterValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object getEnumValue(String enumClassName, String enumValue) {
        try {
            Class cls = Class.forName(enumClassName);
            return Enum.valueOf(cls, enumValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private static void callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues) {
        try {
            Class[] parameterClasses = new Class[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++)
                parameterClasses[i] = Class.forName(parameterTypes[i]);
            Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
            method.invoke(object, parameterValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
