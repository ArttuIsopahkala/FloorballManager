package com.ardeapps.floorballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.ActionMenuDialogFragment;
import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.goalDialog.GoalWizardDialogFragment;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.resources.GoalResourcesWrapper;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GameFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GoalWizardDialogData;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class GameFragment extends Fragment implements DataView {

    TextView dateText;
    TextView periodDurationText;
    TextView homeNameText;
    TextView awayNameText;
    TextView resultText;
    IconView settingsIcon;
    LinearLayout periodLayout1;
    LinearLayout periodLayout2;
    LinearLayout periodLayout3;
    LinearLayout lineStats1;
    LinearLayout lineStats2;
    LinearLayout lineStats3;
    LinearLayout lineStats4;
    Button homeGoalButton;
    Button awayGoalButton;

    private GameFragmentData data;

    int homeGoals = 0;
    int awayGoals = 0;

    @Override
    public void setData(Object viewData) {
        data = (GameFragmentData) viewData;
    }

    @Override
    public GameFragmentData getData() {
        return data;
    }

    public void update() {
        periodDurationText.setText(data.getGame().getPeriodInMinutes() + "min");
        dateText.setText(StringUtils.getDateText(data.getGame().getDate()));

        String homeName = data.getGame().isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : data.getGame().getOpponentName();
        String awayName = !data.getGame().isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : data.getGame().getOpponentName();
        homeNameText.setText(homeName);
        awayNameText.setText(awayName);

        String result = "";
        if(data.getGame().getHomeGoals() != null) {
            result += data.getGame().getHomeGoals() + " - ";
        } else {
            result += "X - ";
        }
        if(data.getGame().getAwayGoals() != null) {
            result += data.getGame().getAwayGoals();
        } else {
            result += "X";
        }
        resultText.setText(result);

        ArrayList<Goal> goalsPeriod1 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod2 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod3 = new ArrayList<>();
        homeGoals = 0;
        awayGoals = 0;

        long firstPeriodEnd = TimeUnit.MINUTES.toMillis(data.getGame().getPeriodInMinutes());
        long secondPeriodEnd = firstPeriodEnd * 2;
        for(Goal goal : data.getGoals().values()) {
            if(goal.getTime() < firstPeriodEnd) {
                goalsPeriod1.add(goal);
            } else if (goal.getTime() > firstPeriodEnd && goal.getTime() < secondPeriodEnd) {
                goalsPeriod2.add(goal);
            } else {
                goalsPeriod3.add(goal);
            }
        }
        setPeriodView(periodLayout1, 1, goalsPeriod1);
        setPeriodView(periodLayout2, 2, goalsPeriod2);
        setPeriodView(periodLayout3, 3, goalsPeriod3);
        setLineStatsView(lineStats1, data.getLines().get(1));
        setLineStatsView(lineStats2, data.getLines().get(2));
        setLineStatsView(lineStats3, data.getLines().get(3));
        setLineStatsView(lineStats4, data.getLines().get(4));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);

        dateText = v.findViewById(R.id.dateText);
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
        lineStats1 = v.findViewById(R.id.lineStats1);
        lineStats2 = v.findViewById(R.id.lineStats2);
        lineStats3 = v.findViewById(R.id.lineStats3);
        lineStats4 = v.findViewById(R.id.lineStats4);

        update();

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameSettingsFragmentData gameSettingsFragmentData = new GameSettingsFragmentData();
                gameSettingsFragmentData.setGame(data.getGame());
                gameSettingsFragmentData.setLines(data.getLines());
                FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(gameSettingsFragmentData);
            }
        });

        homeGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoalWizardDialog(null, true);
            }
        });

        awayGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoalWizardDialog(null, false);
            }
        });

        return v;
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
        RelativeLayout goalContainer;
        LinearLayout homeContainer;
        LinearLayout awayContainer;
    }

    private void setPeriodView(LinearLayout view, int period, List<Goal> goals) {
        TextView periodText = view.findViewById(R.id.periodText);
        LinearLayout goalList = view.findViewById(R.id.goalList);

        periodText.setText(period + ". " + getString(R.string.period).toUpperCase());

        goalList.removeAllViewsInLayout();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        GoalHolder holder = new GoalHolder();

        Collections.sort(goals, new Comparator<Goal>() {
            @Override
            public int compare(Goal o1, Goal o2) {
                return Long.valueOf(o1.getTime()).compareTo(o2.getTime());
            }
        });
        for(final Goal goal : goals) {
            View cv = inf.inflate(R.layout.list_item_goal, goalList, false);
            holder.homeGoalMenuIcon = cv.findViewById(R.id.editHomeGoalIcon);
            holder.awayGoalMenuIcon = cv.findViewById(R.id.editAwayGoalIcon);
            holder.homeScoreText = cv.findViewById(R.id.homeScoreText);
            holder.homeAssistText = cv.findViewById(R.id.homeAssistText);
            holder.awayScoreText = cv.findViewById(R.id.awayScoreText);
            holder.awayAssistText = cv.findViewById(R.id.awayAssistText);
            holder.timeText = cv.findViewById(R.id.timeText);
            holder.scoreText = cv.findViewById(R.id.scoreText);
            holder.goalContainer = cv.findViewById(R.id.goalContainer);
            holder.homeContainer = cv.findViewById(R.id.homeContainer);
            holder.awayContainer = cv.findViewById(R.id.awayContainer);

            String scorerName = "";
            String assistantName = "";
            // Collect data
            String scorerId = goal.getScorerId();
            if (scorerId != null) {
                Player scorer = AppRes.getInstance().getPlayers().get(scorerId);
                if (scorer != null) {
                    scorerName = scorer.getNameWithNumber(true);
                } else {
                    scorerName = AppRes.getContext().getString(R.string.removed_player);
                }
            }
            String assistantId = goal.getAssistantId();
            if (assistantId != null) {
                Player assistant = AppRes.getInstance().getPlayers().get(assistantId);
                if (assistant != null) {
                    assistantName = assistant.getNameWithNumber(true);
                } else {
                    assistantName = AppRes.getContext().getString(R.string.removed_player);
                }
            }

            // Add goals
            boolean isHomeGoal = (!goal.isOpponentGoal() && data.getGame().isHomeGame()) || (goal.isOpponentGoal() && !data.getGame().isHomeGame());
            if(isHomeGoal) {
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
            if(goal.isOpponentGoal()) {
                if(data.getGame().isHomeGame()) {
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
                if(data.getGame().isHomeGame()) {
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

            if(isHomeGoal) {
                holder.awayGoalMenuIcon.setVisibility(View.GONE);
                holder.homeGoalMenuIcon.setVisibility(View.VISIBLE);
                setGoalMenuIconListener(holder.homeGoalMenuIcon, goal, true);
            } else {
                holder.homeGoalMenuIcon.setVisibility(View.GONE);
                holder.awayGoalMenuIcon.setVisibility(View.VISIBLE);
                setGoalMenuIconListener(holder.awayGoalMenuIcon, goal, false);
            }

            goalList.addView(cv);
        }
    }

    private void setGoalMenuIconListener(IconView icon, final Goal goal, final boolean isHomeGoal) {
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ActionMenuDialogFragment dialog = new ActionMenuDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
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
                        dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                            @Override
                            public void onDialogYesButtonClick() {
                                GoalResourcesWrapper.getInstance(data).removeGoal(goal, isHomeGoal, new GoalResourcesWrapper.RemoveGoalListener() {
                                    @Override
                                    public void onGoalRemoved(GameFragmentData data) {
                                        GameFragment.this.data = data;
                                        update();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void openGoalWizardDialog(final Goal goal, boolean homeGoal) {
        final GoalWizardDialogFragment dialog = new GoalWizardDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager(), "Muokkaa maalia");
        final boolean opponentGoal = (data.getGame().isHomeGame() && !homeGoal) || (!data.getGame().isHomeGame() && homeGoal);

        GoalWizardDialogData dialogData = new GoalWizardDialogData();
        dialogData.setGoal(goal);
        dialogData.setGame(data.getGame());
        dialogData.setLines(data.getLines());
        dialogData.setOpponentGoal(opponentGoal);
        dialog.setData(dialogData);

        dialog.setListener(new GoalWizardDialogFragment.GoalWizardListener() {
            @Override
            public void onGoalSaved(final Goal goalToSave) {
                dialog.dismiss();
                GoalResourcesWrapper.getInstance(data).editGoal(goal, goalToSave, opponentGoal, new GoalResourcesWrapper.EditGoalListener() {
                    @Override
                    public void onGoalEdited(GameFragmentData data) {
                        GameFragment.this.data = data;
                        update();
                    }
                });
            }
        });
    }

    // PLAYER STATS
    public class PlayerStatsHolder {
        TextView statsText;
        TextView plusMinusText;
        TextView positionText;
        TextView nameText;
    }

    private void setLineStatsView(LinearLayout view, Line line) {
        PlayerStatsHolder holder = new PlayerStatsHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());

        if(line == null || line.getSortedPlayers().isEmpty()) {
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
        for(Goal goal : data.getGoals().values()) {
            if(goal.getScorerId() != null && goal.getScorerId().equals(playerId)) {
                scores++;
            }
            if(goal.getAssistantId() != null && goal.getAssistantId().equals(playerId)) {
                assists++;
            }
        }
        return scores + " + " + assists;
    }

    private String getPlusMinusText(String playerId) {
        int stats = 0;
        for(Goal goal : data.getGoals().values()) {
            Goal.Mode mode = Goal.Mode.valueOf(goal.getGameMode());
            if(goal.getPlayerIds().contains(playerId)) {
                // Plus
                if(!goal.isOpponentGoal() && !(Goal.Mode.YV == mode || Goal.Mode.RL == mode)) {
                    stats++;
                }

                // Minus
                if(goal.isOpponentGoal() && !(Goal.Mode.AV == mode || Goal.Mode.RL == mode)) {
                    stats--;
                }
            }
        }

        return stats > 0 ? "+" + stats : String.valueOf(stats);
    }

}
