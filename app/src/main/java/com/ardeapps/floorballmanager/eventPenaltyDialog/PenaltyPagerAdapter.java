package com.ardeapps.floorballmanager.eventPenaltyDialog;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ardeapps.floorballmanager.fragments.SelectPlayerFragment;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.viewObjects.SelectPlayerFragmentData;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Arttu on 9.10.2019.
 */
public class PenaltyPagerAdapter extends FragmentStatePagerAdapter {

    private PenaltyDetailsFragment detailsFragment;
    private SelectPlayerFragment selectPlayerFragment;
    private boolean isOpponentPenalty;
    private Map<Integer, Line> lines;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Penalty penalty;

    public PenaltyPagerAdapter(FragmentManager supportFragmentManager, boolean isOpponentPenalty, Map<Integer, Line> lines) {
        super(supportFragmentManager);
        this.isOpponentPenalty = isOpponentPenalty;
        this.lines = lines;

        detailsFragment = new PenaltyDetailsFragment();
        selectPlayerFragment = new SelectPlayerFragment();

        if (isOpponentPenalty) {
            fragments.add(detailsFragment);
        } else {
            fragments.add(detailsFragment);
            fragments.add(selectPlayerFragment);
        }
    }

    public Penalty getPenalty() {
        Penalty detailsFragmentData = detailsFragment.getData();

        Penalty penaltyToSave = new Penalty();
        if (penalty != null) {
            penaltyToSave = penalty.clone();
        }
        if (isOpponentPenalty) {
            penaltyToSave.setTime(detailsFragment.getData().getTime());
            penaltyToSave.setLength(detailsFragment.getData().getLength());
        } else {
            penaltyToSave.setTime(detailsFragmentData.getTime());
            penaltyToSave.setLength(detailsFragmentData.getLength());
            penaltyToSave.setPlayerId(selectPlayerFragment.getData().getPlayerId());
        }

        return penaltyToSave;
    }

    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;

        Penalty penaltyToSet;
        SelectPlayerFragmentData selectPlayerFragmentData = new SelectPlayerFragmentData();
        selectPlayerFragmentData.setLines(lines);

        if (penalty != null) {
            penaltyToSet = penalty.clone();
            selectPlayerFragmentData.setPlayerId(penalty.getPlayerId());
        } else {
            penaltyToSet = new Penalty();
            selectPlayerFragmentData.setPlayerId(null);
        }

        detailsFragment.setData(penaltyToSet);
        selectPlayerFragment.setData(selectPlayerFragmentData);
    }

    public boolean validate(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment instanceof PenaltyDetailsFragment) {
            return ((PenaltyDetailsFragment) fragment).validate();
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
