package com.android.permissionsplugin.permissionsplugincontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class PermissionsPluginControllerActivity extends AppCompatActivity implements PluginFragment.OnListFragmentInteractionListener {

    private static final String TAG = "PermissionsPluginControllerActivity";

    private static final Boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugincontroller);

    }

    @Override
    public void onListFragmentInteraction(PluginParser.Plugin plugin) {
        if(DEBUG) {
            Log.i(TAG, "Plugin interaction:" + plugin.packageName);
        }
    }

    @Override
    public void onPluginActivation(PluginParser.Plugin plugin) {
        if(DEBUG) {
            Log.i(TAG, "Plugin activation:" + plugin.packageName + ":" + plugin.isActive);
        }

        // update plugin
        PluginFragment fragment = (PluginFragment) getSupportFragmentManager().findFragmentById(R.id.plugin_fragment);
        fragment.updatePlugin(plugin);
    }

}
