package com.example.qcdefectchecklist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "qc_inspection.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "qc_inspections"
        const val COLUMN_ID = "id"
        const val COLUMN_BATCH_CODE = "batch_code"
        const val COLUMN_COMPONENT_NAME = "component_name"
        const val COLUMN_DEFECT_TYPE = "defect_type"
        const val COLUMN_SAMPLE_SIZE = "sample_size"
        const val COLUMN_DEFECT_COUNT = "defect_count"
        const val COLUMN_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BATCH_CODE TEXT NOT NULL,
                $COLUMN_COMPONENT_NAME TEXT NOT NULL,
                $COLUMN_DEFECT_TYPE TEXT NOT NULL,
                $COLUMN_SAMPLE_SIZE INTEGER NOT NULL,
                $COLUMN_DEFECT_COUNT INTEGER NOT NULL,
                $COLUMN_STATUS TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // CREATE
    fun insertInspection(item: Inspection): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BATCH_CODE, item.batchCode)
            put(COLUMN_COMPONENT_NAME, item.componentName)
            put(COLUMN_DEFECT_TYPE, item.defectType)
            put(COLUMN_SAMPLE_SIZE, item.sampleSize)
            put(COLUMN_DEFECT_COUNT, item.defectCount)
            put(COLUMN_STATUS, item.status)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // READ
    fun getAllInspections(): List<Inspection> {
        val list = mutableListOf<Inspection>()
        val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val item = Inspection(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    batchCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BATCH_CODE)),
                    componentName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPONENT_NAME)),
                    defectType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEFECT_TYPE)),
                    sampleSize = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SAMPLE_SIZE)),
                    defectCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DEFECT_COUNT)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
                )
                list.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // UPDATE
    fun updateInspection(item: Inspection): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BATCH_CODE, item.batchCode)
            put(COLUMN_COMPONENT_NAME, item.componentName)
            put(COLUMN_DEFECT_TYPE, item.defectType)
            put(COLUMN_SAMPLE_SIZE, item.sampleSize)
            put(COLUMN_DEFECT_COUNT, item.defectCount)
            put(COLUMN_STATUS, item.status)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(item.id.toString()))
    }

    // DELETE
    fun deleteInspection(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}