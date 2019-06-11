package com.ardeapps.floorballcoach.services;

import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Team;

import java.util.Map;

/**
 * Created by Arttu on 18.6.2017.
 */

public class FragmentListeners {

    public static final int MY_PERMISSION_ACCESS_TAKING_PICTURE = 15;
    public static final int MY_PERMISSION_ACCESS_READ_EXTERNAL_STORAGE = 12;
    public static final int MY_PERMISSION_ACCESS_BLUETOOTH = 11;
    public final static int MY_PERMISSION_ACCESS_COARSE_LOCATION = 10;

    private static FragmentListeners instance;
    private PermissionHandledListener permissionHandledListener;
    private FragmentChangeListener fragmentChangeListener;
    private FragmentRefreshListener fragmentRefreshListener;

    public static FragmentListeners getInstance() {
        if(instance == null) {
            instance = new FragmentListeners();
        }
        return instance;
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public FragmentChangeListener getFragmentChangeListener() {
        return fragmentChangeListener;
    }

    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public PermissionHandledListener getPermissionHandledListener() {
        return permissionHandledListener;
    }

    public void setPermissionHandledListener(PermissionHandledListener permissionHandledListener) {
        this.permissionHandledListener = permissionHandledListener;
    }

    public interface FragmentRefreshListener {
        void refreshMainSelectionFragment();
        void refreshPlayersFragment();
        void refreshLinesFragment();
    }

    public interface FragmentChangeListener {
        void goToLoginFragment();
        void goToMainSelectionFragment();
        void goToEditTeamFragment(Team team);
        void goToEditPlayerFragment(Player player);
        void goToTeamDashboardFragment(Team team);
        void goToPlayerDashboardFragment(Player player);
        void goToLinesFragment();
        void goToPlayersFragment();
        void goToSettingsFragment();
        void goToGameFragment(Game game);
        void goToGameSettingsFragment(Game game, Map<Integer, Line> lines);
        void goToPlayerMonitorFragment();
        void goToGamesFragment();
        void goToBluetoothFragment();
    }

    public interface PermissionHandledListener {
        void onPermissionGranted(int MY_PERMISSION);
        void onPermissionDenied(int MY_PERMISSION);
    }
}
