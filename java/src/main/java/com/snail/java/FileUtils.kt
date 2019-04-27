package com.snail.java

import java.io.*
import java.text.DecimalFormat
import java.util.*
import java.util.zip.GZIPOutputStream

object FileUtils {

    /**
     * 格式化文件大小，根据文件大小不同使用不同单位
     *
     * @param size 文件大小
     * @return 字符串形式的大小，包含单位(B,KB,MB,GB,TB,PB)
     */
    @JvmOverloads
    @JvmStatic 
    fun formatFileSize(size: Long, format: DecimalFormat? = null): String {
        var decimalFormat = format
        if (decimalFormat == null) {
            decimalFormat = DecimalFormat("#.00")
        }
        return when {
            size < 1024L -> size.toString() + "B"
            size < 1048576L -> decimalFormat.format((size / 1024f).toDouble()) + "KB"
            size < 1073741824L -> decimalFormat.format((size / 1048576f).toDouble()) + "MB"
            size < 1099511627776L -> decimalFormat.format((size / 1073741824f).toDouble()) + "GB"
            size < 1125899906842624L -> decimalFormat.format((size / 1099511627776f).toDouble()) + "TB"
            size < 1152921504606846976L -> decimalFormat.format((size / 1125899906842624f).toDouble()) + "PB"
            else -> "size: out of range"
        }
    }

    /**
     * 从路径中获取文件名，包含扩展名
     *
     * @param path 路径
     * @return 如果所传参数是合法路径，截取文件名，如果不是返回原值
     */
    @JvmStatic 
    fun getFileName(path: String): String {
        if ((path.contains("/") || path.contains("\\"))) {
            var fileName = path.trim { it <= ' ' }
            var beginIndex = fileName.lastIndexOf("\\")            
            if (beginIndex != -1) {
                fileName = fileName.substring(beginIndex + 1)
            }
            beginIndex = fileName.lastIndexOf("/")
            if (beginIndex != -1) {
                fileName = fileName.substring(beginIndex + 1)
            }
            return fileName
        }
        return path
    }

    /**
     * 从路径中获取文件名，不包含扩展名
     *
     * @param path 路径
     * @return 如果所传参数是合法路径，截取文件名，如果不是返回原值
     */
    @JvmStatic 
    fun getFileNameWithoutSuffix(path: String): String {
        if ((path.contains("/") || path.contains("\\"))) {
            var fileName = path.trim { it <= ' ' }
            var beginIndex = fileName.lastIndexOf("\\")
            if (beginIndex != -1) {
                fileName = fileName.substring(beginIndex + 1)
            }
            beginIndex = fileName.lastIndexOf("/")
            if (beginIndex != -1) {
                fileName = fileName.substring(beginIndex + 1)
            }
            return deleteSuffix(fileName)
        }
        return deleteSuffix(path)
    }

    /**
     * 获取扩展名
     *
     * @param s 路径或后缀
     * @return 不存在后缀时返回null
     */
    @JvmStatic 
    fun getSuffix(s: String): String {
        return if (s.contains(".")) {
            s.substring(s.lastIndexOf("."))
        } else ""
    }

    /**
     * 返回去掉扩展名的文件名
     */
    @JvmStatic 
    fun deleteSuffix(fileName: String): String {
        var filename = fileName
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."))
        }
        return filename
    }

    /**
     * 检查是否有同名文件，有则在自动在文件名后加当前时间的毫秒值
     */
    @JvmStatic 
    fun checkAndRename(target: File): File {
        if (target.exists()) {
            var fileName = target.name
            fileName = if (fileName.contains(".")) {
                val sub = fileName.substring(0, fileName.lastIndexOf("."))
                fileName.replace(sub, sub + "_" + System.currentTimeMillis())
            } else {
                fileName + "_" + System.currentTimeMillis()
            }
            return File(target.parent, fileName)
        }
        return target
    }

    /**
     * 去掉字符串中重复部分字符串
     *
     * @param dup  重复部分字符串
     * @param strs 要去重的字符串
     * @return 按参数先后顺序返回一个字符串数组
     */
    @JvmStatic 
    fun removeDuplicate(dup: String, vararg strs: String): Array<String> {
        val out = Array(strs.size) {""}
        for (i in strs.indices) {
            out[i] = strs[i].replace("$dup+".toRegex(), "")
        }
        return out
    }

    /**
     * 获取随机UUID文件名
     *
     * @param fileName 原文件名
     * @return 生成的文件名
     */
    @JvmStatic 
    fun generateRandonFileName(fileName: String): String {
        // 获得扩展名
        val beginIndex = fileName.lastIndexOf(".")
        var ext = ""
        if (beginIndex != -1) {
            ext = fileName.substring(beginIndex)
        }
        return UUID.randomUUID().toString() + ext
    }

    /**
     * 复制文件或文件夹，不适合Android
     *
     * @param src    源文件或文件夹
     * @param target 目标文件或文件夹
     */
    @JvmStatic 
    fun copy(src: String, target: String) {
        File(src).copyTo(File(target))
    }
}

/*############################################ 扩展函数 #########################################*/

/**
 * 删除文件夹内所有文件
 */
fun File.clear() {
    val files = listFiles()
    if (files != null) {
        for (f in files) {
            if (f.isDirectory) {
                deleteRecursively()
            } else {
                f.delete()
            }
        }
    }
}

