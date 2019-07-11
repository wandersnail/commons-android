package com.snail.commons.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
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
            size < 1048576L -> decimalFormat.format((size / 1024f.toDouble())) + "KB"
            size < 1073741824L -> decimalFormat.format((size / 1048576.toDouble())) + "MB"
            size < 1099511627776L -> decimalFormat.format((size / 1073741824.toDouble())) + "GB"
            size < 1125899906842624L -> decimalFormat.format((size / 1099511627776.toDouble())) + "TB"
            size < 1152921504606846976L -> decimalFormat.format((size / 1125899906842624f.toDouble())) + "PB"
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

    /**
     * 根据路径获取MimeType
     */
    @JvmStatic
    fun getMimeType(filePath: String): String {
        val mmr = MediaMetadataRetriever()
        var mime = "*/*"
        try {
            mmr.setDataSource(filePath)
            mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        } catch (e: Exception) {
            return mime
        }
        return mime
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
        !exists() -> 0
        isFile -> length()
        else -> {
            var s: Long = 0
            val files = listFiles()
            if (files != null) {
                for (file in files) {
                    s += when {
                        file.isDirectory -> file.size()
                        file.isFile -> file.length()
                        else -> 0
                    }
                }
                s
            } else 0
        }
    }
}

/**
 * 移动文件或文件夹
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
@JvmOverloads
fun File.compressByGZIP(target: File, bufferSize: Int = 10240) {
    FileInputStream(this).use {
        GZIPOutputStream(FileOutputStream(target)).use { out ->
            val buf = ByteArray(bufferSize)
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
@JvmOverloads
fun InputStream.toFile(targetFile: File, bufferSize: Int = 10240) {
    BufferedInputStream(this).use {
        BufferedOutputStream(FileOutputStream(targetFile)).use { out ->
            // 缓冲数组   
            val b = ByteArray(bufferSize)
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

/*
 * 快速复制文件
 * @param source 源文件
 * @param target 目标文件
 */
private fun copyFile(source: File, target: File) {
    FileInputStream(source).channel.use {
        FileOutputStream(target).channel.use { out -> 
            var position = 0L
            var size = it.size()
            while (size > 0) {
                val count = it.transferTo(position, size, out)
                if (count > 0) {
                    position += count
                    size -= count
                }
            }            
        }
    }
}

/*
 * 复制文件夹
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

/**
 * 兼容Android7.0以上，获取Intent传递的File的Uri
 */
fun File.toUri(context: Context): Uri {
    // 判断版本大于等于7.0
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", this)
    } else {
        Uri.fromFile(this)
    }
}

fun File.setIntentDataAndType(context: Context, intent: Intent, type: String, writeable: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.setDataAndType(toUri(context), type)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (writeable) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
    } else {
        intent.setDataAndType(Uri.fromFile(this), type)
    }
}

/**
 * 获取文件真实路径
 */
fun Context.getFileRealPath(uri: Uri): String? = getRealPathFromUri(this, uri)

/**
 * 获取文件真实路径
 *
 * @param path 可能是content://或file://或真实路径
 */
fun Context.getFileRealPath(path: String): String? = getRealPathFromUri(this, Uri.parse(path))

private fun getRealPathFromUri(context: Context, uri: Uri): String? {
    // DocumentProvider
    try {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    //只处理主存储，外置的可能性太多
                    if ("primary".equals(type, ignoreCase = true)) {
                        return "${Environment.getExternalStorageDirectory().absolutePath}/${split[1]}"
                    }
                }
                isDownloadsDocument(uri) -> { // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.removePrefix("raw:")
                    }
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
                    return getDataColumn(context, contentUri, null, null)
                }
                isMediaDocument(uri) -> { // MediaProvider
                    val split = DocumentsContract.getDocumentId(uri).split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    return getDataColumn(context, contentUri, BaseColumns._ID + "=?", arrayOf(split[1]))
                }
            }
        } else return if ("content".equals(uri.scheme!!, ignoreCase = true)) { // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) { // File
            uri.path
        } else {
            getRealPathFromURIDB(context, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * Gets real path from uri.
 *
 * @param uri the content uri
 * @return the real path from uri
 */
private fun getRealPathFromURIDB(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return if (cursor == null) {
        uri.path
    } else {
        cursor.moveToFirst()
        val index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
        val realPath = cursor.getString(index)
        cursor.close()
        realPath
    }
}

/**
 * Gets items column.
 *
 * @param uri           the uri
 * @param selection     the selection
 * @param selectionArgs the selection args
 * @return the items column
 */
private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
    cursor?.use { c ->
        if (c.moveToFirst()) {
            val index = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val string = c.getString(index)
            c.close()
            return string
        }
    }
    cursor?.close()
    return null
}

/**
 * Is external storage document boolean.
 *
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * Is downloads document boolean.
 *
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * Is media document boolean.
 *
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * Is google photos uri boolean.
 *
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

/**
 * 根据文件获取content uri
 * @param context 上下文
 * @param baseUri 父uri
 * @param file 文件
 */
private fun getContentUri(context: Context, baseUri: Uri, file: File): Uri? {
    val filePath = file.absolutePath
    val cursor = context.contentResolver.query(baseUri, arrayOf(BaseColumns._ID),
        MediaStore.MediaColumns.DATA + "=? ", arrayOf(filePath), null)
    return if (cursor != null && cursor.moveToFirst()) {
        val id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
        cursor.close()
        Uri.withAppendedPath(baseUri, "" + id)
    } else {
        if (file.exists()) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DATA, filePath)
            context.contentResolver.insert(baseUri, values)
        } else {
            null
        }
    }
}

/**
 * 获取视频文件的content uri
 */
fun File.getVideoContentUri(context: Context): Uri? {
    return getContentUri(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this)
}

/**
 * 获取图片文件的content uri
 */
fun File.getImageContentUri(context: Context): Uri? {
    return getContentUri(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this)
}

/**
 * 获取音频文件的content uri
 */
fun File.getAudioContentUri(context: Context): Uri? {
    return getContentUri(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this)
}