package com.ardeapps.floorballmanager.tacticBoard.views;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Pair;
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
import com.ardeapps.floorballmanager.tacticBoard.media.AnimationRecorder;
import com.ardeapps.floorballmanager.tacticBoard.objects.MovableView;
import com.ardeapps.floorballmanager.tacticBoard.objects.Position;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.CENTER;
import static com.ardeapps.floorballmanager.tacticBoard.views.TacticBoardFragment.BLUE;
import static com.ardeapps.floorballmanager.tacticBoard.views.TacticBoardFragment.RED;

public class TacticBoardAnimation extends RelativeLayout {

    public TacticBoardAnimation(Context context) {
        super(context);
        createView(context);
    }

    public TacticBoardAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    public class TacticPlayer {
        int index;
        String name;
    }

    public static class PlayerCardHolder {
        ImageView backgroundImage;
        TextView numberText;
        TextView nameText;
    }

    public static class CurrentlyMovingItem {
        int index;
        MovableView.Type type;

        CurrentlyMovingItem(MovableView.Type type, int index) {
            this.index = index;
            this.type = type;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setDragListener(View view, MovableView.Type type, int index) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (movingItem == null) {
                    movingItem = new CurrentlyMovingItem(type, index);
                    ClipData.Item item = new ClipData.Item(TacticBoardHelper.createTag(type, index));
                    ClipData dragData = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

                    DragShadowBuilder dragView = new DragShadowBuilder(v);
                    v.startDrag(dragData, dragView, null, 0);
                    v.setVisibility(View.GONE);

                    showDisableToolsOverlay(true);
                }
            }
            return false;
        });
    }

    private RelativeLayout disableToolsOverlay;
    private TextView disableToolsInfoText;
    private IconView disableToolsIcon;
    private LinearLayout notAddedPlayersContainer;
    private RelativeLayout fieldViewsContainer;
    private LinearLayout framesContainer;
    private LinearLayout playIcon;
    private LinearLayout stopIcon;
    private LinearLayout plusIcon;
    private LinearLayout minusIcon;
    private IconView playIconView;
    private Pair<PlayerCardHolder,View> notAddedStaticPlayer;

    private Boolean isHomePlayersSelected = null;
    private int ballWidth;
    private int playerWidth;
    private int fieldTopY = 0;
    private int fieldWidth = 0;
    private int homeColor = RED;
    private int awayColor = BLUE;
    private Map<Integer, TacticPlayer> homePlayers = new HashMap<>();
    private Map<Integer, TacticPlayer> awayPlayers = new HashMap<>();
    private Map<Integer, View> homePlayerViews = new HashMap<>();
    private Map<Integer, View> awayPlayerViews = new HashMap<>();
    private Map<Pair<MovableView.Type, Integer>, MovableView> addedViews = new HashMap<>();
    private Map<Pair<MovableView.Type, Integer>, MovableView> notAddedViews = new HashMap<>();
    private CurrentlyMovingItem movingItem;

    private int selectedFrame = 0;
    private int frameCount = 1;
    ArrayList<TextView> frameButtons = new ArrayList<>();
    private boolean isAnimationRunning = false;
    Timer animationTimer = null;
    public static final int animationLength = 1000;
    TacticBoardMenu.Tool selectedTool;

    public void setSelectedTool(TacticBoardMenu.Tool tool) {
        selectedTool = tool;
    }

    public Boolean isHomeSelected() {
        return isHomePlayersSelected;
    }

    public void setHomeSelected(Boolean isHome) {
        isHomePlayersSelected = isHome;
    }

    public void setHomePlayers(Map<Integer, TacticPlayer> homePlayers) {
        this.homePlayers = homePlayers;
    }

    public void setAwayPlayers(Map<Integer, TacticPlayer> awayPlayers) {
        this.awayPlayers = awayPlayers;
    }

    public void setHomeColor(int homeColor) {
        this.homeColor = homeColor;
    }

    public void setAwayColor(int awayColor) {
        this.awayColor = awayColor;
    }

    public void hideNotAddedPlayers() {
        notAddedPlayersContainer.setVisibility(View.GONE);
    }

    public void clearField() {
        addedViews = new HashMap<>();
        fieldViewsContainer.removeAllViewsInLayout();
        drawNotAddedPlayerViews();
    }

    public void setFieldTopY(int fieldTopY) {
        this.fieldTopY = fieldTopY;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fieldTopY);
        disableToolsOverlay.setLayoutParams(params);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.tactic_board_animation, this);
        disableToolsOverlay = findViewById(R.id.disableToolsOverlay);
        disableToolsInfoText = findViewById(R.id.disableToolsInfoText);
        disableToolsIcon = findViewById(R.id.disableToolsIcon);
        fieldViewsContainer = findViewById(R.id.fieldViewsContainer);
        notAddedPlayersContainer = findViewById(R.id.notAddedPlayersContainer);
        playIcon = findViewById(R.id.playIcon);
        stopIcon = findViewById(R.id.stopIcon);
        plusIcon = findViewById(R.id.plusIcon);
        minusIcon = findViewById(R.id.minusIcon);
        framesContainer = findViewById(R.id.framesContainer);
        playIconView = findViewById(R.id.playIconView);

        RelativeLayout staticPlayer = findViewById(R.id.notAddedStaticPlayer);
        PlayerCardHolder holder = new PlayerCardHolder();
        holder.backgroundImage = staticPlayer.findViewById(R.id.backgroundImage);
        holder.numberText = staticPlayer.findViewById(R.id.numberText);
        holder.nameText = staticPlayer.findViewById(R.id.nameText);
        notAddedStaticPlayer = new Pair<>(holder, staticPlayer);

        plusIcon.setOnClickListener(v -> addFrame());
        minusIcon.setOnClickListener(v -> removeFrame());
        playIcon.setOnClickListener(v -> {
            if (isAnimationRunning) {
                // Pause
                Logger.log("frame: " + (selectedFrame - 1));
                stopAnimation(selectedFrame - 1);
            } else {
                // Play
                if (frameCount > 0 && selectedFrame < frameCount - 1) {
                    runAnimation();
                }
            }
        });
        stopIcon.setOnClickListener(v -> stopAnimation(0));

        // Initialize first frame
        selectedFrame = 0;
        addFrame();

        //homePlayers = AppRes.getInstance().getActivePlayers(false);
        homePlayers = new HashMap<>();
        // TODO Mock data
        TacticPlayer player1 = new TacticPlayer();
        player1.name = "Arttu";
        player1.index = 1;
        TacticPlayer player2 = new TacticPlayer();
        player2.name = "Pekka";
        player2.index = 11;
        TacticPlayer player3 = new TacticPlayer();
        player3.index = 15;
        homePlayers.put(player1.index, player1);
        homePlayers.put(player2.index, player2);
        homePlayers.put(player2.index, player2);
        setHomePlayers(homePlayers);
        initializePlayers(true);

        awayPlayers = new HashMap<>();
        // TODO Mock data
        TacticPlayer player4 = new TacticPlayer();
        player4.name = "PASKA";
        player4.index = 1;
        TacticPlayer player5 = new TacticPlayer();
        player5.name = "Läjä";
        player5.index = 11;
        awayPlayers.put(player4.index, player4);
        awayPlayers.put(player5.index, player5);
        setAwayPlayers(awayPlayers);
        initializePlayers(false);

        fieldViewsContainer.post(() -> {
            fieldWidth = fieldViewsContainer.getWidth();

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
                if (selectedTool == TacticBoardMenu.Tool.BALL) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        drawBall(x, y);
                    }
                }
            } else if (view.getTag() != null) {
                // Handle existing items
                String tag = view.getTag().toString();
                MovableView.Type type = MovableView.Type.fromDatabaseName(TacticBoardHelper.getType(tag));
                int index = TacticBoardHelper.getIndex(tag);
                MovableView movableView = addedViews.get(MovableView.getPairId(type, index));
                // TODO add ERASER
                /*if (selectedTool == Tool.ERASER) {
                    if(movableView != null) {
                        if (type == MovableView.Type.PLAYER) {
                            notAddedPlayerIds.add(index);
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
                    onResetItem(movingItem.type, movingItem.index);
                    movingItem = null;
                    hideDisableToolsOverlay();
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
                    int index = TacticBoardHelper.getIndex(itemView);
                    MovableView movableView = addedViews.get(new Pair<>(type, index));

                    float positionX = event.getX();
                    float positionY = event.getY();

                    // Remove item from view
                    if (positionY < fieldTopY) {
                        onResetItem(type, index);
                    } else {
                        if (type == MovableView.Type.BALL) {
                            if (movableView != null) {
                                Logger.log("MOVE BALL");
                                // Replace old position
                                Position position = new Position(positionX, positionY);
                                movableView.positions.set(selectedFrame, position);
                                moveItemInField(movableView, position);
                            }
                        } else if (type == MovableView.Type.HOME_PLAYER || type == MovableView.Type.AWAY_PLAYER) {
                            Logger.log("PLAYER ID: " + index);
                            Map<Integer, View> playerViews = type == MovableView.Type.HOME_PLAYER ? homePlayerViews : awayPlayerViews;
                            View view = playerViews.get(index);
                            if (view != null) {
                                if (movableView != null) {
                                    // Just move player
                                    Logger.log("FROM FIELD TO FIELD");
                                    // Replace old position
                                    Position position = new Position(positionX, positionY);
                                    movableView.positions.set(selectedFrame, position);
                                    moveItemInField(movableView, position);
                                } else {
                                    // Player moves from toolbar to field
                                    MovableView playerItem = new MovableView(AppRes.getContext());
                                    playerItem.paramSize = playerWidth;
                                    playerItem.type = type;
                                    playerItem.view = view;
                                    playerItem.index = getNextAvailableIndex(type);
                                    Position position = new Position(positionX, positionY);
                                    // Add position for all frames
                                    for (int i = 0; i < frameCount; i++) {
                                        playerItem.positions.add(position);
                                    }

                                    Logger.log("FROM TOOLBAR TO FIELD");
                                    // 1. Remove from players tool bar
                                    notAddedPlayersContainer.removeView(view);
                                    //notAddedPlayerIds.remove(playerId);
                                    // 2. Add to field
                                    addItemToField(playerItem, position);
                                }
                            }
                        }
                    }

                    // Invalidates the view to force a redraw
                    movingItem = null;
                    v13.invalidate();

                    hideDisableToolsOverlay();
                    return true;
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializePlayers(boolean isHome) {
        final PlayerCardHolder holder = new PlayerCardHolder();
        int color = isHome ? homeColor : awayColor;
        Map<Integer, View> playerViews = isHome ? homePlayerViews : awayPlayerViews;
        Map<Integer, TacticPlayer> players = isHome ? homePlayers : awayPlayers;
        MovableView.Type type = isHome ? MovableView.Type.HOME_PLAYER : MovableView.Type.AWAY_PLAYER;

        for (Map.Entry<Integer, TacticPlayer> entry : players.entrySet()) {
            Integer index = entry.getKey();
            TacticPlayer player = entry.getValue();

            View cv = LayoutInflater.from(AppRes.getContext()).inflate(R.layout.tactic_board_player, null);
            holder.backgroundImage = cv.findViewById(R.id.backgroundImage);
            holder.numberText = cv.findViewById(R.id.numberText);
            holder.nameText = cv.findViewById(R.id.nameText);

            cv.setTag(TacticBoardHelper.createTag(type, player.index));
            holder.backgroundImage.setBackgroundColor(color);
            holder.numberText.setText(String.valueOf(player.index));
            if (StringUtils.isEmptyString(player.name)) {
                holder.nameText.setVisibility(GONE);
            } else {
                holder.nameText.setVisibility(VISIBLE);
                holder.nameText.setText(player.name);
            }
            setDragListener(cv, type, player.index);

            playerViews.put(index, cv);
        }

        PlayerCardHolder staticHolder = notAddedStaticPlayer.first;
        View staticView = notAddedStaticPlayer.second;
        staticView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (movingItem == null) {
                    int index = getNextAvailableIndex(type);

                    staticHolder.backgroundImage.setBackgroundColor(color);
                    staticHolder.numberText.setText(String.valueOf(getNextAvailableIndex(type)));
                    staticHolder.nameText.setVisibility(GONE);
                    setDragListener(staticView, type, index);

                    int newIndex = getNextAvailableIndex(type);
                    movingItem = new CurrentlyMovingItem(type, newIndex);
                    ClipData.Item item = new ClipData.Item(TacticBoardHelper.createTag(type, newIndex));
                    ClipData dragData = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

                    DragShadowBuilder dragView = new DragShadowBuilder(staticView);
                    staticView.startDrag(dragData, dragView, null, 0);
                    staticView.setVisibility(View.GONE);

                    showDisableToolsOverlay(true);
                }
            }
            return false;
        });
    }

    /*private View createPlayer() {
        MovableView.Type type = isHomePlayersSelected ? MovableView.Type.HOME_PLAYER : MovableView.Type.AWAY_PLAYER;
        int index = getNextAvailableIndex(type);
        final PlayerCardHolder holder = new PlayerCardHolder();
        int color = isHomePlayersSelected ? homeColor : awayColor;
        View cv = LayoutInflater.from(AppRes.getContext()).inflate(R.layout.tactic_board_player, null);
        holder.backgroundImage = cv.findViewById(R.id.backgroundImage);
        holder.numberText = cv.findViewById(R.id.numberText);
        holder.nameText = cv.findViewById(R.id.nameText);

        holder.backgroundImage.setBackgroundColor(color);
        holder.numberText.setText(String.valueOf(index));
        holder.nameText.setVisibility(GONE);
        setDragListener(cv, type, index);
        return cv;
    }*/

    private int getNextAvailableIndex(MovableView.Type type) {
        int index = 1;
        List<Integer> indexes = getAddedIndexes(type);
        while (indexes.contains(index)) {
            index++;
        }
        return index;
    }

   /* private void updatePlayers(ArrayList<TacticPlayer> players, boolean isHome) {
        final PlayerCardHolder holder = new PlayerCardHolder();
        int color = isHome ? homeColor : awayColor;
        Map<String, View> playerViews = isHome ? homePlayerViews : awayPlayerViews;
        Map<String, Player> players = isHome ? homePlayers : awayPlayers;
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            String playerId = entry.getKey();
            Player player = entry.getValue();
            View cv = LayoutInflater.from(AppRes.getContext()).inflate(R.layout.tactic_board_player, null);
            holder.backgroundImage = cv.findViewById(R.id.backgroundImage);
            holder.numberText = cv.findViewById(R.id.numberText);
            holder.nameText = cv.findViewById(R.id.nameText);
            cv.setTag(TacticBoardHelper.createTag(MovableView.Type.PLAYER, player.getPlayerId()));

            holder.backgroundImage.setBackgroundColor(color);
            holder.numberText.setText(String.valueOf(player.getNumber()));
            if(StringUtils.isEmptyString(player.getName())) {
                holder.nameText.setVisibility(GONE);
            } else {
                holder.nameText.setVisibility(VISIBLE);
                holder.nameText.setText(player.getName());
            }
            setDragListener(cv, MovableView.Type.PLAYER, player.getPlayerId());

            playerViews.put(playerId, cv);
        }
    }*/

    private void showDisableToolsOverlay(boolean showRemoveIcon) {
        disableToolsOverlay.setVisibility(View.VISIBLE);
        if (showRemoveIcon) {
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

    private void onResetItem(MovableView.Type type, int index) {
        MovableView item = addedViews.get(MovableView.getPairId(type, index));
        if (type == MovableView.Type.BALL) {
            Logger.log("REMOVE BALL");
            if (item != null) {
                removeItemFromField(item);
            }
        } else if (type == MovableView.Type.HOME_PLAYER || type == MovableView.Type.AWAY_PLAYER) {
            if (item != null) {
                Logger.log("FROM FIELD TO TOOLBAR");
                removeItemFromField(item);
                drawNotAddedPlayerViews();
            } else {
                Map<Integer, View> playerViews = isHomePlayersSelected ? homePlayerViews : awayPlayerViews;
                View playerView = playerViews.get(index);
                if (playerView != null) {
                    Logger.log("FROM TOOLBAR TO TOOLBAR");
                    playerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private ArrayList<Integer> getNotAddedIndexes(MovableView.Type type) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (MovableView item : notAddedViews.values()) {
            if(item.type == type) {
                indexes.add(item.index);
            }
        }
        return indexes;
    }

    private ArrayList<Integer> getAddedIndexes(MovableView.Type type) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (MovableView item : addedViews.values()) {
            if(item.type == type) {
                indexes.add(item.index);
            }
        }
        return indexes;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void drawNotAddedPlayerViews() {
        if (isHomePlayersSelected == null) {
            return;
        }
        Map<Integer, View> playerViews = isHomePlayersSelected ? homePlayerViews : awayPlayerViews;
        Map<Integer, TacticPlayer> allNotAddedPlayers = isHomePlayersSelected ? homePlayers : awayPlayers;
        MovableView.Type type = isHomePlayersSelected ? MovableView.Type.HOME_PLAYER : MovableView.Type.AWAY_PLAYER;

        notAddedPlayersContainer.setVisibility(View.VISIBLE);
        notAddedPlayersContainer.removeAllViewsInLayout();

        PlayerCardHolder staticHolder = notAddedStaticPlayer.first;
        View staticView = notAddedStaticPlayer.second;
        staticHolder.numberText.setText(String.valueOf(getNextAvailableIndex(type)));
        staticHolder.nameText.setVisibility(GONE);
        notAddedPlayersContainer.addView(staticView);

        // Collect players not in field
        Map<Integer, TacticPlayer> notAddedPlayers = new HashMap<>();
        List<Integer> indexesAdded = getAddedIndexes(type);
        for(TacticPlayer player : allNotAddedPlayers.values()) {
            if(!indexesAdded.contains(player.index)) {
                notAddedPlayers.put(player.index, player);
            }
        }

        for (Map.Entry<Integer, View> entry : playerViews.entrySet()) {
            Integer index = entry.getKey();
            View view = entry.getValue();
            if (notAddedPlayers.keySet().contains(index)) {
                view.setVisibility(View.VISIBLE);
                LayoutParams params = new LayoutParams(playerWidth, playerWidth);
                view.setLayoutParams(params);
                notAddedPlayersContainer.addView(view);
            }
        }
    }

    private void drawBall(float x, float y) {
        int index = getNextAvailableIndex(MovableView.Type.BALL);
        ImageView imageView = new ImageView(AppRes.getContext());
        imageView.setImageResource(R.drawable.floorball_icon);
        imageView.setTag(TacticBoardHelper.createTag(MovableView.Type.BALL, index));

        Position position = new Position(x, y);
        MovableView ballItem = new MovableView(AppRes.getContext());

        ballItem.paramSize = ballWidth;
        ballItem.type = MovableView.Type.BALL;
        ballItem.index = index;
        ballItem.view = imageView;
        // Add position for all frames
        for (int i = 0; i < frameCount; i++) {
            ballItem.positions.add(position);
        }
        setDragListener(ballItem.view, ballItem.type, ballItem.index);

        addItemToField(ballItem, position);
    }

    private void addItemToField(MovableView item, Position position) {
        addedViews.put(item.getPairId(), item);
        notAddedViews.remove(item.getPairId());
        LayoutParams params = new LayoutParams(item.paramSize, item.paramSize);
        float leftTopX = position.x - (float) item.paramSize / 2;
        float leftTopY = position.y - (float) item.paramSize / 2;
        params.leftMargin = (int) leftTopX;
        params.topMargin = (int) leftTopY;
        item.view.setVisibility(View.VISIBLE);
        item.view.bringToFront();
        if (item.view instanceof ImageView) {
            ImageView imageView = (ImageView) item.view;
            fieldViewsContainer.addView(imageView, params);
        } else if (item.view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) item.view;
            fieldViewsContainer.addView(relativeLayout, params);
        }
    }

    private void moveItemInField(MovableView item, Position position) {
        LayoutParams params = new LayoutParams(item.paramSize, item.paramSize);
        float leftTopX = position.x - (float) item.paramSize / 2;
        float leftTopY = position.y - (float) item.paramSize / 2;
        params.leftMargin = (int) leftTopX;
        params.topMargin = (int) leftTopY;
        item.view.setVisibility(View.VISIBLE);
        item.view.bringToFront();
        if (item.view instanceof ImageView) {
            ImageView imageView = (ImageView) item.view;
            imageView.setLayoutParams(params);
        } else if (item.view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) item.view;
            relativeLayout.setLayoutParams(params);
        }
    }

    private void removeItemFromField(MovableView item) {
        addedViews.remove(item.getPairId());
        notAddedViews.put(new Pair<>(item.type, item.index), item);
        fieldViewsContainer.removeView(item.view);
    }

    private void removeFrame() {
        if (frameButtons.size() <= 1) {
            return;
        }
        framesContainer.removeViewAt(selectedFrame);
        frameButtons.remove(selectedFrame);
        frameCount = frameButtons.size();

        // Set new indexes for existing buttons
        for (TextView frameButton : frameButtons) {
            int frameIndex = frameButtons.indexOf(frameButton);
            frameButton.setText(String.valueOf(frameIndex + 1));
            frameButton.setOnClickListener(button -> setFrame(frameIndex));
        }

        // Remove position from removed index
        for (MovableView item : addedViews.values()) {
            item.positions.remove(selectedFrame);
        }

        // Select previous frame
        selectedFrame = selectedFrame > 0 ? selectedFrame - 1 : 0;
        setFrame(selectedFrame);
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
        frameCount = frameButtons.size();

        int frameIdx = frameButtons.indexOf(frameTextView);
        frameTextView.setOnClickListener(button -> setFrame(frameIdx));

        // Copy position from last frame
        for (MovableView item : addedViews.values()) {
            Position lastPosition = item.positions.get(frameIdx - 1);
            item.positions.add(lastPosition);
        }

        setFrame(frameIdx);
    }

    private void setFrame(int selectedFrame) {
        this.selectedFrame = selectedFrame;
        setFrameButtonSelected(selectedFrame);
        // Set views position
        for (MovableView item : addedViews.values()) {
            Position position = item.positions.get(selectedFrame);
            moveItemInField(item, position);
        }
    }

    private void setFrameButtonSelected(int selectedFrame) {
        for (int i = 0; i < frameButtons.size(); i++) {
            TextView existingFrame = frameButtons.get(i);
            if (i == selectedFrame) {
                existingFrame.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_yellow_light));
            } else {
                existingFrame.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            }
        }
    }

    private void stopAnimation(int toFrame) {
        playIconView.setText(AppRes.getContext().getString(R.string.icon_play));
        isAnimationRunning = false;
        if (animationTimer != null) {
            animationTimer.cancel();
        }

        for (MovableView item : addedViews.values()) {
            item.view.clearAnimation();
        }

        // Stop recording if converting
        if (AnimationRecorder.getInstance().isRecording()) {
            AnimationRecorder.getInstance().stopRecording();
        }

        setFrame(toFrame);
    }

    private void runAnimation() {
        isAnimationRunning = true;
        playIconView.setText(AppRes.getContext().getString(R.string.icon_pause));
        animationTimer = new Timer();
        // Select frames during animation
        final Handler handler = new Handler();
        final TimerTask task = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    if (selectedFrame < frameCount) {
                        setFrameButtonSelected(selectedFrame);
                    } else {
                        stopAnimation(frameCount - 1);
                    }
                    selectedFrame++;
                });
            }
        };
        animationTimer.schedule(task, 0, animationLength);

        // Move items on field
        for (MovableView item : addedViews.values()) {
            // Add item to initial position
            Position startPosition = item.positions.get(selectedFrame);
            moveItemInField(item, startPosition);

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setInterpolator(new LinearInterpolator());
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Set to last position
                    Position lastPosition = item.positions.get(item.positions.size() - 1);
                    moveItemInField(item, lastPosition);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            // Collect animations
            for (int currentFrame = selectedFrame; currentFrame < frameCount; currentFrame++) {
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
            if (item.view instanceof ImageView) {
                ImageView imageView = (ImageView) item.view;
                imageView.startAnimation(animationSet);
            } else if (item.view instanceof RelativeLayout) {
                RelativeLayout relativeLayout = (RelativeLayout) item.view;
                relativeLayout.startAnimation(animationSet);
            }
        }
    }
}
