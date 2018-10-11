package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PluginDBHelper extends SQLiteOpenHelper {

    // Name of the plugin database
    public static final String DATABASE_NAME = "plugin.db";

    // When changing the database schema increment the version
    private static final int DATABASE_VERSION = 1;

    public PluginDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL query to create plugin table
        final String SQL_CREATE_PLUGIN_TABLE =
                "CREATE TABLE " + PluginContract.PluginEntry.TABLE_NAME + " (" +
                        PluginContract.PluginEntry._ID + " INTEGER PRIMARY KEY," +
                        PluginContract.PluginEntry.COLUMN_NAME_PACKAGE_NAME + " TEXT NOT NULL UNIQUE," +
                        PluginContract.PluginEntry.COLUMN_NAME_IS_ACTIVE + " INTEGER NOT NULL)";
        db.execSQL(SQL_CREATE_PLUGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // SQL query to drop plugin table
        final String SQL_DELETE_PLUGIN_TABLE = "DROP TABLE IF EXISTS " + PluginContract.PluginEntry.TABLE_NAME;

        db.execSQL(SQL_DELETE_PLUGIN_TABLE);

        // Create a new table
        onCreate(db);
    }
}
