package com.ardeapps.floorballmanager.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.fragments.StatsFragment;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.StringUtils;

import java.util.ArrayList;

public class AssistsStatListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<StatsFragment.PlayerStatsItem> items = new ArrayList<>();

    public AssistsStatListAdapter(Context ctx) {
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final AssistsHolder assistsHolder = new AssistsHolder();
        if (v == null) {
            v = inflater.inflate(R.layout.stat_list_item_assists, null);
        }

        if (position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_third));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background));
        }

        assistsHolder.nameText = v.findViewById(R.id.nameText);
        assistsHolder.gamesText = v.findViewById(R.id.gamesText);
        assistsHolder.yvText = v.findViewById(R.id.yvText);
        assistsHolder.avText = v.findViewById(R.id.avText);
        assistsHolder.assistsPerGameText = v.findViewById(R.id.assistsPerGameText);
        assistsHolder.assistsText = v.findViewById(R.id.assistsText);

        final StatsFragment.PlayerStatsItem item = items.get(position);

        assistsHolder.nameText.setText(item.player.getName());
        assistsHolder.gamesText.setText(String.valueOf(item.gameCount));
        assistsHolder.yvText.setText(String.valueOf(item.statsData.yvAssists));
        assistsHolder.avText.setText(String.valueOf(item.statsData.avAssists));
        assistsHolder.assistsPerGameText.setText(StringUtils.getDecimalText(item.statsData.assistsPerGame));
        assistsHolder.assistsText.setText(String.valueOf(item.statsData.assists));

        v.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(item.player));
        return v;
    }

    public class AssistsHolder {
        TextView nameText;
        TextView gamesText;
        TextView yvText;
        TextView avText;
        TextView assistsPerGameText;
        TextView assistsText;
    }
}
