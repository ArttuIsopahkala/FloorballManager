package com.ardeapps.floorballmanager.eventGoalDialog;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ardeapps.floorballmanager.fragments.SelectPlayerFragment;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.viewObjects.GoalDetailsFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GoalPositionFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GoalSelectLineFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GoalWizardDialogData;
import com.ardeapps.floorballmanager.viewObjects.SelectPlayerFragmentData;
import com.ardeapps.floorballmanager.views.PlayerSelector;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Arttu on 24.9.2016.
 */
public class GoalPagerAdapter extends FragmentStatePagerAdapter {

    private GoalDetailsFragment detailsFragment;
    private SelectPlayerFragment selectScorerFragment;
    private SelectPlayerFragment selectAssistantFragment;

    private GoalSelectLineFragment selectLineFragment;
    private GoalPositionFragment positionFragment;
    private boolean isPenaltyShot;

    private GoalWizardDialogData commonData;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Goal goal;
    private String scorerPlayerId;
    private String assistantPlayerId;
    private Integer scorerLineNumber;
    private Integer assistantLineNumber;

    public GoalPagerAdapter(FragmentManager supportFragmentManager, GoalWizardDialogData commonData) {
        super(supportFragmentManager);
        this.commonData = commonData;

        detailsFragment = new GoalDetailsFragment();
        selectScorerFragment = new SelectPlayerFragment();
        selectAssistantFragment = new SelectPlayerFragment();
        positionFragment = new GoalPositionFragment();
        selectLineFragment = new GoalSelectLineFragment();

        if (commonData.isOpponentGoal()) {
            fragments.add(detailsFragment);
            fragments.add(selectLineFragment);
            fragments.add(positionFragment);
        } else {
            fragments.add(detailsFragment);
            fragments.add(selectScorerFragment);
            fragments.add(selectAssistantFragment);
            fragments.add(selectLineFragment);
            fragments.add(positionFragment);
        }

        setGoalFragmentListeners();
    }

    private void setGoalFragmentListeners() {
        detailsFragment.setListener(gameMode -> {
            if (gameMode == Goal.Mode.FULL) {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(6);
                data.setMinSelectPlayers(3);
                isPenaltyShot = false;
                selectLineFragment.setData(data);
            } else if (gameMode == Goal.Mode.YV) {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(commonData.isOpponentGoal() ? 4 : 6);
                data.setMinSelectPlayers(commonData.isOpponentGoal() ? 3 : 4);
                isPenaltyShot = false;
                selectLineFragment.setData(data);
            } else if (gameMode == Goal.Mode.AV) {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(commonData.isOpponentGoal() ? 6 : 4);
                data.setMinSelectPlayers(commonData.isOpponentGoal() ? 4 : 3);
                isPenaltyShot = false;
                selectLineFragment.setData(data);
            } else if (gameMode == Goal.Mode.RL) {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(0);
                data.setMinSelectPlayers(0);
                isPenaltyShot = true;
                selectLineFragment.setData(data);
            }
        });

        selectScorerFragment.setListener(new PlayerSelector.Listener() {
            @Override
            public void onPlayerSelected(int lineNumber, String playerId) {
                scorerLineNumber = lineNumber;
                scorerPlayerId = playerId;
                SelectPlayerFragmentData data = selectAssistantFragment.getData();
                data.setDisabledPlayerId(playerId);
                selectAssistantFragment.setData(data);
                selectAssistantFragment.updateSelection();

                setSelectLineFragment();
            }

            @Override
            public void onPlayerUnSelected(int lineNumber, String playerId) {
                scorerLineNumber = null;
                scorerPlayerId = null;
                SelectPlayerFragmentData data = selectAssistantFragment.getData();
                data.setDisabledPlayerId(null);
                selectAssistantFragment.setData(data);
                selectAssistantFragment.updateSelection();

                setSelectLineFragment();
            }
        });
        selectAssistantFragment.setListener(new PlayerSelector.Listener() {
            @Override
            public void onPlayerSelected(int lineNumber, String playerId) {
                assistantLineNumber = lineNumber;
                assistantPlayerId = playerId;
                SelectPlayerFragmentData data = selectScorerFragment.getData();
                data.setDisabledPlayerId(playerId);
                selectScorerFragment.setData(data);
                selectScorerFragment.updateSelection();

                setSelectLineFragment();
            }

            @Override
            public void onPlayerUnSelected(int lineNumber, String playerId) {
                assistantLineNumber = null;
                assistantPlayerId = null;
                SelectPlayerFragmentData data = selectScorerFragment.getData();
                data.setDisabledPlayerId(null);
                selectScorerFragment.setData(data);
                selectScorerFragment.updateSelection();

                setSelectLineFragment();
            }
        });
    }

