package com.snail.commons.utils

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.core.os.EnvironmentCompat
import com.snail.commons.entity.Storage
import java.io.*
import java.lang.reflect.Method
import java.util.*


/**
 * 安卓系统的工具类
 *
 * @author Zeng
 */
object SystemUtils {

    /**
     * 获取总内存大小，单位是byte
     */
    @JvmStatic
    val totalMemSize: Long
        get() {
            try {
                val br = BufferedReader(InputStreamReader(FileInputStream("/proc/meminfo")))
                val info = br.readLine().toCharArray()
                val sb = StringBuilder()
                for (c in info) {
                    if (c in '0'..'9') {
                        sb.append(c)
                    }
                }
                val kbSize = java.lang.Long.parseLong(sb.toString())
                br.close()
                return kbSize * 1024
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

    /**
     * 扩展卡是否可用
     */
    @JvmStatic
    val isSdCardAvailable: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    /**
     * 获取手机内部剩余存储空间
     *
     * @return 字节数
     */
    @JvmStatic
    val internalFreeSpace: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            return stat.availableBlocksLong * stat.blockSizeLong
        }

    /**
     * 获取扩展卡剩余存储空间
     *
     * @return 字节数。
     */
    @JvmStatic
    val externalFreeSpace: Long
        get() {
            return if (isSdCardAvailable) {
                val stat = StatFs(Environment.getExternalStorageDirectory().absolutePath)
                stat.availableBlocksLong * stat.blockSizeLong
            } else {
                0
            }
        }

    /**
     * 获取存储卡剩余大小
     */
    @JvmStatic
    fun getStorageFreeSpace(path: String): Long {
        if (File(path).exists()) {
            val stat = StatFs(path)
            return stat.availableBlocksLong * stat.blockSizeLong
        }
        return 0
    }

    /**
     * 存储卡总容量
     */
    @JvmStatic
    fun getStorageTotalSpace(path: String): Long {
        if (File(path).exists()) {
            val stat = StatFs(path)
            return stat.blockSizeLong * stat.blockCountLong
        }
        return 0
    }    

    @JvmStatic
    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }
}

/*############################################ 扩展函数 #########################################*/

/**
 * 判断当前系统是否安装指定的应用
 * @param packageName 要判断的应用包名
 */
fun Context.isAppInstalled(packageName: String): Boolean {
    // 获取所有已安装程序的包信息
    val pinfo = packageManager.getInstalledPackages(0)
    for (i in pinfo.indices) {
        if (pinfo[i].packageName == packageName) {
            return true
        }
    }
    return false
}

/**
 * 判断位置服务是否打开
 */
fun Context.isLocationEnabled(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager != null) {
            return locationManager.isLocationEnabled
        }
    } else {
        try {
            val locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
    }
    return false
}

/**
 * 判断GPS是否打开
 */
