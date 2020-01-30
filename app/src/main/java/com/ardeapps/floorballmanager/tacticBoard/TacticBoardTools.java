package com.ardeapps.floorballmanager.tacticBoard;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.utils.Helper;

import java.util.ArrayList;

import static android.view.Gravity.CENTER;

public class TacticBoardTools extends LinearLayout {

    public interface ToolBarListener {
        void onToolChanged(Tool tool);

        void onClearField();

        void onUndoPrevious();

        void onShowPlayers();

        void onBackgroundChanged(TacticSettingsDialogFragment.Field field);

        void onPlayButtonClick();

        void onStopButtonClick();

        void onPauseButtonClick();

        void onFrameChanged();

        void onFrameAdded();

        void onFrameRemoved();

        void onSaveClick();
    }

    public enum Tool {
        PEN,
        ERASER,
        ARROW,
        DOTTED_ARROW,
        CIRCLE,
        CROSS,
        UNDO,
        CLEAR,
        USERS,
        BALL,
        MOVE
    }

    LinearLayout penIcon;
    LinearLayout eraserIcon;
    LinearLayout undoIcon;
    LinearLayout arrowIcon;
    LinearLayout dottedArrowIcon;
    LinearLayout circleIcon;
    LinearLayout crossIcon;
    LinearLayout usersIcon;
    LinearLayout settingsIcon;
    LinearLayout clearIcon;
    LinearLayout ballIcon;
    LinearLayout playIcon;
    LinearLayout stopIcon;
    LinearLayout pauseIcon;
    LinearLayout plusIcon;
    LinearLayout removeIcon;
    LinearLayout moveIcon;
    LinearLayout saveIcon;
    LinearLayout downloadIcon;
    LinearLayout shareIcon;
    LinearLayout galleryIcon;

    LinearLayout framesContainer;
    ArrayList<TextView> frameButtons = new ArrayList<>();
    int selectedFrame = 0;
    ArrayList<LinearLayout> tools = new ArrayList<>();
    TacticSettingsDialogFragment.Field selectedField;
    Tool selectedTool;
    ToolBarListener listener;

    public int getFrameCount() {
        return frameButtons.size();
    }

    public int getSelectedFrame() {
        return selectedFrame;
    }

    public void setListener(ToolBarListener listener) {
        this.listener = listener;
    }

    public TacticBoardTools(Context context) {
        super(context);
        createView(context);
    }

