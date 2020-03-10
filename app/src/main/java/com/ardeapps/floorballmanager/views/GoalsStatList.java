package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.GoalsStatListAdapter;
import com.ardeapps.floorballmanager.fragments.StatsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 5.5.2019.
 */

public class GoalsStatList extends LinearLayout {

    GoalsStatListAdapter adapter;
    ListView goalsList;
    TextView nameHeader;
    TextView gamesHeader;
    TextView yvHeader;
    TextView avHeader;
    TextView rlHeader;
    TextView goalsPerGameHeader;
    TextView goalsHeader;

    ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems;
    Map<StatsFragment.HeaderType, TextView> headers = new HashMap<>();

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems, StatsFragment.HeaderType sort) {
        ArrayList<StatsFragment.PlayerStatsItem> sorted = StatsFragment.sortStatsList(headers, sort, playerStatsItems);
        this.playerStatsItems = sorted;
        adapter.setItems(sorted);
        adapter.notifyDataSetChanged();
    }

    public GoalsStatList(Context context) {
        super(context);
        createView(context);
    }

    public GoalsStatList(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.stat_list_goals, this);
        goalsList = findViewById(R.id.goalsList);
        nameHeader = findViewById(R.id.nameHeader);
        gamesHeader = findViewById(R.id.gamesHeader);
        yvHeader = findViewById(R.id.yvHeader);
        avHeader = findViewById(R.id.avHeader);
        rlHeader = findViewById(R.id.rlHeader);
        goalsPerGameHeader = findViewById(R.id.goalsPerGameHeader);
        goalsHeader = findViewById(R.id.goalsHeader);

        adapter = new GoalsStatListAdapter(AppRes.getActivity());
        goalsList.setAdapter(adapter);

        setHeaderListener(nameHeader, StatsFragment.HeaderType.NAMES);
        setHeaderListener(gamesHeader, StatsFragment.HeaderType.GAMES);
        setHeaderListener(yvHeader, StatsFragment.HeaderType.YV_GOALS);
        setHeaderListener(avHeader, StatsFragment.HeaderType.AV_GOALS);
        setHeaderListener(rlHeader, StatsFragment.HeaderType.RL_GOALS);
        setHeaderListener(goalsPerGameHeader, StatsFragment.HeaderType.GOALS_PER_GAME);
        setHeaderListener(goalsHeader, StatsFragment.HeaderType.GOALS);
    }

    private void setHeaderListener(TextView header, StatsFragment.HeaderType sort) {
        headers.put(sort, header);
        header.setOnClickListener(v -> setItems(playerStatsItems, sort));
    }
}
