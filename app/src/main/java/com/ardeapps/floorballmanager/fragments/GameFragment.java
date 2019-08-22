package com.ardeapps.floorballmanager.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ActionMenuDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.goalDialog.GoalWizardDialogFragment;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GamesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.GoalsResourceWrapper;
import com.ardeapps.floorballmanager.resources.PlayerGamesResource;
import com.ardeapps.floorballmanager.resources.PlayerStatsResource;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GameFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GoalWizardDialogData;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class GameFragment extends Fragment implements DataView {

    TextView dateText;
    TextView seasonText;
    TextView periodDurationText;
    TextView homeNameText;
    TextView awayNameText;
    TextView resultText;
    IconView settingsIcon;
    LinearLayout periodLayout1;
    LinearLayout periodLayout2;
    LinearLayout periodLayout3;
    LinearLayout periodLayoutJA;
    LinearLayout lineStats1;
    LinearLayout lineStats2;
    LinearLayout lineStats3;
    LinearLayout lineStats4;
    Button homeGoalButton;
    Button awayGoalButton;
    int homeGoals = 0;
    int awayGoals = 0;
    private GameFragmentData data;

    @Override
    public GameFragmentData getData() {
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GameFragmentData) viewData;
    }

    public void update() {
        Season season = AppRes.getInstance().getSeasons().get(data.getGame().getSeasonId());
        if (season != null) {
            seasonText.setText(season.getName());
            periodDurationText.setText(data.getGame().getPeriodInMinutes() + "min");
        } else {
            seasonText.setText("-");
            periodDurationText.setText("-");
        }
        dateText.setText(StringUtils.getDateText(data.getGame().getDate()));

        String homeName = data.getGame().isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : data.getGame().getOpponentName();
        String awayName = !data.getGame().isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : data.getGame().getOpponentName();
        homeNameText.setText(homeName);
        awayNameText.setText(awayName);

        String result = "";
        if (data.getGame().getHomeGoals() != null) {
            result += data.getGame().getHomeGoals() + " - ";
        } else {
            result += "X - ";
        }
        if (data.getGame().getAwayGoals() != null) {
            result += data.getGame().getAwayGoals();
        } else {
            result += "X";
        }
        resultText.setText(result);

        ArrayList<Goal> goalsPeriod1 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod2 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod3 = new ArrayList<>();
        ArrayList<Goal> goalsPeriodJA = new ArrayList<>();
        homeGoals = 0;
        awayGoals = 0;

        long firstPeriodEnd = TimeUnit.MINUTES.toMillis(data.getGame().getPeriodInMinutes());
        long secondPeriodEnd = firstPeriodEnd * 2;
        long thirdPeriodEnd = firstPeriodEnd * 3;
        for (Goal goal : data.getGoals().values()) {
            if (goal.getTime() < firstPeriodEnd) {
                goalsPeriod1.add(goal);
            } else if (goal.getTime() >= firstPeriodEnd && goal.getTime() < secondPeriodEnd) {
                goalsPeriod2.add(goal);
            } else if (goal.getTime() >= secondPeriodEnd && goal.getTime() < thirdPeriodEnd) {
                goalsPeriod3.add(goal);
            } else {
                goalsPeriodJA.add(goal);
            }
        }
        setPeriodView(periodLayout1, 1, goalsPeriod1);
        setPeriodView(periodLayout2, 2, goalsPeriod2);
        setPeriodView(periodLayout3, 3, goalsPeriod3);
        setPeriodView(periodLayoutJA, 4, goalsPeriodJA);
        setLineStatsView(lineStats1, data.getLines().get(1));
        setLineStatsView(lineStats2, data.getLines().get(2));
        setLineStatsView(lineStats3, data.getLines().get(3));
        setLineStatsView(lineStats4, data.getLines().get(4));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);

        dateText = v.findViewById(R.id.dateText);
        seasonText = v.findViewById(R.id.seasonText);
        periodDurationText = v.findViewById(R.id.periodDurationText);
        homeNameText = v.findViewById(R.id.homeNameText);
        awayNameText = v.findViewById(R.id.awayNameText);
        resultText = v.findViewById(R.id.resultText);
        settingsIcon = v.findViewById(R.id.settingsIcon);
        homeGoalButton = v.findViewById(R.id.homeGoalButton);
        awayGoalButton = v.findViewById(R.id.awayGoalButton);
        periodLayout1 = v.findViewById(R.id.periodLayout1);
        periodLayout2 = v.findViewById(R.id.periodLayout2);
        periodLayout3 = v.findViewById(R.id.periodLayout3);
        periodLayoutJA = v.findViewById(R.id.periodLayoutJA);
        lineStats1 = v.findViewById(R.id.lineStats1);
        lineStats2 = v.findViewById(R.id.lineStats2);
        lineStats3 = v.findViewById(R.id.lineStats3);
        lineStats4 = v.findViewById(R.id.lineStats4);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.PLAYER) {
            settingsIcon.setVisibility(View.GONE);
            homeGoalButton.setVisibility(View.GONE);
            awayGoalButton.setVisibility(View.GONE);
        } else {
            settingsIcon.setVisibility(View.VISIBLE);
            homeGoalButton.setVisibility(View.VISIBLE);
            awayGoalButton.setVisibility(View.VISIBLE);
        }

        update();

        settingsIcon.setOnClickListener(v13 -> {
            final ActionMenuDialogFragment dialog = new ActionMenuDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
            dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
                @Override
                public void onEditItem() {
                    dialog.dismiss();
                    GameSettingsFragmentData gameSettingsFragmentData = new GameSettingsFragmentData();
                    gameSettingsFragmentData.setGame(data.getGame());
                    gameSettingsFragmentData.setLines(data.getLines());
                    FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(gameSettingsFragmentData);
                }

                @Override
                public void onRemoveItem() {
                    dialog.dismiss();
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.game_remove_confirmation));
                    dialogFragment.show(getChildFragmentManager(), "Poistetaanko ottelu?");
                    dialogFragment.setListener(() -> {
                        final String gameId = data.getGame().getGameId();
                        final Set<String> playerIds = AppRes.getInstance().getPlayers().keySet();
                        PlayerStatsResource.getInstance().removeStats(playerIds, gameId, () -> PlayerGamesResource.getInstance().removeGame(playerIds, gameId, () -> GoalsResource.getInstance().removeGoals(gameId, () -> GameLinesResource.getInstance().removeLines(gameId, () -> GamesResource.getInstance().removeGame(gameId, () -> AppRes.getActivity().onBackPressed())))));
                    });
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
        });

        homeGoalButton.setOnClickListener(v12 -> openGoalWizardDialog(null, true));

        awayGoalButton.setOnClickListener(v1 -> openGoalWizardDialog(null, false));

        return v;
    }

    private void setPeriodView(LinearLayout view, int period, List<Goal> goals) {
        TextView periodText = view.findViewById(R.id.periodText);
        LinearLayout goalList = view.findViewById(R.id.goalList);

        if (period == 4) {
            periodText.setText(getString(R.string.overtime).toUpperCase());
            if (goals.isEmpty()) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            periodText.setText(period + ". " + getString(R.string.period).toUpperCase());
        }

        goalList.removeAllViewsInLayout();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        GoalHolder holder = new GoalHolder();

        Collections.sort(goals, (o1, o2) -> Long.valueOf(o1.getTime()).compareTo(o2.getTime()));
        for (final Goal goal : goals) {
            View cv = inf.inflate(R.layout.list_item_goal, goalList, false);
            holder.homeGoalMenuIcon = cv.findViewById(R.id.editHomeGoalIcon);
            holder.awayGoalMenuIcon = cv.findViewById(R.id.editAwayGoalIcon);
            holder.homeScoreText = cv.findViewById(R.id.homeScoreText);
            holder.homeAssistText = cv.findViewById(R.id.homeAssistText);
            holder.awayScoreText = cv.findViewById(R.id.awayScoreText);
            holder.awayAssistText = cv.findViewById(R.id.awayAssistText);
            holder.timeText = cv.findViewById(R.id.timeText);
            holder.scoreText = cv.findViewById(R.id.scoreText);

            String scorerName;
            String assistantName;
            // Collect data
            String scorerId = goal.getScorerId();
            if (scorerId != null) {
                Player scorer = AppRes.getInstance().getPlayers().get(scorerId);
                if (scorer != null) {
                    scorerName = scorer.getNameWithNumber(true);
                } else {
                    scorerName = AppRes.getContext().getString(R.string.removed_player);
                }
            } else {
                scorerName = getString(R.string.goal).toUpperCase();
            }
            String assistantId = goal.getAssistantId();
            if (assistantId != null) {
                Player assistant = AppRes.getInstance().getPlayers().get(assistantId);
                if (assistant != null) {
                    assistantName = assistant.getNameWithNumber(true);
                } else {
                    assistantName = AppRes.getContext().getString(R.string.removed_player);
                }
            } else {
                assistantName = "";
            }

            // Add goals
            boolean isHomeGoal = (!goal.isOpponentGoal() && data.getGame().isHomeGame()) || (goal.isOpponentGoal() && !data.getGame().isHomeGame());
            if (isHomeGoal) {
                homeGoals++;
            } else {
                awayGoals++;
            }

            // Initialize
            String homeScore;
            String awayScore;
            String homeAssist;
            String awayAssist;

            // Display scorer and assistant OR 'maali'
            if (goal.isOpponentGoal()) {
                if (data.getGame().isHomeGame()) {
                    homeScore = "";
                    homeAssist = "";
                    awayScore = getString(R.string.goal).toUpperCase();
                    awayAssist = "";
                } else {
                    homeScore = getString(R.string.goal).toUpperCase();
                    homeAssist = "";
                    awayScore = "";
                    awayAssist = "";
                }
            } else {
                if (data.getGame().isHomeGame()) {
                    homeScore = scorerName;
                    homeAssist = assistantName;
                    awayScore = "";
                    awayAssist = "";
                } else {
                    homeScore = "";
                    homeAssist = "";
                    awayScore = scorerName;
                    awayAssist = assistantName;
                }
            }

            holder.homeScoreText.setText(homeScore);
            holder.awayScoreText.setText(awayScore);
            holder.homeAssistText.setText(homeAssist);
            holder.awayAssistText.setText(awayAssist);

            holder.timeText.setText(StringUtils.getMinSecTimeText(goal.getTime()));
            holder.scoreText.setText(homeGoals + " - " + awayGoals);

            // Role specific content
            UserConnection.Role role = AppRes.getInstance().getSelectedRole();
            if (role == UserConnection.Role.PLAYER) {
                holder.homeGoalMenuIcon.setVisibility(View.GONE);
                holder.awayGoalMenuIcon.setVisibility(View.GONE);
            } else {
                if (isHomeGoal) {
                    holder.awayGoalMenuIcon.setVisibility(View.GONE);
                    holder.homeGoalMenuIcon.setVisibility(View.VISIBLE);
                    setGoalMenuIconListener(holder.homeGoalMenuIcon, goal, true);
                } else {
                    holder.homeGoalMenuIcon.setVisibility(View.GONE);
                    holder.awayGoalMenuIcon.setVisibility(View.VISIBLE);
                    setGoalMenuIconListener(holder.awayGoalMenuIcon, goal, false);
                }
            }

            goalList.addView(cv);
        }
    }

    private void setGoalMenuIconListener(IconView icon, final Goal goal, final boolean isHomeGoal) {
        icon.setOnClickListener(v -> {
            final ActionMenuDialogFragment dialog = new ActionMenuDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
            dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
                @Override
                public void onEditItem() {
                    dialog.dismiss();
                    openGoalWizardDialog(goal, isHomeGoal);
                }

                @Override
                public void onRemoveItem() {
                    dialog.dismiss();
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_event_remove_confirmation));
                    dialogFragment.show(getChildFragmentManager(), "Poistetaanko maali?");
                    dialogFragment.setListener(() -> GoalsResourceWrapper.getInstance(data).removeGoal(goal, isHomeGoal, data -> {
                        GameFragment.this.data = data;
                        update();
                    }));
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
        });
    }

    private void openGoalWizardDialog(final Goal goal, boolean homeGoal) {
        final GoalWizardDialogFragment dialog = new GoalWizardDialogFragment();
        dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa maalia");
        final boolean opponentGoal = (data.getGame().isHomeGame() && !homeGoal) || (!data.getGame().isHomeGame() && homeGoal);

        GoalWizardDialogData dialogData = new GoalWizardDialogData();
        dialogData.setGoal(goal);
        dialogData.setGame(data.getGame());
        dialogData.setLines(data.getLines());
        dialogData.setOpponentGoal(opponentGoal);
        dialog.setData(dialogData);

        dialog.setListener(goalToSave -> {
            dialog.dismiss();
            GoalsResourceWrapper.getInstance(data).editGoal(goal, goalToSave, opponentGoal, data -> {
                GameFragment.this.data = data;
                update();
            });
        });
    }

    private void setLineStatsView(LinearLayout view, Line line) {
        PlayerStatsHolder holder = new PlayerStatsHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());

        if (line == null || line.getSortedPlayers().isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            TextView lineText = view.findViewById(R.id.lineText);
            LinearLayout playersList = view.findViewById(R.id.playersList);
            lineText.setText(line.getLineNumber() + ". " + getString(R.string.line));
            playersList.removeAllViewsInLayout();

            Map<String, String> players = line.getSortedPlayers();
            for (Map.Entry<String, String> entry : players.entrySet()) {
                View cv = inf.inflate(R.layout.list_item_player_stats, playersList, false);
                String position = entry.getKey();
                String playerId = entry.getValue();
                holder.statsText = cv.findViewById(R.id.statsText);
                holder.plusMinusText = cv.findViewById(R.id.plusMinusText);
                holder.positionText = cv.findViewById(R.id.positionText);
                holder.nameText = cv.findViewById(R.id.nameText);

                holder.statsText.setText(getStatsText(playerId));
                holder.plusMinusText.setText(getPlusMinusText(playerId));
                holder.positionText.setText(Player.getPositionText(position, true));
                holder.nameText.setText(Player.getPlayerName(playerId));

                playersList.addView(cv);
            }
        }
    }

    private String getStatsText(String playerId) {
        int scores = 0;
        int assists = 0;
        for (Goal goal : data.getGoals().values()) {
            if (goal.getScorerId() != null && goal.getScorerId().equals(playerId)) {
                scores++;
            }
            if (goal.getAssistantId() != null && goal.getAssistantId().equals(playerId)) {
                assists++;
            }
        }
        return scores + " + " + assists;
    }

    private String getPlusMinusText(String playerId) {
        int stats = 0;
        for (Goal goal : data.getGoals().values()) {
            Goal.Mode mode = Goal.Mode.valueOf(goal.getGameMode());
            if (goal.getPlayerIds().contains(playerId)) {
                // Plus
                if (!goal.isOpponentGoal() && !(Goal.Mode.YV == mode || Goal.Mode.RL == mode)) {
                    stats++;
                }

                // Minus
                if (goal.isOpponentGoal() && !(Goal.Mode.AV == mode || Goal.Mode.RL == mode)) {
                    stats--;
                }
            }
        }

        return stats > 0 ? "+" + stats : String.valueOf(stats);
    }

    // PERIODS
    public class GoalHolder {
        IconView homeGoalMenuIcon;
        IconView awayGoalMenuIcon;
        TextView homeScoreText;
        TextView homeAssistText;
        TextView awayScoreText;
        TextView awayAssistText;
        TextView timeText;
        TextView scoreText;
    }

    // PLAYER STATS
    public class PlayerStatsHolder {
        TextView statsText;
        TextView plusMinusText;
        TextView positionText;
        TextView nameText;
    }

}
