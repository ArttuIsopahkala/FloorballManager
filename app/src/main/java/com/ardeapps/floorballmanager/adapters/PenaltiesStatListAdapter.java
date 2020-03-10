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

public class PenaltiesStatListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<StatsFragment.PlayerStatsItem> items = new ArrayList<>();

    public PenaltiesStatListAdapter(Context ctx) {
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> penaltiesViewItems) {
        this.items = penaltiesViewItems;
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
        final PenaltiesHolder penaltiesHolder = new PenaltiesHolder();
        if (v == null) {
            v = inflater.inflate(R.layout.stat_list_item_penalties, null);
        }

        if (position % 2 == 0) {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_third));
        } else {
            v.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background));
        }

        penaltiesHolder.nameText = v.findViewById(R.id.nameText);
        penaltiesHolder.gamesText = v.findViewById(R.id.gamesText);
        penaltiesHolder.min2Text = v.findViewById(R.id.min2Text);
        penaltiesHolder.min5Text = v.findViewById(R.id.min5Text);
        penaltiesHolder.min10Text = v.findViewById(R.id.min10Text);
        penaltiesHolder.min20Text = v.findViewById(R.id.min20Text);
        penaltiesHolder.penaltiesPerGameText = v.findViewById(R.id.penaltiesPerGameText);
        penaltiesHolder.penaltiesText = v.findViewById(R.id.penaltiesText);

        final StatsFragment.PlayerStatsItem item = items.get(position);

        penaltiesHolder.nameText.setText(item.player.getName());
        penaltiesHolder.gamesText.setText(String.valueOf(item.gameCount));
        penaltiesHolder.min2Text.setText(String.valueOf(item.penaltiesData.penalties2min));
        penaltiesHolder.min5Text.setText(String.valueOf(item.penaltiesData.penalties5min));
        penaltiesHolder.min10Text.setText(String.valueOf(item.penaltiesData.penalties10min));
        penaltiesHolder.min20Text.setText(String.valueOf(item.penaltiesData.penalties20min));
        penaltiesHolder.penaltiesPerGameText.setText(StringUtils.getDecimalText(item.penaltiesData.penaltiesPerGame));
        penaltiesHolder.penaltiesText.setText(String.valueOf(item.penaltiesData.penalties));

        v.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(item.player));
        return v;
    }

    public class PenaltiesHolder {
        TextView nameText;
        TextView gamesText;
        TextView min2Text;
        TextView min5Text;
        TextView min10Text;
        TextView min20Text;
        TextView penaltiesPerGameText;
        TextView penaltiesText;
    }
}
