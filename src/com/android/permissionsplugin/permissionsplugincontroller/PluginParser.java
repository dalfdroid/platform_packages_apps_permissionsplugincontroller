package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageParser.PackageLite;
import android.content.pm.split.DefaultSplitAssetLoader;
import android.content.res.AssetManager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class PluginParser {

    private static final String TAG = "PluginParser";

    /** File name in an APK for the permissions plugin manifest file. */
    private static final String PERMISSIONS_PLUGIN_MANIFEST_FILENAME = "PermissionsPlugin.json";

    // Constants for parsing permissions plugin JSON file
    private static final String JSON_KEY_ROOT = "permissionsplugin";
    private static final String JSON_KEY_SUPPORTED_PKGS = "supportsPkg";
    private static final String JSON_KEY_INTERPOSED_APIS = "interposesOn";
    private static final String JSON_KEY_PROXY_CLASS = "proxyMain";

    private PackageManager mPackageManager;

    public PluginParser(PackageManager packageManager){
        mPackageManager = packageManager;
    }

    /**
     * Parse plugin given by the packagename
     * @param packageName
     * @return returns a Plugin object with parsed information or null in case of error
     */
    public Plugin parsePlugin(String packageName){
        Plugin plugin = null;
        try {
            plugin = new Plugin(packageName);

            // Get the package.
            ApplicationInfo ai = mPackageManager.getApplicationInfo(packageName, 0);
            plugin.packagePath = ai.sourceDir;
            File f = new File(plugin.packagePath);
            PackageLite pkgLite = PackageParser.parsePackageLite(f, 0);

            // Load the JSON manifest
            DefaultSplitAssetLoader loader = new DefaultSplitAssetLoader(pkgLite, 0);
            InputStream is = loader.getBaseAssetManager().open(PERMISSIONS_PLUGIN_MANIFEST_FILENAME, AssetManager.ACCESS_BUFFER);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Parse the JSON
            String rawJson = new String(buffer, "UTF-8");
            JSONObject json = new JSONObject(rawJson);
            JSONObject root = json.getJSONObject(JSON_KEY_ROOT);

            // Parse proxy class
            plugin.proxyClass = root.getString(JSON_KEY_PROXY_CLASS);

            // Parse supported packages
            JSONArray supportedPackages = root.getJSONArray(JSON_KEY_SUPPORTED_PKGS);
            plugin.supportedPackages = new ArrayList<>();
            for(int i=0; i<supportedPackages.length(); i++){
                plugin.supportedPackages.add(supportedPackages.getString(i));
            }

            // Parse supported APIs
            JSONArray supportedAPIs = root.getJSONArray(JSON_KEY_INTERPOSED_APIS);
            plugin.supportedAPIs = new ArrayList<>();
            for(int i=0; i<supportedAPIs.length(); i++){
                plugin.supportedAPIs.add(supportedAPIs.getString(i));
            }

            // Newly loaded plugin is by default inactive until user explicitly activate it
            plugin.isActive = false;

        }catch (Exception e){
            Log.e(TAG, "Exception while parsing the plugin package: " + packageName + " : " + e);
            e.printStackTrace();
        }

        return plugin;
    }

    public static class Plugin{

        // Plugin ID retrieved from the plugin db
        public long id;

        // Package name of the plugin
        public String packageName;

        // Path to plugin package
        public String packagePath;

        // Main class of the plugin
        public String proxyClass;

        // Packages (apps) supported by this plugin
        public ArrayList<String> supportedPackages;

        // Apis supported by this plugin
        public ArrayList<String> supportedAPIs;

        // Flag to check if plugin is active
        public Boolean isActive;

//        // Apps selected by user to apply this plugin to
//        // must be a subset of the supportedPackages
//        public ArrayList<String> targetPackages;
//
//        // APIs selected by user for this plugin
//        // must be a subset of the supportedAPIs
//        public ArrayList<String> targetAPIs;

        public Plugin(String packageName){
            this.packageName = packageName;
        }

        @Override
        public String toString() {
            return packageName;
        }
    }

}
