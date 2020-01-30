package com.ardeapps.floorballmanager.tacticBoard;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.DrawingBoard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_RECORD_SCREEN;

public class TacticBoardFragment extends Fragment {

    DrawingBoard drawingBoard;
    TacticBoardTools tacticBoardTools;
    TacticBoardTools.Tool selectedTool;
    LinearLayout notAddedPlayersContainer;
    RelativeLayout fieldViewsContainer;
    RelativeLayout removePlayersOverlay;

    TacticSettingsDialogFragment.Field selectedField;
    ArrayList<String> notAddedPlayers = new ArrayList<>();
    Map<String, View> playerViews = new HashMap<>();
    Map<String, MovableView> movableViews = new HashMap<>();

    ArrayList<Player> activePlayers = new ArrayList<>();
    int drawingBoardHeight;
    int color = R.color.color_red_light;
    int size = 10;
    final static int ballSize = 40;
    final static int playerSize = 100;
    private double imageWidth;
    private double imageHeight;
    private boolean isAnimationRunning = false;

    public static final int RECORD_VIDEO = 2;
    Timer animationTimer = null;
    int currentAnimationFrame = 0;

    private class PlayerCardHolder {
        ImageView pictureImage;
        TextView nameText;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setDragListener(View view, MovableView.Type type, String id) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData.Item item = new ClipData.Item(TacticBoardHelper.createTag(type, id));
                ClipData dragData = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

