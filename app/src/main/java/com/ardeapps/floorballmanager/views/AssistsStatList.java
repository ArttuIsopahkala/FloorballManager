package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.AssistsStatListAdapter;
import com.ardeapps.floorballmanager.fragments.StatsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 5.5.2019.
 */

public class AssistsStatList extends LinearLayout {

    AssistsStatListAdapter adapter;
    ListView assistsList;
    TextView nameHeader;
    TextView gamesHeader;
    TextView yvHeader;
    TextView avHeader;
    TextView assistsPerGameHeader;
    TextView assistsHeader;

    ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems;
    Map<StatsFragment.HeaderType, TextView> headers = new HashMap<>();

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems, StatsFragment.HeaderType sort) {
        ArrayList<StatsFragment.PlayerStatsItem> sorted = StatsFragment.sortStatsList(headers, sort, playerStatsItems);
        this.playerStatsItems = sorted;
        adapter.setItems(sorted);
        adapter.notifyDataSetChanged();
    }

    public AssistsStatList(Context context) {
        super(context);
        createView(context);
    }

    public AssistsStatList(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.stat_list_assists, this);
        assistsList = findViewById(R.id.goalsList);
        nameHeader = findViewById(R.id.nameHeader);
        gamesHeader = findViewById(R.id.gamesHeader);
        yvHeader = findViewById(R.id.yvHeader);
        avHeader = findViewById(R.id.avHeader);
        assistsPerGameHeader = findViewById(R.id.assistsPerGameHeader);
        assistsHeader = findViewById(R.id.assistsHeader);

        adapter = new AssistsStatListAdapter(AppRes.getActivity());
        assistsList.setAdapter(adapter);

        setHeaderListener(nameHeader, StatsFragment.HeaderType.NAMES);
        setHeaderListener(gamesHeader, StatsFragment.HeaderType.GAMES);
        setHeaderListener(yvHeader, StatsFragment.HeaderType.YV_ASSISTS);
        setHeaderListener(avHeader, StatsFragment.HeaderType.AV_ASSISTS);
        setHeaderListener(assistsPerGameHeader, StatsFragment.HeaderType.ASSISTS_PER_GAME);
        setHeaderListener(assistsHeader, StatsFragment.HeaderType.ASSISTS);
    }

    private void setHeaderListener(TextView header, StatsFragment.HeaderType sort) {
        headers.put(sort, header);
        header.setOnClickListener(v -> setItems(playerStatsItems, sort));
    }
}
