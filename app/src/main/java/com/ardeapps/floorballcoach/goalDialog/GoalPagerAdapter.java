package com.ardeapps.floorballcoach.goalDialog;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.viewObjects.GoalSelectLineFragmentData;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Arttu on 24.9.2016.
 */
public class GoalPagerAdapter extends FragmentStatePagerAdapter {

    private GoalDetailsFragment detailsFragment;
    private GoalSelectScorerFragment selectScorerFragment;
    private GoalSelectAssistantFragment selectAssistantFragment;
    private GoalSelectLineFragment selectLineFragment;
    private GoalPositionFragment positionFragment;
    private boolean opponentGoal;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Map<Integer, Line> lines = new HashMap<>();
    private Goal goal;
    private String scorerPlayerId;
    private String assistantPlayerId;
    private Integer scorerLineNumber;
    private Integer assistantLineNumber;

    public GoalPagerAdapter(FragmentManager supportFragmentManager, Map<Integer, Line> lines, final boolean opponentGoal) {
        super(supportFragmentManager);
        this.opponentGoal = opponentGoal;
        this.lines = lines;

        detailsFragment = new GoalDetailsFragment();
        selectScorerFragment = new GoalSelectScorerFragment();
        selectAssistantFragment = new GoalSelectAssistantFragment();
        positionFragment = new GoalPositionFragment();
        selectLineFragment = new GoalSelectLineFragment();
        selectScorerFragment.setData(lines);
        selectAssistantFragment.setData(lines);
        GoalSelectLineFragmentData data = new GoalSelectLineFragmentData();
        data.setLines(lines);
        data.setMaxSelectPlayers(6);
        selectLineFragment.setData(data);

        if(opponentGoal) {
            fragments.add(detailsFragment);
            fragments.add(selectLineFragment);
            fragments.add(positionFragment);
        } else {
            fragments.add(detailsFragment);
            fragments.add(selectScorerFragment);
            fragments.add(selectAssistantFragment);
            fragments.add(selectLineFragment);
            fragments.add(positionFragment);

            detailsFragment.setListener(new GoalDetailsFragment.Listener() {
                @Override
                public void onFullRadioButtonChecked() {
                    GoalSelectLineFragmentData data = selectLineFragment.getData();
                    data.setMaxSelectPlayers(6);
                    selectLineFragment.setData(data);
                }

                @Override
                public void onAvRadioButtonChecked() {
                    GoalSelectLineFragmentData data = selectLineFragment.getData();
                    data.setMaxSelectPlayers(opponentGoal ? 6 : 4);
                    selectLineFragment.setData(data);
                }

                @Override
                public void onYvRadioButtonChecked() {
                    GoalSelectLineFragmentData data = selectLineFragment.getData();
                    data.setMaxSelectPlayers(opponentGoal ? 4 : 6);
                    selectLineFragment.setData(data);
                }

                @Override
                public void onRlRadioButtonChecked() {
                    GoalSelectLineFragmentData data = selectLineFragment.getData();
                    data.setMaxSelectPlayers(1);
                    selectLineFragment.setData(data);
                }
            });

            selectScorerFragment.setListener(new PlayerSelector.Listener() {
                @Override
                public void onPlayerSelected(int lineNumber, String playerId) {
                    scorerLineNumber = lineNumber;
                    scorerPlayerId = playerId;
                    selectAssistantFragment.setDisabledPlayerId(playerId);
                    selectAssistantFragment.updateSelection();

                    setSelectLineFragment();
                }

                @Override
                public void onPlayerUnSelected(int lineNumber, String playerId) {
                    scorerLineNumber = null;
                    scorerPlayerId = null;
                    selectAssistantFragment.setDisabledPlayerId(null);
                    selectAssistantFragment.updateSelection();

                    setSelectLineFragment();
                }
            });
            selectAssistantFragment.setListener(new PlayerSelector.Listener() {
                @Override
                public void onPlayerSelected(int lineNumber, String playerId) {
                    assistantLineNumber = lineNumber;
                    assistantPlayerId = playerId;
                    selectScorerFragment.setDisabledPlayerId(playerId);
                    selectScorerFragment.updateSelection();

                    setSelectLineFragment();
                }

                @Override
                public void onPlayerUnSelected(int lineNumber, String playerId) {
                    assistantLineNumber = null;
                    assistantPlayerId = null;
                    selectScorerFragment.setDisabledPlayerId(null);
                    selectScorerFragment.updateSelection();

                    setSelectLineFragment();
                }
            });
        }
    }

