package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.PointsStatListAdapter;
import com.ardeapps.floorballmanager.fragments.StatsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 5.5.2019.
 */

public class PointsStatList extends LinearLayout {

    PointsStatListAdapter adapter;
    ListView statsList;
    TextView nameHeader;
    TextView gamesHeader;
    TextView plusHeader;
    TextView minusHeader;
    TextView plusMinusHeader;
    TextView goalsHeader;
    TextView assistsHeader;
    TextView pointsHeader;

    ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems;
    Map<StatsFragment.HeaderType, TextView> headers = new HashMap<>();

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems, StatsFragment.HeaderType sort) {
        ArrayList<StatsFragment.PlayerStatsItem> sorted = StatsFragment.sortStatsList(headers, sort, playerStatsItems);
        this.playerStatsItems = sorted;
        adapter.setItems(sorted);
        adapter.notifyDataSetChanged();
    }

    public PointsStatList(Context context) {
        super(context);
        createView(context);
    }

    public PointsStatList(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.stat_list_points, this);
        statsList = findViewById(R.id.playerList);
        nameHeader = findViewById(R.id.nameHeader);
        gamesHeader = findViewById(R.id.gamesHeader);
        plusHeader = findViewById(R.id.plusHeader);
        minusHeader = findViewById(R.id.minusHeader);
        plusMinusHeader = findViewById(R.id.plusMinusHeader);
        goalsHeader = findViewById(R.id.goalsHeader);
        assistsHeader = findViewById(R.id.assistsHeader);
        pointsHeader = findViewById(R.id.pointsHeader);

        adapter = new PointsStatListAdapter(AppRes.getActivity());
        statsList.setAdapter(adapter);

        setHeaderListener(nameHeader, StatsFragment.HeaderType.NAMES);
        setHeaderListener(gamesHeader, StatsFragment.HeaderType.GAMES);
        setHeaderListener(plusHeader, StatsFragment.HeaderType.PLUSES);
        setHeaderListener(minusHeader, StatsFragment.HeaderType.MINUSES);
        setHeaderListener(plusMinusHeader, StatsFragment.HeaderType.PLUS_MINUSES);
        setHeaderListener(goalsHeader, StatsFragment.HeaderType.GOALS);
        setHeaderListener(assistsHeader, StatsFragment.HeaderType.ASSISTS);
        setHeaderListener(pointsHeader, StatsFragment.HeaderType.POINTS);
    }

    private void setHeaderListener(TextView header, StatsFragment.HeaderType sort) {
        headers.put(sort, header);
        header.setOnClickListener(v -> setItems(playerStatsItems, sort));
    }
}
