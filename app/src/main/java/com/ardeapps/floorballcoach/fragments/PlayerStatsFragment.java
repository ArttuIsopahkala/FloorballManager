package com.ardeapps.floorballcoach.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.PlayerStatsFragmentData;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class PlayerStatsFragment extends Fragment implements DataView {

    ImageView pictureImage;
    TextView nameText;
    TextView positionText;
    TextView numberText;
    TextView shootsText;
    IconView editIcon;

    ImageView shootmapImage;
    RelativeLayout shootmapPointsContainer;
    Spinner gameSpinner;
    Spinner gameModeSpinner;
    Spinner typeSpinner;
    Spinner seasonSpinner;

    private double imageWidth;
    private double imageHeight;

    private PlayerStatsFragmentData data;
    private ArrayList<Game> sortedGames;
    private ArrayList<Goal.Mode> gameModes;
    private ArrayList<String> seasonIds;
    int gameSpinnerPosition = 0;
    int gameModeSpinnerPosition = 0;
    int typeSpinnerPosition = 0;
    int seasonSpinnerPosition = 0;

    @Override
    public void setData(Object viewData) {
        data = (PlayerStatsFragmentData)viewData;
    }

    @Override
    public PlayerStatsFragmentData getData() {
        return data;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player_stats, container, false);
        pictureImage = v.findViewById(R.id.pictureImage);
        nameText = v.findViewById(R.id.nameText);
        positionText = v.findViewById(R.id.positionText);
        numberText = v.findViewById(R.id.numberText);
        shootsText = v.findViewById(R.id.shootsText);
        editIcon = v.findViewById(R.id.editIcon);
        shootmapImage = v.findViewById(R.id.shootmapImage);
        shootmapPointsContainer = v.findViewById(R.id.shootmapPointsContainer);
        gameSpinner = v.findViewById(R.id.gameSpinner);
        gameModeSpinner = v.findViewById(R.id.gameModeSpinner);
        typeSpinner = v.findViewById(R.id.typeSpinner);
        seasonSpinner = v.findViewById(R.id.seasonSpinner);

        // Player Info
        final Player player = data.getPlayer();
        if(player.getPicture() != null) {
            pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
        } else {
            pictureImage.setImageResource(R.drawable.default_picture);
        }

        nameText.setText(player.getName());
        positionText.setText(Player.getPositionText(player.getPosition(), false));
        String numberString = player.getNumber() != null ? String.valueOf(player.getNumber()) : "";
        numberText.setText(numberString);
        Player.Shoots shoots = Player.Shoots.fromDatabaseName(player.getShoots());
        String shootsString = getString(shoots == Player.Shoots.LEFT ? R.string.add_player_shoots_left : R.string.add_player_shoots_right);
        shootsText.setText(shootsString);

        sortedGames = new ArrayList<>(data.getGames().values());
        Collections.sort(sortedGames, new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                return Long.valueOf(o2.getDate()).compareTo(o1.getDate());
            }
        });

        ArrayList<String> gameTitles = new ArrayList<>();
        gameTitles.add(getString(R.string.player_stats_all_games));
        for(Game game : sortedGames) {
            String title = StringUtils.getDateText(game.getDate()) + ": " + game.getOpponentName();
            gameTitles.add(title);
        }
        Helper.setSpinnerAdapter(gameSpinner, gameTitles);

        Map<Goal.Mode, String> gameModeMap = new HashMap<>();
        gameModeMap.put(null, getString(R.string.player_stats_all_game_modes));
        gameModeMap.put(Goal.Mode.FULL, getString(R.string.add_event_full));
        gameModeMap.put(Goal.Mode.YV, getString(R.string.add_event_yv));
        gameModeMap.put(Goal.Mode.AV, getString(R.string.add_event_av));
        gameModeMap.put(Goal.Mode.RL, getString(R.string.add_event_rl));
        ArrayList<String> gameModeTitles = new ArrayList<>(gameModeMap.values());
        gameModes = new ArrayList<>(gameModeMap.keySet());
        Helper.setSpinnerAdapter(gameModeSpinner, gameModeTitles);

        ArrayList<String> typeTitles = new ArrayList<>();
        typeTitles.add(getString(R.string.player_stats_filter_goals));
        typeTitles.add(getString(R.string.player_stats_filter_assists));
        Helper.setSpinnerAdapter(typeSpinner, typeTitles);

        seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        seasonTitles.add(getString(R.string.player_stats_all_seasons));
        for(Season season : AppRes.getInstance().getSeasons().values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        gameModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameModeSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seasonSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        ViewTreeObserver vto = shootmapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                shootmapImage.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = shootmapImage.getMeasuredHeight();
                imageWidth = shootmapImage.getMeasuredWidth();

                // This triggers onItemSelectedListener
                Helper.setSpinnerSelection(gameSpinner, gameSpinnerPosition);
                Helper.setSpinnerSelection(gameModeSpinner, gameModeSpinnerPosition);
                Helper.setSpinnerSelection(typeSpinner, typeSpinnerPosition);
                Helper.setSpinnerSelection(seasonSpinner, seasonSpinnerPosition);
                return true;
            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(player);
            }
        });

        return v;
    }

    private void drawShootPoints() {
        shootmapPointsContainer.removeAllViewsInLayout();

        ArrayList<Goal> filteredGoals = getFilteredGameGoals(gameSpinnerPosition);
        filteredGoals = getFilteredGameModeGoals(gameModeSpinnerPosition, filteredGoals);
        filteredGoals = getFilteredTypeGoals(typeSpinnerPosition, filteredGoals);
        filteredGoals = getFilteredSeasonGoals(seasonSpinnerPosition, filteredGoals);

        for(Goal goal : filteredGoals) {
            double x = getPositionX(goal.getPositionPercentX());
            double y = getPositionY(goal.getPositionPercentY());

            drawShootPoint(x, y);
        }
    }

    private ArrayList<Goal> getFilteredGameGoals(int spinnerPosition) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        if(spinnerPosition == 0) {
            // Show all
            for(ArrayList<Goal> goals : data.getStats().values()) {
                filteredGoals.addAll(goals);
            }
        } else {
            // Show by game
            Game game = sortedGames.get(spinnerPosition - 1); // -1 because first is all
            ArrayList<Goal> goals = data.getStats().get(game.getGameId());
            if(goals != null) {
                filteredGoals.addAll(goals);
            }
        }
        return filteredGoals;
    }

    private ArrayList<Goal> getFilteredSeasonGoals(int spinnerPosition, ArrayList<Goal> goals) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        if(spinnerPosition == 0) {
            filteredGoals = goals;
        } else {
            String compareSeasonId = seasonIds.get(spinnerPosition - 1); // -1 because first is all
            for(Goal goal : goals) {
                Game game = data.getGames().get(goal.getGameId());
                if(game != null && game.getSeasonId().equals(compareSeasonId)) {
                    filteredGoals.add(goal);
                }
            }
        }
        return filteredGoals;
    }

    private ArrayList<Goal> getFilteredTypeGoals(int spinnerPosition, ArrayList<Goal> goals) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        String playerId = data.getPlayer().getPlayerId();
        for(Goal goal : goals) {
            // Goals
            if(spinnerPosition == 0 && goal.getScorerId().equals(playerId)) {
                filteredGoals.add(goal);
            } else if (spinnerPosition == 1 && goal.getAssistantId() != null && goal.getAssistantId().equals(playerId)){
                filteredGoals.add(goal);
            }
        }
        return filteredGoals;
    }

    private ArrayList<Goal> getFilteredGameModeGoals(int spinnerPosition, ArrayList<Goal> goals) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        Goal.Mode compareMode = gameModes.get(spinnerPosition);
        if(compareMode == null) {
            filteredGoals = goals;
        } else {
            for(Goal goal : goals) {
                Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
                if(mode == compareMode) {
                    filteredGoals.add(goal);
                }
            }
        }
        return filteredGoals;
    }

    public void drawShootPoint(double positionX, double positionY) {
        ImageView shootPoint = new ImageView(AppRes.getActivity());
        shootPoint.setScaleType(ImageView.ScaleType.FIT_XY);
        shootPoint.setAdjustViewBounds(true);

        int strokeWidth = 5;
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(Color.WHITE);
        gD.setShape(GradientDrawable.OVAL);
        gD.setStroke(strokeWidth, Color.BLACK);
        shootPoint.setBackground(gD);

        int shootPointWidth = 40;
        int shootPointHeight = 40;
        double pictureX = positionX - (shootPointWidth / 2.0);
        double pictureY = positionY - (shootPointHeight / 2.0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(shootPointWidth, shootPointHeight);
        params.leftMargin = (int)pictureX;
        params.topMargin = (int)pictureY;
        shootPoint.setLayoutParams(params);

        shootmapPointsContainer.addView(shootPoint);
    }

    private double getPositionX(double positionPercentX) {
        return imageWidth * positionPercentX;
    }

    private double getPositionY(double positionPercentY) {
        return imageHeight * 2 * positionPercentY; // multiple 2 because shootmap is half of full length
    }

}
