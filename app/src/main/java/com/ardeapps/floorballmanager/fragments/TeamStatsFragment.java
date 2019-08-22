package com.ardeapps.floorballmanager.fragments;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.handlers.GetGoalsHandler;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GamesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.services.StatsHelper;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.PlayerPointsData;
import com.ardeapps.floorballmanager.viewObjects.TeamGameData;
import com.ardeapps.floorballmanager.viewObjects.TeamStatsData;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TeamStatsFragment extends Fragment {

    ImageView logoImage;
    IconView editIcon;
    TextView nameText;
    TextView noSeasonsText;
    TextView gamesText;
    TextView winsText;
    TextView winPercentText;
    TextView drawsText;
    TextView drawPercentText;
    TextView losesText;
    TextView losePercentText;
    TextView plusGoalsText;
    TextView plusGoalsPerGameText;
    TextView plusGoalsYvText;
    TextView plusGoalsAvText;
    TextView plusGoalsRlText;
    TextView plusGoalsPeriod1Text;
    TextView plusGoalsPeriod2Text;
    TextView plusGoalsPeriod3Text;
    TextView plusGoalsJAText;
    TextView minusGoalsText;
    TextView minusGoalsPerGameText;
    TextView minusGoalsYvText;
    TextView minusGoalsAvText;
    TextView minusGoalsRlText;
    TextView minusGoalsPeriod1Text;
    TextView minusGoalsPeriod2Text;
    TextView minusGoalsPeriod3Text;
    TextView minusGoalsJAText;
    TextView biggestWinText;
    TextView biggestLoseText;
    TextView longestWinText;
    TextView longestNotLoseText;

    ImageView shootmapImage;
    RelativeLayout shootmapPointsContainer;
    Spinner gameSpinner;
    Spinner gameModeSpinner;
    Spinner seasonSpinner;
    Spinner goalTypeSpinner;
    LinearLayout trendingContainer;
    LinearLayout trendingContent;
    int gameSpinnerPosition = 0;
    int gameModeSpinnerPosition = 0;
    int goalTypeSpinnerPosition = 0;
    private double imageWidth;
    private double imageHeight;
    private ArrayList<Game> sortedGames;
    private ArrayList<Goal.Mode> gameModes;
    private Team team;
    private Map<String, ArrayList<Goal>> stats = new HashMap<>();
    private Map<String, Game> games = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_stats, container, false);

        logoImage = v.findViewById(R.id.logoImage);
        nameText = v.findViewById(R.id.nameText);
        trendingContainer = v.findViewById(R.id.trendingContainer);
        trendingContent = v.findViewById(R.id.trendingContent);
        editIcon = v.findViewById(R.id.editIcon);
        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        noSeasonsText = v.findViewById(R.id.noSeasonsText);
        shootmapImage = v.findViewById(R.id.shootmapImage);
        shootmapPointsContainer = v.findViewById(R.id.shootmapPointsContainer);
        goalTypeSpinner = v.findViewById(R.id.goalTypeSpinner);
        gameSpinner = v.findViewById(R.id.gameSpinner);
        gameModeSpinner = v.findViewById(R.id.gameModeSpinner);
        gamesText = v.findViewById(R.id.gamesText);
        winsText = v.findViewById(R.id.winsText);
        winPercentText = v.findViewById(R.id.winPercentText);
        drawsText = v.findViewById(R.id.drawsText);
        drawPercentText = v.findViewById(R.id.drawPercentText);
        losesText = v.findViewById(R.id.losesText);
        losePercentText = v.findViewById(R.id.losePercentText);
        plusGoalsText = v.findViewById(R.id.plusGoalsText);
        plusGoalsPerGameText = v.findViewById(R.id.plusGoalsPerGameText);
        plusGoalsYvText = v.findViewById(R.id.plusGoalsYvText);
        plusGoalsAvText = v.findViewById(R.id.plusGoalsAvText);
        plusGoalsRlText = v.findViewById(R.id.plusGoalsRlText);
        plusGoalsPeriod1Text = v.findViewById(R.id.plusGoalsPeriod1Text);
        plusGoalsPeriod2Text = v.findViewById(R.id.plusGoalsPeriod2Text);
        plusGoalsPeriod3Text = v.findViewById(R.id.plusGoalsPeriod3Text);
        plusGoalsJAText = v.findViewById(R.id.plusGoalsJAText);
        minusGoalsText = v.findViewById(R.id.minusGoalsText);
        minusGoalsPerGameText = v.findViewById(R.id.minusGoalsPerGameText);
        minusGoalsYvText = v.findViewById(R.id.minusGoalsYvText);
        minusGoalsAvText = v.findViewById(R.id.minusGoalsAvText);
        minusGoalsRlText = v.findViewById(R.id.minusGoalsRlText);
        minusGoalsPeriod1Text = v.findViewById(R.id.minusGoalsPeriod1Text);
        minusGoalsPeriod2Text = v.findViewById(R.id.minusGoalsPeriod2Text);
        minusGoalsPeriod3Text = v.findViewById(R.id.minusGoalsPeriod3Text);
        minusGoalsJAText = v.findViewById(R.id.minusGoalsJAText);
        biggestWinText = v.findViewById(R.id.biggestWinText);
        biggestLoseText = v.findViewById(R.id.biggestLoseText);
        longestWinText = v.findViewById(R.id.longestWinText);
        longestNotLoseText = v.findViewById(R.id.longestNotLoseText);

        team = AppRes.getInstance().getSelectedTeam();
        // Team Info
        if (team.getLogo() != null) {
            logoImage.setImageDrawable(ImageUtil.getRoundedDrawable(team.getLogo()));
        } else {
            logoImage.setImageResource(R.drawable.default_logo);
        }
        nameText.setText(team.getName());

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.PLAYER) {
            editIcon.setVisibility(View.GONE);
        } else {
            editIcon.setVisibility(View.VISIBLE);
        }

        setSeasonSpinner();

        Map<Goal.Mode, String> gameModeMap = new HashMap<>();
        gameModeMap.put(null, getString(R.string.player_stats_all_game_modes));
        gameModeMap.put(Goal.Mode.FULL, getString(R.string.add_event_full));
        gameModeMap.put(Goal.Mode.YV, getString(R.string.add_event_yv));
        gameModeMap.put(Goal.Mode.AV, getString(R.string.add_event_av));
        gameModeMap.put(Goal.Mode.RL, getString(R.string.add_event_rl));
        ArrayList<String> gameModeTitles = new ArrayList<>(gameModeMap.values());
        gameModes = new ArrayList<>(gameModeMap.keySet());
        Helper.setSpinnerAdapter(gameModeSpinner, gameModeTitles);

        ArrayList<String> goalTypeTitles = new ArrayList<>();
        goalTypeTitles.add(getString(R.string.team_stats_plus_goals));
        goalTypeTitles.add(getString(R.string.team_stats_minus_goals));
        Helper.setSpinnerAdapter(goalTypeSpinner, goalTypeTitles);

        goalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goalTypeSpinnerPosition = position;
                drawShootPoints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

        ViewTreeObserver vto = shootmapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                shootmapImage.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = shootmapImage.getMeasuredHeight();
                imageWidth = shootmapImage.getMeasuredWidth();

                // This triggers onItemSelectedListener
                Helper.setSpinnerSelection(gameSpinner, gameSpinnerPosition);
                Helper.setSpinnerSelection(gameModeSpinner, gameModeSpinnerPosition);

                Season season = AppRes.getInstance().getSelectedSeason();
                loadStats(season != null ? season.getSeasonId() : null);
                return true;
            }
        });

        editIcon.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(team));
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

    private void loadStats(final String seasonId) {
        if (seasonId == null) {
            GoalsResource.getInstance().getAllGoals(goals -> GamesResource.getInstance().getAllGames(games -> {
                TeamStatsFragment.this.stats = goals;
                TeamStatsFragment.this.games = games;
                updateStats(null);
            }));
        } else {
            GoalsResource.getInstance().getGoals(seasonId, (GetGoalsHandler) goals -> GamesResource.getInstance().getGames(seasonId, games -> {
                TeamStatsFragment.this.stats = goals;
                TeamStatsFragment.this.games = games;
                updateStats(seasonId);
            }));
        }
    }

    private void updateStats(String seasonId) {
        drawShootPoints();

        // Set games sorted by date
        sortedGames = new ArrayList<>(games.values());
        Collections.sort(sortedGames, (o1, o2) -> Long.valueOf(o2.getDate()).compareTo(o1.getDate()));

        ArrayList<String> gameTitles = new ArrayList<>();
        gameTitles.add(getString(R.string.player_stats_all_games));
        for (Game game : sortedGames) {
            String title = StringUtils.getDateText(game.getDate()) + ": " + game.getOpponentName();
            gameTitles.add(title);
        }
        Helper.setSpinnerAdapter(gameSpinner, gameTitles);

        ArrayList<Goal> goalsInThreeLastGames = new ArrayList<>();

        // Collect filtered map
        Map<Game, ArrayList<Goal>> filteredStats = new HashMap<>();
        for (Game game : sortedGames) {
            ArrayList<Goal> goals = stats.get(game.getGameId());
            ArrayList<Goal> filteredGoals = new ArrayList<>();
            if (goals != null) {
                for (Goal goal : goals) {
                    if (seasonId == null || seasonId.equals(goal.getSeasonId())) {
                        filteredGoals.add(goal);
                    }
                }
            }

            if (filteredStats.size() < 3) {
                goalsInThreeLastGames.addAll(filteredGoals);
            }
            filteredStats.put(game, filteredGoals);
        }

        TeamStatsData stats = StatsHelper.getTeamStats(filteredStats);
        gamesText.setText(String.valueOf(stats.gamesCount));
        winsText.setText(String.valueOf(stats.wins));
        drawsText.setText(String.valueOf(stats.draws));
        losesText.setText(String.valueOf(stats.loses));
        winPercentText.setText("(" + stats.winPercent + "%)");
        drawPercentText.setText("(" + stats.drawPercent + "%)");
        losePercentText.setText("(" + stats.losePercent + "%)");
        plusGoalsText.setText(String.valueOf(stats.plusGoals));
        plusGoalsYvText.setText(String.valueOf(stats.plusGoalsYv));
        plusGoalsAvText.setText(String.valueOf(stats.plusGoalsAv));
        plusGoalsRlText.setText(String.valueOf(stats.plusGoalsRl));
        plusGoalsPeriod1Text.setText(stats.plusGoalsPercentPeriod1 + "%");
        plusGoalsPeriod2Text.setText(stats.plusGoalsPercentPeriod2 + "%");
        plusGoalsPeriod3Text.setText(stats.plusGoalsPercentPeriod3 + "%");
        plusGoalsJAText.setText(stats.plusGoalsPercentPeriodJA + "%");
        minusGoalsText.setText(String.valueOf(stats.minusGoals));
        minusGoalsYvText.setText(String.valueOf(stats.minusGoalsYv));
        minusGoalsAvText.setText(String.valueOf(stats.minusGoalsAv));
        minusGoalsRlText.setText(String.valueOf(stats.minusGoalsRl));
        minusGoalsPeriod1Text.setText(stats.minusGoalsPercentPeriod1 + "%");
        minusGoalsPeriod2Text.setText(stats.minusGoalsPercentPeriod2 + "%");
        minusGoalsPeriod3Text.setText(stats.minusGoalsPercentPeriod3 + "%");
        minusGoalsJAText.setText(stats.minusGoalsPercentPeriodJA + "%");
        plusGoalsPerGameText.setText(getPerGameText(stats.plusGoalsPerGame));
        minusGoalsPerGameText.setText(getPerGameText(stats.minusGoalsPerGame));
        biggestWinText.setText(getLongestWinOrLoseText(stats.biggestWin));
        biggestLoseText.setText(getLongestWinOrLoseText(stats.biggestLose));
        longestWinText.setText(getLongestStatsText(stats.longestWin));
        longestNotLoseText.setText(getLongestStatsText(stats.longestNotLose));

        ArrayList<PlayerPointsData> playerPoints = StatsHelper.getSortedTrendingPlayers(goalsInThreeLastGames);
        final PlayerStatsHolder holder = new PlayerStatsHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        trendingContainer.removeAllViews();

        if (!playerPoints.isEmpty()) {
            trendingContent.setVisibility(View.VISIBLE);
            int addedPlayers = 0;
            for (final PlayerPointsData playerPoint : playerPoints) {
                if (addedPlayers < 3) {
                    View cv = inf.inflate(R.layout.container_card_player_stats, trendingContainer, false);
                    holder.pictureImage = cv.findViewById(R.id.pictureImage);
                    holder.nameText = cv.findViewById(R.id.nameText);
                    holder.statsText = cv.findViewById(R.id.statsText);

                    Player player = AppRes.getInstance().getPlayers().get(playerPoint.playerId);
                    if (player != null) {
                        if (player.getPicture() != null) {
                            holder.pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
                        } else {
                            holder.pictureImage.setImageResource(R.drawable.default_picture);
                        }
                        holder.nameText.setText(player.getName());
                        holder.statsText.setText(playerPoint.goals + " + " + playerPoint.assists);

                        trendingContainer.addView(cv);
                    }
                    addedPlayers++;
                }
            }
        } else {
            trendingContent.setVisibility(View.GONE);
        }
    }

    private String getLongestWinOrLoseText(TeamGameData data) {
        if (data != null) {
            return data.homeGoals + " - " + data.awayGoals + ", " + data.opponentName;
        } else {
            return "-";
        }
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

    private String getPerGameText(double value) {
        return String.format(Locale.ENGLISH, "%.02f", value);
    }

    private void drawShootPoints() {
        shootmapPointsContainer.removeAllViewsInLayout();

        ArrayList<Goal> filteredGoals = getFilteredGameGoals(gameSpinnerPosition);
        filteredGoals = getFilteredGameModeGoals(gameModeSpinnerPosition, filteredGoals);
        filteredGoals = getFilteredGoalTypeGoals(goalTypeSpinnerPosition, filteredGoals);

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

    private ArrayList<Goal> getFilteredGoalTypeGoals(int spinnerPosition, ArrayList<Goal> goals) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        for (Goal goal : goals) {
            if (spinnerPosition == 0) {
                if (!goal.isOpponentGoal()) {
                    filteredGoals.add(goal);
                }
            } else {
                if (goal.isOpponentGoal()) {
                    filteredGoals.add(goal);
                }
            }
        }
        return filteredGoals;
    }

    private ArrayList<Goal> getFilteredGameGoals(int spinnerPosition) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();
        if (spinnerPosition == 0) {
            // Show all
            for (ArrayList<Goal> goals : stats.values()) {
                filteredGoals.addAll(goals);
            }
        } else {
            // Show by game
            Game game = sortedGames.get(spinnerPosition - 1); // -1 because first is all
            ArrayList<Goal> goals = stats.get(game.getGameId());
            if (goals != null) {
                filteredGoals.addAll(goals);
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

    private class PlayerStatsHolder {
        ImageView pictureImage;
        TextView nameText;
        TextView statsText;
    }
}
