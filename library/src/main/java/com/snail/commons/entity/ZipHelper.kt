package com.snail.commons.entity

import android.text.TextUtils
import com.snail.commons.AppHolder
import com.snail.commons.annotation.RunThread
import com.snail.commons.annotation.ThreadType
import com.snail.commons.interfaces.Callback
import com.snail.commons.utils.FileUtils
import com.snail.commons.utils.IOUtils
import java.io.*
import java.lang.reflect.Method
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 描述: 解压缩
 * 时间: 2018/12/10 21:55
 * 作者: zengfansheng
 */
object ZipHelper {

    fun zip(): ZipExecutor {
        return ZipExecutor()
    }

    fun unzip(): UnzipExecutor {
        return UnzipExecutor()
    }

    private fun <T> handleCallback(callback: Callback<T>?, cls: Class<T>?, obj: T?) {
        if (callback != null) {
            try {
                val method: Method = if (obj == null) {
                    callback.javaClass.getMethod("onCallback", Any::class.java)
                } else {
                    callback.javaClass.getMethod("onCallback", cls)
                }
                val annotation = method.getAnnotation(RunThread::class.java)
                if (annotation != null && annotation.value === ThreadType.MAIN) {
                    AppHolder.postToMainThread { callback.onCallback(obj) }
                } else {
                    callback.onCallback(obj)
                }
            } catch (e: Exception) {
                callback.onCallback(obj)
            }

        }
    }

    class ZipExecutor internal constructor() {
        private var comment: String? = null
        private var method = -1
        private var level = -1
        private val files = ArrayList<File>()
        private var targetDir: String? = null
        private var targetName: String? = null
        private var replace: Boolean = false

        fun setComment(comment: String): ZipExecutor {
            this.comment = comment
            return this
        }

        /**
         * 压缩类型
         *
         * @param method [ZipEntry.STORED], [ZipEntry.DEFLATED]
         */
        fun setMethod(method: Int): ZipExecutor {
            if (method == ZipEntry.STORED || method == ZipEntry.DEFLATED) {
                this.method = method
            }
            return this
        }

        /**
         * 压缩级别
         *
         * @param level 0~9
         */
        fun setLevel(level: Int): ZipExecutor {
            if (level > 9) {
                this.level = 9
            } else {
                this.level = level
            }
            return this
        }

        /**
         * 添加待压缩文件
         */
        fun addSourceFile(file: File): ZipExecutor {
            if (file.exists() && !files.contains(file)) {
                files.add(file)
            }
            return this
        }

        /**
         * 添加待压缩文件
         */
        fun addSourceFiles(files: List<File>): ZipExecutor {
            if (!files.isEmpty()) {
                for (file in files) {
                    addSourceFile(file)
                }
            }
            return this
        }

        /**
         * 压缩包保存路径
         * @param dir 保存目录
         * @param filename 保存的文件名，不含后缀
         */
        fun setTarget(dir: String, filename: String): ZipExecutor {
            targetDir = dir
            targetName = filename
            return this
        }

        /**
         * 目标路径下已存在同名压缩包，是否替换
         */
        fun setReplace(replace: Boolean): ZipExecutor {
            this.replace = replace
            return this
        }

        /**
         * 执行压缩，同步的
         */
        fun execute(): File? {
            if (files.isEmpty()) {
                return null
            } else {
                var zipFile: File
                val f = files[0]
                zipFile = if (targetDir == null) {
                    File(f.parent, (if (targetName == null) f.parentFile.name else targetName) + ".zip")
                } else {
                    File(targetDir, (if (targetName == null) f.parentFile.name else targetName) + ".zip")
                }
                val zipParentFile = zipFile.parentFile
                if (!zipParentFile.exists()) {
                    zipParentFile.mkdirs()
                }
                var zos: ZipOutputStream? = null
                try {
                    var first = true
                    for (file in files) {
                        //如果已存在同名压缩包
                        if (first && zipFile.exists()) {
                            if (replace) {
                                if (!zipFile.delete()) {
                                    return null
                                }
                            } else {
                                val path = zipFile.absolutePath
                                val newName = FileUtils.getFileNameWithoutSuffix(path) + System.currentTimeMillis() + FileUtils.getSuffix(path)
                                zipFile = File(zipFile.parent, newName)
                            }
                        }
                        if (zos == null) {
                            zos = ZipOutputStream(FileOutputStream(zipFile))
                            if (level > 0) {
                                zos.setLevel(level)
                            }
                            if (!TextUtils.isEmpty(comment)) {
                                zos.setComment(comment)
                            }
                            if (method > 0) {
                                zos.setMethod(method)
                            }
                        }
                        addEntry("", file, zos)
                        first = false
                    }
                    return zipFile
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                } finally {
                    IOUtils.closeQuietly(zos)
                }
            }
        }

