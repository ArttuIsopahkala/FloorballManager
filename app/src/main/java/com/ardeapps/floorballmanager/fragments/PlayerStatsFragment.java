package com.ardeapps.floorballmanager.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ActionMenuDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.handlers.GetGoalsHandler;
import com.ardeapps.floorballmanager.handlers.GetPenaltiesHandler;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.PenaltiesResource;
import com.ardeapps.floorballmanager.resources.PlayerGamesResource;
import com.ardeapps.floorballmanager.resources.PlayersResource;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.services.StatsHelper;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.PlayerStatsData;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class PlayerStatsFragment extends Fragment implements DataView {

    ImageView pictureImage;
    TextView nameText;
    TextView positionText;
    TextView numberText;
    TextView shootsText;
    IconView editIcon;
    TextView gamesText;
    TextView pointsText;
    TextView pointsPerGameText;
    TextView plusText;
    TextView minusText;
    TextView plusMinusText;
    TextView goalsText;
    TextView goalsPerGameText;
    TextView goalsYvText;
    TextView goalsAvText;
    TextView goalsRlText;
    TextView assistsText;
    TextView assistsPerGameText;
    TextView assistsYvText;
    TextView assistsAvText;
    TextView bestStatsText;
    TextView longestStatsText;
    TextView bestPlusMinusText;
    TextView bestAssistantText;
    TextView bestScorerText;
    TextView bestSameLineText;
    TextView penaltiesText;
    TextView penaltiesPerGameText;

    ImageView shootmapImage;
    RelativeLayout shootmapPointsContainer;
    Spinner gameSpinner;
    Spinner gameModeSpinner;
    Spinner typeSpinner;
    Spinner seasonSpinner;
    TextView noSeasonsText;
    LinearLayout strengthsContainer;
    TextView strengthsText;
    int gameSpinnerPosition = 0;
    int gameModeSpinnerPosition = 0;
    int typeSpinnerPosition = 0;
    private double imageWidth;
    private double imageHeight;
    private ArrayList<Game> sortedGames;
    private ArrayList<Goal.Mode> gameModes;
    private Player player;
    private Map<String, ArrayList<Penalty>> penalties = new HashMap<>();
    private Map<String, ArrayList<Goal>> goals = new HashMap<>();
    private Map<String, Game> games = new HashMap<>();

    @Override
    public Player getData() {
        return player;
    }

    @Override
    public void setData(Object viewData) {
        player = (Player) viewData;
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
        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        noSeasonsText = v.findViewById(R.id.noSeasonsText);
        shootmapImage = v.findViewById(R.id.shootmapImage);
        shootmapPointsContainer = v.findViewById(R.id.shootmapPointsContainer);
        gameSpinner = v.findViewById(R.id.gameSpinner);
        gameModeSpinner = v.findViewById(R.id.gameModeSpinner);
        typeSpinner = v.findViewById(R.id.typeSpinner);
        gamesText = v.findViewById(R.id.gamesText);
        pointsText = v.findViewById(R.id.pointsText);
        pointsPerGameText = v.findViewById(R.id.pointsPerGameText);
        plusText = v.findViewById(R.id.plusText);
        minusText = v.findViewById(R.id.minusText);
        plusMinusText = v.findViewById(R.id.plusMinusText);
        goalsText = v.findViewById(R.id.goalsText);
        goalsPerGameText = v.findViewById(R.id.goalsPerGameText);
        goalsYvText = v.findViewById(R.id.goalsYvText);
        goalsAvText = v.findViewById(R.id.goalsAvText);
        goalsRlText = v.findViewById(R.id.goalsRlText);
        assistsText = v.findViewById(R.id.assistsText);
        assistsPerGameText = v.findViewById(R.id.assistsPerGameText);
        assistsYvText = v.findViewById(R.id.assistsYvText);
        assistsAvText = v.findViewById(R.id.assistsAvText);
        bestStatsText = v.findViewById(R.id.bestStatsText);
        longestStatsText = v.findViewById(R.id.longestStatsText);
        bestPlusMinusText = v.findViewById(R.id.bestPlusMinusText);
        bestAssistantText = v.findViewById(R.id.bestAssistantText);
        bestScorerText = v.findViewById(R.id.bestScorerText);
        bestSameLineText = v.findViewById(R.id.bestSameLineText);
        strengthsContainer = v.findViewById(R.id.strengthsContainer);
        strengthsText = v.findViewById(R.id.strengthsText);
        penaltiesText = v.findViewById(R.id.penaltiesText);
        penaltiesPerGameText = v.findViewById(R.id.penaltiesPerGameText);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.ADMIN) {
            editIcon.setVisibility(View.VISIBLE);
            strengthsContainer.setVisibility(View.VISIBLE);
        } else {
            strengthsContainer.setVisibility(View.GONE);
            if (role == UserConnection.Role.PLAYER) {
                // Allow player edit own info
                String selectedPlayerId = AppRes.getInstance().getSelectedPlayerId();
                if (player.getPlayerId().equals(selectedPlayerId)) {
                    editIcon.setVisibility(View.VISIBLE);
                } else {
                    editIcon.setVisibility(View.GONE);
                }
            } else {
                editIcon.setVisibility(View.GONE);
            }
        }

        setSeasonSpinner();
        // Player Info
        if (player.getPicture() != null) {
            pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
        } else {
            pictureImage.setImageResource(R.drawable.default_picture);
        }

        nameText.setText(player.getName());
        positionText.setText(Player.getPositionText(player.getPosition(), false));
        String numberString = player.getNumber() != null ? String.valueOf(player.getNumber()) : "";
        numberText.setText(numberString);
        String shootsString = "-";
        if (player.getShoots() != null) {
            Player.Shoots shoots = Player.Shoots.fromDatabaseName(player.getShoots());
            shootsString = getString(shoots == Player.Shoots.LEFT ? R.string.add_player_shoots_left : R.string.add_player_shoots_right);
        }
        shootsText.setText(shootsString);

        String result = "";
        List<String> strengths = player.getStrengths();
        if (strengths != null && !strengths.isEmpty()) {
            Map<Player.Skill, String> strengthTextsMap = Player.getStrengthTextsMap();
            int addedCount = 0;
            for (String strength : strengths) {
                Player.Skill skill = Player.Skill.fromDatabaseName(strength);
                if (addedCount > 0) {
                    result += ", ";
                }
                result += strengthTextsMap.get(skill);
                addedCount++;
            }
        } else {
            result = "-";
        }
        strengthsText.setText(result);

        Map<Goal.Mode, String> gameModeMap = new TreeMap<>();
        gameModeMap.put(Goal.Mode.FULL, getString(R.string.add_event_full));
        gameModeMap.put(Goal.Mode.AV, getString(R.string.add_event_av));
        gameModeMap.put(Goal.Mode.YV, getString(R.string.add_event_yv));
        gameModeMap.put(Goal.Mode.SR, getString(R.string.add_event_sr));
        gameModeMap.put(Goal.Mode.IM, getString(R.string.add_event_im));
        gameModeMap.put(Goal.Mode.TM, getString(R.string.add_event_tm));
        gameModeMap.put(Goal.Mode.OM, getString(R.string.add_event_om));
        gameModeMap.put(Goal.Mode.RL, getString(R.string.add_event_rl));
        ArrayList<String> gameModeTitles = new ArrayList<>();
        gameModeTitles.add(getString(R.string.player_stats_all_game_modes));
        gameModeTitles.addAll(gameModeMap.values());
        gameModes = new ArrayList<>();
        gameModes.add(null);
        gameModes.addAll(gameModeMap.keySet());
        Helper.setSpinnerAdapter(gameModeSpinner, gameModeTitles);

        ArrayList<String> typeTitles = new ArrayList<>();
        typeTitles.add(getString(R.string.player_stats_filter_goals));
        typeTitles.add(getString(R.string.player_stats_filter_assists));
        Helper.setSpinnerAdapter(typeSpinner, typeTitles);

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        gameModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameModeSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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

                Season season = AppRes.getInstance().getSelectedSeason();
                loadStats(season != null ? season.getSeasonId() : null);
                return true;
            }
        });

        editIcon.setOnClickListener(v1 -> {
            final ActionMenuDialogFragment dialog = ActionMenuDialogFragment.newInstance(null, getString(R.string.remove_player));
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
            dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
                @Override
                public void onEditItem() {
                    dialog.dismiss();
                    FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(player);
                }

                @Override
                public void onRemoveItem() {
                    dialog.dismiss();
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_player_remove_confirmation));
                    dialogFragment.show(getChildFragmentManager(), "Siirretäänkö pelaaja poistettujen listalle?");
                    dialogFragment.setListener(() -> {
                        player.setActive(false);
                        PlayersResource.getInstance().editPlayer(player, () -> {
                            AppRes.getInstance().setPlayer(player.getPlayerId(), player);
                            AppRes.getActivity().onBackPressed();
                        });
                    });
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
        });

        return v;
    }

    public void setSeasonSpinner() {
        Map<String, Season> seasons = AppRes.getInstance().getSeasons();

        if (seasons.isEmpty()) {
            noSeasonsText.setVisibility(View.VISIBLE);
            seasonSpinner.setVisibility(View.GONE);
        } else {
            noSeasonsText.setVisibility(View.GONE);
            seasonSpinner.setVisibility(View.VISIBLE);
        }
        final ArrayList<String> seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        seasonTitles.add(getString(R.string.player_stats_all_seasons));
        seasonIds.add(null);
        for (Season season : seasons.values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);
        int seasonPosition = 0;
        if (!seasons.isEmpty()) {
            Season selectedSeason = AppRes.getInstance().getSelectedSeason();
            if (selectedSeason != null) {
                seasonPosition = seasonIds.indexOf(selectedSeason.getSeasonId());
            }
            Helper.setSpinnerSelection(seasonSpinner, seasonPosition > -1 ? seasonPosition : 0);
        }

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seasonId = seasonIds.get(position);
                loadStats(seasonId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadStats(String seasonId) {
        final String playerId = player.getPlayerId();
        if (seasonId == null) {
            if(!AppRes.getInstance().getAllGames().isEmpty()) {
                PlayerStatsFragment.this.games = AppRes.getInstance().getAllGames();
                loadGoals(null);
            } else {
                PlayerGamesResource.getInstance().getAllGames(playerId, games -> {
                    AppRes.getInstance().setAllGames(games);
                    PlayerStatsFragment.this.games = games;
                    loadGoals(null);
                });
            }
        } else {
            PlayerGamesResource.getInstance().getGames(playerId, seasonId, games -> {
                PlayerStatsFragment.this.games = games;
                loadGoals(seasonId);
            });
        }
    }

    private void loadGoals(String seasonId) {
        if (seasonId == null) {
            if (!AppRes.getInstance().getGoalsByGame().isEmpty()) {
                PlayerStatsFragment.this.goals = AppRes.getInstance().getGoalsByGame();
                loadPenalties(null);
            } else {
                GoalsResource.getInstance().getAllGoals(goals -> {
                    AppRes.getInstance().setGoalsByGame(goals);
                    PlayerStatsFragment.this.goals = goals;
                    loadPenalties(null);
                });
            }
        } else {
            GoalsResource.getInstance().getGoals(seasonId, (GetGoalsHandler) goals -> {
                PlayerStatsFragment.this.goals = goals;
                loadPenalties(seasonId);
            });
        }
    }

    private void loadPenalties(String seasonId) {
        if (seasonId == null) {
            if (!AppRes.getInstance().getPenaltiesByGame().isEmpty()) {
                PlayerStatsFragment.this.penalties = AppRes.getInstance().getPenaltiesByGame();
                updateStats(null);
            } else {
                PenaltiesResource.getInstance().getAllPenalties(penalties -> {
                    AppRes.getInstance().setPenaltiesByGame(penalties);
                    PlayerStatsFragment.this.penalties = penalties;
                    updateStats(null);
                });
            }
        } else {
            PenaltiesResource.getInstance().getPenalties(seasonId, (GetPenaltiesHandler) penalties -> {
                PlayerStatsFragment.this.penalties = penalties;
                updateStats(seasonId);
            });
        }
    }

    private void updateStats(String seasonId) {
        drawShootPoints();

        // Set games sorted by date
        sortedGames = new ArrayList<>(games.values());
        Collections.sort(sortedGames, (o1, o2) -> Long.compare(o2.getDate(), o1.getDate()));

        ArrayList<String> gameTitles = new ArrayList<>();
        gameTitles.add(getString(R.string.player_stats_all_games));
        for (Game game : sortedGames) {
            String title = StringUtils.getDateText(game.getDate(), false) + ": " + game.getOpponentName();
            gameTitles.add(title);
        }
        Helper.setSpinnerAdapter(gameSpinner, gameTitles);

        // Collect filtered map
        Map<Game, ArrayList<Goal>> filteredStats = new HashMap<>();
        for (Game game : sortedGames) {
            ArrayList<Goal> goals = this.goals.get(game.getGameId());
            ArrayList<Goal> filteredGoals = new ArrayList<>();
            if (goals != null) {
                for (Goal goal : goals) {
                    if (seasonId == null || seasonId.equals(goal.getSeasonId())) {
                        filteredGoals.add(goal);
                    }
                }
            }
            filteredStats.put(game, filteredGoals);
        }

        PlayerStatsData stats = StatsHelper.getPlayerStats(player.getPlayerId(), filteredStats, penalties);
        gamesText.setText(String.valueOf(stats.gamesCount));
        pointsText.setText(String.valueOf(stats.points));
        pointsPerGameText.setText(getPerGameText(stats.pointsPerGame));
        plusText.setText(String.valueOf(stats.pluses));
        minusText.setText(String.valueOf(stats.minuses));
        plusMinusText.setText(String.valueOf(stats.plusMinus));
        goalsText.setText(String.valueOf(stats.scores));
        goalsPerGameText.setText(getPerGameText(stats.scoresPerGame));
        goalsYvText.setText(String.valueOf(stats.yvScores));
        goalsAvText.setText(String.valueOf(stats.avScores));
        goalsRlText.setText(String.valueOf(stats.rlScores));
        assistsText.setText(String.valueOf(stats.assists));
        assistsPerGameText.setText(getPerGameText(stats.assistsPerGame));
        assistsYvText.setText(String.valueOf(stats.yvAssists));
        assistsAvText.setText(String.valueOf(stats.avAssists));
        bestStatsText.setText(stats.bestStats.first + " + " + stats.bestStats.second);
        longestStatsText.setText(getLongestStatsText(stats.longestStats));
        bestPlusMinusText.setText(String.valueOf(stats.bestPlusMinus));
        bestAssistantText.setText(getBestAssistText(stats.bestAssists));
        bestScorerText.setText(getBestScorerText(stats.bestScorers));
        bestSameLineText.setText(getLineMateText(stats.bestLineMates));
        penaltiesText.setText(stats.penalties + " min");
        penaltiesPerGameText.setText(getPerGameText(stats.penaltiesPerGame));
    }

    private String getPerGameText(double value) {
        return String.format(Locale.ENGLISH, "%.02f", value);
    }

    private String getLongestStatsText(int statsLength) {
        String result = statsLength + " ";
        if (statsLength == 1) {
            result += getString(R.string.player_stats_game);
        } else {
            result += getString(R.string.player_stats_games);
        }
        return result;
    }

    private String getLineMateText(Pair<ArrayList<String>, Integer> value) {
        String result = "-";
        if (value.second != null && value.second > 0) {
            result = getPlayersText(value.first) + ": " + value.second + " ";
            if (value.second == 1) {
                result += getString(R.string.player_stats_together_score);
            } else {
                result += getString(R.string.player_stats_together_scores);
            }
        }
        return result;
    }

    private String getBestScorerText(Pair<ArrayList<String>, Integer> value) {
        String result = "-";
        if (value.second != null && value.second > 0) {
            result = getPlayersText(value.first) + ", " + value.second + " ";
            if (value.second == 1) {
                result += getString(R.string.player_stats_score);
            } else {
                result += getString(R.string.player_stats_scores);
            }
        }
        return result;
    }

    private String getBestAssistText(Pair<ArrayList<String>, Integer> value) {
        String result = "-";
        if (value.second != null && value.second > 0) {
            result = getPlayersText(value.first) + ", " + value.second + " ";
            if (value.second == 1) {
                result += getString(R.string.player_stats_assist);
            } else {
                result += getString(R.string.player_stats_assists);
            }
        }
        return result;
    }

    private String getPlayersText(ArrayList<String> playerIds) {
        String result = "";
        Map<String, Player> players = AppRes.getInstance().getPlayers();
        if (!playerIds.isEmpty()) {
            ArrayList<String> addedPlayerIds = new ArrayList<>();
            for (String playerId : playerIds) {
                Player player = players.get(playerId);
                if (player != null) {
                    if (addedPlayerIds.size() > 0) {
                        result += ", ";
                    }
                    result += player.getName();
                    addedPlayerIds.add(playerId);
                    if (addedPlayerIds.size() == 2) {
                        result += ".. ";
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void drawShootPoints() {
        shootmapPointsContainer.removeAllViewsInLayout();

        ArrayList<Goal> filteredGoals = getFilteredGameGoals(gameSpinnerPosition);
        filteredGoals = getFilteredGameModeGoals(gameModeSpinnerPosition, filteredGoals);
        filteredGoals = getFilteredTypeGoals(typeSpinnerPosition, filteredGoals);

        for (Goal goal : filteredGoals) {
            if (goal.getPositionPercentX() != null && goal.getPositionPercentY() != null) {
                double x = getPositionX(goal.getPositionPercentX());
                double y = getPositionY(goal.getPositionPercentY());
                if (y > imageHeight) {
                    y = imageHeight;
                }
                drawShootPoint(x, y);
            }
        }
    }

    private ArrayList<Goal> getFilteredGameGoals(int spinnerPosition) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        if (spinnerPosition == 0) {
            // Show all
            for (ArrayList<Goal> goals : goals.values()) {
                filteredGoals.addAll(goals);
            }
        } else {
            // Show by game
            Game game = sortedGames.get(spinnerPosition - 1); // -1 because first is all
            ArrayList<Goal> goals = this.goals.get(game.getGameId());
            if (goals != null) {
                filteredGoals.addAll(goals);
            }
        }
        return filteredGoals;
    }

    private ArrayList<Goal> getFilteredTypeGoals(int spinnerPosition, ArrayList<Goal> goals) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        String playerId = player.getPlayerId();
        for (Goal goal : goals) {
            // Goals
            if (spinnerPosition == 0 && goal.getScorerId() != null && goal.getScorerId().equals(playerId)) {
                filteredGoals.add(goal);
            } else if (spinnerPosition == 1 && goal.getAssistantId() != null && goal.getAssistantId().equals(playerId)) {
                filteredGoals.add(goal);
            }
        }
        return filteredGoals;
    }

    private ArrayList<Goal> getFilteredGameModeGoals(int spinnerPosition, ArrayList<Goal> goals) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        Goal.Mode compareMode = gameModes.get(spinnerPosition);
        if (compareMode == null) {
            filteredGoals = goals;
        } else {
            for (Goal goal : goals) {
                Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
                if (mode == compareMode) {
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

        int strokeWidth = Helper.dpToPx(3);
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(Color.WHITE);
        gD.setShape(GradientDrawable.OVAL);
        gD.setStroke(strokeWidth, Color.BLACK);
        shootPoint.setBackground(gD);

        int shootPointWidth = Helper.dpToPx(20);
        int shootPointHeight = Helper.dpToPx(20);
        double pictureX = positionX - (shootPointWidth / 2.0);
        double pictureY = positionY - (shootPointHeight / 2.0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(shootPointWidth, shootPointHeight);
        params.leftMargin = (int) pictureX;
        params.topMargin = (int) pictureY;
        shootPoint.setLayoutParams(params);

        shootmapPointsContainer.addView(shootPoint);
    }

    private double getPositionX(double positionPercentX) {
        return imageWidth * positionPercentX;
    }

    private double getPositionY(double positionPercentY) {
        return imageHeight * positionPercentY;
    }

}
