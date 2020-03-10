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

import java.util.ArrayList;

public class PointsStatListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<StatsFragment.PlayerStatsItem> items = new ArrayList<>();

    public PointsStatListAdapter(Context ctx) {
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
        final PointsHolder pointsHolder = new PointsHolder();
        if (v == null) {
            v = inflater.inflate(R.layout.stat_list_item_points, null);
        }

        if (position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_third));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background));
        }

        pointsHolder.nameText = v.findViewById(R.id.nameText);
        pointsHolder.gamesText = v.findViewById(R.id.gamesText);
        pointsHolder.plusText = v.findViewById(R.id.plusText);
        pointsHolder.minusText = v.findViewById(R.id.minusText);
        pointsHolder.plusMinusText = v.findViewById(R.id.plusMinusText);
        pointsHolder.goalsText = v.findViewById(R.id.goalsText);
        pointsHolder.assistsText = v.findViewById(R.id.assistsText);
        pointsHolder.pointsText = v.findViewById(R.id.pointsText);

        final StatsFragment.PlayerStatsItem item = items.get(position);

        pointsHolder.nameText.setText(item.player.getName());
        pointsHolder.gamesText.setText(String.valueOf(item.gameCount));
        pointsHolder.plusText.setText(String.valueOf(item.statsData.pluses));
        pointsHolder.minusText.setText(String.valueOf(item.statsData.minuses));
        pointsHolder.plusMinusText.setText(String.valueOf(item.statsData.plusMinus));
        pointsHolder.goalsText.setText(String.valueOf(item.statsData.scores));
        pointsHolder.assistsText.setText(String.valueOf(item.statsData.assists));
        pointsHolder.pointsText.setText(String.valueOf(item.statsData.points));

        v.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(item.player));
        return v;
    }

    public class PointsHolder {
        TextView nameText;
        TextView gamesText;
        TextView plusText;
        TextView minusText;
        TextView plusMinusText;
        TextView goalsText;
        TextView assistsText;
        TextView pointsText;
    }
}
