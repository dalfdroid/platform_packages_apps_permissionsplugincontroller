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

    /**
     * List of constants to identify group type w.r.t group position
     */
    public static final int GROUP_POSITION_IS_ACTIVE = 0;
    public static final int GROUP_POSITION_SUPPORTED_PACKAGES = 1;
    public static final int GROUP_POSITION_SUPPORTED_APIS = 2;

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

        /**
         * plugin.is_active
         * First group is plugin activation status.
         * This group does not have any child
         */
        listDataGroup.add(getResources().getString(R.string.is_active));
        List<String> activationChildren = new ArrayList<>();
        listDataChild.put(GROUP_POSITION_IS_ACTIVE,activationChildren);

        /**
         * plugin.supportedPackages
         * Second group is packages supported by plugin.
         * The selected children of this group indicates target packages
         */
        listDataGroup.add(getResources().getString(R.string.supported_packages));
        List<String> packagesChildren = new ArrayList<>(mPlugin.supportedPackages);

        // Add all installed packages in the system if supported packages has "*"
        // Do not remove '*' as it is used to select all packages
        if(packagesChildren.contains(PermissionsPlugin.ALL_PACKAGES)){
            List<PackageInfo> installedPackages = getActivity().getPackageManager().getInstalledPackages(0);
            for(PackageInfo pkg : installedPackages){
                // Do not add duplicate packages
                if(!packagesChildren.contains(pkg.packageName)){
                    packagesChildren.add(pkg.packageName);
                }
            }
        }
        listDataChild.put(GROUP_POSITION_SUPPORTED_PACKAGES,packagesChildren);

        /**
         * plugin.supportedAPIs
         * Second group is APIs supported by plugin.
         * The selected children of this group indicates target APIs
         */
        listDataGroup.add(getResources().getString(R.string.supported_api));
        List<String> apisChildren = new ArrayList<>(mPlugin.supportedAPIs);
        listDataChild.put(GROUP_POSITION_SUPPORTED_APIS,apisChildren);

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
