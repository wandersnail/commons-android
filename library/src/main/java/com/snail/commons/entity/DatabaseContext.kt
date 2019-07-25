package com.snail.commons.entity

import android.content.Context
import android.content.ContextWrapper
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase

import java.io.File
import java.io.IOException

/**
 * Created by zengfs on 2016/3/1.
 */
class DatabaseContext
/**
 * @param base 上下文
 * @param dbDir 数据库文件要存放的目录
 */
(base: Context, private val dbDir: File) : ContextWrapper(base) {

    /**
     * 获得数据库路径，如果不存在
     * @param name 数据库文件名
     */
    override fun getDatabasePath(name: String): File? {
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) return null
        }
        //数据库文件是否创建成功
        var isFileCreateSuccess = false
        //判断文件是否存在，不存在则创建该文件
        val dbFile = File(dbDir, name)
        if (!dbFile.exists()) {
            try {
                isFileCreateSuccess = dbFile.createNewFile() //创建文件
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            isFileCreateSuccess = true
        }

        //返回数据库文件对象
        return if (isFileCreateSuccess) dbFile else null
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     */
    override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name)!!, factory)
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     * @see ContextWrapper.openOrCreateDatabase
     */
    override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory?, errorHandler: DatabaseErrorHandler?): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name)!!, factory)
    }
}
