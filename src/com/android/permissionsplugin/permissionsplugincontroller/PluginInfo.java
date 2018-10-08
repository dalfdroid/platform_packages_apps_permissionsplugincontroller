package com.android.permissionsplugin.permissionsplugincontroller;

public class PluginInfo {

    public final String id;
    public final String packageName;

    public PluginInfo(String id, String packageName){
        this.id = id;
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return packageName;
    }
}
