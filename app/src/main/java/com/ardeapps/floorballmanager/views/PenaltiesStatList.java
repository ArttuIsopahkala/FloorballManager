package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.PenaltiesStatListAdapter;
import com.ardeapps.floorballmanager.fragments.StatsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arttu on 5.5.2019.
 */

public class PenaltiesStatList extends LinearLayout {

    PenaltiesStatListAdapter adapter;
    ListView penaltiesList;
    TextView nameHeader;
    TextView gamesHeader;
    TextView min2Header;
    TextView min5Header;
    TextView min10Header;
    TextView min20Header;
    TextView penaltiesPerGameHeader;
    TextView penaltiesHeader;

    ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems;
    Map<StatsFragment.HeaderType, TextView> headers = new HashMap<>();

    public void setItems(ArrayList<StatsFragment.PlayerStatsItem> playerStatsItems, StatsFragment.HeaderType sort) {
        ArrayList<StatsFragment.PlayerStatsItem> sorted = StatsFragment.sortStatsList(headers, sort, playerStatsItems);
        this.playerStatsItems = sorted;
        adapter.setItems(sorted);
        adapter.notifyDataSetChanged();
    }

    public PenaltiesStatList(Context context) {
        super(context);
        createView(context);
    }

    public PenaltiesStatList(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.stat_list_penalties, this);
        penaltiesList = findViewById(R.id.penaltiesList);
        nameHeader = findViewById(R.id.nameHeader);
        gamesHeader = findViewById(R.id.gamesHeader);
        min2Header = findViewById(R.id.min2Header);
        min5Header = findViewById(R.id.min5Header);
        min10Header = findViewById(R.id.min10Header);
        min20Header = findViewById(R.id.min20Header);
        penaltiesPerGameHeader = findViewById(R.id.penaltiesPerGameHeader);
        penaltiesHeader = findViewById(R.id.penaltiesHeader);

        adapter = new PenaltiesStatListAdapter(AppRes.getActivity());
        penaltiesList.setAdapter(adapter);

        setHeaderListener(nameHeader, StatsFragment.HeaderType.NAMES);
        setHeaderListener(gamesHeader, StatsFragment.HeaderType.GAMES);
        setHeaderListener(min2Header, StatsFragment.HeaderType.MIN_2);
        setHeaderListener(min5Header, StatsFragment.HeaderType.MIN_5);
        setHeaderListener(min10Header, StatsFragment.HeaderType.MIN_10);
        setHeaderListener(min20Header, StatsFragment.HeaderType.MIN_20);
        setHeaderListener(penaltiesPerGameHeader, StatsFragment.HeaderType.PENALTIES_PER_GAME);
        setHeaderListener(penaltiesHeader, StatsFragment.HeaderType.PENALTIES);
    }

    private void setHeaderListener(TextView header, StatsFragment.HeaderType sort) {
        headers.put(sort, header);
        header.setOnClickListener(v -> setItems(playerStatsItems, sort));
    }
}