    public TacticBoardTools(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.tactic_board_tools, this);
        framesContainer = findViewById(R.id.framesContainer);
        penIcon = findViewById(R.id.penIcon);
        eraserIcon = findViewById(R.id.eraserIcon);
        undoIcon = findViewById(R.id.undoIcon);
        arrowIcon = findViewById(R.id.arrowIcon);
        circleIcon = findViewById(R.id.circleIcon);
        crossIcon = findViewById(R.id.crossIcon);
        usersIcon = findViewById(R.id.usersIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        moveIcon = findViewById(R.id.moveIcon);
        clearIcon = findViewById(R.id.clearIcon);
        dottedArrowIcon = findViewById(R.id.dottedArrowIcon);
        ballIcon = findViewById(R.id.ballIcon);
        playIcon = findViewById(R.id.playIcon);
        stopIcon = findViewById(R.id.stopIcon);
        pauseIcon = findViewById(R.id.pauseIcon);
        plusIcon = findViewById(R.id.plusIcon);
        removeIcon = findViewById(R.id.removeIcon);
        saveIcon = findViewById(R.id.saveIcon);
        downloadIcon = findViewById(R.id.downloadIcon);
        shareIcon = findViewById(R.id.shareIcon);
        galleryIcon = findViewById(R.id.galleryIcon);

        tools = new ArrayList<>();

        initializeTool(penIcon, Tool.PEN);
        initializeTool(arrowIcon, Tool.ARROW);
        initializeTool(dottedArrowIcon, Tool.DOTTED_ARROW);
        initializeTool(circleIcon, Tool.CIRCLE);
        initializeTool(crossIcon, Tool.CROSS);
        initializeTool(eraserIcon, Tool.ERASER);
        initializeTool(usersIcon, Tool.USERS);
        initializeTool(ballIcon, Tool.BALL);
        initializeTool(moveIcon, Tool.MOVE);

        settingsIcon.setOnClickListener(v1 -> {
            TacticSettingsDialogFragment dialog = new TacticSettingsDialogFragment();
            dialog.setSelectedField(selectedField);
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Piirtoalustan asetukset");
            dialog.setListener(field -> {
                if (selectedField != field) {
                    listener.onBackgroundChanged(field);
                }
                selectedField = field;
            });
        });

        undoIcon.setOnClickListener(v11 -> listener.onUndoPrevious());
        clearIcon.setOnClickListener(v11 -> {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(AppRes.getContext().getString(R.string.tactic_board_clear_confirmation));
            dialogFragment.show(AppRes.getActivity().getSupportFragmentManager(), "Tyhjennetäänkö kenttä");
            dialogFragment.setCancelable(false);
            dialogFragment.setListener(() -> listener.onClearField());
        });

        // Animations
        plusIcon.setOnClickListener(v12 -> addFrame());
        removeIcon.setOnClickListener(v13 -> {
            if (frameButtons.size() > 1) {
                removeFrame();
            }
        });
        playIcon.setOnClickListener(v14 -> listener.onPlayButtonClick());
        stopIcon.setOnClickListener(v14 -> listener.onStopButtonClick());
        pauseIcon.setOnClickListener(v14 -> listener.onPauseButtonClick());

        galleryIcon.setOnClickListener(v1 -> {
            TacticGalleryDialogFragment dialog = new TacticGalleryDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Galleria");
        });
        saveIcon.setOnClickListener(v1 -> listener.onSaveClick());
        downloadIcon.setOnClickListener(v1 -> {

        });
        shareIcon.setOnClickListener(v1 -> {

        });

        // Initialize first frame
        selectedFrame = 0;
        addFrame();
    }

    private void initializeTool(LinearLayout button, Tool tool) {
        tools.add(button);
        button.setOnClickListener(v -> {
            for (LinearLayout existingTool : tools) {
                existingTool.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }

            selectedTool = tool;
            button.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));

            listener.onToolChanged(tool);

            // TODO näitä tarvitaan
            if (tool == Tool.USERS) {
                listener.onShowPlayers();
            } else if (tool == Tool.MOVE) {

            }
        });
    }

    private void removeFrame() {
        framesContainer.removeViewAt(selectedFrame);
        frameButtons.remove(selectedFrame);
        // Set new indexes for existing buttons
        for(TextView frameButton : frameButtons) {
            final int frameIndex = frameButtons.indexOf(frameButton);
            frameButton.setText(String.valueOf(frameIndex + 1));
            frameButton.setOnClickListener(button -> setSelectedFrame(frameIndex));
        }
        listener.onFrameRemoved();
    }

    private void addFrame() {
        TextView frameTextView = new TextView(AppRes.getContext());
        frameTextView.setText(String.valueOf(frameButtons.size() + 1));
        frameTextView.setTypeface(frameTextView.getTypeface(), Typeface.BOLD);
        frameTextView.setGravity(CENTER);
        frameTextView.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
        frameTextView.setLayoutParams(new RelativeLayout.LayoutParams(Helper.dpToPx(40), Helper.dpToPx(50)));

        framesContainer.addView(frameTextView);
        frameButtons.add(frameTextView);

        final int frameIndex = frameButtons.indexOf(frameTextView);

        selectedFrame = frameIndex;
        setFrameButtonSelected(frameIndex);

        frameTextView.setOnClickListener(button -> setSelectedFrame(frameIndex));

        if (frameIndex > 0) {
            listener.onFrameAdded();
        }
    }

    public void setSelectedFrame(int selectedFrame) {
        this.selectedFrame = selectedFrame;
        setFrameButtonSelected(selectedFrame);
        listener.onFrameChanged();
    }

    public void setFrameButtonSelected(int selectedFrame) {
        for (int i = 0; i < frameButtons.size(); i++) {
            TextView existingFrame = frameButtons.get(i);
            if (i == selectedFrame) {
                existingFrame.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_yellow_light));
            } else {
                existingFrame.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }
        }
    }
}
