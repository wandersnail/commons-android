package com.snail.commons.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.*
import java.nio.channels.FileChannel
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
    fun getSuffix(s: String): String {
        return if (s.contains(".")) {
            s.substring(s.lastIndexOf("."))
        } else ""
    }

    /**
     * 返回去掉扩展名的文件名
     */
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
     * 移动文件或文件夹
     *
     * @param src     要移动的文件或文件夹
     * @param target  目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param replace 当有重名文件时是否替换。传false时，自动在原文件名后加上当前时间的毫秒值
     * @return 移动成功返回true, 否则返回false
     */
    fun moveFile(src: File, target: File, replace: Boolean): Boolean {
        var targetFile = target
        if (!src.exists()) {
            return false
        }
        if (!replace) {
            targetFile = checkAndRename(targetFile)
        }
        copy(src, targetFile)
        return compareAndDeleteSrc(src, targetFile)
    }

    private fun compareAndDeleteSrc(src: File, target: File): Boolean {
        //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件
        if (src.isFile) {
            if (target.exists() && target.length() == src.length()) {
                src.delete()
                return true
            }
        } else {
            if (getDirSize(src) == getDirSize(target)) {
                deleteDir(src, true)
                return true
            }
        }
        return false
    }

    /**
     * 移动文件或文件夹
     *
     * @param src     要移动的文件或文件夹
     * @param target  目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     * @param replace 当有重名文件时是否替换。传false时，自动在原文件名后加上当前时间的毫秒值
     * @return 移动成功返回true, 否则返回false
     */
    fun moveFileFit(src: File, target: File, replace: Boolean): Boolean {
        var targetFile = target
        if (!src.exists()) {
            return false
        }
        if (!replace) {
            targetFile = checkAndRename(targetFile)
        }
        copyFit(src, targetFile)

        //如果文件存在，并且大小与源文件相等，则写入成功，删除源文件
        return compareAndDeleteSrc(src, targetFile)
    }

    /**
     * 去掉字符串中重复部分字符串
     *
     * @param dup  重复部分字符串
     * @param strs 要去重的字符串
     * @return 按参数先后顺序返回一个字符串数组
     */
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
     * 删除文件夹
     *
     * @param dir         文件夹
     * @param includeSelf 是否包括本身
     */
    fun deleteDir(dir: File, includeSelf: Boolean) {
        val files = dir.listFiles()
        if (files != null) {
            for (f in files) {
                if (f.isDirectory) {
                    deleteDir(f, true)
                } else {
                    f.delete()
                }
            }
        }
        if (includeSelf)
            dir.delete()
    }


    /**
     * 删除文件内所有文件，不包含文件夹
     *
     * @param dir 文件夹
     */
    fun deleteAllFiles(dir: File) {
        val files = dir.listFiles()
        if (files != null) {
            for (f in files) {
                if (f.isDirectory) {
                    deleteAllFiles(f)
                } else {
                    f.delete()
                }
            }
        }
    }

    /**
     * 获取文件夹的大小
     *
     * @param dir 目录
     * @return 所传参数是目录且存在，则返回文件夹大小，否则返回-1
     */
    fun getDirSize(dir: File): Long {
        if (dir.exists() && dir.isDirectory) {
            var size: Long = 0
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        getDirSize(file)
                    } else {
                        file.length()
                    }
                }
                return size
            }
            return 0
        }
        return -1
    }

    /**
     * 压缩数据
     */
    fun compress(data: ByteArray): ByteArray? {
        var gzip: GZIPOutputStream? = null
        var baos: ByteArrayOutputStream? = null
        var newData: ByteArray? = null
        try {
            baos = ByteArrayOutputStream()
            gzip = GZIPOutputStream(baos)
            gzip.write(data)
            gzip.finish()
            gzip.flush()
            newData = baos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                IOUtils.close(gzip, baos)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return newData
    }

    /**
     * 压缩文件
     *
     * @param source 源文件
     * @param target 目标文件
     */
    fun compressFile(source: File, target: File) {
        var fin: FileInputStream? = null
        var fout: FileOutputStream? = null
        var gzout: GZIPOutputStream? = null
        try {
            fin = FileInputStream(source)
            fout = FileOutputStream(target)
            gzout = GZIPOutputStream(fout)
            val buf = ByteArray(1024)
            var num = fin.read(buf)
            while (num != -1) {
                gzout.write(buf, 0, num)
                num = fin.read(buf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                IOUtils.close(gzout, fout, fin)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
    }

    /*
     * 快速复制文件，不适合Android
     * @param source 源文件
     * @param target 目标文件
     */
    private fun nioCopyFile(source: File, target: File) {
        var fileChannel: FileChannel? = null
        var out: FileChannel? = null
        var inStream: FileInputStream? = null
        var outStream: FileOutputStream? = null
        try {
            inStream = FileInputStream(source)
            outStream = FileOutputStream(target)
            fileChannel = inStream.channel
            out = outStream.channel
            fileChannel!!.transferTo(0, fileChannel.size(), out)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                IOUtils.close(fileChannel, out, inStream, outStream)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
        }
    }

    /*
     * 复制文件夹，不适合Android
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
                nioCopyFile(file, File(targetDir, file.name))
            } else {
                copyDir(file, File(targetDir, file.name))
            }
        }
    }

    /**
     * 复制文件或文件夹，不适合Android
     *
     * @param src    源文件或文件夹
     * @param target 目标文件或文件夹
     */
    fun copy(src: String, target: String) {
        copy(File(src), File(target))
    }

    /**
     * 复制文件或文件夹，不适合Android
     *
     * @param src    源文件或文件夹
     * @param target 目标文件或文件夹。类型需与源相同，如源为文件，则目标也必须是文件
     */
    fun copy(src: File, target: File) {
        if (src.isFile) {
            nioCopyFile(src, target)
        } else {
            copyDir(src, target)
        }
    }

    /**
     * 从流保存到文件
     *
     * @param inputStream 输入流
     * @param targetFile  目标文件
     */
    fun saveToFile(inputStream: InputStream, targetFile: File) {
        var out: BufferedOutputStream? = null
        var bufferedInputStream: BufferedInputStream? = null
        try {
            bufferedInputStream = BufferedInputStream(inputStream)
            out = BufferedOutputStream(FileOutputStream(targetFile))
            // 缓冲数组   
            val b = ByteArray(1024 * 5)
            var len = bufferedInputStream.read(b)
            while (len != -1) {
                out.write(b, 0, len)
                len = bufferedInputStream.read(b)
            }
            // 刷新此缓冲的输出流   
            out.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                IOUtils.close(bufferedInputStream, out)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 复制文件，适合Android平台
     */
    fun copyFile(srcFile: File, targetFile: File) {
        try {
            saveToFile(FileInputStream(srcFile), targetFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 复制文件或文件夹，适合Android
     *
     * @param src    源文件或文件夹
     * @param target 目标文件或文件夹
     */
    fun copyFit(src: File, target: File) {
        if (src.isFile) {
            copyFile(src, target)
        } else {
            copyDir(src, target)
        }
    }

    /**
     * 序列化对象到文件
     *
     * @param obj  要序列化的对象
     * @param file 保存到的文件
     */
    fun saveObjectToFile(obj: Serializable, file: File) {
        try {
            val oos = ObjectOutputStream(FileOutputStream(file))
            oos.writeObject(obj)
            oos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 从文件反序列化对象
     *
     * @param file 保存对象的文件
     */
    fun getObjectFromFile(file: File): Any? {
        try {
            val ois = ObjectInputStream(FileInputStream(file))
            val obj = ois.readObject()
            ois.close()
            return obj
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取文件真实路径
     */
    fun getFileRealPath(context: Context, uri: Uri): String? {
        return getRealPathFromUri(context, uri)
    }

    /**
     * 获取文件真实路径
     *
     * @param path 可能是content://或file://或真实路径
     */
    fun getFileRealPath(context: Context, path: String): String? {
        return getRealPathFromUri(context, Uri.parse(path))
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) { // MediaProvider
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
     * Gets data column.
     *
     * @param uri           the uri
     * @param selection     the selection
     * @param selectionArgs the selection args
     * @return the data column
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
}