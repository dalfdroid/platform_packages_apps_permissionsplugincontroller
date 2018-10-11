package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PluginFragment extends Fragment {

    private final static String TAG = "PluginFragment";

    private final Boolean DEBUG = true;

    private OnListFragmentInteractionListener mListener;

    // A map between plugin package name and the corresponding plugin object
    private Map<String, PluginParser.Plugin> mPlugins = new HashMap<>();

    // A list containing plugins
    private List<PluginParser.Plugin> mPluginList = new ArrayList<>();

    private PluginParser mPluginParser;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PluginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize plugin parser
        mPluginParser = new PluginParser(getActivity().getPackageManager());

        // Populate plugin list
        populatePluginList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plugin_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            if(DEBUG){
                Log.i(TAG,"Setting recyler view with #plugins : "+mPluginList.size());
                Log.i(TAG,"List of plugins in pluginlist:");
                for(PluginParser.Plugin plugin : mPluginList){
                    Log.i(TAG,plugin.id+ ":"+plugin.packageName);
                }
            }

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new PluginRecyclerViewAdapter(mPluginList, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * First query the plugin database to retrieve stored plugins.
     * Then, query the packagemanager to retrieve installed plungins
     * and update the plugin db accordingly.
     */
    public void populatePluginList(){

        // Retrieve stored plugins from the plugin db
        retrievePluginsFromDB();

        // Retrieve installed packages
        List<PackageInfo> installedPackages = getActivity().getPackageManager().getInstalledPackages(0);

        if(DEBUG){
            Log.i(TAG,"Number of installed packages:"+installedPackages.size());
        }

        // List to store installed plugins
        ArrayList<String> installedPlugins = new ArrayList<>();

        for(int i=0; i< installedPackages.size(); i++){
            PackageInfo pkg = installedPackages.get(i);

            // Skip packages that are not plugin
            if(!pkg.isPermissionsPlugin){
                continue;
            }
            installedPlugins.add(pkg.packageName);

            // Add newly installed plugins to plugin db
            if(!mPlugins.containsKey(pkg.packageName)){
                PluginParser.Plugin plugin = mPluginParser.parsePlugin(pkg.packageName);
                if(null!=plugin){
                    long id = addPluginToDB(plugin);
                    if(-1!=id){
                        plugin.id = id;
                        mPlugins.put(pkg.packageName,plugin);
                        if(DEBUG){
                            Log.i(TAG,"New plugin added to db: "+plugin.packageName);
                        }
                    }else{
                        Log.e(TAG,"Failed to add new plugin in plugin db: "+pkg.packageName);
                    }
                }else{
                    Log.e(TAG,"Failed to parse plugin package: "+pkg.packageName);
                }
            }
        }

        // Remove uninstalled plugins from db
        // and add remaining plugins to the data holder
        Set savedPlugins = mPlugins.keySet();
        Iterator it = savedPlugins.iterator();
        while(it.hasNext()){
            String packageName = (String)it.next();
            if(!installedPlugins.contains(packageName)){
                // Remove plugin
                if(1 == removePluginFromDB(mPlugins.get(packageName))){
                    mPlugins.remove(packageName);
                    if(DEBUG){
                        Log.i(TAG,"Remove plugin from db: "+packageName);
                    }
                }else{
                    Log.e(TAG,"Failed to delete plugin from db:"+packageName);
                }
            }else{
                // Add plugin to the adapter used by fragment to display list of plugins
                mPluginList.add(mPlugins.get(packageName));
            }
        }

        if(DEBUG){
            Log.i(TAG,"Number of installed plugins: "+installedPlugins.size());
            Log.i(TAG,"Number of plugins: "+ mPlugins.size() + ":" + mPluginList.size());
            Log.i(TAG,"List of plugins:");
            for(String packageName : mPlugins.keySet()){
                Log.i(TAG,packageName);
            }
        }
    }

    public void retrievePluginsFromDB(){
        // Query plugin db
        Cursor cursor = getActivity().getContentResolver()
                .query(PluginContract.PluginEntry.CONTENT_URI,null,null,null,null);

        if(null == cursor){
            Log.e(TAG,"Failed to retrieve plugins from plugin db");
        }else if(cursor.getCount()>0){
            // Get the index of the columns we are interested in
            int idIndex = cursor.getColumnIndex(PluginContract.PluginEntry._ID);
            int packageNameIndex = cursor.getColumnIndex(PluginContract.PluginEntry.COLUMN_NAME_PACKAGE_NAME);
            int isActiveIndex = cursor.getColumnIndex(PluginContract.PluginEntry.COLUMN_NAME_IS_ACTIVE);

            // Iterate over all rows and load the corresponding plugin information
            // Initial position of the cursor is -1 so we need to call moveToNext before
            // we access the first row
            while(cursor.moveToNext()){
                String packageName = cursor.getString(packageNameIndex);
                PluginParser.Plugin plugin = new PluginParser.Plugin(packageName);
                plugin.id = cursor.getInt(idIndex);
                plugin.isActive = cursor.getInt(isActiveIndex)==1?true:false;

                mPlugins.put(plugin.packageName,plugin);
            }

        }

        if(DEBUG) {
            Log.i(TAG, "Number of plugins loaded form db:" + mPlugins.size());
        }
    }

    /**
     * Add a new plugin to the plugin db
     * @param plugin Plugin to be added
     * @return ID of the inserted plugin record or -1 in case of failure
     */
    public long addPluginToDB(PluginParser.Plugin plugin){
        // Prepare insert query
        ContentValues values = new ContentValues();

        // Plugin package name
        values.put(PluginContract.PluginEntry.COLUMN_NAME_PACKAGE_NAME,plugin.packageName);

        // Newly inserted plugin is by default inactive (0)
        values.put(PluginContract.PluginEntry.COLUMN_NAME_IS_ACTIVE,0);

        // Execute query
        Uri uri = getActivity().getContentResolver().insert(PluginContract.PluginEntry.CONTENT_URI,values);

        // Return id of the newly inserted record
        return (null==uri)?-1:Long.parseLong(uri.getLastPathSegment());
    }

    public int removePluginFromDB(PluginParser.Plugin plugin){
        // Prepare delete query
        Uri uri = PluginContract.PluginEntry.uriWithId(plugin.id);

        // Execute query
        int deletedRows = getActivity().getContentResolver().delete(uri,null,null);

        return deletedRows;
    }

    public void updatePlugin(PluginParser.Plugin plugin){
        if(1 == updatePluginDB(plugin)){
            mPlugins.get(plugin.packageName).isActive = plugin.isActive;
            if(DEBUG){
                Log.i(TAG,"Plugin updated in db: "+plugin.packageName+":"+plugin.isActive);
            }
        }else{
            Log.e(TAG,"Failed to update plugin in db:"+plugin.packageName+":"+plugin.isActive);
        }
    }

    public int updatePluginDB(PluginParser.Plugin plugin){
        // Prepare update query
        ContentValues values = new ContentValues();

        // Plugin package name
        values.put(PluginContract.PluginEntry.COLUMN_NAME_PACKAGE_NAME,plugin.packageName);

        // Newly inserted plugin is by default inactive (0)
        int is_active = plugin.isActive?1:0;
        values.put(PluginContract.PluginEntry.COLUMN_NAME_IS_ACTIVE,is_active);

        // Get URI of the plugin record that should be updated
        Uri uri = PluginContract.PluginEntry.uriWithId(plugin.id);

        // Execute query
        int updatedRows = getActivity().getContentResolver().update(uri,values,null,null);

        return updatedRows;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PluginParser.Plugin item);
        void onPluginActivation(PluginParser.Plugin item);
    }
}