                View.DragShadowBuilder dragView = new View.DragShadowBuilder(v);
                v.startDrag(dragData, dragView, null, 0);
                v.setVisibility(View.GONE);
            }
            return false;
        });
    }

    DragEventListener dragEventListener = new DragEventListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO rmove
        /*animationRecorder = new AnimationRecorder(new AnimationRecorder.AnimationRecorderListener() {
            @Override
            public void onAskForPermission(Intent intent) {
                startActivityForResult(intent, RECORD_VIDEO);
            }
        });*/

        FragmentListeners.getInstance().setPermissionHandledListener(new FragmentListeners.PermissionHandledListener() {
            @Override
            public void onPermissionGranted(int MY_PERMISSION) {
                if(MY_PERMISSION == MY_PERMISSION_ACCESS_RECORD_SCREEN) {
                    Logger.log("RECORD_SCREEN_ACCESS GIVEN");
                    convertAnimationToVideo();
                }
            }

            @Override
            public void onPermissionDenied(int MY_PERMISSION) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RECORD_VIDEO) {
            Logger.log("Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Logger.log("Screen Cast Permission Denied");
            return;
        }
        Logger.log("ACTIVIRY RESULT GIVEN");
        AnimationRecorder.getInstance().setPermissionGranted(resultCode, data);
        convertAnimationToVideo();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tactic_board, container, false);

        removePlayersOverlay = v.findViewById(R.id.removePlayersOverlay);
        fieldViewsContainer = v.findViewById(R.id.fieldViewsContainer);
        notAddedPlayersContainer = v.findViewById(R.id.notAddedPlayersContainer);
        drawingBoard = v.findViewById(R.id.drawingBoard);
        tacticBoardTools = v.findViewById(R.id.tacticBoardTools);

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

        tacticBoardTools.post(() -> {
            drawingBoardHeight = v.getHeight() - tacticBoardTools.getHeight();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.height = drawingBoardHeight;
            fieldViewsContainer.setLayoutParams(params);
            //drawingBoard.setMaxHeight(drawingBoardHeight);
        });

        // Default
        selectedField = TacticSettingsDialogFragment.Field.FULL;
        selectedTool = TacticBoardTools.Tool.PEN;
        /*drawingBoard.setBackgroundField(selectedField);
        drawingBoard.setPaintColor(color);
        drawingBoard.setPaintSize(size);
        drawingBoard.setSelectedTool(selectedTool);*/

        // For not added players
        v.setOnDragListener(dragEventListener);
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                v.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = v.getMeasuredHeight();
                imageWidth = v.getMeasuredWidth();
                return true;
            }
        });

        // Images move events or eraser
        drawingBoard.setOnTouchListener((v1, event) -> {
            float x = event.getX();
            float y = event.getY();
            View view = TacticBoardHelper.findViewAtPosition(fieldViewsContainer, (int) event.getRawX(), (int) event.getRawY());
            Logger.log("View under finger: " + TacticBoardHelper.findViewAtPosition(fieldViewsContainer, (int) event.getRawX(), (int) event.getRawY()));

            if (view == null) {
                // Handle draw functions
                if (selectedTool == TacticBoardTools.Tool.BALL) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            drawBall(x, y);
                            break;
                    }
                }
            } else if (view.getTag() != null) {
                // Handle existing items
                String tag = view.getTag().toString();
                MovableView.Type type = MovableView.Type.fromDatabaseName(TacticBoardHelper.getType(tag));
                String id = TacticBoardHelper.getId(tag);
                Logger.log("ID " + id);
                MovableView movableView = movableViews.get(id);
                if (selectedTool == TacticBoardTools.Tool.ERASER) {
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

        tacticBoardTools.setListener(new TacticBoardTools.ToolBarListener() {
            @Override
            public void onToolChanged(TacticBoardTools.Tool tool) {
                selectedTool = tool;
                drawingBoard.setSelectedTool(tool);
            }

            @Override
            public void onClearField() {
                drawingBoard.clear();
                movableViews = new HashMap<>();
                fieldViewsContainer.removeAllViewsInLayout();
                notAddedPlayers = new ArrayList<>();
                for(Player player : activePlayers) {
                    notAddedPlayers.add(player.getPlayerId());
                }
                drawNotAddedPlayerViews();
            }

            @Override
            public void onUndoPrevious() {
                drawingBoard.restore();
            }

            @Override
            public void onShowPlayers() {
                if (!activePlayers.isEmpty()) {
                    notAddedPlayersContainer.setVisibility(View.VISIBLE);
                    drawNotAddedPlayerViews();
                } else {
                    notAddedPlayersContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBackgroundChanged(TacticSettingsDialogFragment.Field field) {
                selectedField = field;
                drawingBoard.setBackgroundField(field);
            }

            @Override
            public void onPlayButtonClick() {
                runAnimation();
            }

            @Override
            public void onStopButtonClick() {
                tacticBoardTools.setSelectedFrame(0);
                isAnimationRunning = false;
                if(animationTimer != null) {
                    animationTimer.cancel();
                }
                for (MovableView item : movableViews.values()) {
                    item.view.clearAnimation();
                }
                AnimationRecorder.getInstance().stopRecording();
            }

            @Override
            public void onPauseButtonClick() {
                if(isAnimationRunning) {
                    tacticBoardTools.setSelectedFrame(currentAnimationFrame - 1); // Set currently selected frame
                    isAnimationRunning = false;
                    if(animationTimer != null) {
                        animationTimer.cancel();
                    }
                    for (MovableView item : movableViews.values()) {
                        item.view.clearAnimation();
                    }
                }
            }

            @Override
            public void onFrameChanged() {
                // Set views position
                int selectedFrame = tacticBoardTools.getSelectedFrame();
                for (MovableView item : movableViews.values()) {
                    Position position = item.positions.get(selectedFrame);
                    moveItemInField(item, position);
                }
            }

            @Override
            public void onFrameAdded() {
                // Copy position from last frame
                int selectedFrame = tacticBoardTools.getSelectedFrame();
                for (MovableView item : movableViews.values()) {
                    Position lastPosition = item.positions.get(selectedFrame - 1);
                    item.positions.add(lastPosition);
                }
                tacticBoardTools.setSelectedFrame(selectedFrame);
            }

            @Override
            public void onFrameRemoved() {
                // Remove position from removed index and select previous
                int selectedFrame = tacticBoardTools.getSelectedFrame();
                for (MovableView item : movableViews.values()) {
                    item.positions.remove(selectedFrame);
                }
                int newSelectedFrame = selectedFrame > 0 ? selectedFrame - 1 : 0;
                tacticBoardTools.setSelectedFrame(newSelectedFrame);
            }

            @Override
            public void onSaveClick() {
                int frameCount = tacticBoardTools.getFrameCount();
                if (frameCount > 0 && !isAnimationRunning) {
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

                    if(Build.VERSION.SDK_INT >= 21) {
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

                    }
                }
            }
        });

        return v;
    }

    private void runAnimation() {
        int frameCount = tacticBoardTools.getFrameCount();
        int startFrame = tacticBoardTools.getSelectedFrame();
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
                            tacticBoardTools.setFrameButtonSelected(currentAnimationFrame);
                        } else {
                            tacticBoardTools.setSelectedFrame(frameCount - 1);
                            animationTimer.cancel();
                            isAnimationRunning = false;

                            // Stop recording if converting
                            if(AnimationRecorder.getInstance().isRecording()) {
                                removePlayersOverlay.setVisibility(View.GONE);
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

    private void convertAnimationToVideo() {
        if(AnimationRecorder.getInstance().isPermissionGiven(intent -> startActivityForResult(intent, RECORD_VIDEO))) {
            removePlayersOverlay.setVisibility(View.VISIBLE);
            tacticBoardTools.setSelectedFrame(0);
            runAnimation();
            // TODO tallenna oikea tiedostonimi
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            AnimationRecorder.getInstance().prepareRecorder("MOI_" + timeStamp);
            AnimationRecorder.getInstance().startRecording();
        }
    }

    private void drawBall(float x, float y) {
        String id = "ball" + (movableViews.size() + 1);
        ImageView imageView = new ImageView(AppRes.getContext());
        imageView.setImageResource(R.drawable.floorball_icon);
        imageView.setTag(TacticBoardHelper.createTag(MovableView.Type.BALL, id));

        Position position = new Position(x, y);
        MovableView ballItem = new MovableView(AppRes.getContext());
        ballItem.paramSize = ballSize;
        ballItem.type = MovableView.Type.BALL;
        ballItem.id = id;
        ballItem.view = imageView;
        // Add position for all frames
        int frameCount = tacticBoardTools.getFrameCount();
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
            notAddedPlayersContainer.addView(view);
        }
    }

    protected class DragEventListener implements View.OnDragListener {
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate();
                        return true;
                    }
                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DROP:
                    removePlayersOverlay.setVisibility(View.GONE);

                    ClipData.Item item = event.getClipData().getItemAt(0);

                    String itemView = item.getText().toString();
                    MovableView.Type type = MovableView.Type.fromDatabaseName(TacticBoardHelper.getType(itemView));

                    float positionX = event.getX();
                    float positionY = event.getY();
                    Logger.log("Dragged data is " + itemView);
                    Logger.log(positionX + " - " + positionY);

                    // Remove item from view
                    if (positionY > drawingBoardHeight) {
                        if (type == MovableView.Type.BALL) {
                            Logger.log("REMOVE BALL");
                            String ballId = TacticBoardHelper.getId(itemView);
                            MovableView ballItem = movableViews.get(ballId);
                            if (ballItem != null) {
                                removeItemFromField(ballItem);
                            }
                        } else if (type == MovableView.Type.PLAYER) {
                            String playerId = TacticBoardHelper.getId(itemView);
                            if (notAddedPlayers.contains(playerId)) {
                                Logger.log("FROM TOOLBAR TO TOOLBAR");
                                View playerView = playerViews.get(playerId);
                                if (playerView != null) {
                                    playerView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Logger.log("FROM FIELD TO TOOLBAR");
                                MovableView playerItem = movableViews.get(playerId);
                                if(playerItem != null) {
                                    notAddedPlayers.add(playerId);
                                    removeItemFromField(playerItem);
                                    drawNotAddedPlayerViews();
                                }
                            }
                        }
                    } else {
                        if (type == MovableView.Type.BALL) {
                            Logger.log("MOVE BALL");
                            String ballId = TacticBoardHelper.getId(itemView);
                            MovableView ballItem = movableViews.get(ballId);
                            if (ballItem != null) {
                                // Replace old position
                                int selectedFrame = tacticBoardTools.getSelectedFrame();
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
                                    playerItem.paramSize = playerSize;
                                    playerItem.type = MovableView.Type.PLAYER;
                                    playerItem.id = playerId;
                                    playerItem.view = view;
                                    Position position = new Position(positionX, positionY);
                                    // Add position for all frames
                                    int frameCount = tacticBoardTools.getFrameCount();
                                    for (int i = 0; i < frameCount; i++) {
                                        playerItem.positions.add(position);
                                    }
                                    setDragListener(playerItem.view, playerItem.type, playerItem.id);

                                    Logger.log("FROM TOOLBAR TO FIELD");
                                    // 1. Remove from players tool bar
                                    notAddedPlayersContainer.removeView(view);
                                    notAddedPlayers.remove(playerId);
                                    // 2. Add to field
                                    addItemToField(playerItem, position);
                                } else {
                                    // Just move player
                                    // TODO
                                    Logger.log("FROM FIELD TO FIELD");
                                    MovableView playerItem = movableViews.get(playerId);
                                    if (playerItem != null) {
                                        // Replace old position
                                        int selectedFrame = tacticBoardTools.getSelectedFrame();
                                        Position position = new Position(positionX, positionY);
                                        playerItem.positions.set(selectedFrame, position);
                                        moveItemInField(playerItem, position);
                                    }
                                }
                            }
                        }
                    }

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    return true;
            }
            return false;
        }
    }

    private void addItemToField(MovableView item, Position position) {
        movableViews.put(item.id, item);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(item.paramSize, item.paramSize);
        float leftTopX = position.x - (float)item.paramSize / 2;
        float leftTopY = position.y - (float)item.paramSize / 2;
        params.leftMargin = (int) leftTopX;
        params.topMargin = (int) leftTopY;
        item.view.setVisibility(View.VISIBLE);
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

    private float getCenterX(ImageView view, float x) {
        return x - (float)view.getWidth() / 2;
    }

    private float getCenterY(ImageView view, float y) {
        return y - (float)view.getHeight() / 2;
    }

    private double getPositionX(double positionPercentX) {
        return imageWidth * positionPercentX;
    }

    private double getPositionY(double positionPercentY) {
        return imageHeight * positionPercentY;
    }

    public Double getPositionPercentX(Double positionX) {
        if (positionX == null) {
            return null;
        }
        return positionX / imageWidth;
    }

    public Double getPositionPercentY(Double positionY) {
        if (positionY == null) {
            return null;
        }
        return positionY / imageHeight;
    }

    @TargetApi(21)
    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimationRecorder.getInstance().stopRecording();
    }
}
