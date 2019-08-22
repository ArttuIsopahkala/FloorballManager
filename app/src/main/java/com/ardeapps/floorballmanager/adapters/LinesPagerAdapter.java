package com.ardeapps.floorballmanager.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.fragments.LineFragment;

import java.util.List;

/**
 * Created by Arttu on 24.9.2016.
 */
public class LinesPagerAdapter extends FragmentStatePagerAdapter {

    private List<LineFragment> lineFragments;

    public LinesPagerAdapter(FragmentManager supportFragmentManager, List<LineFragment> lineFragments) {
        super(supportFragmentManager);
        this.lineFragments = lineFragments;
    }

    @Override
    public int getItemPosition(Object object) {
        if (lineFragments.contains(object)) {
            return lineFragments.indexOf(object);
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
        return lineFragments.get(position);
    }

    @Override
    public int getCount() {
        return lineFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position + 1) + ". " + AppRes.getContext().getString(R.string.line);
    }

}