fun Context.isGPSEnabled(): Boolean {
    val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

/**
 * 判断屏幕是否亮着
 */
fun Context.isScreenOn(): Boolean {
    return (applicationContext.getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive == true
}

/**
 * 获取可用内存大小，单位byte
 */
fun Context.getAvailMemSize(): Long {
    val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val outInfo = ActivityManager.MemoryInfo()
    if (am == null) {
        return -1
    }
    am.getMemoryInfo(outInfo)
    return outInfo.availMem
}

/**
 * 获取正在运行的进程数
 */
fun Context.getRunningProcessCount(): Int {
    val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return -1
    return am.runningAppProcesses?.size ?: 0
}

/**
 * 获取所有存储路径
 */
fun Context.getStoragePaths(): List<String>? {
    return try {
        val sm = applicationContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val volumePaths = sm.javaClass.getMethod("getVolumePaths").invoke(sm) as Array<String>
        val pathList = ArrayList<String>()
        for (path in volumePaths) {
            if (SystemUtils.getStorageTotalSpace(path) > 0) {
                pathList.add(path)
            }
        }
        pathList
    } catch (e: Exception) {
        null
    }
}

/**
 * 获取设备存储信息
 */
fun Context.getStorages(): ArrayList<Storage>? {
    return try {
        val storageManager = applicationContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        //得到StorageManager中的getVolumeList()方法的对象
        val getVolumeList = storageManager.javaClass.getMethod("getVolumeList")
        //得到StorageVolume类的对象
        val storageValumeClazz = Class.forName("android.os.storage.StorageVolume")
        //获得StorageVolume中的一些方法
        val getPath = storageValumeClazz.getMethod("getPath")
        val isRemovable = storageValumeClazz.getMethod("isRemovable")
        val allowMassStorage = storageValumeClazz.getMethod("allowMassStorage")
        val primary = storageValumeClazz.getMethod("isPrimary")
        val description = storageValumeClazz.getMethod("getDescription", Context::class.java)

        var mGetState: Method? = null
        try {
            mGetState = storageValumeClazz.getMethod("getState")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        //调用getVolumeList方法，参数为：“谁”中调用这个方法
        val invokeVolumeList = getVolumeList.invoke(storageManager)
        val length = java.lang.reflect.Array.getLength(invokeVolumeList)
        val list = ArrayList<Storage>()
        for (i in 0 until length) {
            val storageValume = java.lang.reflect.Array.get(invokeVolumeList, i)//得到StorageVolume对象
            val path = getPath.invoke(storageValume) as? String ?: ""
            val removable = isRemovable.invoke(storageValume) as? Boolean ?: false
            val isAllowMassStorage = allowMassStorage.invoke(storageValume) as? Boolean ?: false
            val isPrimary = primary.invoke(storageValume) as? Boolean ?: false
            val desc = description.invoke(storageValume, this) as? String ?: ""
            val state = if (mGetState != null) {
                mGetState.invoke(storageValume) as? String
            } else {
                Environment.getStorageState(File(path))
            }
            var totalSize: Long = 0
            var availaleSize: Long = 0
            if (Environment.MEDIA_MOUNTED == state) {
                totalSize = SystemUtils.getStorageTotalSpace(path)
                availaleSize = SystemUtils.getStorageFreeSpace(path)
            }
            val storage = Storage()
            storage.availaleSize = availaleSize
            storage.totalSize = totalSize
            storage.state = state ?: EnvironmentCompat.MEDIA_UNKNOWN
            storage.path = path
            storage.isRemovable = removable
            storage.description = desc
            storage.isAllowMassStorage = isAllowMassStorage
            storage.isPrimary = isPrimary
            storage.isUsb = desc.toLowerCase(Locale.ENGLISH).contains("usb")
            list.add(storage)
        }
        list
    } catch (e: Exception) {
        null
    }
}

/**
 * 存储器是否被挂载
 */
fun Context.isMounted(path: String): Boolean {
    return try {
        val sm = applicationContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val state = sm.javaClass.getMethod("getVolumeState", String::class.java).invoke(sm, path) as String
        Environment.MEDIA_MOUNTED == state
    } catch (e: Exception) {
        false
    }
}

/**
 * 判断apk是否是debug包
 */
fun Context.isDebugApk(apkPath: String): Boolean {
    val info = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)
    return try {
        (info.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    } catch (e: Exception) {
        false
    }
}

/**
 * 判断app是否运行在debug模式下
 */
fun Context.isRunInDebug(): Boolean {
    return try {
        (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    } catch (e: Exception) {
        false
    }
}

/**
 * 获取Service的Meta值
 *
 * @param cls     Service的class
 * @param name    meta名
 * @return 没有返回null
 */
fun Context.getServiceMetaValue(cls: Class<*>, name: String): String? {
    return try {
        val info = packageManager.getServiceInfo(ComponentName(this, cls.name), PackageManager.GET_META_DATA)
        val value = info.metaData.get(name)
        value?.toString()
    } catch (e: Exception) {
        null
    }
}

/**
 * 获取Receiver的Meta值
 *
 * @param cls     Receiver的class
 * @param name    meta名
 * @return 没有返回null
 */
fun Context.getReceiverMetaValue(cls: Class<*>, name: String): String? {
    return try {
        val info = packageManager.getReceiverInfo(ComponentName(this, cls.name), PackageManager.GET_META_DATA)
        val value = info.metaData.get(name)
        value?.toString()
    } catch (e: Exception) {
        null
    }    
}

/**
 * 获取Activity的Meta值
 *
 * @param cls     Activity的class
 * @param name    meta名
 * @return 没有返回null
 */
fun Context.getActivityMetaValue(cls: Class<*>, name: String): String? {
    return try {
        val info = packageManager.getActivityInfo(ComponentName(this, cls.name), PackageManager.GET_META_DATA)
        val value = info.metaData.get(name)
        value?.toString()
    } catch (e: Exception) {
        null
    }
}

/**
 * 获取Application的Meta值
 *
 * @param name    meta名
 * @return 没有返回null
 */
fun Context.getApplicationMetaValue(name: String): String? {
    return try {
        val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val value = info.metaData.get(name)
        value?.toString()
    } catch (e: Exception) {
        null
    }
}