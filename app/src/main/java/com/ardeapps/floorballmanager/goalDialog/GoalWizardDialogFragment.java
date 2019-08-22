package com.ardeapps.floorballmanager.goalDialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalWizardDialogData;

public class GoalWizardDialogFragment extends DialogFragment implements DataView {

    GoalWizardListener mListener = null;
    TextView infoText;
    Button previousButton;
    Button nextButton;
    TabLayout tabLayout;
    ViewPager eventPager;
    GoalPagerAdapter goalAdapter;
    GoalWizardDialogData data;
    int scorerFragmentPosition = 1;
    int position = 0;

    public void setListener(GoalWizardListener l) {
        mListener = l;
    }

    @Override
    public GoalWizardDialogData getData() {
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (GoalWizardDialogData) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_goal, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        infoText = v.findViewById(R.id.infoText);
        previousButton = v.findViewById(R.id.previousButton);
        nextButton = v.findViewById(R.id.nextButton);
        eventPager = v.findViewById(R.id.eventPager);
        tabLayout = v.findViewById(R.id.tabLayout);

        goalAdapter = new GoalPagerAdapter(getChildFragmentManager(), data);
        goalAdapter.setGoal(data.getGoal());

        eventPager.setOffscreenPageLimit(goalAdapter.getCount());
        eventPager.setAdapter(goalAdapter);
        tabLayout.setupWithViewPager(eventPager);
        // Disable tab title clicks
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        tabStrip.setEnabled(false);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }

        position = 0;
        changePage(position);

        previousButton.setOnClickListener(v12 -> handlePreviousClick());

        nextButton.setOnClickListener(v1 -> handleNextClick());
        return v;
    }

    private void handlePreviousClick() {
        if (position == 0) {
            dismiss();
        } else {
            if (goalAdapter.isPenaltyShot() && position == goalAdapter.getCount()) {
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
        if (isValid) {
            if (position == max) {
                Goal goalToSave = goalAdapter.getGoal();
                goalToSave.setGameId(data.getGame().getGameId());
                goalToSave.setSeasonId(data.getGame().getSeasonId());
                goalToSave.setOpponentGoal(data.isOpponentGoal());
                mListener.onGoalSaved(goalToSave);
            } else {
                if (goalAdapter.isPenaltyShot() && !data.isOpponentGoal() && position == scorerFragmentPosition) {
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
        if (fragment instanceof GoalDetailsFragment) {
            infoText.setText(R.string.add_event_details);
        } else if (fragment instanceof GoalSelectLineFragment) {
            infoText.setText(R.string.add_event_line);
        } else if (fragment instanceof GoalPositionFragment) {
            infoText.setText(R.string.add_event_position);
        } else if (!data.isOpponentGoal() && position == scorerFragmentPosition) {
            infoText.setText(R.string.add_event_scorer);
        } else if (!data.isOpponentGoal() && position == scorerFragmentPosition + 1) {
            infoText.setText(R.string.add_event_assistant);
        }

        // Buttons
        if (position == 0) {
            previousButton.setText(R.string.cancel);
        } else {
            previousButton.setText(R.string.previous);
        }
        int max = goalAdapter.getCount() - 1;
        if (position == max) {
            nextButton.setText(R.string.save);
        } else {
            nextButton.setText(R.string.next);
        }
    }

    public interface GoalWizardListener {
        void onGoalSaved(Goal goal);
    }
}
