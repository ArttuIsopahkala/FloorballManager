package com.ardeapps.floorballmanager.resources;

import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.GameFragmentData;

/**
 * Created by Arttu on 25.6.2019.
 */

public class GoalsResourceWrapper extends FirebaseDatabaseService {
    private static GoalsResourceWrapper instance;
    private static GameFragmentData fragmentData;

    public static GoalsResourceWrapper getInstance(GameFragmentData data) {
        if (instance == null) {
            instance = new GoalsResourceWrapper();
        }
        fragmentData = data;
        return instance;
    }

    public void editGoal(final Goal oldGoal, final Goal goalToSave, final boolean opponentGoal, boolean addToResult, final EditGoalListener handler) {
        // Change team stats
        editTeamStatsGoal(oldGoal, goalToSave, () -> {
            if(addToResult) {
                // Change game goals
                addGoalToGameIfNeeded(oldGoal, goalToSave, () -> {
                    if (opponentGoal) {
                        handler.onGoalEdited(fragmentData);
                        //editOpponentStatsGoal(oldGoal, goalToSave, () -> handler.onGoalEdited(fragmentData));
                    } else {
                        // TODO tallenna pelaajan maalit eri tavalla
                        boolean scorerAdded = !StringUtils.isEmptyString(goalToSave.getScorerId());
                        boolean assistantAdded = !StringUtils.isEmptyString(goalToSave.getAssistantId());

                        if (scorerAdded && assistantAdded) {
                            editScorerStatsGoal(oldGoal, goalToSave, ()
                                    -> editAssistantStatsGoal(oldGoal, goalToSave, ()
                                    -> handler.onGoalEdited(fragmentData)));
                        } else if (scorerAdded) {
                            editScorerStatsGoal(oldGoal, goalToSave, ()
                                    -> handler.onGoalEdited(fragmentData));
                        } else if (assistantAdded) {
                            editAssistantStatsGoal(oldGoal, goalToSave, ()
                                    -> handler.onGoalEdited(fragmentData));
                        } else {
                            handler.onGoalEdited(fragmentData);
                        }
                    }
                });
            } else {
                if (opponentGoal) {
                    handler.onGoalEdited(fragmentData);
                    //editOpponentStatsGoal(oldGoal, goalToSave, () -> handler.onGoalEdited(fragmentData));
                } else {
                    // TODO tallenna pelaajan maalit eri tavalla
                    boolean scorerAdded = !StringUtils.isEmptyString(goalToSave.getScorerId());
                    boolean assistantAdded = !StringUtils.isEmptyString(goalToSave.getAssistantId());

                    if (scorerAdded && assistantAdded) {
                        editScorerStatsGoal(oldGoal, goalToSave, ()
                                -> editAssistantStatsGoal(oldGoal, goalToSave, ()
                                -> handler.onGoalEdited(fragmentData)));
                    } else if (scorerAdded) {
                        editScorerStatsGoal(oldGoal, goalToSave, ()
                                -> handler.onGoalEdited(fragmentData));
                    } else if (assistantAdded) {
                        editAssistantStatsGoal(oldGoal, goalToSave, ()
                                -> handler.onGoalEdited(fragmentData));
                    } else {
                        handler.onGoalEdited(fragmentData);
                    }
                }
            }
        });
    }