    private void setSelectLineFragment() {
        ArrayList<String> disabledPlayers = new ArrayList<>();
        if (scorerPlayerId != null) {
            disabledPlayers.add(scorerPlayerId);
        }
        if (assistantPlayerId != null) {
            disabledPlayers.add(assistantPlayerId);
        }
        GoalSelectLineFragmentData selectLineFragmentData = selectLineFragment.getData();
        selectLineFragmentData.setSelectedPlayerIds(disabledPlayers);
        selectLineFragmentData.setDisabledPlayerIds(disabledPlayers);

        if (goal == null) {
            // Scorer and assistan in same line -> select all players in line
            Line line = null;
            if (scorerLineNumber != null && assistantLineNumber != null) {
                if (scorerLineNumber.equals(assistantLineNumber)) {
                    line = commonData.getLines().get(scorerLineNumber);
                }
            } else if (scorerLineNumber != null) {
                line = commonData.getLines().get(scorerLineNumber);
            } else if (assistantLineNumber != null) {
                line = commonData.getLines().get(assistantLineNumber);
            }
            if (line != null && line.getPlayerIdMap() != null) {
                ArrayList<String> selectedPlayers = new ArrayList<>(line.getPlayerIdMap().values());
                selectLineFragmentData.setSelectedPlayerIds(selectedPlayers);
            }
        } else {
            // 1. Remove changed scorer/assistant from current playerIds
            // 2. Add new selected to selected playerIds
            ArrayList<String> selectedPlayers = new ArrayList<>(goal.getPlayerIds());
            Iterator<String> itr = selectedPlayers.iterator();
            while (itr.hasNext()) {
                String selectedPlayerId = itr.next();
                if (disabledPlayers.contains(selectedPlayerId)) {
                    itr.remove();
                }
            }
            selectedPlayers.addAll(disabledPlayers);
            selectLineFragmentData.setSelectedPlayerIds(selectedPlayers);
        }

        selectLineFragment.setData(selectLineFragmentData);
        selectLineFragment.updateSelection();
    }

