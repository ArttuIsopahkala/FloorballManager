package com.ardeapps.floorballmanager.tacticBoard.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.tacticBoard.media.AnimationRecorder;
import com.ardeapps.floorballmanager.utils.Helper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.CENTER;

/**
 * Created by Arttu on 18.6.2017.
 */

public class AnimationTool extends RelativeLayout {

    public interface AnimationToolListener {
        void onAddFrame();
        void onPlay();
        void onRemoveFrame();
        void onStop();
        void onPause();
        void onFrameSelected(int selectedFrame);
        void onFrameCountChanged(int frameCount);
    }

    LinearLayout framesContainer;
    LinearLayout playIcon;
    LinearLayout stopIcon;
    LinearLayout pauseIcon;
    LinearLayout plusIcon;
    LinearLayout minusIcon;

    private AnimationToolListener listener;
    private int selectedFrame = 0;
    private int frameCount = 1;
    ArrayList<TextView> frameButtons = new ArrayList<>();
    private boolean isAnimationRunning = false;
    Timer animationTimer = null;
    int currentAnimationFrame = 0;
    public static final int animationLength = 1000;

    public void setListener(AnimationToolListener listener) {
        this.listener = listener;
    }

    public int getSelectedFrame() {
        return selectedFrame;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public AnimationTool(Context context) {
        super(context);
        createView(context);
    }

    public AnimationTool(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.animation_tool, this);
        framesContainer = findViewById(R.id.framesContainer);
        playIcon = findViewById(R.id.playIcon);
        stopIcon = findViewById(R.id.stopIcon);
        pauseIcon = findViewById(R.id.pauseIcon);
        plusIcon = findViewById(R.id.plusIcon);
        minusIcon = findViewById(R.id.minusIcon);

        plusIcon.setOnClickListener(v -> {
            addFrame();
            listener.onAddFrame();
        });
        minusIcon.setOnClickListener(v -> {
            removeFrame();
            listener.onRemoveFrame();
        });
        playIcon.setOnClickListener(v -> {
            if(!isAnimationRunning) {
                if (frameCount > 0 && selectedFrame < frameCount - 1) {
                    runFrameAnimation();
                    listener.onPlay();
                }
            }
        });
        stopIcon.setOnClickListener(v -> {
            if(isAnimationRunning) {
                setSelectedFrame(0);
                isAnimationRunning = false;
                if (animationTimer != null) {
                    animationTimer.cancel();
                }
                listener.onStop();
            }
        });
        pauseIcon.setOnClickListener(v -> {
            if(isAnimationRunning) {
                setSelectedFrame(currentAnimationFrame - 1); // Set currently selected frame
                isAnimationRunning = false;
                if(animationTimer != null) {
                    animationTimer.cancel();
                }
                listener.onPause();
            }
        });

        // Initialize first frame
        selectedFrame = 0;
        addFrame();
    }

    private void removeFrame() {
        if (frameButtons.size() < 2) {
            return;
        }
        framesContainer.removeViewAt(selectedFrame);
        frameButtons.remove(selectedFrame);
        listener.onFrameCountChanged(getFrameCount());

        // Set new indexes for existing buttons
        for(TextView frameButton : frameButtons) {
            final int frameIndex = frameButtons.indexOf(frameButton);
            frameButton.setText(String.valueOf(frameIndex + 1));
            frameButton.setOnClickListener(button -> setSelectedFrame(frameIndex));
        }

        // Remove position from removed index and select previous
        int selectedFrame = getSelectedFrame();
        int newSelectedFrame = selectedFrame > 0 ? selectedFrame - 1 : 0;
        setSelectedFrame(newSelectedFrame);
    }

    private void addFrame() {
        TextView frameTextView = new TextView(AppRes.getContext());
        frameTextView.setText(String.valueOf(frameButtons.size() + 1));
        frameTextView.setTypeface(frameTextView.getTypeface(), Typeface.BOLD);
        frameTextView.setGravity(CENTER);
        frameTextView.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
        frameTextView.setLayoutParams(new LayoutParams(Helper.dpToPx(30), Helper.dpToPx(40)));

        framesContainer.addView(frameTextView);
        frameButtons.add(frameTextView);
        if(listener != null) {
            listener.onFrameCountChanged(getFrameCount());
        }

        final int frameIndex = frameButtons.indexOf(frameTextView);

        selectedFrame = frameIndex;
        setFrameButtonSelected(frameIndex);

        frameTextView.setOnClickListener(button -> {
            setSelectedFrame(frameIndex);
        });

        if (frameIndex > 0) {
            int selectedFrame = getSelectedFrame();
            setSelectedFrame(selectedFrame);
        }
    }

    public void setSelectedFrame(int selectedFrame) {
        this.selectedFrame = selectedFrame;
        setFrameButtonSelected(selectedFrame);
        listener.onFrameSelected(getSelectedFrame());
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

    public void runFrameAnimation() {
        int frameCount = getFrameCount();
        int startFrame = getSelectedFrame();
        if (frameCount > 0 && startFrame < frameCount - 1 && !isAnimationRunning) {
            isAnimationRunning = true;
            animationTimer = new Timer();
            currentAnimationFrame = startFrame;
            // Select frames during animation
            final Handler handler = new Handler();
            final TimerTask task = new TimerTask() {
                public void run() {
                    handler.post(() -> {
                        if(currentAnimationFrame < frameCount) {
                            setFrameButtonSelected(currentAnimationFrame);
                        } else {
                            setSelectedFrame(frameCount - 1);
                            animationTimer.cancel();
                            isAnimationRunning = false;

                            // Stop recording if converting
                            if(AnimationRecorder.getInstance().isRecording()) {
                                AnimationRecorder.getInstance().stopRecording();
                            }
                        }
                        currentAnimationFrame++;
                    });
                }
            };
            animationTimer.schedule(task, 0, animationLength);
        }
    }

}
