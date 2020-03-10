package com.ardeapps.floorballmanager.services;

import com.ardeapps.floorballmanager.handlers.PermissionDenyHandler;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.viewObjects.GameSettingsFragmentData;

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
    private ApplicationListener applicationListener;

    public static FragmentListeners getInstance() {
        if (instance == null) {
            instance = new FragmentListeners();
        }
        return instance;
    }

    public ApplicationListener getApplicationListener() {
        return applicationListener;
    }

    public void setApplicationListener(ApplicationListener applicationListener) {
        this.applicationListener = applicationListener;
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

    public interface FragmentChangeListener {
        void goToLoginFragment();

        void goToTeamSelectionFragment();

        void goToEditTeamFragment(Team team);

        void goToEditPlayerFragment(Player player);

        void goToTeamDashboardFragment(Team team, PermissionDenyHandler handler);

        void goToLinesFragment();

        void goToPlayersFragment();

        void goToSettingsFragment();

        void goToGameFragment(Game game);

        void goToGameSettingsFragment(GameSettingsFragmentData gameSettingsFragmentData);

        void goToGamesFragment();

        void goToBluetoothFragment();

        void goToSearchTeamFragment();

        void goToPlayerStatsFragment(Player player);

        void goToTeamStatsFragment();

        void goToStatsFragment();

        void goToEditUserConnectionFragment(UserConnection userConnection);

        void goToAcceptUserRequestFragment(UserRequest userRequest);

        void goToTeamSettingsFragment();

        void goToInactivePlayersFragment();
    }

    public interface ApplicationListener {
        void onUserInvitationHandled();
    }

    public interface PermissionHandledListener {
        void onPermissionGranted(int MY_PERMISSION);

        void onPermissionDenied(int MY_PERMISSION);
    }
}
