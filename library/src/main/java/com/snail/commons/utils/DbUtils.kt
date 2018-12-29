package com.snail.commons.utils

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.annotation.StringDef
import android.text.TextUtils
import java.util.*

/**
 * Created by zeng on 2017/1/13.
 * 为了适用于如greenDao这样的框架，数据升级使用反射调用相应的方法
 */
object DbUtils {
    //-----------升级数据库---------------
    const val INTEGER = "INTEGER"
    const val TEXT = "TEXT"
    const val REAL = "REAL"
    const val DATE = "DATE"

    //-----------增删改查-----------------
    const val EQ = "=?"
    const val NOT_EQ = "!=?"
    const val LIKE = " like ?"
    const val GT = ">?"
    const val LT = "<?"
    const val GE = ">=?"
    const val LE = "<=?"

    @StringDef(INTEGER, TEXT, REAL, DATE) //限定
    @Retention(AnnotationRetention.SOURCE) //表示注解所存活的时间,在运行时,而不会存在.class文件.
    annotation class DataType

    class Column @JvmOverloads constructor(var name: String = "", @DataType var dataType: String = INTEGER, var defualtValue: Any? = null)

    private fun execSQL(db: Any, sql: String) {
        try {
            db.javaClass.getMethod("execSQL", String::class.java).invoke(db, sql)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun execSQL(db: Any, sql: String, bindArgs: Array<Any>) {
        try {
            db.javaClass.getMethod("execSQL", String::class.java, Array<Any>::class.java).invoke(db, sql, bindArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun invokeVoidNoPrama(db: Any, methodName: String) {
        try {
            db.javaClass.getMethod(methodName).invoke(db)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun beginTransaction(db: Any) {
        invokeVoidNoPrama(db, "beginTransaction")
    }

    private fun endTransaction(db: Any) {
        invokeVoidNoPrama(db, "endTransaction")
    }

    private fun setTransactionSuccessful(db: Any) {
        invokeVoidNoPrama(db, "setTransactionSuccessful")
    }

    /**
     * 重命名表
     */
    fun renameTable(db: Any, oldName: String, newName: String) {
        execSQL(db, "ALTER TABLE $oldName RENAME TO $newName")
    }

    /**
     * 删除表
     */
    fun deleteTable(db: Any, tableName: String) {
        execSQL(db, "DROP TABLE $tableName")
    }

    /**
     * 获取数据表的所有字段名
     */
    fun getColumns(db: Any, tableName: String): ArrayList<Column> {
        val columns = ArrayList<Column>()
        var cursor: Cursor? = null
        try {
            val sql = "PRAGMA table_info($tableName)"
            val method = db.javaClass.getMethod("rawQuery", String::class.java, Array<String>::class.java)
            cursor = method.invoke(db, sql, null) as Cursor
            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex == -1) {
                return columns
            }
            var index = 0
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                columns[index] = Column()
                columns[index].name = cursor.getString(nameIndex)
                columns[index].dataType = cursor.getString(cursor.getColumnIndex("type"))
                val defaultValueIndex = cursor.getColumnIndex("dflt_value")
                when (cursor.getType(defaultValueIndex)) {
                    Cursor.FIELD_TYPE_BLOB -> columns[index].defualtValue = cursor.getBlob(defaultValueIndex)
                    Cursor.FIELD_TYPE_FLOAT -> columns[index].defualtValue = cursor.getDouble(defaultValueIndex)
                    Cursor.FIELD_TYPE_INTEGER -> columns[index].defualtValue = cursor.getLong(defaultValueIndex)
                    Cursor.FIELD_TYPE_STRING -> columns[index].defualtValue = cursor.getString(defaultValueIndex)
                }
                index++
                cursor.moveToNext()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return columns
    }

    /**
     * 更新数据表，删除列
     * @param columnNames 要删除的列名
     */
    fun deleteColumns(db: Any, tableName: String, vararg columnNames: String) {
        try {
            beginTransaction(db)
            val columns = getColumns(db, tableName)
            if (columns.isNotEmpty()) {
                //重命名原表
                val tmpTableName = "t" + UUID.randomUUID().toString().replace("-".toRegex(), "_")
                execSQL(db, "ALTER TABLE $tableName RENAME TO $tmpTableName")
                //新建表sql语句
                var newTableSql = StringBuilder("CREATE TABLE $tableName(")
                val list = Arrays.asList(*columnNames)
                //复制数据sql语句
                var copySql = StringBuilder("INSERT INTO $tableName SELECT ")
                for (column in columns) {
                    if (!list.contains(column.name)) {
                        newTableSql.append(column.name).append(" ").append(column.dataType).append(",")
                        copySql.append(column.name).append(",")
                    }
                }
                newTableSql = StringBuilder(newTableSql.substring(0, newTableSql.length - 1) + ")")
                execSQL(db, newTableSql.toString())
                //复制数据到新表
                copySql = StringBuilder(copySql.substring(0, copySql.length - 1) + " FROM " + tmpTableName)
                execSQL(db, copySql.toString())
                //删除临时表
                execSQL(db, "DROP TABLE $tmpTableName")
            }
            setTransactionSuccessful(db)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            endTransaction(db)
        }
    }

    /**
     * 更新数据表，增加列
     * @param columns 需要增加的列的信息
     */
    fun addColumns(db: Any, tableName: String, vararg columns: Column) {
        try {
            beginTransaction(db)
            for (column in columns) {
                var sql = "ALTER TABLE " + tableName + " ADD " + column.name + " " + column.dataType
                var bindArgs: Array<Any>? = null
                if (column.defualtValue != null) {
                    when {
                        column.defualtValue is String -> sql += " DEFAULT '" + column.defualtValue + "'"
                        column.defualtValue is ByteArray -> {
                            //如果是字节数组，先插入列，再设置默认数据
                            execSQL(db, sql)
                            sql = "UPDATE " + tableName + " SET " + column.name + "=?"
                            bindArgs = arrayOf(column.defualtValue!!)
                        }
                        else -> sql += " DEFAULT " + column.defualtValue!!
                    }
                }
                if (bindArgs == null) {
                    execSQL(db, sql)
                } else {
                    execSQL(db, sql, bindArgs)
                }
            }
            setTransactionSuccessful(db)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            endTransaction(db)
        }
    }

    /**
     * 更新列名
     * @param map key为旧列名，value为新列名
     */
    fun renameColumns(db: Any, tableName: String, map: Map<String, String>) {
        try {
            beginTransaction(db)
            val columns = getColumns(db, tableName)
            if (columns.isNotEmpty()) {
                //重命名原表
                val tmpTableName = "t" + UUID.randomUUID().toString().replace("-".toRegex(), "_")
                execSQL(db, "ALTER TABLE $tableName RENAME TO $tmpTableName")
                //新建表sql语句
                var newTableSql = StringBuilder("CREATE TABLE $tableName(")
                //复制数据sql语句
                var copySql = StringBuilder("INSERT INTO $tableName SELECT ")
                val keySet = map.keys
                for (column in columns) {
                    if (keySet.contains(column.name)) {
                        newTableSql.append(map[column.name]).append(" ").append(column.dataType).append(",")
                    } else {
                        newTableSql.append(column.name).append(" ").append(column.dataType).append(",")
                    }
                    copySql.append(column.name).append(",")
                }
                newTableSql = StringBuilder(newTableSql.substring(0, newTableSql.length - 1) + ")")
                execSQL(db, newTableSql.toString())
                //复制数据到新表
                copySql = StringBuilder(copySql.substring(0, copySql.length - 1) + " FROM " + tmpTableName)
                execSQL(db, copySql.toString())
                //删除临时表
                execSQL(db, "DROP TABLE $tmpTableName")
            }
            setTransactionSuccessful(db)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            endTransaction(db)
        }
    }

    @StringDef(EQ, NOT_EQ, LIKE, GT, LT, GE, LE) //限定
    @Retention(AnnotationRetention.SOURCE) //表示注解所存活的时间,在运行时,而不会存在.class文件.
    annotation class Operator

    open class Builder<T>(protected var db: SQLiteDatabase, protected var table: String) {
        protected var where = ""
        protected var and = ""
        protected var or = ""
        protected var values: MutableList<Any> = ArrayList()

        fun where(column: String, @Operator op: String, value: Any): T {
            where = " where $column$op"
            val index = whereStartIndex()
            while (values.size > index) {
                values.removeAt(index)
            }
            values.add(value)
            return this as T
        }

        //where条件第一个值在values中的索引
        protected open fun whereStartIndex(): Int {
            return 0
        }

        fun and(column: String, @Operator op: String, value: Any): T {
            and += " and $column$op"
            values.add(value)
            return this as T
        }

        fun or(column: String, @Operator op: String, value: Any): T {
            or += " or $column$op"
            values.add(value)
            return this as T
        }
    }

    class QureyBuilder(db: SQLiteDatabase, table: String) : Builder<QureyBuilder>(db, table) {
        private val whats = StringBuilder()
        private var orderBy = ""
        private val groupBy = StringBuilder()
        private var limit = ""
        private var offset = ""

        fun select(what: String?, vararg whats: String): QureyBuilder {
            if (what == null) {
                this.whats.append("*")
            } else {
                this.whats.append(what)
                for (w in whats) {
                    this.whats.append(",").append(w)
                }
            }
            return this
        }

        fun orderByAsc(column: String): QureyBuilder {
            orderBy = " order by $column asc"
            return this
        }

        fun orderByDesc(column: String): QureyBuilder {
            orderBy = " order by $column desc"
            return this
        }

        fun groupBy(vararg columns: String): QureyBuilder {
            if (columns.isNotEmpty()) {
                groupBy.append(" group by ")
                var first = true
                for (column in columns) {
                    if (!first)
                        groupBy.append(",")
                    first = false
                    groupBy.append(column)
                }
            }
            return this
        }

        fun limit(limit: Int): QureyBuilder {
            this.limit = " limit $limit"
            return this
        }

        fun offset(offset: Int): QureyBuilder {
            this.offset = " offset $offset"
            return this
        }

        fun build(): Cursor {
            val sql = "select $whats from $table$where$and$or$groupBy$orderBy$limit$offset"
            val args = arrayOfNulls<String>(values.size)
            for (i in values.indices) {
                args[i] = values[i].toString()
            }
            return db.rawQuery(sql, if (args.size == 0) null else args)
        }
    }

    class UpdateBuilder(db: SQLiteDatabase, table: String) : Builder<UpdateBuilder>(db, table) {
        private var sets = ""

        operator fun set(column: String, value: Any): UpdateBuilder {
            if (TextUtils.isEmpty(sets)) {
                sets = " set $column=?"
            } else {
                sets += ",$column=?"
            }
            values.add(value)
            return this
        }

        override fun whereStartIndex(): Int {
            return sets.replace("[^?]".toRegex(), "").length
        }

        fun execute() {
            val sql = String.format("update %s%s%s%s%s", table, sets, where, and, or)
            db.execSQL(sql, values.toTypedArray())
        }
    }

    class DeleteBuilder(db: SQLiteDatabase, table: String) : Builder<DeleteBuilder>(db, table) {

        fun execute() {
            val sql = String.format("delete from %s%s%s%s", table, where, and, or)
            db.execSQL(sql, values.toTypedArray())
        }
    }

    /**
     * 获取数据库行记录
     * @param cursor 数据库游标
     * @return 当前行对应的所有(列名,列值)
     */
    fun getRowValues(cursor: Cursor): ContentValues {
        val values = ContentValues()
        for (i in 0 until cursor.columnCount) {
            when (cursor.getType(i)) {
                Cursor.FIELD_TYPE_INTEGER -> values.put(cursor.getColumnName(i), cursor.getLong(i))
                Cursor.FIELD_TYPE_FLOAT -> values.put(cursor.getColumnName(i), cursor.getDouble(i))
                Cursor.FIELD_TYPE_STRING -> values.put(cursor.getColumnName(i), cursor.getString(i))
                Cursor.FIELD_TYPE_BLOB -> values.put(cursor.getColumnName(i), cursor.getBlob(i))
            }
        }
        return values
    }

    /**
     * 将查询结果装载成ContentValues集合
     */
    fun getValuesList(cursor: Cursor): List<ContentValues> {
        val valuesList = ArrayList<ContentValues>()
        while (cursor.moveToNext()) {
            valuesList.add(getRowValues(cursor))
        }
        return valuesList
    }

    /**
     * 添加数据库行记录，已存在则替换
     * @param db 数据库对象
     * @param table 表名
     * @param values 行对应的所有(列名,列值)
     */
    fun insertRecord(db: SQLiteDatabase, table: String, values: ContentValues) {
        val keys = values.keySet()
        if (keys.size > 0) {
            val i = keys.iterator()
            val strColumn = StringBuilder()
            val strArg = StringBuilder()
            val vals = arrayOfNulls<Any>(keys.size)
            var n = 0
            while (true) {
                val key = i.next()
                strColumn.append(key)
                strArg.append("?")
                vals[n] = values.get(key)
                if (i.hasNext()) {
                    strColumn.append(",")
                    strArg.append(",")
                } else {
                    break
                }
                n++
            }
            val sql = "replace into $table($strColumn) values ($strArg)"
            db.execSQL(sql, vals)
        }
    }

    /**
     * 删除数据库行记录
     * @param db 数据库对象
     * @param table 表名
     * @param values 行对应的所有(列名,列值)
     */
    fun deleteRecord(db: SQLiteDatabase, table: String, values: ContentValues) {
        val keys = values.keySet()
        if (keys.size > 0) {
            val i = keys.iterator()
            val conditions = StringBuilder()
            val vals = arrayOfNulls<Any>(keys.size)
            var n = 0
            while (true) {
                val key = i.next()
                conditions.append(key).append("=?")
                vals[n] = values.get(key)
                if (i.hasNext()) {
                    conditions.append(" and ")
                } else {
                    break
                }
                n++
            }
            val sql = "delete from $table where $conditions"
            db.execSQL(sql, vals)
        }
    }

    /**
     * 获取数据库记录某列值
     * @param cursor 数据库游标
     * @param columnIndex 列索引
     */
    fun <T> getColumnValue(clazz: Class<T>, cursor: Cursor, columnIndex: Int): T? {
        val o: Any = if (clazz == Int::class.javaPrimitiveType || clazz == Int::class.java) {
            cursor.getInt(columnIndex)
        } else if (clazz == Long::class.javaPrimitiveType || clazz == Long::class.java) {
            cursor.getLong(columnIndex)
        } else if (clazz == Short::class.javaPrimitiveType || clazz == Short::class.java) {
            cursor.getShort(columnIndex)
        } else if (clazz == ByteArray::class.java) {
            cursor.getBlob(columnIndex)
        } else if (clazz == Float::class.javaPrimitiveType || clazz == Float::class.java) {
            cursor.getFloat(columnIndex)
        } else if (clazz == Double::class.javaPrimitiveType || clazz == Double::class.java) {
            cursor.getDouble(columnIndex)
        } else {
            cursor.getString(columnIndex)
        }
        return o as T
    }

    /**
     * 获取数据库记录某列值
     * @param cursor 数据库游标
     * @param columnName 列名
     */
    fun <T> getColumnValue(clazz: Class<T>, cursor: Cursor, columnName: String): T? {
        return getColumnValue(clazz, cursor, cursor.getColumnIndex(columnName))
    }

    /**
     * 获取数据库记录某列值
     * @param cursor 数据库游标
     * @param columnName 列名
     * @param defaultValue 默认值
     */
    fun <T> getColumnValue(clazz: Class<T>, cursor: Cursor, columnName: String, defaultValue: T): T {
        try {
            return getColumnValue(clazz, cursor, columnName) ?: return defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return defaultValue
    }

    /**
     * 获取数据库记录某列值
     * @param cursor 数据库游标
     * @param columnIndex 列索引
     * @param defaultValue 默认值
     */
    fun <T> getColumnValue(clazz: Class<T>, cursor: Cursor, columnIndex: Int, defaultValue: T): T {
        try {
            return getColumnValue(clazz, cursor, columnIndex) ?: return defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return defaultValue
    }

    /**
     * 查询某类统计信息
     * @param clazz 要获取的数据字节码
     * @param db 数据库对象
     * @param sql 统计类Sql
     * @param selectionArgs 点位符对应的值
     */
    fun <T> execScale(clazz: Class<T>, db: SQLiteDatabase, sql: String, selectionArgs: Array<String>): T? {
        return execScale(clazz, db.rawQuery(sql, selectionArgs))
    }

    /**
     * 获取第一列的值
     * @param clazz 要获取的数据字节码
     * @param cursor 数据库游标
     */
    fun <T> execScale(clazz: Class<T>, cursor: Cursor): T? {
        if (cursor.moveToNext()) {
            return getColumnValue(clazz, cursor, 0)
        }
        cursor.close()
        return null
    }

    /**
     * 同一数据库复制数据到另一张表
     * @param db 数据库对象
     * @param srcTable 源表
     * @param targetTable 目标表
     */
    fun copyData(db: SQLiteDatabase, srcTable: String, targetTable: String, column: String?, vararg columns: String) {
        val select: StringBuilder
        if (column == null) {
            select = StringBuilder("*")
        } else {
            select = StringBuilder(column)
            for (col in columns) {
                select.append(",").append(col)
            }
        }
        db.execSQL("replace into $targetTable select $select from $srcTable")
    }
}
