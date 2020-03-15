package com.ardeapps.floorballmanager.tacticBoard.views;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.tacticBoard.media.AnimationRecorder;
import com.ardeapps.floorballmanager.tacticBoard.objects.ExportField;
import com.ardeapps.floorballmanager.tacticBoard.objects.ExportItem;
import com.ardeapps.floorballmanager.tacticBoard.objects.MovableView;
import com.ardeapps.floorballmanager.tacticBoard.objects.Position;
import com.ardeapps.floorballmanager.tacticBoard.utils.JsonDatabase;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.IconView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.CENTER;

public class TacticBoardAnimation extends RelativeLayout {

    @SuppressLint("ClickableViewAccessibility")
    private void setDragListener(View view, MovableView.Type type, String id) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if(movingItem == null) {
                    movingItem = new CurrentlyMovingItem(id, type);
                    ClipData.Item item = new ClipData.Item(TacticBoardHelper.createTag(type, id));
                    ClipData dragData = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

                    View.DragShadowBuilder dragView = new View.DragShadowBuilder(v);
                    v.startDrag(dragData, dragView, null, 0);
                    v.setVisibility(View.GONE);

                    showDisableToolsOverlay(true);
                }
            }
            return false;
        });
    }

    public enum Tool {
        ERASER,
        USERS,
        BALL
    }

    LinearLayout notAddedPlayersContainer;
    RelativeLayout fieldViewsContainer;
    RelativeLayout disableToolsOverlay;
    TextView disableToolsInfoText;
    IconView disableToolsIcon;
    LinearLayout framesContainer;
    LinearLayout undoIcon;
    LinearLayout clearIcon;
    LinearLayout usersIcon;
    LinearLayout ballIcon;
    LinearLayout playIcon;
    LinearLayout stopIcon;
    LinearLayout pauseIcon;
    LinearLayout plusIcon;
    LinearLayout minusIcon;
    LinearLayout animationTools;

    int fieldHeight;
    int selectedFrame = 0;
    Tool selectedTool;
    ArrayList<TextView> frameButtons = new ArrayList<>();
    ArrayList<LinearLayout> tools = new ArrayList<>();
    ArrayList<Player> activePlayers = new ArrayList<>();
    ArrayList<String> notAddedPlayers = new ArrayList<>();
    Map<String, View> playerViews = new HashMap<>();
    Map<String, MovableView> movableViews = new HashMap<>();
    CurrentlyMovingItem movingItem;

    private class CurrentlyMovingItem {
        String id;
        MovableView.Type type;

        CurrentlyMovingItem(String id, MovableView.Type type) {
            this.id = id;
            this.type = type;
        }
    }

    private boolean isAnimationRunning = false;
    Timer animationTimer = null;
    int currentAnimationFrame = 0;
    int ballWidth;
    int playerWidth;

    public int getFrameCount() {
        return frameButtons.size();
    }

    public int getSelectedFrame() {
        return selectedFrame;
    }

    public TacticBoardAnimation(Context context) {
        super(context);
        createView(context);
    }

    public TacticBoardAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private class PlayerCardHolder {
        ImageView pictureImage;
        TextView nameText;
    }

    public boolean isChanges() {
        return movableViews.size() > 0;
    }

    public boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.tactic_board_animation, this);
        fieldViewsContainer = findViewById(R.id.fieldViewsContainer);
        notAddedPlayersContainer = findViewById(R.id.notAddedPlayersContainer);
        framesContainer = findViewById(R.id.framesContainer);
        disableToolsOverlay = findViewById(R.id.disableToolsOverlay);
        disableToolsInfoText = findViewById(R.id.disableToolsInfoText);
        disableToolsIcon = findViewById(R.id.disableToolsIcon);
        undoIcon = findViewById(R.id.undoIcon);
        clearIcon = findViewById(R.id.clearIcon);
        usersIcon = findViewById(R.id.usersIcon);
        ballIcon = findViewById(R.id.ballIcon);
        playIcon = findViewById(R.id.playIcon);
        stopIcon = findViewById(R.id.stopIcon);
        pauseIcon = findViewById(R.id.pauseIcon);
        plusIcon = findViewById(R.id.plusIcon);
        minusIcon = findViewById(R.id.minusIcon);
        animationTools = findViewById(R.id.animationTools);

        tools = new ArrayList<>();

        initializeTool(usersIcon, Tool.USERS);
        initializeTool(ballIcon, Tool.BALL);

        activePlayers = AppRes.getInstance().getActivePlayers(false);

        // TODO Mock data
        Player player1 = new Player();
        player1.setName("Arttu");
        player1.setPlayerId("ARTTU");
        Player player2 = new Player();
        player2.setName("Pekka");
        player2.setPlayerId("PEKKA");
        activePlayers.add(player1);
        activePlayers.add(player2);

        notAddedPlayersContainer.setVisibility(View.GONE);
        final PlayerCardHolder holder = new PlayerCardHolder();
        notAddedPlayersContainer.removeAllViews();

        for (final Player player : activePlayers) {
            View cv = LayoutInflater.from(AppRes.getContext()).inflate(R.layout.tactic_board_player, null);
            holder.pictureImage = cv.findViewById(R.id.pictureImage);
            holder.nameText = cv.findViewById(R.id.nameText);
            cv.setTag(TacticBoardHelper.createTag(MovableView.Type.PLAYER, player.getPlayerId()));

            if (player.getPicture() != null) {
                holder.pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
            } else {
                holder.pictureImage.setImageResource(R.drawable.default_picture);
            }
            holder.nameText.setText(player.getName());
            setDragListener(cv, MovableView.Type.PLAYER, player.getPlayerId());

            playerViews.put(player.getPlayerId(), cv);
            notAddedPlayers.add(player.getPlayerId());
        }
        drawNotAddedPlayerViews();

        undoIcon.setOnClickListener(v11 -> {
            // TODO
        });

        clearIcon.setOnClickListener(v11 -> {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(AppRes.getContext().getString(R.string.tactic_board_clear_confirmation));
            dialogFragment.show(AppRes.getActivity().getSupportFragmentManager(), "Tyhjennetäänkö kenttä");
            dialogFragment.setCancelable(false);
            dialogFragment.setListener(this::clearField);
        });

        // Animations
        plusIcon.setOnClickListener(v12 -> addFrame());
        minusIcon.setOnClickListener(v13 -> removeFrame());
        playIcon.setOnClickListener(v13 -> runAnimation());
        stopIcon.setOnClickListener(v14 -> {
            setSelectedFrame(0);
            isAnimationRunning = false;
            if(animationTimer != null) {
                animationTimer.cancel();
            }
            for (MovableView item : movableViews.values()) {
                item.view.clearAnimation();
            }
            AnimationRecorder.getInstance().stopRecording();
        });
        pauseIcon.setOnClickListener(v14 -> {
            if(isAnimationRunning) {
                setSelectedFrame(currentAnimationFrame - 1); // Set currently selected frame
                isAnimationRunning = false;
                if(animationTimer != null) {
                    animationTimer.cancel();
                }
                for (MovableView item : movableViews.values()) {
                    item.view.clearAnimation();
                }
            }
        });

        // Initialize first frame
        selectedFrame = 0;
        addFrame();

        setOnTouchListener((v1, event) -> {
            float x = event.getX();
            float y = event.getY();
            View view = TacticBoardHelper.findViewAtPosition(fieldViewsContainer, (int) event.getRawX(), (int) event.getRawY());
            Logger.log("View under finger: " + TacticBoardHelper.findViewAtPosition(fieldViewsContainer, (int) event.getRawX(), (int) event.getRawY()));

            if (view == null) {
                // Handle draw functions
                if (selectedTool == Tool.BALL) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        drawBall(x, y);
                    }
                }
            } else if (view.getTag() != null) {
                // Handle existing items
                String tag = view.getTag().toString();
                MovableView.Type type = MovableView.Type.fromDatabaseName(TacticBoardHelper.getType(tag));
                String id = TacticBoardHelper.getId(tag);
                MovableView movableView = movableViews.get(id);
                // TODO add ERASER
                if (selectedTool == Tool.ERASER) {
                    if(movableView != null) {
                        if (type == MovableView.Type.PLAYER) {
                            notAddedPlayers.add(id);
                            drawNotAddedPlayerViews();
                        }
                        removeItemFromField(movableView);
                    }
                }
            }
            return false;
        });

        setOnDragListener((v13, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_EXITED:
                    onResetItem(movingItem.id, movingItem.type);
                    movingItem = null;
                    break;
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v13.invalidate();
                        return true;
                    }
                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String itemView = item.getText().toString();
                    MovableView.Type type = MovableView.Type.fromDatabaseName(TacticBoardHelper.getType(itemView));
                    hideDisableToolsOverlay();
                    float positionX = event.getX();
                    float positionY = event.getY();

                    // Remove item from view
                    if (positionY > fieldHeight) {
                        String id = TacticBoardHelper.getId(itemView);
                        onResetItem(id, type);
                    } else {
                        if (type == MovableView.Type.BALL) {
                            String ballId = TacticBoardHelper.getId(itemView);
                            MovableView ballItem = movableViews.get(ballId);
                            if (ballItem != null) {
                                Logger.log("MOVE BALL");
                                // Replace old position
                                int selectedFrame = getSelectedFrame();
                                Position position = new Position(positionX, positionY);
                                ballItem.positions.set(selectedFrame, position);
                                moveItemInField(ballItem, position);
                            }
                        } else if (type == MovableView.Type.PLAYER) {
                            String playerId = TacticBoardHelper.getId(itemView);
                            Logger.log("PLAYER ID: " + playerId);

                            View view = playerViews.get(playerId);
                            if (view != null) {
                                if (notAddedPlayers.contains(playerId)) {
                                    // Player moves from toolbar to field
                                    MovableView playerItem = new MovableView(AppRes.getContext());
                                    playerItem.paramSize = playerWidth;
                                    playerItem.type = MovableView.Type.PLAYER;
                                    playerItem.id = playerId;
                                    playerItem.view = view;
                                    Position position = new Position(positionX, positionY);
                                    // Add position for all frames
                                    int frameCount = getFrameCount();
                                    for (int i = 0; i < frameCount; i++) {
                                        playerItem.positions.add(position);
                                    }

                                    Logger.log("FROM TOOLBAR TO FIELD");
                                    // 1. Remove from players tool bar
                                    notAddedPlayersContainer.removeView(view);
                                    notAddedPlayers.remove(playerId);
                                    // 2. Add to field
                                    addItemToField(playerItem, position);
                                } else {
                                    // Just move player

                                    MovableView playerItem = movableViews.get(playerId);
                                    if (playerItem != null) {
                                        Logger.log("FROM FIELD TO FIELD");
                                        // Replace old position
                                        int selectedFrame = getSelectedFrame();
                                        Position position = new Position(positionX, positionY);
                                        playerItem.positions.set(selectedFrame, position);
                                        moveItemInField(playerItem, position);
                                    }
                                }
                            }
                        }
                    }

                    // Invalidates the view to force a redraw
                    movingItem = null;
                    v13.invalidate();

                    return true;
            }
            return false;
        });
    }

    private void onResetItem(String id, MovableView.Type type) {
        hideDisableToolsOverlay();
        if (type == MovableView.Type.BALL) {
            Logger.log("REMOVE BALL");
            MovableView ballItem = movableViews.get(id);
            if (ballItem != null) {
                removeItemFromField(ballItem);
            }
        } else if (type == MovableView.Type.PLAYER) {
            if (notAddedPlayers.contains(id)) {
                View playerView = playerViews.get(id);
                if (playerView != null) {
                    Logger.log("FROM TOOLBAR TO TOOLBAR");
                    playerView.setVisibility(View.VISIBLE);
                }
            } else {
                MovableView playerItem = movableViews.get(id);
                if(playerItem != null) {
                    Logger.log("FROM FIELD TO TOOLBAR");
                    notAddedPlayers.add(id);
                    removeItemFromField(playerItem);
                    drawNotAddedPlayerViews();
                }
            }
        }
    }

    public void setFieldHeight(int heightInPxl, int widthInPxl) {
        fieldHeight = heightInPxl;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = heightInPxl;
        fieldViewsContainer.setLayoutParams(params);

        // Calculate views' size to screen
        ballWidth = Helper.pxToDp(widthInPxl) / 6;
        playerWidth = Helper.pxToDp(widthInPxl) / 3;
    }

    public int getToolBarHeight() {
        return Helper.dpToPx(80);
    }

    private void initializeTool(LinearLayout button, Tool tool) {
        tools.add(button);
        button.setOnClickListener(v -> {
            for (LinearLayout existingTool : tools) {
                existingTool.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }

            selectedTool = tool;
            button.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));

            if (tool == Tool.USERS) {
                if (!activePlayers.isEmpty()) {
                    notAddedPlayersContainer.setVisibility(View.VISIBLE);
                    drawNotAddedPlayerViews();
                } else {
                    notAddedPlayersContainer.setVisibility(View.GONE);
                }
            } else {
                notAddedPlayersContainer.setVisibility(View.GONE);
            }
        });
    }

    public void clearField() {
        movableViews = new HashMap<>();
        fieldViewsContainer.removeAllViewsInLayout();
        notAddedPlayers = new ArrayList<>();
        for(Player player : activePlayers) {
            notAddedPlayers.add(player.getPlayerId());
        }
        drawNotAddedPlayerViews();
    }

    private void drawBall(float x, float y) {
        String id = "ball" + (movableViews.size() + 1);
        ImageView imageView = new ImageView(AppRes.getContext());
        imageView.setImageResource(R.drawable.floorball_icon);
        imageView.setTag(TacticBoardHelper.createTag(MovableView.Type.BALL, id));

        Position position = new Position(x, y);
        MovableView ballItem = new MovableView(AppRes.getContext());

        ballItem.paramSize = ballWidth;
        ballItem.type = MovableView.Type.BALL;
        ballItem.id = id;
        ballItem.view = imageView;
        // Add position for all frames
        int frameCount = getFrameCount();
        for (int i = 0; i < frameCount; i++) {
            ballItem.positions.add(position);
        }
        setDragListener(ballItem.view, ballItem.type, ballItem.id);

        addItemToField(ballItem, position);
    }

    private void drawNotAddedPlayerViews() {
        notAddedPlayersContainer.removeAllViews();
        ArrayList<View> views = new ArrayList<>();
        for (Map.Entry<String, View> entry : playerViews.entrySet()) {
            String playerId = entry.getKey();
            View view = entry.getValue();
            if (notAddedPlayers.contains(playerId)) {
                views.add(view);
            }
        }
        for (final View view : views) {
            view.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(playerWidth, playerWidth);
            view.setLayoutParams(params);
            notAddedPlayersContainer.addView(view);
        }
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

    private void addItemToField(MovableView item, Position position) {
        movableViews.put(item.id, item);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(item.paramSize, item.paramSize);
        float leftTopX = position.x - (float)item.paramSize / 2;
        float leftTopY = position.y - (float)item.paramSize / 2;
        params.leftMargin = (int) leftTopX;
        params.topMargin = (int) leftTopY;
        item.view.setVisibility(View.VISIBLE);
        item.view.bringToFront();
        if(item.view instanceof ImageView) {
            ImageView imageView = (ImageView) item.view;
            fieldViewsContainer.addView(imageView, params);
        } else if (item.view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) item.view;
            fieldViewsContainer.addView(relativeLayout, params);
        }
    }

    private void moveItemInField(MovableView item, Position position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(item.paramSize, item.paramSize);
        float leftTopX = position.x - (float)item.paramSize / 2;
        float leftTopY = position.y - (float)item.paramSize / 2;
        params.leftMargin = (int) leftTopX;
        params.topMargin = (int) leftTopY;
        item.view.setVisibility(View.VISIBLE);
        item.view.bringToFront();
        if(item.view instanceof ImageView) {
            ImageView imageView = (ImageView) item.view;
            imageView.setLayoutParams(params);
        } else if (item.view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) item.view;
            relativeLayout.setLayoutParams(params);
        }
    }

    private void removeItemFromField(MovableView item) {
        movableViews.remove(item.id);
        fieldViewsContainer.removeView(item.view);
    }

    private void removeFrame() {
        if (frameButtons.size() < 2) {
            return;
        }
        framesContainer.removeViewAt(selectedFrame);
        frameButtons.remove(selectedFrame);
        // Set new indexes for existing buttons
        for(TextView frameButton : frameButtons) {
            final int frameIndex = frameButtons.indexOf(frameButton);
            frameButton.setText(String.valueOf(frameIndex + 1));
            frameButton.setOnClickListener(button -> setSelectedFrame(frameIndex));
        }

        // Remove position from removed index and select previous
        int selectedFrame = getSelectedFrame();
        for (MovableView item : movableViews.values()) {
            item.positions.remove(selectedFrame);
        }
        int newSelectedFrame = selectedFrame > 0 ? selectedFrame - 1 : 0;
        setSelectedFrame(newSelectedFrame);
    }

    private void addFrame() {
        TextView frameTextView = new TextView(AppRes.getContext());
        frameTextView.setText(String.valueOf(frameButtons.size() + 1));
        frameTextView.setTypeface(frameTextView.getTypeface(), Typeface.BOLD);
        frameTextView.setGravity(CENTER);
        frameTextView.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
        frameTextView.setLayoutParams(new RelativeLayout.LayoutParams(Helper.dpToPx(30), Helper.dpToPx(40)));

        framesContainer.addView(frameTextView);
        frameButtons.add(frameTextView);

        final int frameIndex = frameButtons.indexOf(frameTextView);

        selectedFrame = frameIndex;
        setFrameButtonSelected(frameIndex);

        frameTextView.setOnClickListener(button -> setSelectedFrame(frameIndex));

        if (frameIndex > 0) {
            // Copy position from last frame
            int selectedFrame = getSelectedFrame();
            for (MovableView item : movableViews.values()) {
                Position lastPosition = item.positions.get(selectedFrame - 1);
                item.positions.add(lastPosition);
            }
            setSelectedFrame(selectedFrame);
        }
    }

    public void setSelectedFrame(int selectedFrame) {
        this.selectedFrame = selectedFrame;
        setFrameButtonSelected(selectedFrame);

        // Set views position
        for (MovableView item : movableViews.values()) {
            Position position = item.positions.get(selectedFrame);
            moveItemInField(item, position);
        }
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

    public void runAnimation() {
        int frameCount = getFrameCount();
        int startFrame = getSelectedFrame();
        if (frameCount > 0 && startFrame < frameCount - 1 && !isAnimationRunning) {

            int animationLength = 1000;

            for (MovableView item : movableViews.values()) {
                // Add item to initial position
                Position startPosition = item.positions.get(startFrame);
                moveItemInField(item, startPosition);

                AnimationSet animationSet = new AnimationSet(true);
                animationSet.setInterpolator(new LinearInterpolator());
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(isAnimationRunning) {
                            // Set to last position
                            Position lastPosition = item.positions.get(item.positions.size() - 1);
                            moveItemInField(item, lastPosition);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                // Collect animations
                for(int currentFrame = startFrame; currentFrame < frameCount; currentFrame++) {
                    Position currentPosition = item.positions.get(currentFrame);
                    if (frameCount > currentFrame + 1) {
                        Position nextPosition = item.positions.get(currentFrame + 1);

                        float movementX = nextPosition.x - currentPosition.x;
                        float movementY = nextPosition.y - currentPosition.y;

                        TranslateAnimation animation = new TranslateAnimation(
                                Animation.ABSOLUTE, 0,
                                Animation.ABSOLUTE, movementX,
                                Animation.ABSOLUTE, 0,
                                Animation.ABSOLUTE, movementY);

                        animation.setDuration(animationLength);
                        animation.setStartOffset(animationLength * animationSet.getAnimations().size());
                        animationSet.addAnimation(animation);
                    }
                }

                // Play animations
                if(item.view instanceof ImageView) {
                    ImageView imageView = (ImageView) item.view;
                    imageView.startAnimation(animationSet);
                } else if (item.view instanceof RelativeLayout) {
                    RelativeLayout relativeLayout = (RelativeLayout) item.view;
                    relativeLayout.startAnimation(animationSet);
                }
            }

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
                                hideDisableToolsOverlay();
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

    public void saveField(String tag) {
        String base64 = ImageUtil.getBitmapAsBase64(ImageUtil.getDrawableAsBitmap(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.default_logo)));
        ExportField exportFile1 = new ExportField();
        exportFile1.name = tag;
        exportFile1.image = base64;

        JsonDatabase.saveField(exportFile1);

        /*takeScreenShot(new ScreenshotRecorder.OnScreenshotHandlerCallback() {
            @Override
            public void onScreenshotFinished(Bitmap screenshot) {
                String base64 = ImageUtil.getBitmapAsBase64(screenshot);
                ExportField exportFile = new ExportField();
                exportFile.name = tag;
                exportFile.image = base64;

                JsonDatabase.saveField(exportFile);

                ArrayList<ExportField> items = JsonDatabase.getSavedFields();
                for(ExportField item : items) {
                    Logger.log(item.name);
                    Logger.log(item.image);
                }

                // TODO save screenshot to memory?
                *//*if(StorageHelper.isExternalStorageWritable()) {
                    recordingsPath = StorageHelper.getExternalDirectory();
                } else {
                    recordingsPath = StorageHelper.getInternalDirectory();
                }
                Logger.log("Saving debug screenshot to " + fileName);
                try (FileOutputStream out = new FileOutputStream(fileName)) {
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*
            }

            @Override
            public void onScreenshotFailed(@Nullable Throwable e) {

            }
        });*/
        ArrayList<ExportItem> exportItems = new ArrayList<>();
        for (MovableView item : movableViews.values()) {
            exportItems.add(new ExportItem(item));
        }
        Gson gson = new Gson();
        String json = gson.toJson(exportItems);
        Logger.log(json);
        ArrayList<ExportItem> items = gson.fromJson(json, new TypeToken<List<ExportItem>>(){}.getType());
        for(ExportItem item : items) {
            Logger.log(item.id);
            Logger.log(item.type);
        }
        ExportField exportFile = new ExportField();
        exportFile.name = tag;
        exportFile.items = items;

        /*if(Build.VERSION.SDK_INT >= 21) {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.tactic_board_record_info));
            dialogFragment.show(getChildFragmentManager(), "Tallennetaanko video?");
            dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                @Override
                public void onDialogYesButtonClick() {
                    // TODO kysy luvat, toista animaatio, näytä "muunnetaan.." ja tallenna video
                    convertAnimationToVideo();
                }
            });
        } else {
            // TODO jos vanha versio -> tallenna vain kuvio

        }*/
    }
}
