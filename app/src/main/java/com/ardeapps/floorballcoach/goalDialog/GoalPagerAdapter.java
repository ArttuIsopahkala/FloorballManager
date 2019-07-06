package com.ardeapps.floorballcoach.goalDialog;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.viewObjects.GoalDetailsFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GoalPositionFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GoalSelectLineFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GoalSelectPlayerFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GoalWizardDialogData;
import com.ardeapps.floorballcoach.views.PlayerSelector;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Arttu on 24.9.2016.
 */
public class GoalPagerAdapter extends FragmentStatePagerAdapter {

    private GoalDetailsFragment detailsFragment;
    private GoalSelectPlayerFragment selectScorerFragment;
    private GoalSelectPlayerFragment selectAssistantFragment;

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
        selectScorerFragment = new GoalSelectPlayerFragment();
        selectAssistantFragment = new GoalSelectPlayerFragment();
        positionFragment = new GoalPositionFragment();
        selectLineFragment = new GoalSelectLineFragment();

        if(commonData.isOpponentGoal()) {
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
        detailsFragment.setListener(new GoalDetailsFragment.Listener() {
            @Override
            public void onFullRadioButtonChecked() {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(6);
                isPenaltyShot = false;
                selectLineFragment.setData(data);
            }

            @Override
            public void onAvRadioButtonChecked() {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(commonData.isOpponentGoal() ? 6 : 4);
                isPenaltyShot = false;
                selectLineFragment.setData(data);
            }

            @Override
            public void onYvRadioButtonChecked() {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(commonData.isOpponentGoal() ? 4 : 6);
                isPenaltyShot = false;
                selectLineFragment.setData(data);
            }

            @Override
            public void onRlRadioButtonChecked() {
                GoalSelectLineFragmentData data = selectLineFragment.getData();
                data.setMaxSelectPlayers(1);
                isPenaltyShot = true;
                selectLineFragment.setData(data);
            }
        });

        selectScorerFragment.setListener(new PlayerSelector.Listener() {
            @Override
            public void onPlayerSelected(int lineNumber, String playerId) {
                scorerLineNumber = lineNumber;
                scorerPlayerId = playerId;
                GoalSelectPlayerFragmentData data = selectAssistantFragment.getData();
                data.setDisabledPlayerId(playerId);
                selectAssistantFragment.setData(data);
                selectAssistantFragment.updateSelection();

                setSelectLineFragment();
            }

            @Override
            public void onPlayerUnSelected(int lineNumber, String playerId) {
                scorerLineNumber = null;
                scorerPlayerId = null;
                GoalSelectPlayerFragmentData data = selectAssistantFragment.getData();
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
                GoalSelectPlayerFragmentData data = selectScorerFragment.getData();
                data.setDisabledPlayerId(playerId);
                selectScorerFragment.setData(data);
                selectScorerFragment.updateSelection();

                setSelectLineFragment();
            }

            @Override
            public void onPlayerUnSelected(int lineNumber, String playerId) {
                assistantLineNumber = null;
                assistantPlayerId = null;
                GoalSelectPlayerFragmentData data = selectScorerFragment.getData();
                data.setDisabledPlayerId(null);
                selectScorerFragment.setData(data);
                selectScorerFragment.updateSelection();

                setSelectLineFragment();
            }
        });
    }

