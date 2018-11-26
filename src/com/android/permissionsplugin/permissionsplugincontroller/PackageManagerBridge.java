package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.pm.PackageManager;
import android.util.Log;

import com.android.permissionsplugin.PermissionsPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * A bridge class to access package manager APIs.
 *
 */
public class PackageManagerBridge {

    public static final String TAG = "heimdall";

    public static List<PermissionsPlugin> getInstalledPermissionsPlugins(PackageManager pm){
        return pm.getInstalledPermissionsPlugins();
    }

    public static boolean activatePlugin(PackageManager pm, String pluginPackage, String targetPackage, String targetAPI, boolean activate){
        Log.i(TAG,"activatePlugin pluginPackage:" + pluginPackage + " targetPackage:" + targetPackage + " targetAPI:" + targetAPI + " activate: " + activate);
        return pm.activatePlugin(pluginPackage,targetPackage,targetAPI,activate);
    }

    public static List<String> getInstalledUntrustedPackages(PackageManager pm){
        Log.i(TAG,"getInstalledUntrustedPackages");
	return pm.getInstalledUntrustedPackages();
    }

}

