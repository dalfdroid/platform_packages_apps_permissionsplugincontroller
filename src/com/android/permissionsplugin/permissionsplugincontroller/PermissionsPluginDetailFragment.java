package com.android.permissionsplugin.permissionsplugincontroller;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.permissionsplugin.PermissionsPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a single PermissionsPlugin detail screen.
 * This fragment is either contained in a {@link PermissionsPluginListActivity}
 * in two-pane mode (on tablets) or a {@link PermissionsPluginDetailActivity}
 * on handsets.
 */
public class PermissionsPluginDetailFragment extends Fragment {

    private static final String TAG = "heimdall";

    /**
     * The fragment argument representing the plugin that this fragment represents.
     */
    public static final String ARG_PERMISSIONS_PLUGIN = "permissions_plugin";

    /**
     * The permissions plugin this fragment is presenting.
     */
    private PermissionsPlugin mPlugin;

    private ExpandableListView expandableListView;

    private PermissionsPluginDetailViewAdapter expandableListViewAdapter;

    private List<String> listDataGroup = new ArrayList<>();

    private HashMap<Integer, List<String>> listDataChild = new HashMap<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PermissionsPluginDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_PERMISSIONS_PLUGIN)) {
            // Load the permissions plugin specified by the fragment arguments.
            mPlugin = getArguments().getParcelable(ARG_PERMISSIONS_PLUGIN);
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && mPlugin != null) {
                appBarLayout.setTitle(mPlugin.packageName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.permissionsplugin_detail, container, false);
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.plugin_expandableListView);

        if(mPlugin == null){
            Log.e(TAG, "Failed to load plugin details due to null plugin");
            Toast.makeText(getActivity().getApplicationContext(),
                    "Failed to load plugin details",
                    Toast.LENGTH_SHORT).show();
            return rootView;
        }

        setupListeners();

        loadPluginDetails();

        return rootView;
    }

    private void loadPluginDetails(){

        // Construct a data group for each supported package.
        // The children of each data group are the supported APIs.
        // Activate/deactivate an API/package based on user selection.
        int nPackages = mPlugin.supportedPackages.size();
        for (int i=0; i<nPackages; i++) {
            String supportedPackage = mPlugin.supportedPackages.get(i);
            listDataGroup.add(supportedPackage);
            List<String> listChildren = new ArrayList<>(mPlugin.supportedAPIs);
            listDataChild.put(i,listChildren);
        }

        // Initialize data adapter and populate list view
        expandableListViewAdapter = new PermissionsPluginDetailViewAdapter(getActivity(), listDataGroup, listDataChild, mPlugin);
        expandableListView.setAdapter(expandableListViewAdapter);
    }

    private void setupListeners(){
//
//        // ExpandableListView on child click listener
//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getActivity().getApplicationContext(),
//                        listDataGroup.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataGroup.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
//                return false;
//            }
//        });
//
//        // ExpandableListView Group expanded listener
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        listDataGroup.get(groupPosition) + " " + getString(R.string.text_collapsed),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // ExpandableListView Group collapsed listener
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        listDataGroup.get(groupPosition) + " " + getString(R.string.text_collapsed),
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }


}
