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

        convertView = layoutInflater.inflate(R.layout.plugindetail_group, null);
        TextView textViewGroup  = convertView.findViewById(R.id.plugindetail_group_text);

        final String headerTitle = (String) getGroup(groupPosition);

        textViewGroup.setTypeface(null, Typeface.BOLD);
        textViewGroup.setText(headerTitle);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final String childText = (String) getChild(groupPosition, childPosition);

        convertView = layoutInflater.inflate(R.layout.plugindetail_child_check, null);
        TextView textViewChild  = convertView.findViewById(R.id.plugindetail_child_check_text);

        // Select target apis
        final CheckBox apiCheckView = convertView.findViewById(R.id.plugindetail_child_check_box);
        final String targetPackageName = (String) getGroup(groupPosition);
        if(mPlugin.targetPackageToAPIs.containsKey(targetPackageName) &&
                mPlugin.targetPackageToAPIs.get(targetPackageName).contains(childText)){
            apiCheckView.setChecked(true);
        } else {
            apiCheckView.setChecked(false);
        }

        // Setup listener
        apiCheckView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            PackageManagerBridge.activatePlugin(context.getPackageManager(),mPlugin.packageName,targetPackageName, childText,isChecked);
            }
        });
        textViewChild.setTypeface(null, Typeface.BOLD);
        textViewChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
