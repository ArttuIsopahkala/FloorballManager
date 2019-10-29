package com.ardeapps.floorballmanager.eventPenaltyDialog;

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
import com.ardeapps.floorballmanager.fragments.SelectPlayerFragment;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.viewObjects.PenaltyWizardDialogData;

public class PenaltyWizardDialogFragment extends DialogFragment implements DataView {

    public void setListener(PenaltyWizardListener l) {
        mListener = l;
    }

    @Override
    public PenaltyWizardDialogData getData() {
        return data;
    }

    @Override
    public void setData(Object viewData) {
        data = (PenaltyWizardDialogData) viewData;
    }

    public interface PenaltyWizardListener {
        void onPenaltySaved(Penalty penalty);
    }

    PenaltyWizardListener mListener = null;

    TextView titleText;
    TextView infoText;
    Button previousButton;
    Button nextButton;
    TabLayout tabLayout;
    ViewPager eventPager;
    PenaltyPagerAdapter penaltyAdapter;
    int position = 0;
    private PenaltyWizardDialogData data;

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

        if(data.getPenalty() == null) {
            titleText.setText(getString(R.string.event_title_penalty_add).toUpperCase());
        } else {
            titleText.setText(getString(R.string.event_title_penalty_edit).toUpperCase());
        }

        penaltyAdapter = new PenaltyPagerAdapter(getChildFragmentManager(), data.isOpponentPenalty(), data.getLines());
        penaltyAdapter.setPenalty(data.getPenalty());

        eventPager.setOffscreenPageLimit(penaltyAdapter.getCount());
        eventPager.setAdapter(penaltyAdapter);
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
            position--;
            changePage(position);
        }
    }

    private void handleNextClick() {
        int max = penaltyAdapter.getCount() - 1;
        boolean isValid = penaltyAdapter.validate(position);
        if (isValid) {
            if (position == max) {
                Penalty penaltyToSave = penaltyAdapter.getPenalty();
                penaltyToSave.setGameId(data.getGame().getGameId());
                penaltyToSave.setSeasonId(data.getGame().getSeasonId());
                penaltyToSave.setOpponentPenalty(data.isOpponentPenalty());
                mListener.onPenaltySaved(penaltyToSave);
            } else {
                position++;
                changePage(position);
            }
        }
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

        Fragment fragment = penaltyAdapter.getItem(position);
        // Header text
        if (fragment instanceof PenaltyDetailsFragment) {
            infoText.setText(R.string.add_event_details);
        } else if (fragment instanceof SelectPlayerFragment) {
            infoText.setText(R.string.add_event_penalty_player);
        }

        // Buttons
        if (position == 0) {
            previousButton.setText(R.string.cancel);
        } else {
            previousButton.setText(R.string.previous);
        }
        int max = penaltyAdapter.getCount() - 1;
        if (position == max) {
            nextButton.setText(R.string.save);
        } else {
            nextButton.setText(R.string.next);
        }
    }

}
