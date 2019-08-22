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
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.ImageUtil;


public class TeamDashboardFragment extends Fragment {

    Button ownStatsButton;
    Button linesButton;
    Button playersButton;
    Button gamesButton;
    Button teamStatsButton;
    Button settingsButton;
    TextView teamNameText;
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
        settingsButton = v.findViewById(R.id.settingsButton);
        teamNameText = v.findViewById(R.id.teamNameText);
        logoImage = v.findViewById(R.id.logoImage);

        Bitmap logo = AppRes.getInstance().getSelectedTeam().getLogo();
        if (logo != null) {
            logoImage.setImageBitmap(ImageUtil.getSquarePicture(logo));
        } else {
            logoImage.setImageResource(R.drawable.default_logo);
        }

        teamNameText.setText(AppRes.getInstance().getSelectedTeam().getName());

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.PLAYER) {
            linesButton.setVisibility(View.GONE);
            settingsButton.setVisibility(View.GONE);
            ownStatsButton.setVisibility(View.VISIBLE);
        } else {
            linesButton.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
            ownStatsButton.setVisibility(View.GONE);
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
        teamStatsButton.setOnClickListener(v12 -> FragmentListeners.getInstance().getFragmentChangeListener().goToTeamStatsFragment());
        settingsButton.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSettingsFragment());
        return v;
    }

}
