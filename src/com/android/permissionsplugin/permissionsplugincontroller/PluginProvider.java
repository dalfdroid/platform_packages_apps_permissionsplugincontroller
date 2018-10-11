package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import static android.security.keystore.AndroidKeyStoreProvider.PROVIDER_NAME;

public class PluginProvider extends ContentProvider {

    private PluginDBHelper mPluginDBHelper;

    // Constants for content uri to point to tables/rows
    public static final int CODE_PLUGIN = 100;
    public static final int CODE_PLUGIN_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PluginContract.CONTENT_AUTHORITY;

        // URI for plugin table
        matcher.addURI(authority, PluginContract.PluginEntry.TABLE_NAME, CODE_PLUGIN);

        // URI for a specific row in the plugin table
        matcher.addURI(authority, PluginContract.PluginEntry.TABLE_NAME + "/#", CODE_PLUGIN_WITH_ID);

        return matcher;
    }

    public PluginProvider() {
    }

    @Override
    public boolean onCreate() {
        mPluginDBHelper = new PluginDBHelper(getContext());
        return  mPluginDBHelper != null;
    }


    /**
     *
     * @param uri URI of the insertion request
     * @param values Set of column_name-value pair to be inserted
     * @return URI of the newly inserted record on success otherwise null
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase db = mPluginDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_PLUGIN:

                long newRowId = db.insert(PluginContract.PluginEntry.TABLE_NAME, null, values);

                // If newRowId = -1 then insertion has failed
                if (-1 != newRowId) {
                    // Notify clients that database has changed
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                // Return URI that points to the newly inserted record
                return PluginContract.PluginEntry.uriWithId(newRowId);

            default:
                return null;
        }
    }

    /**
     *
     * @param uri URI to query
     * @param projection List of columns to return. If null, all columns are returned.
     * @param selection Selection criteria for filtering rows. If null, all rows are returned.
     * @param selectionArgs Selection argument. "?" in Selection argument will be replaced by
     *                      the values given here.
     * @param sortOrder Sorting order of rows.
     * @return Cursor with resulting rows.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        // Get readable database
        SQLiteDatabase db = mPluginDBHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_PLUGIN:
                // Query all records
                cursor = db.query(
                        PluginContract.PluginEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_PLUGIN_WITH_ID:
                // Query particular record specified by uri
                String _ID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_ID};
                cursor = db.query(
                        PluginContract.PluginEntry.TABLE_NAME,
                        projection,
                        PluginContract.PluginEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase db = mPluginDBHelper.getWritableDatabase();

        // Count stores the number of records deleted
        int deletedRows = 0;

        switch (sUriMatcher.match(uri)){
            case CODE_PLUGIN:
                // Delete all records
                deletedRows = db.delete(
                        PluginContract.PluginEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_PLUGIN_WITH_ID:
                // Delete a particular record specified by uri
                String _ID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_ID};
                deletedRows = db.delete(
                        PluginContract.PluginEntry.TABLE_NAME,
                        PluginContract.PluginEntry._ID + " = ? ",
                        selectionArguments);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase db = mPluginDBHelper.getWritableDatabase();

        // Count stores the number of records updated
        int updatedRows = 0;

        switch (sUriMatcher.match(uri)){
            case CODE_PLUGIN:
                // Update all records
                updatedRows = db.update(
                        PluginContract.PluginEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_PLUGIN_WITH_ID:
                // Update a particular record specified by uri
                String _ID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_ID};
                updatedRows = db.update(
                        PluginContract.PluginEntry.TABLE_NAME,
                        values,
                        PluginContract.PluginEntry._ID + " = ? ",
                        selectionArguments);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case CODE_PLUGIN:
                return "vnd.android.cursor.dir/vnd.permissionsplugin.plugin";
            case CODE_PLUGIN_WITH_ID:
                return "vnd.android.cursor.item/vnd.permissionsplugin.plugin";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
