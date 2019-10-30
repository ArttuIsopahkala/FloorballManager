package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.LinesPagerAdapter;
import com.ardeapps.floorballmanager.analyzer.AnalyzerService;
import com.ardeapps.floorballmanager.fragments.LineFragment;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.viewObjects.LineFragmentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Arttu on 5.5.2019.
 */

public class LineUpSelector extends LinearLayout {

    ImageView fieldPicture;
    TabLayout tabLayout;
    ViewPager linesPager;
    LinesPagerAdapter linesAdapter;
    TextView lineChemistryValueText;
    ProgressBar lineChemistryBar;
    RelativeLayout lineChemistryContainer;
    TextView fromLineText;
    Spinner toLineSpinner;
    IconView switchLinesIcon;
    RelativeLayout teamChemistryContainer;
    TextView teamChemistryValueText;
    ProgressBar teamChemistryBar;

    Map<Integer, Line> lines = new HashMap<>();
    List<LineFragment> lineFragments = new ArrayList<>();
    boolean showChemistry = false;

    public LineUpSelector(Context context) {
        super(context);
    }

    public LineUpSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void createView(Fragment parent, boolean enableChemistry, CreateViewListener listener) {
        showChemistry = false;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        inflater.inflate(R.layout.container_line_up, this);
        linesPager = findViewById(R.id.linesPager);
        tabLayout = findViewById(R.id.tabLayout);
        lineChemistryValueText = findViewById(R.id.lineChemistryValueText);
        lineChemistryContainer = findViewById(R.id.lineChemistryContainer);
        lineChemistryBar = findViewById(R.id.lineChemistryBar);
        fromLineText = findViewById(R.id.fromLineText);
        toLineSpinner = findViewById(R.id.toLineSpinner);
        switchLinesIcon = findViewById(R.id.switchLinesIcon);
        teamChemistryValueText = findViewById(R.id.teamChemistryValueText);
        teamChemistryBar = findViewById(R.id.teamChemistryBar);
        teamChemistryContainer = findViewById(R.id.teamChemistryContainer);
        fieldPicture = findViewById(R.id.fieldPicture);

        // Show chemistry bars
        lineChemistryContainer.setVisibility(enableChemistry ? View.VISIBLE : View.GONE);
        lineChemistryValueText.setText("-");
        lineChemistryBar.post(() -> lineChemistryBar.setProgress(0));
        teamChemistryContainer.setVisibility(enableChemistry ? View.VISIBLE : View.GONE);
        teamChemistryValueText.setText("-");
        teamChemistryBar.post(() -> teamChemistryBar.setProgress(0));

        Loader.show();
        fieldPicture.post(() -> {
            int fieldHeight = fieldPicture.getHeight();
            lineFragments = new ArrayList<>();
            lineFragments.add(createLineFragment(1, fieldHeight));
            lineFragments.add(createLineFragment(2, fieldHeight));
            lineFragments.add(createLineFragment(3, fieldHeight));
            lineFragments.add(createLineFragment(4, fieldHeight));

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linesPager.getLayoutParams();
            params.height = fieldHeight;
            linesPager.setLayoutParams(params);

            linesAdapter = new LinesPagerAdapter(parent.getChildFragmentManager(), lineFragments);
            linesPager.setOffscreenPageLimit(linesAdapter.getCount());
            linesPager.setAdapter(linesAdapter);
            tabLayout.setupWithViewPager(linesPager);
            linesPager.post(() -> {
                Loader.hide();
                linesPager.setCurrentItem(0);
                listener.onViewCreated();
            });
        });

        ArrayList<String> lineTitles = new ArrayList<>();
        lineTitles.add(AppRes.getContext().getString(R.string.line_number, "1"));
        lineTitles.add(AppRes.getContext().getString(R.string.line_number, "2"));
        lineTitles.add(AppRes.getContext().getString(R.string.line_number, "3"));
        lineTitles.add(AppRes.getContext().getString(R.string.line_number, "4"));

        Helper.setSpinnerAdapter(toLineSpinner, lineTitles);
        fromLineText.post(() -> fromLineText.setText(lineTitles.get(0)));
        Helper.setSpinnerSelection(toLineSpinner, 1);
        switchLinesIcon.setOnClickListener(v1 -> {
            int fromLineNumber = linesPager.getCurrentItem() + 1;
            int toLineNumber = toLineSpinner.getSelectedItemPosition() + 1;
            if(fromLineNumber != toLineNumber) {
                Map<Integer, Line> newLines = getLines();
                Line fromLine = newLines.get(fromLineNumber);
                Line toLine = newLines.get(toLineNumber);
                if(fromLine == null) {
                    fromLine = new Line();
                }
                fromLine.setLineNumber(toLineNumber);
                if(toLine == null) {
                    toLine = new Line();
                }
                toLine.setLineNumber(fromLineNumber);
                newLines.put(toLineNumber, fromLine);
                newLines.put(fromLineNumber, toLine);
                setLines(newLines);
                refreshLines(showChemistry);
                refreshChemistry();
            }
        });

        linesPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(final int position) {
                fromLineText.setText(lineTitles.get(position));
                if (position < 3) {
                    Helper.setSpinnerSelection(toLineSpinner, position + 1);
                } else {
                    Helper.setSpinnerSelection(toLineSpinner, 2);
                }
                refreshChemistry();
            }
        });

    }

    private LineFragment createLineFragment(final int lineNumber, final int fieldHeight) {
        LineFragment lineFragment = new LineFragment();
        LineFragmentData data = new LineFragmentData();
        data.setFieldHeight(fieldHeight);
        data.setLineNumber(lineNumber);
        lineFragment.setData(data);
        lineFragment.setListener((line, playerId) -> {
            // Remove old player if he is in other lines
            if (playerId != null) {
                for (final Line existingLine : lines.values()) {
                    if (lineNumber != existingLine.getLineNumber()) {
                        Iterator<Map.Entry<String, String>> it = existingLine.getPlayerIdMap().entrySet().iterator();
                        while (it.hasNext()) {
                            String existingPlayerId = it.next().getValue();
                            if (existingPlayerId.equals(playerId)) {
                                it.remove();
                                updateLineFragment(existingLine);
                            }
                        }
                    }
                }
            }
            // Refresh state of lines
            lines.put(lineNumber, line);
            updateLineFragment(line);

            refreshChemistry();
        });

        return lineFragment;
    }

    // Päivitetään kun pelaaja vaihtuu, pelaajakemiat analysoidaan tai kentällinen vaihtuu
    private void refreshChemistry() {
        if (showChemistry) {
            int position = linesPager.getCurrentItem();
            LineFragmentData data = lineFragments.get(position).getData();
            Line line = data.getLine();
            int lineChemistryPercent = AnalyzerService.getInstance().getLineChemistryPercent(line);
            lineChemistryValueText.setText(String.valueOf(lineChemistryPercent));
            lineChemistryBar.setProgress(lineChemistryPercent);

            Map<Integer, Line> lines = AppRes.getInstance().getLines();
            int teamChemistryPercent = AnalyzerService.getInstance().getTeamChemistryPercent(lines);
            teamChemistryValueText.setText(String.valueOf(teamChemistryPercent));
            teamChemistryBar.setProgress(teamChemistryPercent);
        }
    }

    public void refreshLines(boolean showChemistry) {
        this.showChemistry = showChemistry;
        for (LineFragment lineFragment : lineFragments) {
            lineFragment.update(showChemistry);
        }
        refreshChemistry();
    }

    private void updateLineFragment(Line line) {
        LineFragment lineFragment = lineFragments.get(line.getLineNumber() - 1);
        LineFragmentData data = lineFragment.getData();
        data.setLine(line);
        data.setLineNumber(line.getLineNumber());
        lineFragment.setData(data);
        lineFragment.update(showChemistry);
        linesAdapter.notifyDataSetChanged();
    }

    public Map<Integer, Line> getLines() {
        Map<Integer, Line> lines = new HashMap<>();
        for (LineFragment lineFragment : lineFragments) {
            LineFragmentData data = lineFragment.getData();
            Line line = data.getLine();
            if (line != null && !line.getPlayerIdMap().isEmpty()) {
                lines.put(data.getLineNumber(), data.getLine());
            }
        }
        return lines;
    }

    public void setLines(Map<Integer, Line> newLines) {
        Map<Integer, Line> lines = new HashMap<>(newLines);
        this.lines = lines;
        for (LineFragment lineFragment : lineFragments) {
            LineFragmentData data = lineFragment.getData();
            Line line = lines.get(data.getLineNumber());
            data.setLine(line);
            lineFragment.setData(data);
        }
    }

}
