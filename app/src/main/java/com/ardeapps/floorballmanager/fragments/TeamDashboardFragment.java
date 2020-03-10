package com.ardeapps.floorballmanager.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserInvitation;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.ImageUtil;

import java.util.Map;


public class TeamDashboardFragment extends Fragment {

    Button ownStatsButton;
    Button linesButton;
    Button playersButton;
    Button playerStatsButton;
    Button gamesButton;
    Button teamStatsButton;
    Button settingsButton;
    TextView infoText;
    ImageView logoImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_dashboard, container, false);

        ownStatsButton = v.findViewById(R.id.ownStatsButton);
        gamesButton = v.findViewById(R.id.gamesButton);
        linesButton = v.findViewById(R.id.linesButton);
        teamStatsButton = v.findViewById(R.id.teamStatsButton);
        playersButton = v.findViewById(R.id.playersButton);
        playerStatsButton = v.findViewById(R.id.playerStatsButton);
        settingsButton = v.findViewById(R.id.settingsButton);
        infoText = v.findViewById(R.id.infoText);
        logoImage = v.findViewById(R.id.logoImage);

        Bitmap logo = AppRes.getInstance().getSelectedTeam().getLogo();
        if (logo != null) {
            logoImage.setImageBitmap(ImageUtil.getSquarePicture(logo));
        } else {
            logoImage.setImageResource(R.drawable.default_logo);
        }

        boolean isPendingRequests = false;
        for(UserRequest userJoinRequest : AppRes.getInstance().getUserJoinRequests().values()) {
            if(UserRequest.Status.fromDatabaseName(userJoinRequest.getStatus()) == UserRequest.Status.PENDING) {
                isPendingRequests = true;
                break;
            }
        }
        Map<String, UserInvitation> userInvitations = AppRes.getInstance().getUserInvitations();
        if(!userInvitations.isEmpty()) {
            infoText.setText(getString(R.string.team_selection_user_invitation_info));
        } else if(isPendingRequests) {
            infoText.setText(getString(R.string.team_dashboard_info_new_requests));
        } else if (AppRes.getInstance().getSelectedSeason() == null) {
            infoText.setText(getString(R.string.team_dashboard_info_get_started));
        } else {
            infoText.setText(AppRes.getInstance().getSelectedTeam().getName());
        }

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.ADMIN) {
            settingsButton.setVisibility(View.VISIBLE);
            ownStatsButton.setVisibility(View.GONE);
        } else {
            settingsButton.setVisibility(View.GONE);
            if(role == UserConnection.Role.PLAYER) {
                ownStatsButton.setVisibility(View.VISIBLE);
            } else {
                ownStatsButton.setVisibility(View.GONE);
            }
        }

        ownStatsButton.setOnClickListener(v16 -> {
            String selectedPlayerId = AppRes.getInstance().getSelectedPlayerId();
            Player player = AppRes.getInstance().getPlayers().get(selectedPlayerId);
            if (player != null) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(player);
            }
        });
        gamesButton.setOnClickListener(v15 -> FragmentListeners.getInstance().getFragmentChangeListener().goToGamesFragment());
        linesButton.setOnClickListener(v14 -> FragmentListeners.getInstance().getFragmentChangeListener().goToLinesFragment());
        playersButton.setOnClickListener(v13 -> FragmentListeners.getInstance().getFragmentChangeListener().goToPlayersFragment());
        playerStatsButton.setOnClickListener(v11 -> FragmentListeners.getInstance().getFragmentChangeListener().goToStatsFragment());
        teamStatsButton.setOnClickListener(v12 -> FragmentListeners.getInstance().getFragmentChangeListener().goToTeamStatsFragment());
        settingsButton.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSettingsFragment());
        return v;
    }

}
