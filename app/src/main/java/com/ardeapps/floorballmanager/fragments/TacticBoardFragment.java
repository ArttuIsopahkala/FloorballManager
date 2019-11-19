package com.ardeapps.floorballmanager.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.TacticSettingsDialogFragment;
import com.ardeapps.floorballmanager.views.DrawingBoard;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;

public class TacticBoardFragment extends Fragment {

    public enum Tool {
        PEN,
        ERASER,
        ARROW,
        DOTTED_ARROW,
        CIRCLE,
        CROSS,
        UNDO,
        CLEAR
    }

    public enum StaticTool {
        UNDO
    }

    IconView penIcon;
    IconView eraserIcon;
    IconView undoIcon;
    IconView arrowIcon;
    IconView dottedArrowIcon;
    IconView circleIcon;
    IconView crossIcon;
    IconView usersIcon;
    IconView settingsIcon;
    IconView clearIcon;
    ImageView ballIcon;
    DrawingBoard drawingBoard;
    Tool selectedTool;

    ArrayList<IconView> tools = new ArrayList<>();

    int color = R.color.color_red_light;
    int size = 10;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tactic_board, container, false);

        drawingBoard = v.findViewById(R.id.drawingBoard);
        penIcon = v.findViewById(R.id.penIcon);
        eraserIcon = v.findViewById(R.id.eraserIcon);
        undoIcon = v.findViewById(R.id.undoIcon);
        arrowIcon = v.findViewById(R.id.arrowIcon);
        circleIcon = v.findViewById(R.id.circleIcon);
        crossIcon = v.findViewById(R.id.crossIcon);
        usersIcon = v.findViewById(R.id.usersIcon);
        settingsIcon = v.findViewById(R.id.settingsIcon);
        clearIcon = v.findViewById(R.id.clearIcon);
        dottedArrowIcon = v.findViewById(R.id.dottedArrowIcon);
        ballIcon = v.findViewById(R.id.ballIcon);

        tools = new ArrayList<>();

        initializeTool(penIcon, Tool.PEN);
        initializeTool(arrowIcon, Tool.ARROW);
        initializeTool(dottedArrowIcon, Tool.DOTTED_ARROW);
        initializeTool(circleIcon, Tool.CIRCLE);
        initializeTool(crossIcon, Tool.CROSS);
        initializeTool(eraserIcon, Tool.ERASER);
        initializeTool(undoIcon, Tool.UNDO);
        initializeTool(clearIcon, Tool.CLEAR);

        usersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settingsIcon.setOnClickListener(v1 -> {
            TacticSettingsDialogFragment dialog = new TacticSettingsDialogFragment();
            dialog.show(getChildFragmentManager(), "Piirtoalustan asetukset");
            dialog.setListener(field -> {
                drawingBoard.setBackgroundField(field);
            });
        });

        selectedTool = Tool.PEN; // Default
        drawingBoard.setPaintColor(color);
        drawingBoard.setPaintSize(size);
        drawingBoard.setSelectedTool(selectedTool);
        return v;
    }

    private void initializeTool(IconView button, Tool tool) {
        tools.add(button);
        button.setOnClickListener(v -> {
            for(IconView existingTool : tools) {
                existingTool.setSelected(false);
            }

            selectedTool = tool;
            drawingBoard.setSelectedTool(tool);
            button.setSelected(true);

            if(tool == Tool.CLEAR) {
                drawingBoard.clear();
            } else if(tool == Tool.UNDO) {
                drawingBoard.restore();
            }
        });
    }
}
