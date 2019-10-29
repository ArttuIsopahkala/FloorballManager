package com.ardeapps.floorballmanager.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ActionMenuDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.GameResultDialogFragment;
import com.ardeapps.floorballmanager.objects.Event;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GamesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.PenaltiesResource;
import com.ardeapps.floorballmanager.resources.PlayerGamesResource;
import com.ardeapps.floorballmanager.resources.PlayerStatsResource;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GameFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GameResultDialogData;
import com.ardeapps.floorballmanager.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballmanager.views.IconView;
import com.ardeapps.floorballmanager.wrappers.GameEventWrapper;
import com.ardeapps.floorballmanager.wrappers.GoalsResourceWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
    Spinner eventSpinner;
    IconView homePlusIcon;
    IconView awayPlusIcon;
    RelativeLayout addEventsContainer;

    private int currentHomeGoals = 0;
    private int currentAwayGoals = 0;
    private int markedHomeGoals = 0;
    private int markedAwayGoals = 0;
    private String homeName;
    private String awayName;
    private GameFragmentData data;
    private ArrayList<Event> events;
    private Event currentEvent;

    @Override
    public GameFragmentData getData() {
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GameFragmentData) viewData;
    }

    public void update() {
        // Initialize and collect data
        ArrayList<Goal> goalsPeriod1 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod2 = new ArrayList<>();
        ArrayList<Goal> goalsPeriod3 = new ArrayList<>();
        ArrayList<Goal> goalsPeriodJA = new ArrayList<>();
        ArrayList<Penalty> penaltiesPeriod1 = new ArrayList<>();
        ArrayList<Penalty> penaltiesPeriod2 = new ArrayList<>();
        ArrayList<Penalty> penaltiesPeriod3 = new ArrayList<>();
        ArrayList<Penalty> penaltiesPeriodJA = new ArrayList<>();

        currentHomeGoals = 0;
        currentAwayGoals = 0;
        markedHomeGoals = 0;
        markedAwayGoals = 0;
        long firstPeriodEnd = TimeUnit.MINUTES.toMillis(data.getGame().getPeriodInMinutes());
        long secondPeriodEnd = firstPeriodEnd * 2;
        long thirdPeriodEnd = firstPeriodEnd * 3;

        // Collect goals
        for (Goal goal : data.getGoals().values()) {
            // Add user marked goals
            boolean isHomeGoal = (!goal.isOpponentGoal() && data.getGame().isHomeGame()) || (goal.isOpponentGoal() && !data.getGame().isHomeGame());
            if (isHomeGoal) {
                markedHomeGoals++;
            } else {
                markedAwayGoals++;
            }

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

        // Collect penalties
        for (Penalty penalty : data.getPenalties().values()) {
            if (penalty.getTime() < firstPeriodEnd) {
                penaltiesPeriod1.add(penalty);
            } else if (penalty.getTime() >= firstPeriodEnd && penalty.getTime() < secondPeriodEnd) {
                penaltiesPeriod2.add(penalty);
            } else if (penalty.getTime() >= secondPeriodEnd && penalty.getTime() < thirdPeriodEnd) {
                penaltiesPeriod3.add(penalty);
            } else {
                penaltiesPeriodJA.add(penalty);
            }
        }

        homeName = data.getGame().isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : data.getGame().getOpponentName();
        awayName = !data.getGame().isHomeGame() ? AppRes.getInstance().getSelectedTeam().getName() : data.getGame().getOpponentName();

        // Set data
        homeNameText.setText(homeName);
        awayNameText.setText(awayName);

        Season season = AppRes.getInstance().getSeasons().get(data.getGame().getSeasonId());
        if (season != null) {
            seasonText.setText(season.getName());
            periodDurationText.setText(data.getGame().getPeriodInMinutes() + "min");
        } else {
            seasonText.setText("-");
            periodDurationText.setText("-");
        }

        dateText.setText(StringUtils.getDateText(data.getGame().getDate(), true));

        setPeriodView(periodLayout1, 1, goalsPeriod1, penaltiesPeriod1);
        setPeriodView(periodLayout2, 2, goalsPeriod2, penaltiesPeriod2);
        setPeriodView(periodLayout3, 3, goalsPeriod3, penaltiesPeriod3);
        setPeriodView(periodLayoutJA, 4, goalsPeriodJA, penaltiesPeriodJA);
        setLineStatsView(lineStats1, data.getLines().get(1));
        setLineStatsView(lineStats2, data.getLines().get(2));
        setLineStatsView(lineStats3, data.getLines().get(3));
        setLineStatsView(lineStats4, data.getLines().get(4));

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
        periodLayout1 = v.findViewById(R.id.periodLayout1);
        periodLayout2 = v.findViewById(R.id.periodLayout2);
        periodLayout3 = v.findViewById(R.id.periodLayout3);
        periodLayoutJA = v.findViewById(R.id.periodLayoutJA);
        lineStats1 = v.findViewById(R.id.lineStats1);
        lineStats2 = v.findViewById(R.id.lineStats2);
        lineStats3 = v.findViewById(R.id.lineStats3);
        lineStats4 = v.findViewById(R.id.lineStats4);
        eventSpinner = v.findViewById(R.id.eventSpinner);
        homePlusIcon = v.findViewById(R.id.homePlusIcon);
        awayPlusIcon = v.findViewById(R.id.awayPlusIcon);
        addEventsContainer = v.findViewById(R.id.addEventsContainer);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.ADMIN) {
            settingsIcon.setVisibility(View.VISIBLE);
            addEventsContainer.setVisibility(View.VISIBLE);
            resultText.setBackground(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.button_background));
            resultText.setOnClickListener(v14 -> onEditResultManually());
        } else {
            resultText.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_second));
            settingsIcon.setVisibility(View.GONE);
            addEventsContainer.setVisibility(View.GONE);
        }

        Map<Event, String> eventsMap = new TreeMap<>();
        eventsMap.put(Event.GOAL, getString(R.string.game_event_goal));
        eventsMap.put(Event.PENALTY, getString(R.string.game_event_penalty));

        ArrayList<String> eventTitles = new ArrayList<>(eventsMap.values());
        events = new ArrayList<>(eventsMap.keySet());
        Helper.setSpinnerAdapter(eventSpinner, eventTitles);
        // Set default event to goal
        currentEvent = Event.GOAL;

        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEvent = events.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        update();

        settingsIcon.setOnClickListener(v13 -> {
            final ActionMenuDialogFragment dialog = ActionMenuDialogFragment.newInstance(null, getString(R.string.remove_game));
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
                        PlayerStatsResource.getInstance().removeStats(playerIds, gameId, ()
                                -> PlayerGamesResource.getInstance().removeGame(playerIds, gameId, ()
                                -> GoalsResource.getInstance().removeGoals(gameId, ()
                                -> PenaltiesResource.getInstance().removePenalties(gameId, ()
                                -> GameLinesResource.getInstance().removeLines(gameId, ()
                                -> GamesResource.getInstance().removeGame(gameId, ()
                                -> AppRes.getActivity().onBackPressed()))))));
                    });
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
        });

        homePlusIcon.setOnClickListener(v12 -> chooseEvent(true));

        awayPlusIcon.setOnClickListener(v1 -> chooseEvent(false));

        return v;
    }

    private void chooseEvent(boolean isHomeEvent) {
        if(currentEvent == Event.GOAL) {
            editGoal(null, isHomeEvent);
        } else if (currentEvent == Event.PENALTY) {
            editPenalty(null, isHomeEvent);
        }
    }

    private void editGoal(final Goal goal, boolean isHomeGoal) {
        GameEventWrapper.getInstance(data).openGoalWizardDialog(goal, isHomeGoal, goalToSave -> {
            final boolean opponentGoal = (data.getGame().isHomeGame() && !isHomeGoal) || (!data.getGame().isHomeGame() && isHomeGoal);
            int homeGoals = data.getGame().getHomeGoals() != null ? data.getGame().getHomeGoals() : 0;
            int awayGoals = data.getGame().getAwayGoals() != null ? data.getGame().getAwayGoals() : 0;
            // Add goal to result only if it equals to marked goals
            boolean addToResult = goal == null && (isHomeGoal && homeGoals == markedHomeGoals) || (!isHomeGoal && awayGoals == markedAwayGoals);

            GoalsResourceWrapper.getInstance(data).editGoal(goal, goalToSave, opponentGoal, addToResult, data -> {
                GameFragment.this.data = data;
                update();
            });
        });
    }

    private void editPenalty(final Penalty penalty, boolean isHomePenalty) {
        GameEventWrapper.getInstance(data).openPenaltyWizardDialog(penalty, isHomePenalty, penaltyToSave -> {
            if(penalty == null) {
                PenaltiesResource.getInstance().addPenalty(penaltyToSave, id -> {
                    penaltyToSave.setPenaltyId(id);
                    data.getPenalties().put(id, penaltyToSave);
                    update();
                });
            } else {
                PenaltiesResource.getInstance().editPenalty(penaltyToSave, () -> {
                    data.getPenalties().put(penaltyToSave.getPenaltyId(), penaltyToSave);
                    update();
                });
            }
        });
    }

    private void onEditResultManually() {
        Game game = data.getGame();
        GameResultDialogData resultData = new GameResultDialogData();
        resultData.setHomeName(homeName);
        resultData.setAwayName(awayName);
        resultData.setHomeGoals(game.getHomeGoals() != null ? game.getHomeGoals() : 0);
        resultData.setAwayGoals(game.getAwayGoals() != null ? game.getAwayGoals() : 0);
        resultData.setMarkedHomeGoals(markedHomeGoals);
        resultData.setMarkedAwayGoals(markedAwayGoals);
        GameResultDialogFragment dialog = new GameResultDialogFragment();
        dialog.setData(resultData);
        dialog.show(getChildFragmentManager(), "Ottelun lopputulos");
        dialog.setListener((homeGoals, awayGoals) -> {
            game.setHomeGoals(homeGoals);
            game.setAwayGoals(awayGoals);
            GamesResource.getInstance().editGame(game, this::update);
        });
    }

    private class EventView {
        View view;
        long time;
    }

    private void setPeriodView(LinearLayout view, int period, List<Goal> goals, List<Penalty> penalties) {
        TextView periodText = view.findViewById(R.id.periodText);
        LinearLayout eventList = view.findViewById(R.id.eventList);

        if (period == 4) {
            periodText.setText(getString(R.string.overtime).toUpperCase());
            if (goals.isEmpty() && penalties.isEmpty()) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            periodText.setText(period + ". " + getString(R.string.period).toUpperCase());
        }

        eventList.removeAllViewsInLayout();

        EventHolder holder = new EventHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        ArrayList<EventView> eventViews = new ArrayList<>();

        // Sort by time so goals are calculated right order
        Collections.sort(goals, (o1, o2) -> Long.compare(o1.getTime(), o2.getTime()));

        // ADD GOALS TO LIST
        for (final Goal goal : goals) {
            View cv = inf.inflate(R.layout.list_item_event, eventList, false);
            holder.timeContainer = cv.findViewById(R.id.timeContainer);
            holder.homeContainer = cv.findViewById(R.id.homeContainer);
            holder.awayContainer = cv.findViewById(R.id.awayContainer);
            holder.homeMainText = cv.findViewById(R.id.homeScoreText);
            holder.homeSecondText = cv.findViewById(R.id.homeAssistText);
            holder.awayMainText = cv.findViewById(R.id.awayScoreText);
            holder.awaySecondText = cv.findViewById(R.id.awayAssistText);
            holder.timeText = cv.findViewById(R.id.timeText);
            holder.resultText = cv.findViewById(R.id.scoreText);

            holder.timeContainer.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_background_second));

            String scorerName;
            String assistantName;
            // Collect data
            String scorerId = goal.getScorerId();
            if (scorerId != null) {
                Player scorer = AppRes.getInstance().getPlayers().get(scorerId);
                if (scorer != null) {
                    scorerName = scorer.getNameWithNumber();
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
                    assistantName = assistant.getNameWithNumber();
                } else {
                    assistantName = AppRes.getContext().getString(R.string.removed_player);
                }
            } else {
                assistantName = "";
            }

            // Add goals
            boolean isHomeGoal = (!goal.isOpponentGoal() && data.getGame().isHomeGame()) || (goal.isOpponentGoal() && !data.getGame().isHomeGame());
            if (isHomeGoal) {
                currentHomeGoals++;
            } else {
                currentAwayGoals++;
            }

            // Initialize
            String homeScore;
            String awayScore;
            String homeAssist;
            String awayAssist;

            // Display scorer and assistant OR 'maali'
            String modeText = "";
            Goal.Mode mode = Goal.Mode.fromDatabaseName(goal.getGameMode());
            if (mode != Goal.Mode.FULL) {
                modeText = " (" + mode.name() + ")";
            }
            if (goal.isOpponentGoal()) {
                if (data.getGame().isHomeGame()) {
                    homeScore = "";
                    homeAssist = "";
                    awayScore = getString(R.string.goal).toUpperCase() + modeText;
                    awayAssist = "";
                } else {
                    homeScore = getString(R.string.goal).toUpperCase() + modeText;
                    homeAssist = "";
                    awayScore = "";
                    awayAssist = "";
                }
            } else {
                if (data.getGame().isHomeGame()) {
                    homeScore = scorerName + modeText;
                    homeAssist = assistantName;
                    awayScore = "";
                    awayAssist = "";
                } else {
                    homeScore = "";
                    homeAssist = "";
                    awayScore = scorerName + modeText;
                    awayAssist = assistantName;
                }
            }

            holder.homeMainText.setText(homeScore);
            holder.awayMainText.setText(awayScore);
            holder.homeSecondText.setVisibility(StringUtils.isEmptyString(homeAssist) ? View.GONE : View.VISIBLE);
            holder.homeSecondText.setText(homeAssist);
            holder.awaySecondText.setVisibility(StringUtils.isEmptyString(awayAssist) ? View.GONE : View.VISIBLE);
            holder.awaySecondText.setText(awayAssist);

            holder.timeText.setText(StringUtils.getMinSecTimeText(goal.getTime()));
            holder.resultText.setText(currentHomeGoals + " - " + currentAwayGoals);

            if (isHomeGoal) {
                holder.awayContainer.setVisibility(View.GONE);
                holder.homeContainer.setVisibility(View.VISIBLE);
            } else {
                holder.homeContainer.setVisibility(View.GONE);
                holder.awayContainer.setVisibility(View.VISIBLE);
            }

            // Role specific content
            UserConnection.Role role = AppRes.getInstance().getSelectedRole();
            if (role == UserConnection.Role.ADMIN) {
                if (isHomeGoal) {
                    setGoalMenuListener(holder.homeContainer, goal, true);
                } else {
                    setGoalMenuListener(holder.awayContainer, goal, false);
                }
            }

            EventView eventView = new EventView();
            eventView.view = cv;
            eventView.time = goal.getTime();
            eventViews.add(eventView);
        }

        // ADD PENALTIES TO LIST
        for (Penalty penalty : penalties) {
            View cv = inf.inflate(R.layout.list_item_event, eventList, false);
            holder.timeContainer = cv.findViewById(R.id.timeContainer);
            holder.homeContainer = cv.findViewById(R.id.homeContainer);
            holder.awayContainer = cv.findViewById(R.id.awayContainer);
            holder.homeMainText = cv.findViewById(R.id.homeScoreText);
            holder.homeSecondText = cv.findViewById(R.id.homeAssistText);
            holder.awayMainText = cv.findViewById(R.id.awayScoreText);
            holder.awaySecondText = cv.findViewById(R.id.awayAssistText);
            holder.timeText = cv.findViewById(R.id.timeText);
            holder.resultText = cv.findViewById(R.id.scoreText);

            holder.timeContainer.setBackgroundColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_red_dark));

            String playerName;
            // Collect data
            String playerId = penalty.getPlayerId();
            if (playerId != null) {
                Player player = AppRes.getInstance().getPlayers().get(playerId);
                if (player != null) {
                    playerName = player.getNameWithNumber();
                } else {
                    playerName = AppRes.getContext().getString(R.string.removed_player);
                }
            } else {
                playerName = getString(R.string.penalty).toUpperCase();
            }

            // Initialize
            String homePenalty;
            String awayPenalty;

            // Display scorer and assistant OR 'maali'
            if (penalty.isOpponentPenalty()) {
                if (data.getGame().isHomeGame()) {
                    homePenalty = "";
                    awayPenalty = getString(R.string.penalty).toUpperCase();
                } else {
                    homePenalty = getString(R.string.penalty).toUpperCase();
                    awayPenalty = "";
                }
            } else {
                if (data.getGame().isHomeGame()) {
                    homePenalty = playerName;
                    awayPenalty = "";
                } else {
                    homePenalty = "";
                    awayPenalty = playerName;
                }
            }

            holder.homeMainText.setText(homePenalty);
            holder.awayMainText.setText(awayPenalty);
            holder.homeSecondText.setVisibility(View.GONE);
            holder.awaySecondText.setVisibility(View.GONE);

            holder.timeText.setText(StringUtils.getMinSecTimeText(penalty.getTime()));
            holder.resultText.setText(penalty.getLength() + " min");

            boolean isHomePenalty = (!penalty.isOpponentPenalty() && data.getGame().isHomeGame()) || (penalty.isOpponentPenalty() && !data.getGame().isHomeGame());
            if (isHomePenalty) {
                holder.awayContainer.setVisibility(View.GONE);
                holder.homeContainer.setVisibility(View.VISIBLE);
            } else {
                holder.homeContainer.setVisibility(View.GONE);
                holder.awayContainer.setVisibility(View.VISIBLE);
            }

            // Role specific content
            UserConnection.Role role = AppRes.getInstance().getSelectedRole();
            if (role == UserConnection.Role.ADMIN) {
                if (isHomePenalty) {
                    setPenaltyMenuListener(holder.homeContainer, penalty, true);
                } else {
                    setPenaltyMenuListener(holder.awayContainer, penalty, false);
                }
            }

            EventView eventView = new EventView();
            eventView.view = cv;
            eventView.time = penalty.getTime();
            eventViews.add(eventView);
        }

        // SORT AND POPULATE VIEW
        Collections.sort(eventViews, (o1, o2) -> Long.compare(o2.time, o1.time));
        for(EventView eventView : eventViews) {
            eventList.addView(eventView.view);
        }
    }

    private void setGoalMenuListener(RelativeLayout layout, final Goal goal, final boolean isHomeGoal) {
        layout.setOnClickListener(v -> {
            final ActionMenuDialogFragment dialog = ActionMenuDialogFragment.newInstance(null, getString(R.string.remove_goal));
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
            dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
                @Override
                public void onEditItem() {
                    dialog.dismiss();
                    editGoal(goal, isHomeGoal);
                }

                @Override
                public void onRemoveItem() {
                    dialog.dismiss();
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_event_remove_goal_confirmation));
                    dialogFragment.show(getChildFragmentManager(), "Poistetaanko maali?");
                    dialogFragment.setListener(() -> {
                        int homeGoals = data.getGame().getHomeGoals() != null ? data.getGame().getHomeGoals() : 0;
                        int awayGoals = data.getGame().getAwayGoals() != null ? data.getGame().getAwayGoals() : 0;
                        // Remove goal from result only if it equals to marked goals
                        boolean removeFromResult = (isHomeGoal && homeGoals == markedHomeGoals) || (!isHomeGoal && awayGoals == markedAwayGoals);
                        GoalsResourceWrapper.getInstance(data).removeGoal(goal, isHomeGoal, removeFromResult, data -> {
                            GameFragment.this.data = data;
                            update();
                        });
                    });
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
        });
    }

    private void setPenaltyMenuListener(RelativeLayout layout, final Penalty penalty, final boolean isHomePenalty) {
        layout.setOnClickListener(v -> {
            final ActionMenuDialogFragment dialog = ActionMenuDialogFragment.newInstance(null, getString(R.string.remove_penalty));
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
            dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
                @Override
                public void onEditItem() {
                    dialog.dismiss();
                    editPenalty(penalty, isHomePenalty);
                }

                @Override
                public void onRemoveItem() {
                    dialog.dismiss();
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_event_remove_penalty_confirmation));
                    dialogFragment.show(getChildFragmentManager(), "Poistetaanko jäähy?");
                    dialogFragment.setListener(() -> {
                        PenaltiesResource.getInstance().removePenalty(data.getGame().getGameId(), penalty.getPenaltyId(), () -> {
                            data.getPenalties().remove(penalty.getPenaltyId());
                            update();
                        });
                    });
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
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
                holder.penaltyText = cv.findViewById(R.id.penaltyText);

                holder.statsText.setText(getStatsText(playerId));
                holder.plusMinusText.setText(getPlusMinusText(playerId));
                holder.positionText.setText(Player.getPositionText(position, true));
                holder.nameText.setText(Player.getPlayerName(playerId));
                holder.penaltyText.setText(getPenaltyText(playerId));
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
                if (!goal.isOpponentGoal() && Goal.Mode.RL != mode) {
                    stats++;
                }

                // Minus
                if (goal.isOpponentGoal() && Goal.Mode.RL != mode) {
                    stats--;
                }
            }
        }

        return stats > 0 ? "+" + stats : String.valueOf(stats);
    }

    private String getPenaltyText(String playerId) {
        int minutes = 0;
        for (Penalty penalty : data.getPenalties().values()) {
            if (playerId.equals(penalty.getPlayerId())) {
                minutes += penalty.getLength();
            }
        }

        return minutes > 0 ? minutes + " min" : "";
    }

    // PERIODS
    public class EventHolder {
        LinearLayout timeContainer;
        RelativeLayout homeContainer;
        RelativeLayout awayContainer;
        TextView homeMainText;
        TextView homeSecondText;
        TextView awayMainText;
        TextView awaySecondText;
        TextView timeText;
        TextView resultText;
    }

    // PLAYER STATS
    public class PlayerStatsHolder {
        TextView statsText;
        TextView plusMinusText;
        TextView positionText;
        TextView nameText;
        TextView penaltyText;
    }

}
