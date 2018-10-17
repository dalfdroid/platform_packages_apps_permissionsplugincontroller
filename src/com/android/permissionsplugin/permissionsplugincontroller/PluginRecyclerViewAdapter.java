package com.android.permissionsplugin.permissionsplugincontroller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.permissionsplugin.PermissionsPlugin;

import com.android.permissionsplugin.permissionsplugincontroller.PluginFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PermissionsPlugin} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PluginRecyclerViewAdapter extends RecyclerView.Adapter<PluginRecyclerViewAdapter.ViewHolder> {

    private final List<PermissionsPlugin> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PluginRecyclerViewAdapter(List<PermissionsPlugin> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_plugin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PermissionsPlugin plugin = mValues.get(position);
        holder.mPlugin = plugin;
        holder.mPackageNameView.setText(plugin.packageName);
        holder.mIsActiveView.setChecked(plugin.isActive);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mPlugin);
                }
            }
        });

        // Register listener for active switch
        holder.mIsActiveView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update plugin activation and notify the callback interface
                holder.mPlugin.isActive = isChecked;
                mListener.onPluginActivation(holder.mPlugin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPackageNameView;
        public final Switch mIsActiveView;
        public PermissionsPlugin mPlugin;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPackageNameView = (TextView) view.findViewById(R.id.plugin_name);
            mIsActiveView = (Switch) view.findViewById(R.id.plugin_active);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPackageNameView.getText() + "'";
        }
    }
}
