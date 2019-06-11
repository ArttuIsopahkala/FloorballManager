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
import com.ardeapps.floorballcoach.goalDialog.GoalWizardFragment;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.resources.GamesResource;
import com.ardeapps.floorballcoach.resources.GoalsByTeamResource;
import com.ardeapps.floorballcoach.resources.StatsByPlayerResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;


public class GameFragment extends Fragment {

    TextView dateText;
    TextView periodDurationText;
    TextView homeNameText;
    TextView awayNameText;
    TextView resultText;
    IconView settingsIcon;
    LinearLayout periodLayout1;
    LinearLayout periodLayout2;
    LinearLayout periodLayout3;
    LinearLayout linesList;
    Button homeGoalButton;
    Button awayGoalButton;

    private Game game;
    Map<Integer, Line> lines = new HashMap<>();
    private Map<String, Goal> goals = new HashMap<>();

    int homeGoals = 0;
    int awayGoals = 0;

    public void setGame(Game game) {
        this.game = game;
    }

    public void setLines(Map<Integer, Line> lines) {
        this.lines = lines;
    }

    public void update() {
        periodDurationText.setText(game.getPeriodInMinutes() + "min");
        dateText.setText(StringUtils.getDateText(game.getDate()));

        String homeName = game.isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : game.getOpponentName();
        String awayName = !game.isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : game.getOpponentName();
        homeNameText.setText(homeName);
        awayNameText.setText(awayName);

        String result = "";
        if(game.getHomeGoals() != null) {
            result += game.getHomeGoals() + " - ";
        } else {
            result += "X - ";
        }
        if(game.getAwayGoals() != null) {
            result += game.getAwayGoals();
        } else {
            result += "X";
        }
        resultText.setText(result);

        goals = AppRes.getInstance().getGoals();

        ArrayList<Goal> goalsPeriod1 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod2 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod3 = new ArrayList<>();
        homeGoals = 0;
        awayGoals = 0;

        long firstPeriodEnd = TimeUnit.MINUTES.toMillis(game.getPeriodInMinutes());
        long secondPeriodEnd = firstPeriodEnd * 2;
        for(Goal goal : goals.values()) {
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
        SortedSet<Integer> keys = new TreeSet<>(lines.keySet());
        linesList.removeAllViews();
        for (Integer key : keys) {
            Line line = lines.get(key);
            if(line != null) {
                setLineView(line);
            }
        }
    }

    public class GoalHolder {
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

    public class PlayerStatsHolder {
        TextView statsText;
        TextView plusMinusText;
        TextView positionText;
        TextView nameText;
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
        linesList = v.findViewById(R.id.linesList);

        update();

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(game, lines);
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

    private void openGoalWizardDialog(Goal goal, boolean homeGoal) {
        final GoalWizardFragment dialog = new GoalWizardFragment();
        dialog.show(getActivity().getSupportFragmentManager(), "Muokkaa maalia");
        dialog.setGoal(goal);
        dialog.setGame(game);
        final boolean opponentGoal = (game.isHomeGame() && !homeGoal) || (!game.isHomeGame() && homeGoal);
        dialog.setOpponentGoal(opponentGoal);

        dialog.setListener(new GoalWizardFragment.GoalWizardListener() {
            @Override
            public void onGoalSaved(final Goal goal) {
                dialog.dismiss();
                GoalsByTeamResource.getInstance().addGoal(goal, new FirebaseDatabaseService.AddDataSuccessListener() {
                    @Override
                    public void onAddDataSuccess(String id) {
                        goal.setGoalId(id);
                        AppRes.getInstance().setGoal(goal.getGoalId(), goal);

                        if(!opponentGoal) {
                            // Add goal to player stats
                            StatsByPlayerResource.getInstance().editStat(goal.getScorerId(), goal, new FirebaseDatabaseService.EditDataSuccessListener() {
                                @Override
                                public void onEditDataSuccess() {
                                    // Add assist to player stats
                                    if(!StringUtils.isEmptyString(goal.getAssistantId())) {
                                        StatsByPlayerResource.getInstance().editStat(goal.getAssistantId(), goal, new FirebaseDatabaseService.EditDataSuccessListener() {
                                            @Override
                                            public void onEditDataSuccess() {
                                                addGoalToGame(goal);
                                            }
                                        });
                                    } else {
                                        addGoalToGame(goal);
                                    }
                                }
                            });
                        } else {
                            addGoalToGame(goal);
                        }
                    }
                });
            }
        });
    }

    private void addGoalToGame(final Goal goal) {
        // Add goal to game
        Integer homeGoals = game.getHomeGoals();
        Integer awayGoals = game.getAwayGoals();
        if(homeGoals == null) {
            homeGoals = 0;
        }
        if(awayGoals == null) {
            awayGoals = 0;
        }

        boolean isHomeGoal = (!goal.isOpponentGoal() && game.isHomeGame()) || (goal.isOpponentGoal() && !game.isHomeGame());
        if(isHomeGoal) {
            homeGoals++;
        } else {
            awayGoals++;
        }
        game.setHomeGoals(homeGoals);
        game.setAwayGoals(awayGoals);
        GamesResource.getInstance().editGame(game, new FirebaseDatabaseService.EditDataSuccessListener() {
            @Override
            public void onEditDataSuccess() {
                update();
            }
        });
    }

    private void setLineView(Line line) {
        PlayerStatsHolder holder = new PlayerStatsHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        Map<String, String> players = line.getSortedPlayers();
        for (Map.Entry<String, String> entry : players.entrySet()) {
            View cv = inf.inflate(R.layout.list_item_player_stats, linesList, false);
            String position = entry.getKey();
            String playerId = entry.getValue();
            holder.statsText = cv.findViewById(R.id.statsText);
            holder.plusMinusText = cv.findViewById(R.id.plusMinusText);
            holder.positionText = cv.findViewById(R.id.positionText);
            holder.nameText = cv.findViewById(R.id.nameText);

            holder.statsText.setText(getStatsText(playerId));
            holder.plusMinusText.setText(getPlusMinusText(playerId));
            holder.positionText.setText(StringUtils.getPositionText(position, true));
            holder.nameText.setText(StringUtils.getPlayerName(playerId));

            linesList.addView(cv);
        }
    }

    private String getStatsText(String playerId) {
        int scores = 0;
        int assists = 0;
        for(Goal goal : goals.values()) {
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
        for(Goal goal : goals.values()) {
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

        return stats > 0 ? "+" + String.valueOf(stats) : String.valueOf(stats);
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
        for(Goal goal : goals) {
            View cv = inf.inflate(R.layout.list_item_goal, goalList, false);
            holder.homeScoreText = cv.findViewById(R.id.homeScoreText);
            holder.homeAssistText = cv.findViewById(R.id.homeAssistText);
            holder.awayScoreText = cv.findViewById(R.id.awayScoreText);
            holder.awayAssistText = cv.findViewById(R.id.awayAssistText);
            holder.timeText = cv.findViewById(R.id.timeText);
            holder.scoreText = cv.findViewById(R.id.scoreText);
            holder.goalContainer = cv.findViewById(R.id.goalContainer);
            holder.homeContainer = cv.findViewById(R.id.homeContainer);
            holder.awayContainer = cv.findViewById(R.id.awayContainer);

            // Collect data
            Player scorer = AppRes.getInstance().getPlayers().get(goal.getScorerId());
            Player assistant = AppRes.getInstance().getPlayers().get(goal.getAssistantId());
            String scorerName = scorer != null ? scorer.getNameWithNumber(true) : "";
            String assistantName = assistant != null ? assistant.getNameWithNumber(true) : "";

            // Add goals
            boolean isHomeGoal = (!goal.isOpponentGoal() && game.isHomeGame()) || (goal.isOpponentGoal() && !game.isHomeGame());
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
                if(game.isHomeGame()) {
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
                if(game.isHomeGame()) {
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

            goalList.addView(cv);
        }
    }

}
