package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.SaveLinesHandler;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.views.LineUpSelector;

import java.util.Map;


public class LinesFragment extends Fragment {

    LineUpSelector lineUpSelector;

    Map<Integer, Line> lines;

    public void refreshData() {
        lines = AppRes.getInstance().getLines();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lines, container, false);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);

        lineUpSelector.createView(this);
        lineUpSelector.setLines(lines);
        lineUpSelector.setListener(new LineUpSelector.Listener() {

            @Override
            public void onLinesChanged() {
                Map<Integer, Line> linesToSave = lineUpSelector.getLines();
                LinesResource.getInstance().saveLines(linesToSave, new SaveLinesHandler() {
                    @Override
                    public void onLinesSaved(Map<Integer, Line> lines) {
                        AppRes.getInstance().setLines(lines);

                    }
                });
            }
        });

        return v;
    }

}
