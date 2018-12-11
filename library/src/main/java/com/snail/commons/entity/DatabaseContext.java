package com.snail.commons.entity;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by zengfs on 2016/3/1.
 */
public class DatabaseContext extends ContextWrapper {

	private File dbDir;

	/**
	 * @param base 上下文
	 * @param dbDir 数据库文件要存放的目录
	 */
	public DatabaseContext(@NonNull Context base, @NonNull File dbDir) {
		super(base);
		this.dbDir = dbDir;
	}

	/**
	 * 获得数据库路径，如果不存在
	 * @param name 数据库文件名
	 */
	@Override
	public File getDatabasePath(String name) {
		if(!dbDir.exists()) {
			if (!dbDir.mkdirs()) return null;
		}
		//数据库文件是否创建成功
		boolean isFileCreateSuccess = false;
		//判断文件是否存在，不存在则创建该文件
		File dbFile = new File(dbDir, name);
		if(!dbFile.exists()){
			try {
				isFileCreateSuccess = dbFile.createNewFile();//创建文件
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			isFileCreateSuccess = true;
		}

		//返回数据库文件对象
		if(isFileCreateSuccess)
			return dbFile;
		else
			return null;
	}

	/**
	 * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}

	/**
	 * Android 4.0会调用此方法获取数据库。
	 * @see ContextWrapper#openOrCreateDatabase(String, int,
	 *              SQLiteDatabase.CursorFactory,
	 *              DatabaseErrorHandler)
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}
}
