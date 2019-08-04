package com.ardeapps.floorballcoach.fragments;

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

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.ImageUtil;


public class TeamDashboardFragment extends Fragment {

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

        gamesButton = v.findViewById(R.id.gamesButton);
        linesButton = v.findViewById(R.id.linesButton);
        teamStatsButton = v.findViewById(R.id.teamStatsButton);
        playersButton = v.findViewById(R.id.playersButton);
        settingsButton = v.findViewById(R.id.settingsButton);
        teamNameText = v.findViewById(R.id.teamNameText);
        logoImage = v.findViewById(R.id.logoImage);

        Bitmap logo = AppRes.getInstance().getSelectedTeam().getLogo();
        if(logo != null) {
            logoImage.setImageBitmap(ImageUtil.getSquarePicture(logo));
        } else {
            logoImage.setImageResource(R.drawable.default_logo);
        }

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if(role == UserConnection.Role.PLAYER) {
            teamNameText.setText(AppRes.getInstance().getSelectedTeam().getName());
            gamesButton.setVisibility(View.GONE);
            linesButton.setVisibility(View.GONE);
            settingsButton.setVisibility(View.GONE);
        } else {
            gamesButton.setVisibility(View.VISIBLE);
            linesButton.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
        }

        gamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToGamesFragment();
            }
        });
        linesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToLinesFragment();
            }
        });
        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToPlayersFragment();
            }
        });
        teamStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToTeamStatsFragment();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSettingsFragment();
            }
        });
        return v;
    }

}
