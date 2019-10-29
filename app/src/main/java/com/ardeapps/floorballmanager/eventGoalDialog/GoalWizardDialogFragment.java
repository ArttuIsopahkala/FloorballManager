package com.ardeapps.floorballmanager.eventGoalDialog;

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
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.GoalWizardDialogData;

import java.util.concurrent.TimeUnit;

public class GoalWizardDialogFragment extends DialogFragment implements DataView {

    public void setListener(GoalWizardListener l) {
        mListener = l;
    }

    public interface GoalWizardListener {
        void onGoalSaved(Goal goal);
    }

    GoalWizardListener mListener = null;

    TextView titleText;
    TextView infoText;
    Button previousButton;
    Button nextButton;
    TabLayout tabLayout;
    ViewPager eventPager;
    GoalPagerAdapter goalAdapter;
    int scorerFragmentPosition = 1;
    int position = 0;
    GoalWizardDialogData data;

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
        View v = inflater.inflate(R.layout.dialog_event, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        titleText = v.findViewById(R.id.titleText);
        infoText = v.findViewById(R.id.infoText);
        previousButton = v.findViewById(R.id.previousButton);
        nextButton = v.findViewById(R.id.nextButton);
        eventPager = v.findViewById(R.id.eventPager);
        tabLayout = v.findViewById(R.id.tabLayout);

        if(data.getGoal() == null) {
            titleText.setText(getString(R.string.event_title_goal_add).toUpperCase());
        } else {
            titleText.setText(getString(R.string.event_title_goal_edit).toUpperCase());
        }

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
            int max = goalAdapter.getCount() - 1;
            if (goalAdapter.isPenaltyShot() && position == max) {
                position = data.isOpponentGoal() ? 0 : 1;
            } else {
                position--;
            }
            changePage(position);
        }
    }

    private void handleNextClick() {
        boolean isValid = goalAdapter.validate(position);
        if (isValid) {
            checkNextClickChanges(() -> {
                int max = goalAdapter.getCount() - 1;
                if (position == max) {
                    Goal goalToSave = goalAdapter.getGoal();
                    goalToSave.setGameId(data.getGame().getGameId());
                    goalToSave.setSeasonId(data.getGame().getSeasonId());
                    goalToSave.setOpponentGoal(data.isOpponentGoal());
                    mListener.onGoalSaved(goalToSave);
                } else {
                    if (goalAdapter.isPenaltyShot() && (data.isOpponentGoal() || position == scorerFragmentPosition)) {
                        position = max;
                    } else {
                        position++;
                    }
                    changePage(position);
                }
            });
        }
    }

    interface NextClickChangesListener {
        void onNextClickChangesHandled();
    }

    private void checkNextClickChanges(NextClickChangesListener handler) {
        Fragment fragment = goalAdapter.getItem(position);
        if (fragment instanceof GoalDetailsFragment) {
            // Correct game mode if penalty is ongoing
            GoalDetailsFragment goalDetailsFragment = (GoalDetailsFragment) fragment;
            Penalty penalty = getOnGoingPenalty(goalDetailsFragment.getData().getTime());
            Goal.Mode modeToChange = null;
            String modeDescription = "";
            if(penalty != null) {
                Goal.Mode gameMode = goalDetailsFragment.getData().getGameMode();
                if(gameMode != Goal.Mode.AV && penalty.isOpponentPenalty() && data.isOpponentGoal()) {
                    modeToChange = Goal.Mode.AV;
                    modeDescription = getString(R.string.add_event_opponent_penalty_av);
                } else if (gameMode != Goal.Mode.AV && !penalty.isOpponentPenalty() && !data.isOpponentGoal()) {
                    modeToChange = Goal.Mode.AV;
                    modeDescription = getString(R.string.add_event_home_penalty_av);
                } else if (gameMode != Goal.Mode.YV && penalty.isOpponentPenalty() && !data.isOpponentGoal()) {
                    modeToChange = Goal.Mode.YV;
                    modeDescription = getString(R.string.add_event_opponent_penalty_yv);
                } else if (gameMode != Goal.Mode.YV && !penalty.isOpponentPenalty() && data.isOpponentGoal()) {
                    modeToChange = Goal.Mode.YV;
                    modeDescription = getString(R.string.add_event_home_penalty_yv);
                }
                if(modeToChange != null) {
                    goalDetailsFragment.askForWrongGameMode(modeToChange, modeDescription, handler);
                } else {
                    handler.onNextClickChangesHandled();
                }
            } else {
                handler.onNextClickChangesHandled();
            }
        } else {
            handler.onNextClickChangesHandled();
        }
    }

    private Penalty getOnGoingPenalty(long goalTime) {
        for(Penalty penalty : data.getPenalties().values()) {
            // Don't count game penalties
            if(penalty.getLength() != 20) {
                long penaltyStart = penalty.getTime();
                long penaltyEnd = penaltyStart + TimeUnit.MINUTES.toMillis(penalty.getLength());
                if(penaltyStart < goalTime && goalTime < penaltyEnd) {
                    return penalty;
                }
            }
        }
        return null;
    }

    // This sets dialog full screen
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setLayout(width, height);
        }
    }

    private void changePage(int position) {
        eventPager.setCurrentItem(position);

        Fragment fragment = goalAdapter.getItem(position);
        // Header text
        if (fragment instanceof GoalDetailsFragment) {
            infoText.setText(R.string.add_event_details);
        } else if (fragment instanceof GoalSelectLineFragment) {
            String title = getString(R.string.add_event_line)
                    + System.getProperty("line.separator")
                    + getString(data.isOpponentGoal() ? R.string.add_event_minus_players : R.string.add_event_plus_players);
            infoText.setText(title);
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

}
