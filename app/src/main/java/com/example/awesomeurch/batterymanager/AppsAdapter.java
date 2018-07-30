package com.example.awesomeurch.batterymanager;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Awesome Urch on 01/05/2018.
 * My AppsAdapter with ArrayAdapter
 */

public class AppsAdapter extends BaseAdapter {
    private Context context;
    private List<UsageStats> apps;
    private PackageManager pm;
    private ApplicationInfo ai;

    public AppsAdapter(Context context, List<UsageStats> apps) {
        this.context = context;
        this.apps = apps;
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.app_item, parent, false);
        }

        UsageStats currentItem = (UsageStats) getItem(position);
        // Get the data item for this position
        //UsageStats apps = getItem(position);
        pm = context.getPackageManager();


        try {
            ai = pm.getApplicationInfo( currentItem.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        final Drawable appIcon = (Drawable) (ai != null ? pm.getApplicationIcon(ai) : "");
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.appName);
        ImageView appLogo = convertView.findViewById(R.id.appLogo);

        //tvName.setText(currentItem.getPackageName());
        tvName.setText(applicationName);
        appLogo.setImageDrawable(appIcon);
        //appLogo.setImageIcon(appIcon);

        //tvHome.setText(user.getHometown());
        // Return the completed view to render on screen
        return convertView;
    }
}
