package com.ardeapps.floorballcoach.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.LinesPagerAdapter;
import com.ardeapps.floorballcoach.fragments.LineFragment;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.viewObjects.LineFragmentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Arttu on 5.5.2019.
 */

public class LineUpSelector extends LinearLayout {

    private Listener mListener = null;

    public interface Listener {
        void onLinesChanged();
    }

    TabLayout tabLayout;
    ViewPager linesPager;
    LinesPagerAdapter linesAdapter;
    Map<Integer, Line> lines = new HashMap<>();
    List<LineFragment> lineFragments = new ArrayList<>();

    public LineUpSelector(Context context) {
        super(context);
    }

    public LineUpSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void createView(Fragment parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        inflater.inflate(R.layout.container_line_up, this);
        linesPager = findViewById(R.id.linesPager);
        tabLayout = findViewById(R.id.tabLayout);

        lineFragments = new ArrayList<>();
        lineFragments.add(createLineFragment(1));
        lineFragments.add(createLineFragment(2));
        lineFragments.add(createLineFragment(3));
        lineFragments.add(createLineFragment(4));

        linesAdapter = new LinesPagerAdapter(parent.getFragmentManager(), lineFragments);

        linesPager.setOffscreenPageLimit(linesAdapter.getCount());
        linesPager.setAdapter(linesAdapter);
        tabLayout.setupWithViewPager(linesPager);
        linesPager.setCurrentItem(0);
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
        for(LineFragment lineFragment : lineFragments) {
            LineFragmentData data = lineFragment.getData();
            Line line = lines.get(data.getLineNumber());
            data.setLine(line);
            lineFragment.setData(data);
        }
    }

    public Map<Integer, Line> getLines() {
        Map<Integer, Line> lines = new HashMap<>();
        for(LineFragment lineFragment : lineFragments) {
            LineFragmentData data = lineFragment.getData();
            if(data.getLine() != null) {
                lines.put(data.getLineNumber(), data.getLine());
            }
        }
        return lines;
    }

    public void updateLineFragments() {
        for(LineFragment lineFragment : lineFragments) {
            lineFragment.update();
        }
    }

    private LineFragment createLineFragment(final int lineNumber) {
        LineFragment lineFragment = new LineFragment();
        LineFragmentData data = new LineFragmentData();
        data.setLineNumber(lineNumber);
        lineFragment.setData(data);
        lineFragment.setListener(new LineFragment.Listener() {

            @Override
            public void onLineChanged(Line line, String playerId) {
                // Remove old player if he is in other lines
                for(final Line existingLine : lines.values()) {
                    if(lineNumber != existingLine.getLineNumber()) {
                        Iterator<Map.Entry<String, String>> it = existingLine.getPlayerIdMap().entrySet().iterator();
                        while (it.hasNext()) {
                            String existingPlayerId = it.next().getValue();
                            if(existingPlayerId.equals(playerId)) {
                                it.remove();
                                linesAdapter.updateLineFragment(existingLine);
                            }
                        }
                    }
                }
                lines.put(lineNumber, line);

                linesAdapter.updateLineFragment(line);
                mListener.onLinesChanged();
            }
        });

        return lineFragment;
    }

}
