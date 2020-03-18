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
import com.ardeapps.floorballmanager.tacticBoard.utils.AnimationTool;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;

public class TacticBoardMenu extends RelativeLayout {

    public interface MenuListener {
        void onToolChanged(Tool tool);
        void onActionButtonClick(Tool tool);
    }

    public enum Tool {
        PEN,
        LINE,
        ERASER,
        ARROW,
        DOTTED_ARROW,
        CIRCLE,
        CROSS,
        USERS,
        BALL,
        UNDO,
        CLEAR,
        SETTINGS,
        SAVE,
        GALLERY
    }

    RelativeLayout disableToolsOverlay;
    TextView disableToolsInfoText;
    IconView disableToolsIcon;
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
    LinearLayout ballIcon;
    IconView settingsIcon;
    IconView saveIcon;
    IconView galleryIcon;
    TextView fieldNameText;
    AnimationTool animationTool;

    Tool selectedTool;
    ArrayList<LinearLayout> paintTools = new ArrayList<>();
    MenuListener listener;

    public void setListener(MenuListener listener) {
        this.listener = listener;
    }

    public void setAnimationToolListener(AnimationTool.AnimationToolListener listener) {
        animationTool.setListener(listener);
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
        disableToolsOverlay = findViewById(R.id.disableToolsOverlay);
        disableToolsInfoText = findViewById(R.id.disableToolsInfoText);
        disableToolsIcon = findViewById(R.id.disableToolsIcon);
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
        ballIcon = findViewById(R.id.ballIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        saveIcon = findViewById(R.id.saveIcon);
        galleryIcon = findViewById(R.id.galleryIcon);
        fieldNameText = findViewById(R.id.fieldNameText);
        animationTool = findViewById(R.id.animationTool);

        paintTools = new ArrayList<>();

        initializePaintTool(penIcon, Tool.PEN);
        initializePaintTool(lineIcon, Tool.LINE);
        initializePaintTool(arrowIcon, Tool.ARROW);
        initializePaintTool(dottedArrowIcon, Tool.DOTTED_ARROW);
        initializePaintTool(circleIcon, Tool.CIRCLE);
        initializePaintTool(crossIcon, Tool.CROSS);
        initializePaintTool(eraserIcon, Tool.ERASER);
        initializePaintTool(homePlayersIcon, Tool.USERS);
        initializePaintTool(ballIcon, Tool.BALL);

        settingsIcon.setOnClickListener(v -> listener.onActionButtonClick(Tool.SETTINGS));
        saveIcon.setOnClickListener(v -> listener.onActionButtonClick(Tool.SAVE));
        galleryIcon.setOnClickListener(v -> listener.onActionButtonClick(Tool.GALLERY));
        undoIcon.setOnClickListener(v -> listener.onActionButtonClick(Tool.UNDO));
        clearIcon.setOnClickListener(v -> listener.onActionButtonClick(Tool.CLEAR));

        // Initialize first frame
        fieldNameText.setVisibility(View.GONE);
    }

    public void showDisableToolsOverlay(boolean showRemoveIcon) {
        disableToolsOverlay.setVisibility(View.VISIBLE);
        if(showRemoveIcon) {
            disableToolsInfoText.setVisibility(View.GONE);
            disableToolsIcon.setVisibility(View.VISIBLE);
        } else {
            disableToolsInfoText.setVisibility(View.VISIBLE);
            disableToolsIcon.setVisibility(View.GONE);
        }
    }

    private void hideDisableToolsOverlay() {
        disableToolsOverlay.setVisibility(View.GONE);
    }

    private void initializePaintTool(LinearLayout button, Tool tool) {
        paintTools.add(button);
        button.setOnClickListener(v -> {
            selectedTool = tool;
            for (LinearLayout existingTool : paintTools) {
                existingTool.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }
            button.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));

            listener.onToolChanged(tool);
        });
    }
}
