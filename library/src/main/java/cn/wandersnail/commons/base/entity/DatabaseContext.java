package cn.wandersnail.commons.base.entity;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Objects;

/**
 * date: 2019/8/6 12:49
 * author: zengfansheng
 */
public class DatabaseContext extends ContextWrapper {
    private File dbDir;
    
    /**
     * @param base 上下文
     * @param dbDir 数据库文件要存放的目录
     */
    public DatabaseContext(Context base, @NonNull File dbDir) {
        super(base);
        Objects.requireNonNull(dbDir, "dbDir is null");   
        this.dbDir = dbDir;
    }

    @Override
    public File getDatabasePath(String name) {
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }        
        return new File(dbDir, name);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return super.openOrCreateDatabase(getDatabasePath(name).getName(), mode, factory);
    }
}
