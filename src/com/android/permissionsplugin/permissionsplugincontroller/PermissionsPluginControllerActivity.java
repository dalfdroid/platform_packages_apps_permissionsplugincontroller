package com.android.permissionsplugin.permissionsplugincontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class PermissionsPluginControllerActivity extends AppCompatActivity implements PluginFragment.OnListFragmentInteractionListener {

    private static final String TAG = "PermissionsPluginControllerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugincontroller);

    }

    @Override
    public void onListFragmentInteraction(PluginInfo item) {
        Log.i(TAG,"Id: "+item.id);
        Log.i(TAG,"Content: "+item.packageName);
        Log.i(TAG,"String:"+item.toString());
    }

}
