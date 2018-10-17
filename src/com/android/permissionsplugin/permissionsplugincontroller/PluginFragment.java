package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.Context;
import android.content.pm.PackageManager;
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
import java.util.List;
import java.util.Map;

import com.android.permissionsplugin.PermissionsPlugin;

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
    private Map<String, PermissionsPlugin> mPlugins = new HashMap<>();

    // A list containing plugins
    private List<PermissionsPlugin> mPluginList = new ArrayList<>();


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PluginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                for(PermissionsPlugin plugin : mPluginList){
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

    public void populatePluginList(){
        mPluginList = getActivity().getPackageManager().getInstalledPermissionsPlugins();
        for(PermissionsPlugin p : mPluginList){
            mPlugins.put(p.packageName,p);
        }
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
        void onListFragmentInteraction(PermissionsPlugin item);
        void onPluginActivation(PermissionsPlugin item);
    }
}
