package com.ardeapps.floorballcoach.goalDialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalWizardFragmentData;

public class GoalWizardFragment extends DialogFragment implements DataView {

    GoalWizardListener mListener = null;

    public void setListener(GoalWizardListener l) {
        mListener = l;
    }

    public interface GoalWizardListener {
        void onGoalSaved(Goal goal);
    }

    TextView infoText;
    Button previousButton;
    Button nextButton;
    TabLayout tabLayout;
    ViewPager eventPager;
    GoalPagerAdapter goalAdapter;

    GoalWizardFragmentData data;

    int position = 0;

    @Override
    public void setData(Object viewData) {
        data = (GoalWizardFragmentData) viewData;
    }

    @Override
    public GoalWizardFragmentData getData() {
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        infoText = v.findViewById(R.id.infoText);
        previousButton = v.findViewById(R.id.previousButton);
        nextButton = v.findViewById(R.id.nextButton);
        eventPager = v.findViewById(R.id.eventPager);
        tabLayout = v.findViewById(R.id.tabLayout);

        goalAdapter = new GoalPagerAdapter(getChildFragmentManager(), data.getLines(), data.isOpponentGoal());
        goalAdapter.setGoal(data.getGoal());

        eventPager.setOffscreenPageLimit(goalAdapter.getCount());
        eventPager.setAdapter(goalAdapter);
        tabLayout.setupWithViewPager(eventPager);
        // Disable tab title clicks
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.setEnabled(false);
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }

        position = 0;
        changePage(position);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePreviousClick();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNextClick();
            }
        });
        return v;
    }

    private void handlePreviousClick() {
        if(position == 0) {
            dismiss();
        } else {
            position--;
            changePage(position);
        }
    }

    private void handleNextClick() {
        int max = goalAdapter.getCount() - 1;
        boolean isValid = goalAdapter.validate(position);
        if(isValid) {
            if(position == max) {
                // TODO tallenna goal
                Goal goalToSave = goalAdapter.getGoal();
                goalToSave.setGameId(data.getGame().getGameId());
                goalToSave.setOpponentGoal(data.isOpponentGoal());

                Logger.log("goal getGoalId: " + goalToSave.getGoalId());
                Logger.log("goal getGameId: " + goalToSave.getGameId());
                Logger.log("goal getTime: " + goalToSave.getTime());
                Logger.log("goal getScorerId: " + goalToSave.getScorerId());
                Logger.log("goal getAssistantId: " + goalToSave.getAssistantId());
                Logger.log("goal getGameMode: " + goalToSave.getGameMode());
                Logger.log("goal getPositionPercentX: " + goalToSave.getPositionPercentX());
                Logger.log("goal getPositionPercentY: " + goalToSave.getPositionPercentY());
                Logger.log("goal getPlayerIds: " + goalToSave.getPlayerIds());
                Logger.log("goal isOpponentGoal: " + goalToSave.isOpponentGoal());
                mListener.onGoalSaved(goalToSave);
            } else {
                position++;
                changePage(position);
            }
        }
    }

    private void changePage(int position) {
        eventPager.setCurrentItem(position);

        Fragment fragment = goalAdapter.getItem(position);
        // Header text
        if(fragment instanceof GoalDetailsFragment) {
            infoText.setText(R.string.add_event_details);
        } else if(fragment instanceof GoalSelectScorerFragment) {
            infoText.setText(R.string.add_event_scorer);
        } else if(fragment instanceof GoalSelectAssistantFragment) {
            infoText.setText(R.string.add_event_assistant);
        } else if(fragment instanceof GoalSelectLineFragment) {
            infoText.setText(R.string.add_event_line);
        } else if(fragment instanceof GoalPositionFragment) {
            infoText.setText(R.string.add_event_position);
        }

        // Buttons
        if(position == 0) {
            previousButton.setText(R.string.cancel);
        } else {
            previousButton.setText(R.string.previous);
        }
        int max = goalAdapter.getCount() - 1;
        if(position == max) {
            nextButton.setText(R.string.save);
        } else {
            nextButton.setText(R.string.next);
        }
    }
}
