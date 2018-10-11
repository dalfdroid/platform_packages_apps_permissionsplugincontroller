package com.android.permissionsplugin.permissionsplugincontroller;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PluginContract {

    // Make the constructor private to avoid instantiating the contract class
    private PluginContract(){}

    public static final String CONTENT_AUTHORITY = "com.android.permissionsplugin.premissionsplugincontroller.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Inner class that defines the table contents
    // BaseColumns automatically adds _ID field that acts as a primary key
    public static class PluginEntry implements BaseColumns {

        // Name of the plugin table
        public static final String TABLE_NAME = "plugin";

        // Name of the plugin package stored as string
        public static final String COLUMN_NAME_PACKAGE_NAME = "package_name";

        // A boolean flag stored as integer indicating if the plugin is active
        // 1 - active, 0 - inactive
        public static final String COLUMN_NAME_IS_ACTIVE = "is_active";

        // Content URI of the plugin table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /**
         * Builds a URI that adds the plugin _ID at the end of the plugin table CONTENT_URI
         * It is used to access a single row (specific plugin) identified by the _ID
         *
         * @param id unique id of the row
         * @return URI to query row identified by the given id
         */
        public static Uri uriWithId(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }
    }
}