/**
 * 获取文件夹的大小
 *
 * @return 所传参数是目录且存在，则返回文件夹大小，否则返回-1
 */
fun File.size(): Long {
    return when {
        !exists() -> -1
        isFile -> length()
        else -> {
            var s: Long = 0
            val files = listFiles()
            if (files != null) {
                for (file in files) {
                    s += if (file.isDirectory) {
                        size()
                    } else {
                        file.length()
                    }
                }
                s
            } else 0
        }
    }
}

/**
 * 移动文件或文件夹，不适用于Android
 *
 * @param target  目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
 * @param replace 当有重名文件时是否替换。传false时，自动在原文件名后加上当前时间的毫秒值
 * @return 移动成功返回true, 否则返回false
 */
fun File.moveTo(target: File, replace: Boolean): Boolean {
    var targetFile = target
    if (!exists()) {
        return false
    }
    if (!replace) {
        targetFile = FileUtils.checkAndRename(targetFile)
    }
    copyTo(targetFile)
    return compareAndDeleteSrc(this, targetFile)
}

/*
 * 比较大小并删除源
 */
private fun compareAndDeleteSrc(src: File, target: File): Boolean {
    //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件或文件夹
    if (src.isFile) {
        if (target.exists() && target.length() == src.length()) {
            src.delete()
            return true
        }
    } else {
        if (src.size() == target.size()) {
            src.deleteRecursively()
            return true
        }
    }
    return false
}

/**
 * 移动文件或文件夹，适用于Android
 *
 * @param target  目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
 * @param replace 当有重名文件时是否替换。传false时，自动在原文件名后加上当前时间的毫秒值
 * @return 移动成功返回true, 否则返回false
 */
fun File.moveToForAndroid(target: File, replace: Boolean): Boolean {
    var targetFile = target
    if (!exists()) {
        return false
    }
    if (!replace) {
        targetFile = FileUtils.checkAndRename(targetFile)
    }
    copyToForAndroid(targetFile)

    //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件
    return compareAndDeleteSrc(this, targetFile)
}

/**
 * 使用GZIP压缩数据
 */
fun ByteArray.compressByGZIP(): ByteArray? {
    val baos = ByteArrayOutputStream()
    GZIPOutputStream(baos).use {
        it.write(this)
        it.finish()
        it.flush()
        return baos.toByteArray()
    }
}

/**
 * 使用GZIP压缩文件
 *
 * @param target 目标文件
 */
fun File.compressByGZIP(target: File) {
    FileInputStream(this).use {
        GZIPOutputStream(FileOutputStream(target)).use { out ->
            val buf = ByteArray(1024)
            var num = it.read(buf)
            while (num != -1) {
                out.write(buf, 0, num)
                num = it.read(buf)
            }
        }
    }
}

/**
 * 复制文件或文件夹，不适用于Android
 *
 * @param target 目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
 */
fun File.copyTo(target: File) {
    if (isFile) {
        copyFile(this, target)
    } else {
        copyDir(this, target)
    }
}

/**
 * 从流保存到文件
 *
 * @param targetFile  目标文件
 */
fun InputStream.toFile(targetFile: File) {
    BufferedInputStream(this).use {
        BufferedOutputStream(FileOutputStream(targetFile)).use { out ->
            // 缓冲数组   
            val b = ByteArray(1024 * 5)
            var len = it.read(b)
            while (len != -1) {
                out.write(b, 0, len)
                len = it.read(b)
            }
            // 刷新此缓冲的输出流   
            out.flush()
        }
    }
}

/**
 * 适用于Android平台的文件复制
 */
private fun copyFileForAndroid(src: File, target: File) {
    try {
        FileInputStream(src).toFile(target)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}

/**
 * 复制文件或文件夹，适用于Android
 *
 * @param target 目标文件或文件夹
 */
fun File.copyToForAndroid(target: File) {
    if (isFile) {
        copyFileForAndroid(this, target)
    } else {
        copyDirForAndroid(this, target)
    }
}

/*
 * 快速复制文件，不适用于Android
 * @param source 源文件
 * @param target 目标文件
 */
private fun copyFile(source: File, target: File) {
    FileInputStream(source).channel.use {
        FileOutputStream(target).channel.use { out -> 
            it.transferTo(0, it.size(), out)
        }
    }
}

/*
 * 复制文件夹，不适用于Android
 * @param sourceDir 源文件夹
 * @param targetDir 目标文件夹
 */
private fun copyDir(sourceDir: File, targetDir: File) {
    //目标目录新建源文件夹
    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }
    // 获取源文件夹当前下的文件或目录   
    val files = sourceDir.listFiles()
    for (file in files) {
        if (file.isFile) {
            copyFile(file, File(targetDir, file.name))
        } else {
            copyDir(file, File(targetDir, file.name))
        }
    }
}

/*
 * 复制文件夹，适用于Android
 * @param sourceDir 源文件夹
 * @param targetDir 目标文件夹
 */
private fun copyDirForAndroid(sourceDir: File, targetDir: File) {
    //目标目录新建源文件夹
    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }
    // 获取源文件夹当前下的文件或目录   
    val files = sourceDir.listFiles()
    for (file in files) {
        if (file.isFile) {
            copyFileForAndroid(file, File(targetDir, file.name))
        } else {
            copyDirForAndroid(file, File(targetDir, file.name))
        }
    }
}