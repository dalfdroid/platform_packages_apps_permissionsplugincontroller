package com.android.permissionsplugin.permissionsplugincontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.permissionsplugin.PermissionsPlugin;


public class PermissionsPluginControllerActivity extends AppCompatActivity implements PluginFragment.OnListFragmentInteractionListener {

    private static final String TAG = "PermissionsPluginControllerActivity";

    private static final Boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugincontroller);

    }

    @Override
    public void onListFragmentInteraction(PermissionsPlugin plugin) {
        if(DEBUG) {
            Log.i(TAG, "Plugin interaction:" + plugin.packageName);
        }
    }

    @Override
    public void onPluginActivation(PermissionsPlugin plugin) {
        if(DEBUG) {
            Log.i(TAG, "Plugin activation:" + plugin.packageName + ":" + plugin.isActive);
        }

        // update plugin
        boolean res = getPackageManager().setActivationStatusForPermissionsPlugin(plugin.packageName,plugin.isActive);
        if(!res){
            Log.e(TAG,"Failed to update activation status for plugin "+plugin.packageName);
        }
    }

}
