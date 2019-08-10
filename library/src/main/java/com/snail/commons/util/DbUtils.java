package com.snail.commons.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 数组库
 * date: 2019/8/6 22:01
 * author: zengfansheng
 */
public class DbUtils {
    //-----------升级数据库---------------
    public static final String INTEGER = "INTEGER";
    public static final String TEXT = "TEXT";
    public static final String REAL = "REAL";
    public static final String DATE = "DATE";

    //-----------增删改查-----------------
    public static final String EQ = "=?";
    public static final String NOT_EQ = "!=?";
    public static final String LIKE = " like ?";
    public static final String GT = ">?";
    public static final String LT = "<?";
    public static final String GE = ">=?";
    public static final String LE = "<=?";

    public static class Column {
        public String name;
        public String dataType;
        public Object defualtValue;

        public Column() {
        }

        public Column(String name, String dataType, Object defualtValue) {
            this.name = name;
            this.dataType = dataType;
            this.defualtValue = defualtValue;
        }
    }

    private static void execSQL(Object db, String sql) {
        try {
            db.getClass().getMethod("execSQL", String.class).invoke(db, sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execSQL(Object db, String sql, Object... bindArgs) {
        try {
            db.getClass().getMethod("execSQL", String.class, Object[].class).invoke(db, sql, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeVoidNoPrama(Object db, String methodName) {
        try {
            db.getClass().getMethod(methodName).invoke(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void beginTransaction(Object db) {
        invokeVoidNoPrama(db, "beginTransaction");
    }

    private static void endTransaction(Object db) {
        invokeVoidNoPrama(db, "endTransaction");
    }

    private static void setTransactionSuccessful(Object db) {
        invokeVoidNoPrama(db, "setTransactionSuccessful");
    }

    /**
     * 重命名表
     */
    public static void renameTable(Object db, String oldName, String newName) {
        execSQL(db, "ALTER TABLE " + oldName + " RENAME TO " + newName + "");
    }

    /**
     * 删除表
     */
    public static void deleteTable(Object db, String tableName) {
        execSQL(db, "DROP TABLE " + tableName + "");
    }

    /**
     * 获取数据表的所有字段名
     */
    public static Column[] getColumns(Object db, String tableName) {
        Column[] columns = null;
        Cursor cursor = null;
        try {
            String sql = "PRAGMA table_info(" + tableName + ")";
            Method method = db.getClass().getMethod("rawQuery", String.class, String[].class);
            cursor = (Cursor) method.invoke(db, sql, null);
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex == -1) {
                return null;
            }
            int index = 0;
            columns = new Column[cursor.getCount()];
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                columns[index] = new Column();
                columns[index].name = cursor.getString(nameIndex);
                columns[index].dataType = cursor.getString(cursor.getColumnIndex("type"));
                int defaultValueIndex = cursor.getColumnIndex("dflt_value");
                switch (cursor.getType(defaultValueIndex)) {
                    case Cursor.FIELD_TYPE_BLOB:
                        columns[index].defualtValue = cursor.getBlob(defaultValueIndex);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        columns[index].defualtValue = cursor.getDouble(defaultValueIndex);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        columns[index].defualtValue = cursor.getLong(defaultValueIndex);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        columns[index].defualtValue = cursor.getString(defaultValueIndex);
                        break;
                }
                index++;
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }

    /**
     * 更新数据表，删除列
     *
     * @param columnNames 要删除的列名
     */
    public static void deleteColumns(Object db, String tableName, String... columnNames) {
        try {
            beginTransaction(db);
            Column[] columns = getColumns(db, tableName);
            if (columns != null && columns.length > 0) {
                //重命名原表
                String tmpTableName = "t" + UUID.randomUUID().toString().replace("-", "_");
                execSQL(db, "ALTER TABLE " + tableName + " RENAME TO " + tmpTableName + "");
                //新建表sql语句
                StringBuilder newTableSql = new StringBuilder("CREATE TABLE " + tableName + "(");
                List<String> list = Arrays.asList(columnNames);
                //复制数据sql语句
                StringBuilder copySql = new StringBuilder("INSERT INTO " + tableName + " SELECT ");
                for (Column column : columns) {
                    if (!list.contains(column.name)) {
                        newTableSql.append(column.name).append(" ").append(column.dataType).append(",");
                        copySql.append(column.name).append(",");
                    }
                }
                newTableSql = new StringBuilder(newTableSql.substring(0, newTableSql.length() - 1) + ")");
                execSQL(db, newTableSql.toString());
                //复制数据到新表
                copySql = new StringBuilder(copySql.substring(0, copySql.length() - 1) + " FROM " + tmpTableName);
                execSQL(db, copySql.toString());
                //删除临时表
                execSQL(db, "DROP TABLE " + tmpTableName + "");
            }
            setTransactionSuccessful(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction(db);
        }
    }

    /**
     * 更新数据表，增加列
     *
     * @param columns 需要增加的列的信息
     */
    public static void addColumns(Object db, String tableName, Column... columns) {
        try {
            beginTransaction(db);
            for (Column column : columns) {
                String sql = "ALTER TABLE " + tableName + " ADD " + column.name + " " + column.dataType;
                Object[] bindArgs = null;
                if (column.defualtValue != null) {
                    if (column.defualtValue instanceof String) {
                        sql += " DEFAULT '" + column.defualtValue + "'";
                    } else if (column.defualtValue instanceof byte[]) {
                        //如果是字节数组，先插入列，再设置默认数据
                        execSQL(db, sql);
                        sql = "UPDATE " + tableName + " SET " + column.name + "=?";
                        bindArgs = new Object[]{column.defualtValue};
                    } else {
                        sql += " DEFAULT " + column.defualtValue;
                    }
                }
                if (bindArgs == null) {
                    execSQL(db, sql);
                } else {
                    execSQL(db, sql, bindArgs);
                }
            }
            setTransactionSuccessful(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction(db);
        }
    }

    /**
     * 更新列名
     *
     * @param map key为旧列名，value为新列名
     */
    public static void renameColumns(Object db, String tableName, Map<String, String> map) {
        try {
            beginTransaction(db);
            Column[] columns = getColumns(db, tableName);
            if (columns != null && columns.length > 0) {
                //重命名原表
                String tmpTableName = "t" + UUID.randomUUID().toString().replace("-", "_");
                execSQL(db, "ALTER TABLE " + tableName + " RENAME TO " + tmpTableName + "");
                //新建表sql语句
                StringBuilder newTableSql = new StringBuilder("CREATE TABLE " + tableName + "(");
                //复制数据sql语句
                StringBuilder copySql = new StringBuilder("INSERT INTO " + tableName + " SELECT ");
                Set<String> keySet = map.keySet();
                for (Column column : columns) {
                    if (keySet.contains(column.name)) {
                        newTableSql.append(map.get(column.name)).append(" ").append(column.dataType).append(",");
                    } else {
                        newTableSql.append(column.name).append(" ").append(column.dataType).append(",");
                    }
                    copySql.append(column.name).append(",");
                }
                newTableSql = new StringBuilder(newTableSql.substring(0, newTableSql.length() - 1) + ")");
                execSQL(db, newTableSql.toString());
                //复制数据到新表
                copySql = new StringBuilder(copySql.substring(0, copySql.length() - 1) + " FROM " + tmpTableName);
                execSQL(db, copySql.toString());
                //删除临时表
                execSQL(db, "DROP TABLE " + tmpTableName + "");
            }
            setTransactionSuccessful(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction(db);
        }
    }

    public static class Builder<T> {
        protected SQLiteDatabase db;
        protected String table;
        protected String where = "";
        protected String and = "";
        protected String or = "";
        protected List<Object> values = new ArrayList<>();

        public Builder(SQLiteDatabase db, String table) {
            this.db = db;
            this.table = table;
        }

        @SuppressWarnings("unchecked")
        public T where(String column, String op, Object value) {
            where = String.format(" where %s%s", column, op);
            int index = whereStartIndex();
            while (values.size() > index) {
                values.remove(index);
            }
            values.add(value);
            return (T) this;
        }

        //where条件第一个值在values中的索引
        protected int whereStartIndex() {
            return 0;
        }

        @SuppressWarnings("unchecked")
        public T and(String column, String op, Object value) {
            and += String.format(" and %s%s", column, op);
            values.add(value);
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T or(String column, String op, Object value) {
            or += String.format(" or %s%s", column, op);
            values.add(value);
            return (T) this;
        }
    }

    public static class QureyBuilder extends Builder<QureyBuilder> {
        private StringBuilder whats = new StringBuilder();
        private String orderBy = "";
        private StringBuilder groupBy = new StringBuilder();
        private String limit = "";
        private String offset = "";

        public QureyBuilder(SQLiteDatabase db, String table) {
            super(db, table);
        }

        public QureyBuilder select(String what, String... whats) {
            if (what == null) {
                this.whats.append("*");
            } else {
                this.whats.append(what);
                for (String w : whats) {
                    this.whats.append(",").append(w);
                }
            }
            return this;
        }

        public QureyBuilder orderByAsc(String column) {
            orderBy = " order by " + column + " asc";
            return this;
        }

        public QureyBuilder orderByDesc(String column) {
            orderBy = " order by " + column + " desc";
            return this;
        }

        public QureyBuilder groupBy(String... columns) {
            if (columns != null && columns.length > 0) {
                groupBy.append(" group by ");
                boolean first = true;
                for (String column : columns) {
                    if (!first)
                        groupBy.append(",");
                    first = false;
                    groupBy.append(column);
                }
            }
            return this;
        }

        public QureyBuilder limit(int limit) {
            this.limit = " limit " + limit;
            return this;
        }

        public QureyBuilder offset(int offset) {
            this.offset = " offset " + offset;
            return this;
        }

        public Cursor build() {
            String sql = String.format("select %s from %s%s%s%s%s%s%s%s", whats, table, where, and, or, 
                    groupBy, limit, offset, orderBy);
            String[] args = new String[values.size()];
            for (int i = 0; i < args.length; i++) {
                args[i] = values.get(i).toString();
            }
            return db.rawQuery(sql, args.length == 0 ? null : args);
        }
    }

    public static class UpdateBuilder extends Builder<UpdateBuilder> {
        private String sets = "";

        public UpdateBuilder(SQLiteDatabase db, String table) {
            super(db, table);
        }

        public UpdateBuilder set(String column, Object value) {
            if (TextUtils.isEmpty(sets)) {
                sets = " set " + column + "=?";
            } else {
                sets += "," + column + "=?";
            }
            values.add(value);
            return this;
        }

        @Override
        public int whereStartIndex() {
            return sets.replace("[^?]", "").length();
        }

        public void execute() {
            String sql = String.format("update %s%s%s%s%s", table, sets, where, and, or);
            db.execSQL(sql, values.toArray());
        }
    }

    public static class DeleteBuilder extends Builder<DeleteBuilder> {

        public DeleteBuilder(SQLiteDatabase db, String table) {
            super(db, table);
        }

        public void execute() {
            String sql = String.format("delete from %s%s%s%s", table, where, and, or);
            db.execSQL(sql, values.toArray());
        }
    }

    /**
     * 获取数据库行记录
     *
     * @param cursor 数据库游标
     * @return 当前行对应的所有(列名, 列值)
     */
    public static ContentValues getRowValues(Cursor cursor) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_INTEGER:
                    values.put(cursor.getColumnName(i), cursor.getLong(i));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    values.put(cursor.getColumnName(i), cursor.getDouble(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    values.put(cursor.getColumnName(i), cursor.getString(i));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    values.put(cursor.getColumnName(i), cursor.getBlob(i));
                    break;
            }
        }
        return values;
    }

    /**
     * 将查询结果装载成ContentValues集合
     */
    public static List<ContentValues> getValuesList(Cursor cursor) {
        List<ContentValues> valuesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            valuesList.add(getRowValues(cursor));
        }
        return valuesList;
    }

    /**
     * 添加数据库行记录，已存在则替换
     *
     * @param db     数据库对象
     * @param table  表名
     * @param values 行对应的所有(列名,列值)
     */
    public static void insertRecord(SQLiteDatabase db, String table, ContentValues values) {
        Set<String> keys = values.keySet();
        if (keys.size() > 0) {
            Iterator<String> i = keys.iterator();
            StringBuilder strColumn = new StringBuilder();
            StringBuilder strArg = new StringBuilder();
            Object[] vals = new Object[keys.size()];
            int n = 0;
            while (true) {
                String key = i.next();
                strColumn.append(key);
                strArg.append("?");
                vals[n] = values.get(key);
                if (i.hasNext()) {
                    strColumn.append(",");
                    strArg.append(",");
                } else {
                    break;
                }
                n++;
            }
            String sql = String.format("replace into %s(%s) values (%s)", table, strColumn, strArg);
            db.execSQL(sql, vals);
        }
    }

    /**
     * 删除数据库行记录
     *
     * @param db     数据库对象
     * @param table  表名
     * @param values 行对应的所有(列名,列值)
     */
    public static void deleteRecord(SQLiteDatabase db, String table, ContentValues values) {
        Set<String> keys = values.keySet();
        if (keys.size() > 0) {
            Iterator<String> i = keys.iterator();
            StringBuilder conditions = new StringBuilder();
            Object[] vals = new Object[keys.size()];
            int n = 0;
            while (true) {
                String key = i.next();
                conditions.append(key).append("=?");
                vals[n] = values.get(key);
                if (i.hasNext()) {
                    conditions.append(" and ");
                } else {
                    break;
                }
                n++;
            }
            db.execSQL("delete from " + table + " where " + conditions, vals);
        }
    }

    /**
     * 获取数据库记录某列值
     *
     * @param cursor      数据库游标
     * @param columnIndex 列索引
     */
    @SuppressWarnings("unchecked")
    public static <T> T getColumnValue(Class<T> clazz, Cursor cursor, int columnIndex) {
        Object o;
        if (clazz == int.class || clazz == Integer.class) {
            o = cursor.getInt(columnIndex);
        } else if (clazz == Long.class || clazz == long.class) {
            o = cursor.getLong(columnIndex);
        } else if (clazz == short.class || clazz == Short.class) {
            o = cursor.getShort(columnIndex);
        } else if (clazz == byte[].class) {
            o = cursor.getBlob(columnIndex);
        } else if (clazz == float.class || clazz == Float.class) {
            o = cursor.getFloat(columnIndex);
        } else if (clazz == double.class || clazz == Double.class) {
            o = cursor.getDouble(columnIndex);
        } else {
            o = cursor.getString(columnIndex);
        }
        return (T) o;
    }

    /**
     * 获取数据库记录某列值
     *
     * @param cursor     数据库游标
     * @param columnName 列名
     */
    public static <T> T getColumnValue(Class<T> clazz, Cursor cursor, String columnName) {
        return getColumnValue(clazz, cursor, cursor.getColumnIndex(columnName));
    }

    /**
     * 获取数据库记录某列值
     *
     * @param cursor       数据库游标
     * @param columnName   列名
     * @param defaultValue 默认值
     */
    public static <T> T getColumnValue(Class<T> clazz, Cursor cursor, String columnName, T defaultValue) {
        try {
            T value = getColumnValue(clazz, cursor, columnName);
            return value == null ? defaultValue : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取数据库记录某列值
     *
     * @param cursor       数据库游标
     * @param columnIndex  列索引
     * @param defaultValue 默认值
     */
    public static <T> T getColumnValue(Class<T> clazz, Cursor cursor, int columnIndex, T defaultValue) {
        try {
            T value = getColumnValue(clazz, cursor, columnIndex);
            return value == null ? defaultValue : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 查询某类统计信息
     *
     * @param clazz         要获取的数据字节码
     * @param db            数据库对象
     * @param sql           统计类Sql
     * @param selectionArgs 点位符对应的值
     */
    public static <T> T execScale(Class<T> clazz, SQLiteDatabase db, String sql, String[] selectionArgs) {
        return execScale(clazz, db.rawQuery(sql, selectionArgs));
    }

    /**
     * 获取第一列的值
     *
     * @param clazz  要获取的数据字节码
     * @param cursor 数据库游标
     */
    public static <T> T execScale(Class<T> clazz, Cursor cursor) {
        if (cursor.moveToNext()) {
            return getColumnValue(clazz, cursor, 0);
        }
        cursor.close();
        return null;
    }

    /**
     * 同一数据库复制数据到另一张表
     *
     * @param db          数据库对象
     * @param srcTable    源表
     * @param targetTable 目标表
     */
    public static void copyData(SQLiteDatabase db, String srcTable, String targetTable, String column, String... columns) {
        StringBuilder select;
        if (column == null) {
            select = new StringBuilder("*");
        } else {
            select = new StringBuilder(column);
            for (String col : columns) {
                select.append(",").append(col);
            }
        }
        db.execSQL(String.format("replace into %s select %s from %s", targetTable, select, srcTable));
    }
}
