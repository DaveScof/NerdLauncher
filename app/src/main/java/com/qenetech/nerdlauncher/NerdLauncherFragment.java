package com.qenetech.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by davescof on 3/31/17.
 */

public class NerdLauncherFragment extends Fragment {
    private static final String TAG ="NerdLlauncherFragment";

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance (){
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_nerd_launcher, container, false);

        mRecyclerView  = (RecyclerView) view.findViewById(R.id.fragment_recyler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpAdapter();
        return view;
    }

    private void setUpAdapter (){
        Intent startUpIntent = new Intent(Intent.ACTION_MAIN);
        startUpIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List <ResolveInfo> activities = pm.queryIntentActivities(startUpIntent, 0);

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(pm).toString(), o2.loadLabel(pm).toString());
            }
        });

        Log.i(TAG, "Found " + activities.size() + " activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    private class ActivitHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ResolveInfo mActivity;
        TextView mTextView;

        public ActivitHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_activity, parent, false));
            mTextView = (TextView) itemView.findViewById(R.id.activity_textView);
        }

        public void bindActivity (ResolveInfo activity){
            mActivity = activity;
            String appName = activity.loadLabel(getActivity().getPackageManager()).toString();
            mTextView.setText(appName);
            mTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mActivity.activityInfo;
            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter <ActivitHolder> {

        List<ResolveInfo> mActivities;

        public ActivityAdapter (List<ResolveInfo> activities)
        {
            mActivities = activities;
        }

        @Override
        public ActivitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ActivitHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ActivitHolder holder, int position) {
            ResolveInfo activity = mActivities.get(position);
            holder.bindActivity(activity);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }
}
