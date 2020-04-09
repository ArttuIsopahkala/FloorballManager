package com.ardeapps.floorballmanager.tacticBoard.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;

public class TacticBoardMenu extends RelativeLayout {

    public interface MenuListener {
        void onToolChanged(Tool tool);
        void onActionButtonClick(ActionTool tool);
    }

    public enum Tool {
        PEN,
        LINE,
        ERASER,
        ARROW,
        DOTTED_ARROW,
        CIRCLE,
        CROSS,
        HOME_PLAYERS,
        AWAY_PLAYERS,
        BALL
    }

    public enum ActionTool {
        UNDO,
        CLEAR,
        SETTINGS,
        SAVE,
        GALLERY,
        HOME_SETTINGS,
        AWAY_SETTINGS
    }

    LinearLayout framesContainer;
    LinearLayout penIcon;
    LinearLayout lineIcon;
    LinearLayout eraserIcon;
    LinearLayout arrowIcon;
    LinearLayout dottedArrowIcon;
    LinearLayout circleIcon;
    LinearLayout crossIcon;
    LinearLayout undoIcon;
    LinearLayout clearIcon;
    LinearLayout homePlayersIcon;
    LinearLayout awayPlayersIcon;
    LinearLayout ballIcon;
    LinearLayout homeSettingsIcon;
    LinearLayout awaySettingsIcon;
    IconView settingsIcon;
    IconView saveIcon;
    IconView galleryIcon;
    TextView fieldNameText;

    Tool selectedTool;
    ArrayList<LinearLayout> stickyTools = new ArrayList<>();
    MenuListener listener;

    public void setListener(MenuListener listener) {
        this.listener = listener;
    }

    public TacticBoardMenu(Context context) {
        super(context);
        createView(context);
    }

    public TacticBoardMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.tactic_board_menu, this);
        framesContainer = findViewById(R.id.framesContainer);
        penIcon = findViewById(R.id.penIcon);
        lineIcon = findViewById(R.id.lineIcon);
        eraserIcon = findViewById(R.id.eraserIcon);
        arrowIcon = findViewById(R.id.arrowIcon);
        circleIcon = findViewById(R.id.circleIcon);
        crossIcon = findViewById(R.id.crossIcon);
        dottedArrowIcon = findViewById(R.id.dottedArrowIcon);
        undoIcon = findViewById(R.id.undoIcon);
        clearIcon = findViewById(R.id.clearIcon);
        homePlayersIcon = findViewById(R.id.homePlayersIcon);
        awayPlayersIcon = findViewById(R.id.awayPlayersIcon);
        ballIcon = findViewById(R.id.ballIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        saveIcon = findViewById(R.id.saveIcon);
        galleryIcon = findViewById(R.id.galleryIcon);
        fieldNameText = findViewById(R.id.fieldNameText);
        homeSettingsIcon = findViewById(R.id.homeSettingsIcon);
        awaySettingsIcon = findViewById(R.id.awaySettingsIcon);

        stickyTools = new ArrayList<>();

        initializeStickyTool(penIcon, Tool.PEN);
        initializeStickyTool(lineIcon, Tool.LINE);
        initializeStickyTool(arrowIcon, Tool.ARROW);
        initializeStickyTool(dottedArrowIcon, Tool.DOTTED_ARROW);
        initializeStickyTool(circleIcon, Tool.CIRCLE);
        initializeStickyTool(crossIcon, Tool.CROSS);
        initializeStickyTool(eraserIcon, Tool.ERASER);
        initializeStickyTool(homePlayersIcon, Tool.HOME_PLAYERS);
        initializeStickyTool(awayPlayersIcon, Tool.AWAY_PLAYERS);
        initializeStickyTool(ballIcon, Tool.BALL);

        settingsIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.SETTINGS));
        saveIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.SAVE));
        galleryIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.GALLERY));
        undoIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.UNDO));
        clearIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.CLEAR));
        homeSettingsIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.HOME_SETTINGS));
        awaySettingsIcon.setOnClickListener(v -> listener.onActionButtonClick(ActionTool.AWAY_SETTINGS));

        // Initialize first frame
        fieldNameText.setVisibility(View.GONE);
    }

    // TODO set real text
    public void setFieldNameText(String text) {
        fieldNameText.setText(text);
    }

    public void resetTools() {
        selectedTool = null;
        for (LinearLayout existingTool : stickyTools) {
            existingTool.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
        }
    }

    private void initializeStickyTool(LinearLayout button, Tool tool) {
        stickyTools.add(button);
        button.setOnClickListener(v -> {
            selectedTool = tool;
            for (LinearLayout existingTool : stickyTools) {
                existingTool.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }
            button.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));

            listener.onToolChanged(tool);
        });
    }
}
