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

public class GoalsStatListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<StatsFragment.PlayerStatsItem> items = new ArrayList<>();

    public GoalsStatListAdapter(Context ctx) {
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> pointsViewItems) {
        this.items = pointsViewItems;
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
        final GoalsHolder goalsHolder = new GoalsHolder();
        if (v == null) {
            v = inflater.inflate(R.layout.stat_list_item_goals, null);
        }

        if (position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_third));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background));
        }

        goalsHolder.nameText = v.findViewById(R.id.nameText);
        goalsHolder.gamesText = v.findViewById(R.id.gamesText);
        goalsHolder.yvText = v.findViewById(R.id.yvText);
        goalsHolder.avText = v.findViewById(R.id.avText);
        goalsHolder.rlText = v.findViewById(R.id.rlText);
        goalsHolder.goalsPerGameText = v.findViewById(R.id.goalsPerGameText);
        goalsHolder.goalsText = v.findViewById(R.id.goalsText);

        final StatsFragment.PlayerStatsItem item = items.get(position);

        goalsHolder.nameText.setText(item.player.getName());
        goalsHolder.gamesText.setText(String.valueOf(item.gameCount));
        goalsHolder.yvText.setText(String.valueOf(item.statsData.yvScores));
        goalsHolder.avText.setText(String.valueOf(item.statsData.avScores));
        goalsHolder.rlText.setText(String.valueOf(item.statsData.rlScores));
        goalsHolder.goalsPerGameText.setText(StringUtils.getDecimalText(item.statsData.scoresPerGame));
        goalsHolder.goalsText.setText(String.valueOf(item.statsData.scores));

        v.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(item.player));
        return v;
    }

    public class GoalsHolder {
        TextView nameText;
        TextView gamesText;
        TextView yvText;
        TextView avText;
        TextView rlText;
        TextView goalsPerGameText;
        TextView goalsText;
    }
}
