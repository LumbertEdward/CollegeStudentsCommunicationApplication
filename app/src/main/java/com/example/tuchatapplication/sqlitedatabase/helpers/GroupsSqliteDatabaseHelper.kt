package com.example.tuchatapplication.sqlitedatabase.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tuchatapplication.sqlitedatabase.database.Database
import com.example.tuchatapplication.sqlitedatabase.queries.GroupQueries

class GroupsSqliteDatabaseHelper(context: Context): SQLiteOpenHelper(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL(GroupQueries.GROUP_SQL_ENTRIES)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL(GroupQueries.GROUP_DELETE_SQL)
        onCreate(p0)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}