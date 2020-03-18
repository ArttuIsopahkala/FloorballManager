package com.ardeapps.floorballmanager.tacticBoard.views;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.tacticBoard.media.AnimationRecorder;
import com.ardeapps.floorballmanager.tacticBoard.objects.MovableView;
import com.ardeapps.floorballmanager.tacticBoard.objects.Position;
import com.ardeapps.floorballmanager.tacticBoard.utils.AnimationTool;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ardeapps.floorballmanager.tacticBoard.utils.AnimationTool.animationLength;

public class TacticBoardAnimation extends RelativeLayout {

    public TacticBoardAnimation(Context context) {
        super(context);
        createView(context);
    }

    public TacticBoardAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    public static class PlayerCardHolder {
        ImageView pictureImage;
        TextView nameText;
    }

    public static class CurrentlyMovingItem {
        String id;
        MovableView.Type type;

        CurrentlyMovingItem(String id, MovableView.Type type) {
            this.id = id;
            this.type = type;
        }
    }

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

                    // TODO
                    //showDisableToolsOverlay(true);
                }
            }
            return false;
        });
    }

    private LinearLayout notAddedPlayersContainer;
    private RelativeLayout fieldViewsContainer;

    private int ballWidth;
    private int playerWidth;
    private int selectedFrame = 0;
    private int frameCount = 1;
    private int fieldHeight = 0;
    private int fieldWidth = 0;
    private ArrayList<Player> activePlayers = new ArrayList<>();
    private ArrayList<String> notAddedPlayers = new ArrayList<>();
    private Map<String, View> playerViews = new HashMap<>();
    private Map<String, MovableView> movableViews = new HashMap<>();
    private CurrentlyMovingItem movingItem;

    public AnimationTool.AnimationToolListener getAnimationToolListener() {
        return animationToolListener;
    }

    AnimationTool.AnimationToolListener animationToolListener = new AnimationTool.AnimationToolListener() {
        @Override
        public void onAddFrame() {

        }

        @Override
        public void onPlay() {

        }

        @Override
        public void onRemoveFrame() {

        }

        @Override
        public void onStop() {
            setFrame(0);
            for (MovableView item : movableViews.values()) {
                item.view.clearAnimation();
            }
            AnimationRecorder.getInstance().stopRecording();
        }

        @Override
        public void onPause() {
            setFrame(selectedFrame - 1); // Set currently selected frame
            for (MovableView item : movableViews.values()) {
                item.view.clearAnimation();
            }
        }

        @Override
        public void onFrameSelected(int frame) {
            selectedFrame = frame;
            Logger.log("FRAME SELECTED");
        }

        @Override
        public void onFrameCountChanged(int count) {
            frameCount = count;
        }
    };

    public boolean isChanges() {
        return movableViews.size() > 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.tactic_board_animation, this);
        fieldViewsContainer = findViewById(R.id.fieldViewsContainer);
        notAddedPlayersContainer = findViewById(R.id.notAddedPlayersContainer);

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

        fieldViewsContainer.post(() -> {
            fieldHeight = fieldViewsContainer.getHeight();
            fieldWidth  = fieldViewsContainer.getWidth();

            // Calculate views' size to screen
            ballWidth = Helper.pxToDp(fieldWidth) / 6;
            playerWidth = Helper.pxToDp(fieldWidth) / 3;
        });

        setOnTouchListener((v1, event) -> {
            float x = event.getX();
            float y = event.getY();
            View view = TacticBoardHelper.findViewAtPosition(fieldViewsContainer, (int) event.getRawX(), (int) event.getRawY());
            Logger.log("View under finger: " + TacticBoardHelper.findViewAtPosition(fieldViewsContainer, (int) event.getRawX(), (int) event.getRawY()));

            if (view == null) {
                // Handle draw functions
                /*if (selectedTool == Tool.BALL) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        drawBall(x, y);
                    }
                }*/
            } else if (view.getTag() != null) {
                // Handle existing items
                String tag = view.getTag().toString();
                MovableView.Type type = MovableView.Type.fromDatabaseName(TacticBoardHelper.getType(tag));
                String id = TacticBoardHelper.getId(tag);
                MovableView movableView = movableViews.get(id);
                // TODO add ERASER
                /*if (selectedTool == Tool.ERASER) {
                    if(movableView != null) {
                        if (type == MovableView.Type.PLAYER) {
                            notAddedPlayers.add(id);
                            drawNotAddedPlayerViews();
                        }
                        removeItemFromField(movableView);
                    }
                }*/
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
                    // TODO
                    //hideDisableToolsOverlay();
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
        // TODO
        //hideDisableToolsOverlay();
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

    public void setNotAddedPlayersVisible(boolean visible) {
        if (visible) {
            if (!activePlayers.isEmpty()) {
                notAddedPlayersContainer.setVisibility(View.VISIBLE);
                drawNotAddedPlayerViews();
            } else {
                notAddedPlayersContainer.setVisibility(View.GONE);
            }
        } else {
            notAddedPlayersContainer.setVisibility(View.GONE);
        }
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
        // Remove position from removed index and select previous
        for (MovableView item : movableViews.values()) {
            item.positions.remove(selectedFrame);
        }
        int newSelectedFrame = selectedFrame > 0 ? selectedFrame - 1 : 0;
        setFrame(newSelectedFrame);
    }

    private void addFrame() {
        if (selectedFrame > 0) {
            // Copy position from last frame
            for (MovableView item : movableViews.values()) {
                Position lastPosition = item.positions.get(selectedFrame - 1);
                item.positions.add(lastPosition);
            }
            setFrame(selectedFrame);
        }
    }

    public void setFrame(int selectedFrame) {
        // Set views position
        for (MovableView item : movableViews.values()) {
            Position position = item.positions.get(selectedFrame);
            moveItemInField(item, position);
        }
    }

    public void runAnimation() {
        for (MovableView item : movableViews.values()) {
            // Add item to initial position
            Position startPosition = item.positions.get(selectedFrame);
            moveItemInField(item, startPosition);

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setInterpolator(new LinearInterpolator());
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Set to last position
                    Position lastPosition = item.positions.get(item.positions.size() - 1);
                    moveItemInField(item, lastPosition);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            // Collect animations
            for(int currentFrame = selectedFrame; currentFrame < frameCount; currentFrame++) {
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
    }

}