    public Goal getGoal() {
        GoalDetailsFragmentData detailsFragmentData = detailsFragment.getData();
        GoalSelectLineFragmentData selectLineFragmentData = selectLineFragment.getData();
        GoalPositionFragmentData positionFragmentData = positionFragment.getData();

        Goal goalToSave = new Goal();
        if (goal != null) {
            goalToSave = goal.clone();
        }
        if (commonData.isOpponentGoal()) {
            if (isPenaltyShot) {
                goalToSave.setPlayerIds(null);
            } else {
                goalToSave.setPlayerIds(selectLineFragmentData.getSelectedPlayerIds());
            }
            goalToSave.setTime(detailsFragmentData.getTime());
            goalToSave.setGameMode(detailsFragmentData.getGameMode().toDatabaseName());
            goalToSave.setPositionPercentX(positionFragmentData.getPositionPercentX());
            goalToSave.setPositionPercentY(positionFragmentData.getPositionPercentY());
        } else {
            SelectPlayerFragmentData scorerFragmentData = selectScorerFragment.getData();
            SelectPlayerFragmentData assistantFragmentData = selectAssistantFragment.getData();
            if (isPenaltyShot) {
                goalToSave.setAssistantId(null);
                goalToSave.setPlayerIds(null);
            } else {
                goalToSave.setAssistantId(assistantFragmentData.getPlayerId());
                goalToSave.setPlayerIds(selectLineFragmentData.getSelectedPlayerIds());
            }
            goalToSave.setTime(detailsFragmentData.getTime());
            goalToSave.setGameMode(detailsFragmentData.getGameMode().toDatabaseName());
            goalToSave.setScorerId(scorerFragmentData.getPlayerId());
            goalToSave.setPositionPercentX(positionFragmentData.getPositionPercentX());
            goalToSave.setPositionPercentY(positionFragmentData.getPositionPercentY());
        }

        return goalToSave;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;

        // Initialize dialog
        isPenaltyShot = false;

        // Details
        GoalDetailsFragmentData detailsFragmentData = new GoalDetailsFragmentData();
        detailsFragmentData.setTime(0);
        detailsFragmentData.setGameMode(Goal.Mode.FULL);
        detailsFragmentData.setGoals(commonData.getGoals());
        detailsFragmentData.setCurrentGoalId(null);
        // Scorer
        SelectPlayerFragmentData scorerFragmentData = new SelectPlayerFragmentData();
        scorerFragmentData.setLines(commonData.getLines());
        scorerFragmentData.setPlayerId(null);
        scorerFragmentData.setDisabledPlayerId(null);
        // Assistant
        SelectPlayerFragmentData assistantFragmentData = new SelectPlayerFragmentData();
        assistantFragmentData.setLines(commonData.getLines());
        assistantFragmentData.setPlayerId(null);
        assistantFragmentData.setDisabledPlayerId(null);
        // Select line
        GoalSelectLineFragmentData selectLineFragmentData = new GoalSelectLineFragmentData();
        selectLineFragmentData.setLines(commonData.getLines());
        selectLineFragmentData.setMaxSelectPlayers(6);
        selectLineFragmentData.setMinSelectPlayers(3);
        selectLineFragmentData.setSelectedPlayerIds(null);
        selectLineFragmentData.setDisabledPlayerIds(null);
        // Position
        GoalPositionFragmentData positionFragmentData = new GoalPositionFragmentData();
        positionFragmentData.setOpponentName(commonData.getGame().getOpponentName());
        positionFragmentData.setOpponentGoal(commonData.isOpponentGoal());
        positionFragmentData.setPositionPercentX(null);
        positionFragmentData.setPositionPercentY(null);

        if (goal != null) {
            // Details
            detailsFragmentData.setTime(goal.getTime());
            detailsFragmentData.setGameMode(Goal.Mode.fromDatabaseName(goal.getGameMode()));
            detailsFragmentData.setCurrentGoalId(goal.getGoalId());
            // Scorer
            scorerFragmentData.setPlayerId(goal.getScorerId());
            scorerFragmentData.setDisabledPlayerId(goal.getAssistantId());
            // Assistant
            assistantFragmentData.setPlayerId(goal.getAssistantId());
            assistantFragmentData.setDisabledPlayerId(goal.getScorerId());
            // Select line
            scorerPlayerId = goal.getScorerId();
            assistantPlayerId = goal.getAssistantId();
            ArrayList<String> disabledPlayers = new ArrayList<>();
            if (scorerPlayerId != null) {
                disabledPlayers.add(scorerPlayerId);
            }
            if (assistantPlayerId != null) {
                disabledPlayers.add(assistantPlayerId);
            }
            selectLineFragmentData.setSelectedPlayerIds(goal.getPlayerIds());
            selectLineFragmentData.setDisabledPlayerIds(disabledPlayers);
            // Position
            positionFragmentData.setPositionPercentX(goal.getPositionPercentX());
            positionFragmentData.setPositionPercentY(goal.getPositionPercentY());

        }

        // Set data to fragments
        detailsFragment.setData(detailsFragmentData);
        selectScorerFragment.setData(scorerFragmentData);
        selectAssistantFragment.setData(assistantFragmentData);
        selectLineFragment.setData(selectLineFragmentData);
        positionFragment.setData(positionFragmentData);
    }

    public boolean validate(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment instanceof GoalDetailsFragment) {
            return ((GoalDetailsFragment) fragment).validate();
        } else if (fragment instanceof GoalSelectLineFragment) {
            return ((GoalSelectLineFragment) fragment).validate();
        }
        return true;
    }

    public boolean isPenaltyShot() {
        return isPenaltyShot;
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