    private void editTeamStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if (oldGoal == null) {
            GoalsResource.getInstance().addGoal(goalToSave, id -> {
                goalToSave.setGoalId(id);
                fragmentData.getGoals().put(goalToSave.getGoalId(), goalToSave);
                listener.editCompleted();
            });
        } else {
            GoalsResource.getInstance().editGoal(goalToSave, () -> {
                fragmentData.getGoals().put(goalToSave.getGoalId(), goalToSave);
                listener.editCompleted();
            });
        }
    }

    private void addGoalToGameIfNeeded(Goal oldGoal, Goal goalToSave, final EditGoalStatsListener listener) {
        if (oldGoal == null) {
            // Add goal to game
            Integer homeGoals = fragmentData.getGame().getHomeGoals();
            Integer awayGoals = fragmentData.getGame().getAwayGoals();
            if (homeGoals == null) {
                homeGoals = 0;
            }
            if (awayGoals == null) {
                awayGoals = 0;
            }

            boolean isHomeGoal = (!goalToSave.isOpponentGoal() && fragmentData.getGame().isHomeGame()) || (goalToSave.isOpponentGoal() && !fragmentData.getGame().isHomeGame());
            if (isHomeGoal) {
                homeGoals++;
            } else {
                awayGoals++;
            }
            fragmentData.getGame().setHomeGoals(homeGoals);
            fragmentData.getGame().setAwayGoals(awayGoals);
            GamesResource.getInstance().editGame(fragmentData.getGame(), listener::editCompleted);
        } else {
            listener.editCompleted();
        }
    }

    private void editOpponentStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if (oldGoal != null && !oldGoal.getPlayerIds().isEmpty() && !oldGoal.getPlayerIds().containsAll(goalToSave.getPlayerIds())) {
            // Players changed -> Remove goal from existing players stats and add to new ones
            PlayerStatsResource.getInstance().removeStats(oldGoal.getPlayerIds(), oldGoal.getGameId(), oldGoal.getGoalId(), ()
                    -> PlayerStatsResource.getInstance().editStats(goalToSave.getPlayerIds(), goalToSave, listener::editCompleted));
        } else {
            // Edit always
            PlayerStatsResource.getInstance().editStats(goalToSave.getPlayerIds(), goalToSave, listener::editCompleted);
        }
    }

    private void editScorerStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if (oldGoal != null && !StringUtils.isEmptyString(oldGoal.getScorerId()) && !goalToSave.getScorerId().equals(oldGoal.getScorerId())) {
            // Scorer changed -> Remove goal from existing player stats and add to new
            PlayerStatsResource.getInstance().removeStat(oldGoal.getScorerId(), oldGoal.getGameId(), oldGoal.getGoalId(), ()
                    -> PlayerStatsResource.getInstance().editStat(goalToSave.getScorerId(), goalToSave, listener::editCompleted));
        } else {
            // Edit always
            PlayerStatsResource.getInstance().editStat(goalToSave.getScorerId(), goalToSave, listener::editCompleted);
        }
    }

    private void editAssistantStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if (oldGoal != null && !StringUtils.isEmptyString(oldGoal.getAssistantId()) && !goalToSave.getAssistantId().equals(oldGoal.getAssistantId())) {
            PlayerStatsResource.getInstance().removeStat(oldGoal.getAssistantId(), oldGoal.getGameId(), oldGoal.getGoalId(), ()
                    -> PlayerStatsResource.getInstance().editStat(goalToSave.getAssistantId(), goalToSave, listener::editCompleted));
        } else {
            PlayerStatsResource.getInstance().editStat(goalToSave.getAssistantId(), goalToSave, listener::editCompleted);
        }
    }

    public void removeGoal(final Goal goal, final boolean isHomeGoal, boolean removeFromResult, final RemoveGoalListener handler) {
        removeTeamStatsGoal(goal, () -> {
            if(removeFromResult) {
                removeGoalFromGame(isHomeGoal, () -> {
                    final boolean opponentGoal = (fragmentData.getGame().isHomeGame() && !isHomeGoal) || (!fragmentData.getGame().isHomeGame() && isHomeGoal);
                    if (opponentGoal) {
                        handler.onGoalRemoved(fragmentData);
                        //removeOpponentStatsGoal(goal, () -> handler.onGoalRemoved(fragmentData));
                    } else {
                        // TODO tallenna pelaajan maalit eri tavalla
                        boolean scorerAdded = !StringUtils.isEmptyString(goal.getScorerId());
                        boolean assistantAdded = !StringUtils.isEmptyString(goal.getAssistantId());

                        if (scorerAdded && assistantAdded) {
                            removeScorerStatsGoal(goal, () -> removeAssistantStatsGoal(goal, ()
                                    -> handler.onGoalRemoved(fragmentData)));
                        } else if (scorerAdded) {
                            removeScorerStatsGoal(goal, () -> handler.onGoalRemoved(fragmentData));
                        } else if (assistantAdded) {
                            removeAssistantStatsGoal(goal, () -> handler.onGoalRemoved(fragmentData));
                        } else {
                            handler.onGoalRemoved(fragmentData);
                        }
                    }
                });
            } else {
                final boolean opponentGoal = (fragmentData.getGame().isHomeGame() && !isHomeGoal) || (!fragmentData.getGame().isHomeGame() && isHomeGoal);
                if (opponentGoal) {
                    handler.onGoalRemoved(fragmentData);
                    //removeOpponentStatsGoal(goal, () -> handler.onGoalRemoved(fragmentData));
                } else {
                    // TODO tallenna pelaajan maalit eri tavalla
                    boolean scorerAdded = !StringUtils.isEmptyString(goal.getScorerId());
                    boolean assistantAdded = !StringUtils.isEmptyString(goal.getAssistantId());

                    if (scorerAdded && assistantAdded) {
                        removeScorerStatsGoal(goal, () -> removeAssistantStatsGoal(goal, ()
                                -> handler.onGoalRemoved(fragmentData)));
                    } else if (scorerAdded) {
                        removeScorerStatsGoal(goal, () -> handler.onGoalRemoved(fragmentData));
                    } else if (assistantAdded) {
                        removeAssistantStatsGoal(goal, () -> handler.onGoalRemoved(fragmentData));
                    } else {
                        handler.onGoalRemoved(fragmentData);
                    }
                }
            }
        });
    }

    private void removeTeamStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        GoalsResource.getInstance().removeGoal(goal.getGameId(), goal.getGoalId(), () -> {
            fragmentData.getGoals().remove(goal.getGoalId());
            listener.removeCompleted();
        });
    }

    private void removeGoalFromGame(final boolean isHomeGoal, final RemoveGoalStatsListener listener) {
        Integer homeGoals = fragmentData.getGame().getHomeGoals();
        Integer awayGoals = fragmentData.getGame().getAwayGoals();
        if (homeGoals == null) {
            homeGoals = 0;
        }
        if (awayGoals == null) {
            awayGoals = 0;
        }

        if (isHomeGoal) {
            if (homeGoals > 0) {
                homeGoals--;
            }
        } else {
            if (awayGoals > 0) {
                awayGoals--;
            }
        }
        fragmentData.getGame().setHomeGoals(homeGoals);
        fragmentData.getGame().setAwayGoals(awayGoals);
        GamesResource.getInstance().editGame(fragmentData.getGame(), listener::removeCompleted);
    }

    private void removeOpponentStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        PlayerStatsResource.getInstance().removeStats(goal.getPlayerIds(), goal.getGameId(), goal.getGoalId(), listener::removeCompleted);
    }

    private void removeScorerStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        PlayerStatsResource.getInstance().removeStat(goal.getScorerId(), goal.getGameId(), goal.getGoalId(), listener::removeCompleted);
    }

    private void removeAssistantStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        PlayerStatsResource.getInstance().removeStat(goal.getAssistantId(), goal.getGameId(), goal.getGoalId(), listener::removeCompleted);
    }

    // EDIT GOAL
    public interface EditGoalListener {
        void onGoalEdited(GameFragmentData data);
    }

    private interface EditGoalStatsListener {
        void editCompleted();
    }

    // REMOVE GOAL
    public interface RemoveGoalListener {
        void onGoalRemoved(GameFragmentData data);
    }

    private interface RemoveGoalStatsListener {
        void removeCompleted();
    }
}
