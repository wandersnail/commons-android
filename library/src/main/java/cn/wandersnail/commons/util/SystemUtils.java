package cn.wandersnail.commons.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.os.EnvironmentCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.wandersnail.commons.util.entity.Storage;

/**
 * 系统的工具类
 * <p>
 * date: 2019/8/7 22:38
 * author: zengfansheng
 */
public class SystemUtils {
    /**
     * 获取总内存大小，单位是byte
     */
    public static long getTotalMemSize() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
            char[] info = br.readLine().toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : info) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            long kbSize = Long.parseLong(sb.toString());
            br.close();
            return kbSize * 1024;
        } catch (Exception ignore) {
        }
        return 0;
    }

    /**
     * 存储卡是否可用
     */
    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取内置存储卡剩余存储空间
     */
    public static long getInternalFreeSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    }

    /**
     * 获取扩展卡剩余存储空间
     */
    public static long getExternalFreeSpace() {
        if (isSdCardAvailable()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        } else {
            return 0;
        }
    }

    /**
     * 获取存储设备剩余大小
     */
    public static long getStorageFreeSpace(@NonNull String path) {
        if (new File(path).exists()) {
            StatFs stat = new StatFs(path);
            return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        }
        return 0;
    }

    /**
     * 存储设备总容量
     */
    public static long getStorageTotalSpace(@NonNull String path) {
        if (new File(path).exists()) {
            StatFs stat = new StatFs(path);
            return stat.getBlockSizeLong() * stat.getBlockCountLong();
        }
        return 0;
    }

    public static String getSystemProperty(@NonNull String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
            return line;
        } catch (IOException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 判断当前系统是否安装指定的应用
     *
     * @param packageName 要判断的应用包名
     */
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断位置服务是否打开
     */
    public static boolean isLocationEnabled(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                return locationManager.isLocationEnabled();
            }
        } else {
            try {
                int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;
            } catch (Exception ignore) {
            }
        }
        return false;
    }

    /**
     * 判断GPS是否打开
     */
    public static boolean isGPSEnabled(@NonNull Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断屏幕是否亮着
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static boolean isScreenOn(@NonNull Context context) {
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        return powerManager != null && powerManager.isInteractive();
    }

    /**
     * 获取可用内存大小，单位byte
     */
    public static long getAvailMemSize(@NonNull Context context) {
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
    public static int getRunningProcessCount(@NonNull Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        return am == null ? 0 : am.getRunningAppProcesses().size();
    }

    @Nullable
    public static String getCurrentProcessName(@NonNull Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /**
     * 获取所有存储路径
     */
    @NonNull
    public static List<String> getStoragePaths(@NonNull Context context) {
        try {
            StorageManager sm = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
            String[] volumePaths = (String[]) Objects.requireNonNull(sm).getClass().getMethod("getVolumePaths").invoke(sm);
            List<String> pathList = new ArrayList<>();
            for (String path : volumePaths) {
                if (SystemUtils.getStorageTotalSpace(path) > 0) {
                    pathList.add(path);
                }
            }
            return pathList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取设备存储信息
     */
    @NonNull
    public static List<Storage> getStorages(@NonNull Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
            Objects.requireNonNull(storageManager);
            //得到StorageManager中的getVolumeList()方法的对象
            Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");
            //得到StorageVolume类的对象
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            //获得StorageVolume中的一些方法
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Method allowMassStorage = storageVolumeClazz.getMethod("allowMassStorage");
            Method primary = storageVolumeClazz.getMethod("isPrimary");
            Method description = storageVolumeClazz.getMethod("getDescription", Context.class);

            Method mGetState = null;
            try {
                mGetState = storageVolumeClazz.getMethod("getState");
            } catch (NoSuchMethodException ignore) {
            }

            //调用getVolumeList方法，参数为：“谁”中调用这个方法
            Object invokeVolumeList = getVolumeList.invoke(storageManager);
            int length = java.lang.reflect.Array.getLength(invokeVolumeList);
            List<Storage> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Object storageValume = java.lang.reflect.Array.get(invokeVolumeList, i);//得到StorageVolume对象
                Object invokePath = getPath.invoke(storageValume);
                String path = invokePath == null ? "" : (String) invokePath;
                Object invokeRemovable = isRemovable.invoke(storageValume);
                boolean removable = invokeRemovable != null && (boolean) invokeRemovable;
                Object invokeAllowMass = allowMassStorage.invoke(storageValume);
                boolean isAllowMassStorage = invokeAllowMass != null && (boolean) invokeAllowMass;
                Object invokePrimary = primary.invoke(storageValume);
                boolean isPrimary = invokePrimary != null && (boolean) invokePrimary;
                Object invokeVolume = description.invoke(storageValume, context);
                String desc = invokeVolume == null ? "" : (String) invokeVolume;
                String state;
                if (mGetState != null) {
                    state = (String) mGetState.invoke(storageValume);
                } else {
                    state = Environment.getStorageState(new File(path));
                }
                long totalSize = 0;
                long availableSize = 0;
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    totalSize = SystemUtils.getStorageTotalSpace(path);
                    availableSize = SystemUtils.getStorageFreeSpace(path);
                }
                Storage storage = new Storage();
                storage.setAvailaleSize(availableSize);
                storage.setTotalSize(totalSize);
                storage.setState(state == null ? EnvironmentCompat.MEDIA_UNKNOWN : state);
                storage.setPath(path);
                storage.setRemovable(removable);
                storage.setDescription(desc);
                storage.setAllowMassStorage(isAllowMassStorage);
                storage.setPrimary(isPrimary);
                storage.setUsb(desc.toLowerCase(Locale.ENGLISH).contains("usb"));
                list.add(storage);
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 存储器是否被挂载
     */
    public static boolean isMounted(@NonNull Context context, @NonNull String path) {
        try {
            StorageManager sm = (StorageManager) context.getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
            Objects.requireNonNull(sm);
            String state = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, path);
            return Environment.MEDIA_MOUNTED.equals(state);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断apk是否是debug包
     */
    public static boolean isDebugApk(@NonNull Context context, @NonNull String apkPath) {
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        try {
            return (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断app是否运行在debug模式下
     */
    public static boolean isRunInDebug(@NonNull Context context) {
        try {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取Service的Meta值
     *
     * @param cls  Service的class
     * @param name meta名
     * @return 没有返回null
     */
    @Nullable
    public static String getServiceMetaValue(@NonNull Context context, @NonNull Class<?> cls, @NonNull String name) {
        try {
            ServiceInfo info = context.getPackageManager().getServiceInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Receiver的Meta值
     *
     * @param cls  Receiver的class
     * @param name meta名
     * @return 没有返回null
     */
    @Nullable
    public static String getReceiverMetaValue(@NonNull Context context, @NonNull Class<?> cls, @NonNull String name) {
        try {
            ActivityInfo info = context.getPackageManager().getReceiverInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Activity的Meta值
     *
     * @param cls  Activity的class
     * @param name meta名
     * @return 没有返回null
     */
    @Nullable
    public static String getActivityMetaValue(@NonNull Context context, @NonNull Class<?> cls, @NonNull String name) {
        try {
            ActivityInfo info = context.getPackageManager().getActivityInfo(new ComponentName(context, cls.getName()), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Application的Meta值
     *
     * @param name meta名
     * @return 没有返回null
     */
    @Nullable
    public static String getApplicationMetaValue(@NonNull Context context, @NonNull String name) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = info.metaData.get(name);
            return value == null ? null : value.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void goNotificationSetting(@NonNull Context context) {
        Intent intent = new Intent();
        //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        } else {
            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 获取IMEI
     */
    @SuppressLint("MissingPermission")
    @Nullable
    public static String getImei(@NonNull Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.getImei();
            } else {
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