    private void setSelectLineFragment() {
        ArrayList<String> disabledPlayers = new ArrayList<>();
        if(scorerPlayerId != null) {
            disabledPlayers.add(scorerPlayerId);
        }
        if(assistantPlayerId != null) {
            disabledPlayers.add(assistantPlayerId);
        }
        GoalSelectLineFragmentData selectLineFragmentData = selectLineFragment.getData();
        selectLineFragmentData.setSelectedPlayerIds(disabledPlayers);
        selectLineFragmentData.setDisabledPlayerIds(disabledPlayers);

        if(goal == null) {
            // Scorer and assistan in same line -> select all players in line
            if (scorerLineNumber != null) {
                if (assistantLineNumber == null || scorerLineNumber.equals(assistantLineNumber)) {
                    Line scorerLine = commonData.getLines().get(scorerLineNumber);
                    if (scorerLine != null && scorerLine.getPlayerIdMap() != null) {
                        ArrayList<String> selectedPlayers = new ArrayList<>(scorerLine.getPlayerIdMap().values());
                        selectLineFragmentData.setSelectedPlayerIds(selectedPlayers);
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
            selectLineFragmentData.setSelectedPlayerIds(selectedPlayers);
        }

        selectLineFragment.setData(selectLineFragmentData);
        selectLineFragment.updateSelection();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;

        // Initialize dialog
        isPenaltyShot = false;

        // Details
        GoalDetailsFragmentData detailsFragmentData = new GoalDetailsFragmentData();
        detailsFragmentData.setTime(0);
        detailsFragmentData.setGameMode(Goal.Mode.FULL);
        // Scorer
        GoalSelectPlayerFragmentData scorerFragmentData = new GoalSelectPlayerFragmentData();
        scorerFragmentData.setLines(commonData.getLines());
        scorerFragmentData.setPlayerId(null);
        scorerFragmentData.setDisabledPlayerId(null);
        // Assistant
        GoalSelectPlayerFragmentData assistantFragmentData = new GoalSelectPlayerFragmentData();
        assistantFragmentData.setLines(commonData.getLines());
        assistantFragmentData.setPlayerId(null);
        assistantFragmentData.setDisabledPlayerId(null);
        // Select line
        GoalSelectLineFragmentData selectLineFragmentData = new GoalSelectLineFragmentData();
        selectLineFragmentData.setLines(commonData.getLines());
        selectLineFragmentData.setMaxSelectPlayers(6);
        selectLineFragmentData.setSelectedPlayerIds(null);
        selectLineFragmentData.setDisabledPlayerIds(null);
        // Position
        GoalPositionFragmentData positionFragmentData = new GoalPositionFragmentData();
        positionFragmentData.setOpponentName(commonData.getGame().getOpponentName());
        positionFragmentData.setPositionPercentX(null);
        positionFragmentData.setPositionPercentY(null);

        if(goal != null) {
            // Details
            detailsFragmentData.setTime(goal.getTime());
            detailsFragmentData.setGameMode(Goal.Mode.fromDatabaseName(goal.getGameMode()));
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
            if(scorerPlayerId != null) {
                disabledPlayers.add(scorerPlayerId);
            }
            if(assistantPlayerId != null) {
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

    public Goal getGoal() {
        GoalDetailsFragmentData detailsFragmentData = detailsFragment.getData();
        GoalSelectLineFragmentData selectLineFragmentData = selectLineFragment.getData();
        GoalPositionFragmentData positionFragmentData = positionFragment.getData();

        Goal goalToSave = new Goal();
        if(goal != null) {
            goalToSave = goal.clone();
        }
        if(commonData.isOpponentGoal()) {
            if(isPenaltyShot) {
                goalToSave.setPlayerIds(null);
            } else {
                goalToSave.setPlayerIds(selectLineFragmentData.getSelectedPlayerIds());
            }
            goalToSave.setTime(detailsFragmentData.getTime());
            goalToSave.setGameMode(detailsFragmentData.getGameMode().toDatabaseName());
            goalToSave.setPositionPercentX(positionFragmentData.getPositionPercentX());
            goalToSave.setPositionPercentY(positionFragmentData.getPositionPercentY());
        } else {
            GoalSelectPlayerFragmentData scorerFragmentData = selectScorerFragment.getData();
            GoalSelectPlayerFragmentData assistantFragmentData = selectAssistantFragment.getData();
            if(isPenaltyShot) {
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

    public boolean validate(int position) {
        int scorerFragmentPosition = 1;
        Fragment fragment = fragments.get(position);
        if (fragment instanceof GoalDetailsFragment) {
            return ((GoalDetailsFragment) fragment).validate();
        } else if (fragment instanceof GoalSelectLineFragment) {
            return ((GoalSelectLineFragment) fragment).validate();
        } else if (!commonData.isOpponentGoal() && position == scorerFragmentPosition) {
            return ((GoalSelectPlayerFragment) fragment).validate();
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