    private void setSelectLineFragment() {
        ArrayList<String> disabledPlayers = new ArrayList<>();
        if(scorerPlayerId != null) {
            disabledPlayers.add(scorerPlayerId);
        }
        if(assistantPlayerId != null) {
            disabledPlayers.add(assistantPlayerId);
        }
        selectLineFragment.setDisabledPlayerIds(disabledPlayers);
        selectLineFragment.setSelectedPlayerIds(disabledPlayers);

        if(goal == null) {
            // Scorer and assistan in same line -> select all players in line
            if (scorerLineNumber != null) {
                if (assistantLineNumber == null || scorerLineNumber.equals(assistantLineNumber)) {
                    Line scorerLine = lines.get(scorerLineNumber);
                    if (scorerLine != null && scorerLine.getPlayerIdMap() != null) {
                        ArrayList<String> selectedPlayers = new ArrayList<>(scorerLine.getPlayerIdMap().values());
                        selectLineFragment.setSelectedPlayerIds(selectedPlayers);
                    }
                }
            }
        } else {
            // 1. Remove changed scorer/assistant from current playerIds
            // 2. Add new selected to selected playerIds
            ArrayList<String> selectedPlayers = new ArrayList<>(goal.getPlayerIds());
            Iterator<String> itr = selectedPlayers.iterator();
            while(itr.hasNext()) {
                String selectedPlayerId = itr.next();
                if(disabledPlayers.contains(selectedPlayerId)) {
                    itr.remove();
                }
            }
            selectedPlayers.addAll(disabledPlayers);
            selectLineFragment.setSelectedPlayerIds(selectedPlayers);
        }
        selectLineFragment.updateSelection();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;

        // Initialize dialog
        detailsFragment.setTime(0);
        detailsFragment.setGameMode(Goal.Mode.FULL);
        selectScorerFragment.setScorerPlayerId(null);
        selectAssistantFragment.setAssistantPlayerId(null);
        selectLineFragment.setSelectedPlayerIds(null);
        positionFragment.setPositionPercents(0, 0);

        if(goal != null) {
            // Details
            detailsFragment.setTime(goal.getTime());
            detailsFragment.setGameMode(Goal.Mode.fromDatabaseName(goal.getGameMode()));
            // Scorer
            selectScorerFragment.setScorerPlayerId(goal.getScorerId());
            selectScorerFragment.setDisabledPlayerId(goal.getAssistantId());
            // Assistant
            selectAssistantFragment.setAssistantPlayerId(goal.getAssistantId());
            selectAssistantFragment.setDisabledPlayerId(goal.getScorerId());
            // Select line
            scorerPlayerId = goal.getScorerId();
            assistantPlayerId = goal.getAssistantId();
            selectLineFragment.setSelectedPlayerIds(goal.getPlayerIds());
            ArrayList<String> disabledPlayers = new ArrayList<>();
            if(scorerPlayerId != null) {
                disabledPlayers.add(scorerPlayerId);
            }
            if(assistantPlayerId != null) {
                disabledPlayers.add(assistantPlayerId);
            }
            selectLineFragment.setDisabledPlayerIds(disabledPlayers);
            // Position
            double positionX = goal.getPositionPercentX() != null ? goal.getPositionPercentX() : 0;
            double positionY = goal.getPositionPercentY() != null ? goal.getPositionPercentY() : 0;
            positionFragment.setPositionPercents(positionX, positionY);
        }
    }

    public Goal getGoal() {
        Goal goalToSave = new Goal();
        if(goal != null) {
            goalToSave = goal.clone();
        }
        if(opponentGoal) {
            goalToSave.setTime(detailsFragment.getTime());
            goalToSave.setGameMode(detailsFragment.getGameMode().toDatabaseName());
            goalToSave.setPlayerIds(selectLineFragment.getSelectedPlayerIds());
            goalToSave.setPositionPercentX(positionFragment.getPositionPercentX());
            goalToSave.setPositionPercentY(positionFragment.getPositionPercentY());
        } else {
            goalToSave.setTime(detailsFragment.getTime());
            goalToSave.setGameMode(detailsFragment.getGameMode().toDatabaseName());
            goalToSave.setScorerId(selectScorerFragment.getScorerPlayerId());
            goalToSave.setAssistantId(selectAssistantFragment.getAssistantPlayerId());
            goalToSave.setPlayerIds(selectLineFragment.getSelectedPlayerIds());
            goalToSave.setPositionPercentX(positionFragment.getPositionPercentX());
            goalToSave.setPositionPercentY(positionFragment.getPositionPercentY());
        }


        return goalToSave;
    }

    public boolean validate(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment instanceof GoalDetailsFragment) {
            return ((GoalDetailsFragment) fragment).validate();
        } else if (fragment instanceof GoalSelectScorerFragment) {
            return ((GoalSelectScorerFragment) fragment).validate();
        } else if (fragment instanceof GoalSelectLineFragment) {
            return ((GoalSelectLineFragment) fragment).validate();
        }
        return true;
    }

    @Override
    public int getItemPosition(Object object) {
        if (fragments.contains(object)) {
            return fragments.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
