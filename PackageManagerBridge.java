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

    public static boolean setActivationStatusForPermissionsPlugin(PackageManager pm, String pluginPackage, boolean isActive){
        Log.i(TAG,"setActivationStatusForPermissionsPlugin pluginPackage:" + pluginPackage + " isActive:" + isActive);
        return pm.setActivationStatusForPermissionsPlugin(pluginPackage,isActive);
    }

    public static boolean addTargetPackagesForPlugin(PackageManager pm, String pluginPackage, List<String> targetPackages, boolean reset){
        Log.i(TAG,"addTargetPackagesForPlugin pluginPackage:" + pluginPackage);
        return pm.addTargetPackagesForPlugin(pluginPackage, targetPackages, reset);
    }

    public static boolean removeTargetPackagesForPlugin(PackageManager pm, String pluginPackage, List<String> targetPackages){
        Log.i(TAG,"removeTargetPackagesForPlugin pluginPackage:" + pluginPackage);
        return pm.removeTargetPackagesForPlugin(pluginPackage, targetPackages);
    }

    public static boolean addTargetAPIsForPlugin(PackageManager pm, String pluginPackage, List<String> targetAPIs, boolean reset){
        Log.i(TAG,"addTargetAPIsForPlugin pluginPackage:" + pluginPackage);
        return pm.addTargetAPIsForPlugin(pluginPackage,targetAPIs, reset);
    }

    public static boolean removeTargetAPIsForPlugin(PackageManager pm, String pluginPackage, List<String> targetAPIs){
        Log.i(TAG,"removeTargetAPIsForPlugin pluginPackage:" + pluginPackage);
        return pm.removeTargetAPIsForPlugin(pluginPackage, targetAPIs);
    }

    public static List<String> getInstalledUntrustedPackages(PackageManager pm){
        Log.i(TAG,"getInstalledUntrustedPackages");
	return pm.getInstalledUntrustedPackages();
    }

}

