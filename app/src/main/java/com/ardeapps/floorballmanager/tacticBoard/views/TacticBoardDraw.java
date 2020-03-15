package com.ardeapps.floorballmanager.tacticBoard.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.PrefRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;
import com.ardeapps.floorballmanager.utils.Helper;

import java.util.ArrayList;

import static com.ardeapps.floorballmanager.PrefRes.DRAW_COLOR_PROGRESS;

public class TacticBoardDraw extends LinearLayout {

    public enum Tool {
        PEN,
        ERASER,
        ARROW,
        DOTTED_ARROW,
        CIRCLE,
        CROSS
    }

    DrawingBoard drawingBoard;
    LinearLayout penIcon;
    LinearLayout eraserIcon;
    LinearLayout undoIcon;
    LinearLayout arrowIcon;
    LinearLayout dottedArrowIcon;
    LinearLayout circleIcon;
    LinearLayout crossIcon;
    LinearLayout clearIcon;

    ArrayList<LinearLayout> tools = new ArrayList<>();
    Tool selectedTool;

    public TacticBoardDraw(Context context) {
        super(context);
        createView(context);
    }

    public TacticBoardDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.tactic_board_draw, this);
        drawingBoard = findViewById(R.id.drawingBoard);
        penIcon = findViewById(R.id.penIcon);
        eraserIcon = findViewById(R.id.eraserIcon);
        undoIcon = findViewById(R.id.undoIcon);
        arrowIcon = findViewById(R.id.arrowIcon);
        circleIcon = findViewById(R.id.circleIcon);
        crossIcon = findViewById(R.id.crossIcon);
        clearIcon = findViewById(R.id.clearIcon);
        dottedArrowIcon = findViewById(R.id.dottedArrowIcon);

        tools = new ArrayList<>();

        initializeTool(penIcon, Tool.PEN);
        initializeTool(arrowIcon, Tool.ARROW);
        initializeTool(dottedArrowIcon, Tool.DOTTED_ARROW);
        initializeTool(circleIcon, Tool.CIRCLE);
        initializeTool(crossIcon, Tool.CROSS);
        initializeTool(eraserIcon, Tool.ERASER);

        // Defaults
        selectedTool = TacticBoardDraw.Tool.PEN;
        drawingBoard.setSelectedTool(selectedTool);
        if(!PrefRes.containsKey(DRAW_COLOR_PROGRESS)) {
            PrefRes.putInt(DRAW_COLOR_PROGRESS, 1650); // sun yellow
        }

        undoIcon.setOnClickListener(v11 -> drawingBoard.restore());
        clearIcon.setOnClickListener(v11 -> {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(AppRes.getContext().getString(R.string.tactic_board_clear_confirmation));
            dialogFragment.show(AppRes.getActivity().getSupportFragmentManager(), "Tyhjennetäänkö kenttä");
            dialogFragment.setCancelable(false);
            dialogFragment.setListener(() -> drawingBoard.clear());
        });
    }

    public void setFieldHeight(int heightInPxl, int widthInPxl) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = heightInPxl;
        drawingBoard.setLayoutParams(params);
    }

    public void setPaintColorProgress(int paintColorProgress) {
        int color = TacticBoardHelper.getColorFromProgress(paintColorProgress);
        drawingBoard.setPaintColor(color);
    }

    public int getToolBarHeight() {
        return Helper.dpToPx(50);
    }

    private void initializeTool(LinearLayout button, Tool tool) {
        tools.add(button);
        button.setOnClickListener(v -> {
            for (LinearLayout existingTool : tools) {
                existingTool.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }

            selectedTool = tool;
            button.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));

            drawingBoard.setSelectedTool(tool);
        });
    }
}
