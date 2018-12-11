package com.snail.commons.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.Settings;

import com.snail.commons.entity.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 安卓系统的工具类
 *
 * @author Zeng
 */
public class SystemUtils {
    /**
     * 判断位置服务是否打开
     */
    public static boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                return locationManager.isLocationEnabled();
            }
        } else {
            try {
                int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }        
        return false;
    }

    /**
     * 判断GPS是否打开
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断服务是否正在运行
     *
     * @param context   上下文
     * @param className 服务的完整类名
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo serviceInfo : services) {
            if (serviceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断屏幕是否亮着
     * @param context 上下文
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (pm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    /**
     * 判断当前系统是否安装指定的应用
     * @param context 上下文
     * @param packageName 要判断的应用包名
     */
    public boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if(pinfo.get(i).packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取总内存大小，单位是byte
     */
    public static long getTotalMemSize() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")));
            char[] info = br.readLine().toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : info) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            long kbSize = Long.parseLong(sb.toString());
            br.close();
            return (kbSize * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取可用内存大小，单位byte
     */
    public static long getAvailMemSize(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        if (am == null) {
            return -1;
        }
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取正在运行的进程数
     */
    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return -1;
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses == null ? 0 : runningAppProcesses.size();
    }

    /**
     * 扩展卡是否可用
     */
    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return 字节数
     */
    public static long getInternalFreeSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    }

    /**
     * 获取扩展卡剩余存储空间
     *
     * @return 字节数。
     */
    public static long getExternalFreeSpace() {
        if (isSdCardAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        } else {
            return 0;
        }
    }

    /**
     * 获取存储卡剩余大小
     */
    public static long getStorageFreeSpace(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                StatFs stat = new StatFs(path);
                return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            }
        }
        return 0;
    }

    /**
     * 存储卡总容量
     */
    public static long getStorageTotalSpace(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                StatFs stat = new StatFs(path);
                return stat.getBlockSizeLong() * stat.getBlockCountLong();
            }
        }
        return 0;
    }

    /**
     * 获取所有存储路径
     */
    public static List<String> getStoragePaths(Context context) {
        StorageManager sm = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            String[] volumePaths = (String[]) sm.getClass().getMethod("getVolumePaths").invoke(sm);
            List<String> pathList = new ArrayList<>();
            for (String path : volumePaths) {
                if (getStorageTotalSpace(path) > 0) {
                    pathList.add(path);
                }
            }
            return pathList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Storage> getStorages(Context context) {
        StorageManager storageManager = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            //得到StorageManager中的getVolumeList()方法的对象
            Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");
            //得到StorageVolume类的对象
            Class<?> storageValumeClazz = Class.forName("android.os.storage.StorageVolume");
            //获得StorageVolume中的一些方法
            Method getPath = storageValumeClazz.getMethod("getPath");
            Method isRemovable = storageValumeClazz.getMethod("isRemovable");
            Method allowMassStorage = storageValumeClazz.getMethod("allowMassStorage");
            Method primary = storageValumeClazz.getMethod("isPrimary");
            Method description = storageValumeClazz.getMethod("getDescription", Context.class);

            Method mGetState = null;
            //getState 方法是在4.4_r1之后的版本加的，之前版本（含4.4_r1）没有
            // （http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.4_r1/android/os/Environment.java/）
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                try {
                    mGetState = storageValumeClazz.getMethod("getState");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            //调用getVolumeList方法，参数为：“谁”中调用这个方法
            Object invokeVolumeList = getVolumeList.invoke(storageManager);
            int length = Array.getLength(invokeVolumeList);
            ArrayList<Storage> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Object storageValume = Array.get(invokeVolumeList, i);//得到StorageVolume对象
                String path = (String) getPath.invoke(storageValume);
                boolean removable = (boolean) isRemovable.invoke(storageValume);
                boolean isAllowMassStorage = (boolean) allowMassStorage.invoke(storageValume);
                boolean isPrimary = (boolean) primary.invoke(storageValume);
                String desc = (String) description.invoke(storageValume, context);
                String state;
                if (mGetState != null) {
                    state = (String) mGetState.invoke(storageValume);
                } else {
                    state = Environment.getStorageState(new File(path));
                }
                long totalSize = 0;
                long availaleSize = 0;
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    totalSize = getStorageTotalSpace(path);
                    availaleSize = getStorageFreeSpace(path);
                }
                Storage storage = new Storage();
                storage.availaleSize = availaleSize;
                storage.totalSize = totalSize;
                storage.state = state;
                storage.path = path;
                storage.isRemovable = removable;
                storage.description = desc;
                storage.isAllowMassStorage = isAllowMassStorage;
                storage.isPrimary = isPrimary;
                storage.isUsb = desc != null && desc.toLowerCase().contains("usb");
                list.add(storage);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储器是否被挂载
     */
    public static boolean isMounted(Context context, String path) {
        StorageManager sm = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            String state = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, path);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * 获取Application的Meta值
     *
     * @param context 上下文
     * @param name    meta名
     * @return 没有返回null
     */
    public static String getApplicationMetaValue(Context context, String name) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Activity的Meta值
     *
     * @param context 上下文
     * @param cls     Activity的class
     * @param name    meta名
     * @return 没有返回null
     */
    public static String getActivityMetaValue(Context context, Class<?> cls, String name) {
        try {
            ActivityInfo info = context.getPackageManager().getActivityInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Receiver的Meta值
     *
     * @param context 上下文
     * @param cls     Receiver的class
     * @param name    meta名
     * @return 没有返回null
     */
    public static String getReceiverMetaValue(Context context, Class<?> cls, String name) {
        try {
            ActivityInfo info = context.getPackageManager().getReceiverInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Service的Meta值
     *
     * @param context 上下文
     * @param cls     Service的class
     * @param name    meta名
     * @return 没有返回null
     */
    public static String getServiceMetaValue(Context context, Class<?> cls, String name) {
        try {
            ServiceInfo info = context.getPackageManager().getServiceInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
