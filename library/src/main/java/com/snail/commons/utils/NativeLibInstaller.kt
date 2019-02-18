package com.snail.commons.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException

/**
 * 描述: so库动态加载工具类
 * 时间: 2018/12/10 11:17
 * 作者: zengfansheng
 */
object NativeLibInstaller {

    /**
     * fuck部分机型删了该成员属性，兼容
     *
     * @return 被厂家删了返回1，否则正常读取
     */
    private val previousSdkInt: Int
        @RequiresApi(Build.VERSION_CODES.M)
        get() {
            try {
                return Build.VERSION.PREVIEW_SDK_INT
            } catch (ignore: Throwable) {}
            return 1
        }

    /**
     * 将包含so文件的目录添加到系统可加载列表中
     * @param classLoader
     * @param folder
     * @throws Throwable
     */
    @Synchronized
    @Throws(Throwable::class)
    fun installLibrary(classLoader: ClassLoader, folder: File?) {
        if (folder == null || !folder.exists()) {
            return
        }
        // android o sdk_int 26
        // for android o preview sdk_int 25
        if (Build.VERSION.SDK_INT == 25 && previousSdkInt != 0 || Build.VERSION.SDK_INT > 25) {
            try {
                V25.install(classLoader, folder)
            } catch (throwable: Throwable) {
                // install fail, try to treat it as v23
                // some preview N version may go here
                V23.install(classLoader, folder)
            }

        } else if (Build.VERSION.SDK_INT >= 23) {
            try {
                V23.install(classLoader, folder)
            } catch (throwable: Throwable) {
                // install fail, try to treat it as v14
                V14.install(classLoader, folder)
            }
        }
    }

    private object V14 {
        @Throws(Throwable::class)
        internal fun install(classLoader: ClassLoader, folder: File) {
            val pathListField = ShareReflectUtil.findField(classLoader, "pathList")
            val dexPathList = pathListField.get(classLoader)
            ShareReflectUtil.expandFieldArray(dexPathList, "nativeLibraryDirectories", arrayOf(folder))
        }
    }

    private object V23 {
        @Throws(Throwable::class)
        internal fun install(classLoader: ClassLoader, folder: File) {
            val pathListField = ShareReflectUtil.findField(classLoader, "pathList")
            val dexPathList = pathListField.get(classLoader)
            val nativeLibraryDirectories = ShareReflectUtil.findField(dexPathList, "nativeLibraryDirectories")
            val libDirs = nativeLibraryDirectories.get(dexPathList) as MutableList<File>
            libDirs.add(0, folder)
            val systemNativeLibraryDirectories = ShareReflectUtil.findField(dexPathList, "systemNativeLibraryDirectories")
            val systemLibDirs = systemNativeLibraryDirectories.get(dexPathList) as MutableList<File>
            val makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", List::class.java, File::class.java, List::class.java)
            val suppressedExceptions = ArrayList<IOException>()
            libDirs.addAll(systemLibDirs)
            val elements = makePathElements.invoke(dexPathList, libDirs, null, suppressedExceptions) as Array<Any>
            val nativeLibraryPathElements = ShareReflectUtil.findField(dexPathList, "nativeLibraryPathElements")
            nativeLibraryPathElements.isAccessible = true
            nativeLibraryPathElements.set(dexPathList, elements)
        }
    }

    private object V25 {
        @Throws(Throwable::class)
        internal fun install(classLoader: ClassLoader, folder: File) {
            val pathListField = ShareReflectUtil.findField(classLoader, "pathList")
            val dexPathList = pathListField.get(classLoader)
            val nativeLibraryDirectories = ShareReflectUtil.findField(dexPathList, "nativeLibraryDirectories")
            val libDirs = nativeLibraryDirectories.get(dexPathList) as MutableList<File>
            libDirs.add(0, folder)
            val systemNativeLibraryDirectories = ShareReflectUtil.findField(dexPathList, "systemNativeLibraryDirectories")
            val systemLibDirs = systemNativeLibraryDirectories.get(dexPathList) as MutableList<File>
            val makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", List::class.java)
            libDirs.addAll(systemLibDirs)
            val elements = makePathElements.invoke(dexPathList, libDirs) as Array<Any>
            val nativeLibraryPathElements = ShareReflectUtil.findField(dexPathList, "nativeLibraryPathElements")
            nativeLibraryPathElements.isAccessible = true
            nativeLibraryPathElements.set(dexPathList, elements)
        }
    }
}
