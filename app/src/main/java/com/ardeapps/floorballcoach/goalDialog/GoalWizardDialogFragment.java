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
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.GoalWizardDialogFragmentData;

public class GoalWizardDialogFragment extends DialogFragment implements DataView {

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

    GoalWizardDialogFragmentData data;

    int position = 0;

    @Override
    public void setData(Object viewData) {
        data = (GoalWizardDialogFragmentData) viewData;
    }

    @Override
    public GoalWizardDialogFragmentData getData() {
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

        /*GoalPagerAdapterData adapterData = new GoalPagerAdapterData();
        adapterData.setLines(data.getLines());
        adapterData.setOpponentGoal(data.isOpponentGoal());
        adapterData.setOpponentName(data.getGame().getOpponentName());
        adapterData.setGoal(data.getGoal());*/

        goalAdapter = new GoalPagerAdapter(getChildFragmentManager(), data.getLines(), data.isOpponentGoal(), data.getGame().getOpponentName());
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
            if(goalAdapter.isPenaltyShot() && goalAdapter.getItem(position) instanceof GoalPositionFragment) {
                position = data.isOpponentGoal() ? 0 : 1;
            } else {
                position--;
            }
            changePage(position);
        }
    }

    private void handleNextClick() {
        int max = goalAdapter.getCount() - 1;
        boolean isValid = goalAdapter.validate(position);
        if(isValid) {
            if(position == max) {
                Goal goalToSave = goalAdapter.getGoal();
                goalToSave.setGameId(data.getGame().getGameId());
                goalToSave.setOpponentGoal(data.isOpponentGoal());
                mListener.onGoalSaved(goalToSave);
            } else {
                if(goalAdapter.isPenaltyShot() && goalAdapter.getItem(position) instanceof GoalSelectScorerFragment) {
                    position = max;
                } else {
                    position++;
                }
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