        /**
         * 执行压缩，异步的
         */
        fun execute(callback: Callback<File>) {
            Thread(Runnable { 
                val file = execute()
                handleCallback(callback, file?.javaClass, file) }
            ).start()
        }

        /**
         * 扫描添加文件Entry
         *
         * @param base   基路径
         * @param source 源文件
         * @param zos    Zip文件输出流
         */
        @Throws(IOException::class)
        private fun addEntry(base: String, source: File, zos: ZipOutputStream?) {
            var basePath = base
            // 按目录分级，形如：/aaa/bbb.txt
            basePath += source.name
            if (source.isDirectory) {
                val files = source.listFiles()
                if (files != null && files.isNotEmpty()) {
                    for (file in files) {
                        // 递归列出目录下的所有文件，添加文件Entry
                        addEntry("$basePath/", file, zos)
                    }
                } else {
                    zos!!.putNextEntry(ZipEntry("$basePath/"))
                }
            } else {
                var bis: BufferedInputStream? = null
                try {
                    zos!!.putNextEntry(ZipEntry(basePath))
                    val buffer = ByteArray(1024 * 10)
                    bis = BufferedInputStream(FileInputStream(source), buffer.size)
                    var read = bis.read(buffer, 0, buffer.size)
                    while (read != -1) {
                        zos.write(buffer, 0, read)
                        read = bis.read(buffer, 0, buffer.size)
                    }
                    zos.closeEntry()
                } finally {
                    IOUtils.closeQuietly(bis)
                }
            }
        }
    }

    class UnzipExecutor internal constructor() {
        private val zipFiles = ArrayList<File>()
        private var targetDir: String? = null

        fun addZipFile(zipFile: File): UnzipExecutor {
            if (zipFile.exists() && !zipFiles.contains(zipFile)) {
                zipFiles.add(zipFile)
            }
            return this
        }

        fun addZipFiles(zipFiles: List<File>): UnzipExecutor {
            if (!zipFiles.isEmpty()) {
                for (file in zipFiles) {
                    addZipFile(file)
                }
            }
            return this
        }

        fun setTargetDir(targetDir: String): UnzipExecutor {
            this.targetDir = targetDir
            return this
        }

        /**
         * 执行解压，同步的
         */
        fun execute(): Boolean {
            if (zipFiles.isEmpty()) {
                return false
            } else {
                for (source in zipFiles) {
                    var zis: ZipInputStream? = null
                    var bos: BufferedOutputStream? = null
                    try {
                        zis = ZipInputStream(FileInputStream(source))
                        var entry = zis.nextEntry
                        while (entry != null) {
                            val target: File = if (targetDir == null) {
                                File(source.parent, entry.name)
                            } else {
                                File(targetDir, entry.name)
                            }
                            if (!target.parentFile.exists()) {
                                // 创建文件父目录
                                if (!target.parentFile.mkdirs()) {
                                    return false
                                }
                            }
                            if (!entry.isDirectory) {
                                // 写入文件
                                bos = BufferedOutputStream(FileOutputStream(target))
                                val buffer = ByteArray(1024 * 10)
                                var read = zis.read(buffer, 0, buffer.size)                                
                                while (read != -1) {
                                    bos.write(buffer, 0, read)
                                    read = zis.read(buffer, 0, buffer.size)
                                }
                                bos.flush()
                            }
                            entry = zis.nextEntry
                        }
                        zis.closeEntry()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return false
                    } finally {
                        IOUtils.closeQuietly(zis, bos)
                    }
                }
                return true
            }
        }

        /**
         * 执行解压，异步的
         */
        fun execute(callback: Callback<Boolean>) {
            Thread(Runnable { 
                val result = execute()
                handleCallback(callback, result.javaClass, result) 
            }).start()
        }
    }
}
