package com.android.permissionsplugin.permissionsplugincontroller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.android.permissionsplugin.PermissionsPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of PermissionsPlugins. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PermissionsPluginDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PermissionsPluginListActivity extends AppCompatActivity {

    private static final String TAG = "heimdall";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    // A list of permissions plugins
    private List<PermissionsPlugin> permissionsPlugins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissionsplugin_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.permissionsplugin_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.permissionsplugin_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        permissionsPlugins = PackageManagerBridge.getInstalledPermissionsPlugins(getPackageManager());
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, permissionsPlugins, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final PermissionsPluginListActivity mParentActivity;
        private final List<PermissionsPlugin> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionsPlugin plugin = (PermissionsPlugin) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(PermissionsPluginDetailFragment.ARG_PERMISSIONS_PLUGIN, plugin);
                    PermissionsPluginDetailFragment fragment = new PermissionsPluginDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.permissionsplugin_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PermissionsPluginDetailActivity.class);
                    intent.putExtra(PermissionsPluginDetailFragment.ARG_PERMISSIONS_PLUGIN, plugin);
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(PermissionsPluginListActivity parent,
                                      List<PermissionsPlugin> plugins,
                                      boolean twoPane) {
            mValues = plugins;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.permissionsplugin_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            PermissionsPlugin plugin = mValues.get(position);

            holder.itemView.setTag(plugin);
            holder.itemView.setOnClickListener(mOnClickListener);

            holder.mPackageNameView.setText(plugin.packageName);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mPackageNameView;

            ViewHolder(View view) {
                super(view);
                mPackageNameView = (TextView) view.findViewById(R.id.package_name);
            }
        }
    }
}
