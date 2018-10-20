package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.permissionsplugin.PermissionsPlugin;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionsPluginDetailViewAdapter extends BaseExpandableListAdapter {

    private static  final String TAG = "heimdall";

    private Context context;

    private PermissionsPlugin mPlugin;

    // group titles
    private List<String> listDataGroup;

    // child data
    private HashMap<Integer, List<String>> listDataChild;

    public PermissionsPluginDetailViewAdapter(Context context, List<String> listDataGroup,
                                              HashMap<Integer, List<String>> listChildData, PermissionsPlugin plugin) {
        this.context = context;
        this.listDataGroup = listDataGroup;
        this.listDataChild = listChildData;
        this.mPlugin = plugin;
    }

    @Override
    public int getGroupCount() {
        return this.listDataGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textViewGroup;

        // Setup appropriate view based on group content
        switch (groupPosition){
            case PermissionsPluginDetailFragment.GROUP_POSITION_IS_ACTIVE:
                convertView = layoutInflater.inflate(R.layout.plugindetail_group_switch, null);
                textViewGroup = convertView.findViewById(R.id.plugindetail_group_switch_text);

                // Set activation status for this plugin
                Switch isActiveSwitch = convertView.findViewById(R.id.plugindetail_group_switch_box);
                isActiveSwitch.setChecked(mPlugin.isActive);

                // Set up listener
                isActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mPlugin.isActive = isChecked;
                        PackageManagerBridge.setActivationStatusForPermissionsPlugin(context.getPackageManager(), mPlugin.packageName,mPlugin.isActive);
                    }
                });

                break;
            case PermissionsPluginDetailFragment.GROUP_POSITION_SUPPORTED_PACKAGES:
            case PermissionsPluginDetailFragment.GROUP_POSITION_SUPPORTED_APIS:
                convertView = layoutInflater.inflate(R.layout.plugindetail_group_check, null);
                textViewGroup  = convertView.findViewById(R.id.plugindetail_group_check_text);
                break;
            default:
                Log.e(TAG,"Unknown group position " + groupPosition);
                throw new IllegalArgumentException();
        }

        final String headerTitle = (String) getGroup(groupPosition);

        textViewGroup.setTypeface(null, Typeface.BOLD);
        textViewGroup.setText(headerTitle);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final String childText = (String) getChild(groupPosition, childPosition);

        TextView textViewChild;

        // Setup appropriate view based on group content
        switch (groupPosition){
            case PermissionsPluginDetailFragment.GROUP_POSITION_SUPPORTED_PACKAGES:
                convertView = layoutInflater.inflate(R.layout.plugindetail_child_check, null);
                textViewChild  = convertView.findViewById(R.id.plugindetail_child_check_text);

                // Select target packages
                final CheckBox pkgCheckView = convertView.findViewById(R.id.plugindetail_child_check_box);
                if(mPlugin.targetPackages.contains(PermissionsPlugin.ALL_PACKAGES) ||
                        mPlugin.targetPackages.contains(childText)){
                    pkgCheckView.setChecked(true);
                }

                // Setup listener
                pkgCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        List<String> targetPackages = new ArrayList<>();
                        targetPackages.add(childText);
                        if(isChecked){
                            // Add this package to the list of target packages
                            PackageManagerBridge.addTargetPackagesForPlugin(context.getPackageManager(),mPlugin.packageName,targetPackages,false);
                        }else{
                            // Remove this package from the list of target packages
                            PackageManagerBridge.removeTargetPackagesForPlugin(context.getPackageManager(),mPlugin.packageName,targetPackages);
                        }
                    }
                });

                break;
            case PermissionsPluginDetailFragment.GROUP_POSITION_SUPPORTED_APIS:
                convertView = layoutInflater.inflate(R.layout.plugindetail_child_check, null);
                textViewChild  = convertView.findViewById(R.id.plugindetail_child_check_text);

                // Select target apis
                final CheckBox apiCheckView = convertView.findViewById(R.id.plugindetail_child_check_box);
                if(mPlugin.targetAPIs.contains(childText)){
                    apiCheckView.setChecked(true);
                }

                // Setup listener
                apiCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        List<String> targetAPIs = new ArrayList<>();
                        targetAPIs.add(childText);
                        if(isChecked){
                            // Add this package to the list of target packages
                            PackageManagerBridge.addTargetAPIsForPlugin(context.getPackageManager(),mPlugin.packageName,targetAPIs,false);
                        }else{
                            // Remove this package from the list of target packages
                            PackageManagerBridge.removeTargetAPIsForPlugin(context.getPackageManager(),mPlugin.packageName,targetAPIs);
                        }
                    }
                });


                break;
            default:
                Log.e(TAG,"Unknown child position " + childPosition + " in group " + groupPosition);
                throw new IllegalArgumentException();
        }

        textViewChild.setTypeface(null, Typeface.BOLD);
        textViewChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
