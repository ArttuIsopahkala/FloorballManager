package com.ardeapps.floorballcoach.resources;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.GameFragmentData;

/**
 * Created by Arttu on 25.6.2019.
 */

public class GoalResourcesWrapper extends FirebaseDatabaseService {
    private static GoalResourcesWrapper instance;
    private static GameFragmentData fragmentData;

    public static GoalResourcesWrapper getInstance(GameFragmentData data) {
        if (instance == null) {
            instance = new GoalResourcesWrapper();
        }
        fragmentData = data;
        return instance;
    }

    // EDIT GOAL
    public interface EditGoalListener {
        void onGoalEdited(GameFragmentData data);
    }

    public void editGoal(final Goal oldGoal, final Goal goalToSave, final boolean opponentGoal, final EditGoalListener handler) {
        // Change team stats
        editTeamStatsGoal(oldGoal, goalToSave, new EditGoalStatsListener() {
            @Override
            public void editCompleted() {
                // Change game goals
                addGoalToGameIfNeeded(oldGoal, goalToSave, new EditGoalStatsListener() {
                    @Override
                    public void editCompleted() {
                        if(opponentGoal) {
                            handler.onGoalEdited(fragmentData);
                            return;
                        }

                        // Change scorer stats
                        editScorerStatsGoal(oldGoal, goalToSave, new EditGoalStatsListener() {
                            @Override
                            public void editCompleted() {
                                if(StringUtils.isEmptyString(goalToSave.getAssistantId())) {
                                    handler.onGoalEdited(fragmentData);
                                    return;
                                }

                                // Change assistant stats
                                editAssistantStatsGoal(oldGoal, goalToSave, new EditGoalStatsListener() {
                                    @Override
                                    public void editCompleted() {
                                        handler.onGoalEdited(fragmentData);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private interface EditGoalStatsListener {
        void editCompleted();
    }

    private void editTeamStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if(oldGoal == null) {
            GoalsByTeamResource.getInstance().addGoal(goalToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                @Override
                public void onAddDataSuccess(String id) {
                    goalToSave.setGoalId(id);
                    fragmentData.getGoals().put(goalToSave.getGoalId(), goalToSave);
                    listener.editCompleted();
                }
            });
        } else {
            GoalsByTeamResource.getInstance().editGoal(goalToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    fragmentData.getGoals().put(goalToSave.getGoalId(), goalToSave);
                    listener.editCompleted();
                }
            });
        }
    }

    private void addGoalToGameIfNeeded(Goal oldGoal, Goal goalToSave, final EditGoalStatsListener listener) {
        if(oldGoal == null) {
            // Add goal to game
            Integer homeGoals = fragmentData.getGame().getHomeGoals();
            Integer awayGoals = fragmentData.getGame().getAwayGoals();
            if(homeGoals == null) {
                homeGoals = 0;
            }
            if(awayGoals == null) {
                awayGoals = 0;
            }

            boolean isHomeGoal = (!goalToSave.isOpponentGoal() && fragmentData.getGame().isHomeGame()) || (goalToSave.isOpponentGoal() && !fragmentData.getGame().isHomeGame());
            if(isHomeGoal) {
                homeGoals++;
            } else {
                awayGoals++;
            }
            fragmentData.getGame().setHomeGoals(homeGoals);
            fragmentData.getGame().setAwayGoals(awayGoals);
            GamesResource.getInstance().editGame(fragmentData.getGame(), new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    listener.editCompleted();
                }
            });
        } else {
            listener.editCompleted();
        }
    }

    private void editScorerStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if(oldGoal != null && !oldGoal.getScorerId().equals(goalToSave.getScorerId())) {
            // Scorer changed -> Remove goal from existing player stats and add to new
            StatsByPlayerResource.getInstance().removeStat(oldGoal.getScorerId(), oldGoal.getGameId(), oldGoal.getGoalId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                @Override
                public void onDeleteDataSuccess() {
                    StatsByPlayerResource.getInstance().editStat(goalToSave.getScorerId(), goalToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                        @Override
                        public void onEditDataSuccess() {
                            listener.editCompleted();
                        }
                    });
                }
            });
        } else {
            // Edit always
            StatsByPlayerResource.getInstance().editStat(goalToSave.getScorerId(), goalToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    listener.editCompleted();
                }
            });
        }
    }

    private void editAssistantStatsGoal(Goal oldGoal, final Goal goalToSave, final EditGoalStatsListener listener) {
        if(oldGoal != null && !StringUtils.isEmptyString(oldGoal.getAssistantId()) && oldGoal.getAssistantId().equals(goalToSave.getAssistantId())) {
            StatsByPlayerResource.getInstance().removeStat(oldGoal.getAssistantId(), oldGoal.getGameId(), oldGoal.getGoalId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                @Override
                public void onDeleteDataSuccess() {
                    StatsByPlayerResource.getInstance().editStat(goalToSave.getAssistantId(), goalToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                        @Override
                        public void onEditDataSuccess() {
                            listener.editCompleted();
                        }
                    });
                }
            });
        } else {
            StatsByPlayerResource.getInstance().editStat(goalToSave.getAssistantId(), goalToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    listener.editCompleted();
                }
            });
        }
    }

    // REMOVE GOAL
    public interface RemoveGoalListener {
        void onGoalRemoved(GameFragmentData data);
    }

    public void removeGoal(final Goal goal, final boolean isHomeGoal, final RemoveGoalListener handler) {
        removeTeamStatsGoal(goal, new RemoveGoalStatsListener() {
            @Override
            public void removeCompleted() {
                removeGoalFromGame(isHomeGoal, new RemoveGoalStatsListener() {
                    @Override
                    public void removeCompleted() {
                        final boolean opponentGoal = (fragmentData.getGame().isHomeGame() && !isHomeGoal) || (!fragmentData.getGame().isHomeGame() && isHomeGoal);
                        if(opponentGoal) {
                            handler.onGoalRemoved(fragmentData);
                            return;
                        }

                        // Change scorer stats
                        removeScorerStatsGoal(goal, new RemoveGoalStatsListener() {
                            @Override
                            public void removeCompleted() {
                                if(StringUtils.isEmptyString(goal.getAssistantId())) {
                                    handler.onGoalRemoved(fragmentData);
                                    return;
                                }

                                // Change assistant stats
                                removeAssistantStatsGoal(goal, new RemoveGoalStatsListener() {
                                    @Override
                                    public void removeCompleted() {
                                        handler.onGoalRemoved(fragmentData);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private interface RemoveGoalStatsListener {
        void removeCompleted();
    }

    private void removeTeamStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        GoalsByTeamResource.getInstance().removeGoal(goal.getGameId(), goal.getGoalId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
            @Override
            public void onDeleteDataSuccess() {
                fragmentData.getGoals().remove(goal.getGoalId());
                listener.removeCompleted();
            }
        });
    }

    private void removeGoalFromGame(final boolean isHomeGoal, final RemoveGoalStatsListener listener) {
        Integer homeGoals = fragmentData.getGame().getHomeGoals();
        Integer awayGoals = fragmentData.getGame().getAwayGoals();
        if(homeGoals == null) {
            homeGoals = 0;
        }
        if(awayGoals == null) {
            awayGoals = 0;
        }

        if(isHomeGoal) {
            if(homeGoals > 0) {
                homeGoals--;
            }
        } else {
            if(awayGoals > 0) {
                awayGoals--;
            }
        }
        fragmentData.getGame().setHomeGoals(homeGoals);
        fragmentData.getGame().setAwayGoals(awayGoals);
        GamesResource.getInstance().editGame(fragmentData.getGame(), new FirebaseDatabaseService.EditDataSuccessListener() {
            @Override
            public void onEditDataSuccess() {
                listener.removeCompleted();
            }
        });
    }

    private void removeScorerStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        StatsByPlayerResource.getInstance().removeStat(goal.getScorerId(), goal.getGameId(), goal.getGoalId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
            @Override
            public void onDeleteDataSuccess() {
                listener.removeCompleted();
            }
        });
    }

    private void removeAssistantStatsGoal(final Goal goal, final RemoveGoalStatsListener listener) {
        StatsByPlayerResource.getInstance().removeStat(goal.getAssistantId(), goal.getGameId(), goal.getGoalId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
            @Override
            public void onDeleteDataSuccess() {
                listener.removeCompleted();
            }
        });
    }
}
