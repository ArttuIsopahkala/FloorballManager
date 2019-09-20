package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GamesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.LinesResource;
import com.ardeapps.floorballmanager.resources.SeasonsResource;
import com.ardeapps.floorballmanager.resources.TeamsResource;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UserInvitationsResource;
import com.ardeapps.floorballmanager.resources.UsersResource;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class TeamSettingsFragment extends Fragment {

    Button editTeamButton;
    Button addUserConnectionButton;
    Button removeTeamButton;
    Button inactivePlayersButton;
    LinearLayout userConnectionsContainer;

    private Map<String, UserConnection> userConnections = new HashMap<>();

    public void refreshData() {
        userConnections = AppRes.getInstance().getUserConnections();
    }

    public void update() {
        final UserConnectionHolder holder = new UserConnectionHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        userConnectionsContainer.removeAllViews();

        ArrayList<UserConnection> userConnectionsList = new ArrayList<>(userConnections.values());
        Collections.sort(userConnectionsList, (o1, o2) -> {
            String founder = AppRes.getInstance().getSelectedTeam().getFounder();
            boolean isFounder1 = founder.equals(o1.getUserId());
            boolean isFounder2 = founder.equals(o2.getUserId());
            UserConnection.Role role1 = UserConnection.Role.fromDatabaseName(o1.getRole());
            UserConnection.Role role2 = UserConnection.Role.fromDatabaseName(o2.getRole());
            if (role1 == role2) {
                if (isFounder1) {
                    return -1;
                } else if (isFounder2) {
                    return 1;
                } else {
                    return 0;
                }
            } else if (role1 == UserConnection.Role.ADMIN) {
                return -1;
            } else if (role1 == UserConnection.Role.PLAYER) {
                return -1;
            } else {
                return 1;
            }
        });

        for (final UserConnection userConnection : userConnectionsList) {
            View cv = inf.inflate(R.layout.list_item_user_connection, userConnectionsContainer, false);
            holder.emailText = cv.findViewById(R.id.emailText);
            holder.roleText = cv.findViewById(R.id.roleText);
            holder.statusUserConnectionIcon = cv.findViewById(R.id.statusUserConnectionIcon);
            holder.removeUserConnectionIcon = cv.findViewById(R.id.removeUserConnectionIcon);
            holder.editUserConnectionIcon = cv.findViewById(R.id.editUserConnectionIcon);

            holder.emailText.setText(userConnection.getEmail());

            String founder = AppRes.getInstance().getSelectedTeam().getFounder();
            boolean isFounder = founder.equals(userConnection.getUserId());

            String roleText;
            UserConnection.Role role = UserConnection.Role.fromDatabaseName(userConnection.getRole());
            if (role == UserConnection.Role.ADMIN) {
                roleText = getString(isFounder ? R.string.founder : R.string.admin);
            } else if (role == UserConnection.Role.PLAYER) {
                roleText = getString(R.string.player);
            } else {
                roleText = getString(R.string.guest);
            }
            holder.roleText.setText(roleText);

            final UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnection.getStatus());
            if (status == UserConnection.Status.DENY) {
                holder.statusUserConnectionIcon.setText(getString(R.string.icon_close));
                holder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_red_light));
            } else if (status == UserConnection.Status.PENDING) {
                holder.statusUserConnectionIcon.setText(getString(R.string.icon_clock));
                holder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_yellow_light));
            } else {
                holder.statusUserConnectionIcon.setText(getString(R.string.icon_check));
                holder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));
            }

            String userId = AppRes.getInstance().getUser().getUserId();
            // Founder or self cannot be remove
            if (isFounder || userId.equals(userConnection.getUserId())) {
                holder.removeUserConnectionIcon.setVisibility(View.GONE);
                holder.editUserConnectionIcon.setVisibility(View.GONE);
            } else {
                holder.removeUserConnectionIcon.setVisibility(View.VISIBLE);
                holder.editUserConnectionIcon.setVisibility(View.VISIBLE);
            }

            holder.removeUserConnectionIcon.setOnClickListener(v -> {
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.team_settings_remove_confirmation));
                dialogFragment.show(getChildFragmentManager(), "Poistetaanko linkitys käyttäjään?");
                dialogFragment.setListener(() -> {
                    final String userConnectionId = userConnection.getUserConnectionId();
                    UserConnectionsResource.getInstance().removeUserConnection(userConnectionId, () -> {
                        userConnections.remove(userConnectionId);
                        UserInvitationsResource.getInstance().removeUserInvitation(userConnectionId, () -> {
                            // Remove connection from user
                            if (userConnection.getUserId() != null) {
                                UsersResource.getInstance().getUser(userConnection.getUserId(), user -> {
                                    user.getTeamIds().remove(AppRes.getInstance().getSelectedTeam().getTeamId());
                                    UsersResource.getInstance().editUser(user);
                                    update();
                                });
                            } else {
                                update();
                            }
                        });
                    });
                });
            });
            holder.editUserConnectionIcon.setOnClickListener(v -> FragmentListeners.getInstance().getFragmentChangeListener().goToEditUserConnectionFragment(userConnection));

            userConnectionsContainer.addView(cv);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_settings, container, false);
        editTeamButton = v.findViewById(R.id.editTeamButton);
        addUserConnectionButton = v.findViewById(R.id.addUserConnectionButton);
        userConnectionsContainer = v.findViewById(R.id.userConnectionsContainer);
        removeTeamButton = v.findViewById(R.id.removeTeamButton);
        inactivePlayersButton = v.findViewById(R.id.inactivePlayersButton);

        String founder = AppRes.getInstance().getSelectedTeam().getFounder();
        boolean isFounder = founder.equals(AppRes.getInstance().getUser().getUserId());
        removeTeamButton.setVisibility(isFounder ? View.VISIBLE : View.GONE);

        update();

        editTeamButton.setOnClickListener(v14 -> {
            Team team = AppRes.getInstance().getSelectedTeam();
            FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(team);
        });

        addUserConnectionButton.setOnClickListener(v13 -> FragmentListeners.getInstance().getFragmentChangeListener().goToEditUserConnectionFragment(null));

        inactivePlayersButton.setOnClickListener(v12 -> FragmentListeners.getInstance().getFragmentChangeListener().goToInactivePlayersFragment());

        removeTeamButton.setOnClickListener(v1 -> {
            if (!AppRes.getInstance().getPlayers().isEmpty()) {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.team_settings_remove_players_first));
                dialog.show(getChildFragmentManager(), "Poista ensin pelaajat");
                return;
            }

            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.team_settings_remove_team_confirmation));
            dialogFragment.show(getChildFragmentManager(), "Poistetaanko joukkue ja data?");
            dialogFragment.setListener(this::deleteAllTeamData);

        });

        return v;
    }

    private void deleteAllTeamData() {
        // Delete all team data
        final Team team = AppRes.getInstance().getSelectedTeam();
        UserConnectionsResource.getInstance().getUserConnections(team.getTeamId(), userConnections ->
                UserInvitationsResource.getInstance().removeUserInvitations(userConnections.keySet(), () ->
                        UserConnectionsResource.getInstance().removeUserConnections(team.getTeamId(), () ->
                                LinesResource.getInstance().removeAllLines(() ->
                                        GameLinesResource.getInstance().removeAllLines(() ->
                                                GoalsResource.getInstance().removeAllGoals(() ->
                                                        GamesResource.getInstance().removeAllGames(() ->
                                                                TeamsResource.getInstance().removeTeam(team, () ->
                                                                        SeasonsResource.getInstance().removeAllSeasons(() -> {
                                                                            User user = AppRes.getInstance().getUser();
                                                                            user.getTeamIds().remove(team.getTeamId());
                                                                            UsersResource.getInstance().editUser(user, () -> {
                                                                                AppRes.getInstance().setSeasons(null);
                                                                                AppRes.getInstance().setUserConnections(null);
                                                                                AppRes.getInstance().setLines(null);
                                                                                AppRes.getInstance().setLinesByGame(null);
                                                                                AppRes.getInstance().setGoalsByGame(null);
                                                                                AppRes.getInstance().setGames(null);
                                                                                AppRes.getInstance().setTeam(team.getTeamId(), null);
                                                                                AppRes.getInstance().setSelectedTeam(null);

                                                                                // Reset common user data
                                                                                for (String userConnectionId : userConnections.keySet()) {
                                                                                    AppRes.getInstance().setUserInvitation(userConnectionId, null);
                                                                                }
                                                                                Logger.toast(R.string.team_settings_remove_team_successful);
                                                                                AppRes.getActivity().finish();
                                                                            });
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private class UserConnectionHolder {
        TextView emailText;
        TextView roleText;
        IconView statusUserConnectionIcon;
        IconView removeUserConnectionIcon;
        IconView editUserConnectionIcon;
    }
}
