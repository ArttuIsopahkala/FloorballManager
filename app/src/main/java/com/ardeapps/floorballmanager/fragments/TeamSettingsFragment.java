package com.ardeapps.floorballmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.MainActivity;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GamesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.LinesResource;
import com.ardeapps.floorballmanager.resources.SeasonsResource;
import com.ardeapps.floorballmanager.resources.TeamsResource;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UserInvitationsResource;
import com.ardeapps.floorballmanager.resources.UserRequestsResource;
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
    TextView userRequestsTitle;
    LinearLayout userJoinRequestsContainer;

    public void update() {
        Map<String, UserConnection> userConnections = AppRes.getInstance().getUserConnections();
        Map<String, UserRequest> userJoinRequests = AppRes.getInstance().getUserJoinRequests();

        final Map<String, UserRequest> filteredUserRequests = new HashMap<>();
        // Show only pending requests
        for(UserRequest userJoinRequest : userJoinRequests.values()) {
            if(UserRequest.Status.fromDatabaseName(userJoinRequest.getStatus()) == UserRequest.Status.PENDING) {
                filteredUserRequests.put(userJoinRequest.getUserConnectionId(), userJoinRequest);
            }
        }

        final UserJoinRequestHolder userJoinHolder = new UserJoinRequestHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        userJoinRequestsContainer.removeAllViews();
        userRequestsTitle.setVisibility(filteredUserRequests.isEmpty() ? View.GONE : View.VISIBLE);

        for (final UserRequest userRequest : filteredUserRequests.values()) {
            View cv = inf.inflate(R.layout.list_item_user_join_request, userJoinRequestsContainer, false);
            userJoinHolder.joinEmailText = cv.findViewById(R.id.joinEmailText);
            userJoinHolder.removeIcon = cv.findViewById(R.id.removeIcon);
            userJoinHolder.acceptIcon = cv.findViewById(R.id.acceptIcon);

            userJoinHolder.joinEmailText.setText(userRequest.getEmail());

            userJoinHolder.removeIcon.setOnClickListener(v -> {
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.team_settings_request_remove));
                dialogFragment.show(getChildFragmentManager(), "Poistetaanko tämä pyyntö?");
                dialogFragment.setListener(() -> {
                    UserRequestsResource.getInstance().removeUserRequest(userRequest.getUserConnectionId(), () -> {
                        AppRes.getInstance().setUserJoinRequest(userRequest.getUserConnectionId(), null);
                        update();
                    });
                });
            });
            userJoinHolder.acceptIcon.setOnClickListener(v -> {
                FragmentListeners.getInstance().getFragmentChangeListener().goToAcceptUserRequestFragment(userRequest);
            });

            userJoinRequestsContainer.addView(cv);
        }

        final UserConnectionHolder userConnectionHolder = new UserConnectionHolder();
        inf = LayoutInflater.from(AppRes.getContext());
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
            userConnectionHolder.emailText = cv.findViewById(R.id.emailText);
            userConnectionHolder.roleText = cv.findViewById(R.id.roleText);
            userConnectionHolder.statusUserConnectionIcon = cv.findViewById(R.id.statusUserConnectionIcon);
            userConnectionHolder.removeUserConnectionIcon = cv.findViewById(R.id.removeUserConnectionIcon);
            userConnectionHolder.editUserConnectionIcon = cv.findViewById(R.id.editUserConnectionIcon);

            userConnectionHolder.emailText.setText(userConnection.getEmail());

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
            userConnectionHolder.roleText.setText(roleText);

            final UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnection.getStatus());
            if (status == UserConnection.Status.DENY) {
                userConnectionHolder.statusUserConnectionIcon.setText(getString(R.string.icon_close));
                userConnectionHolder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_red_light));
            } else if (status == UserConnection.Status.PENDING) {
                userConnectionHolder.statusUserConnectionIcon.setText(getString(R.string.icon_clock));
                userConnectionHolder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_yellow_light));
            } else {
                userConnectionHolder.statusUserConnectionIcon.setText(getString(R.string.icon_check));
                userConnectionHolder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));
            }

            String userId = AppRes.getInstance().getUser().getUserId();
            // Founder or self cannot be remove
            if (isFounder || userId.equals(userConnection.getUserId())) {
                userConnectionHolder.removeUserConnectionIcon.setVisibility(View.GONE);
                userConnectionHolder.editUserConnectionIcon.setVisibility(View.GONE);
            } else {
                userConnectionHolder.removeUserConnectionIcon.setVisibility(View.VISIBLE);
                userConnectionHolder.editUserConnectionIcon.setVisibility(View.VISIBLE);
            }

            userConnectionHolder.removeUserConnectionIcon.setOnClickListener(v -> {
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.team_settings_remove_confirmation));
                dialogFragment.show(getChildFragmentManager(), "Poistetaanko linkitys käyttäjään?");
                dialogFragment.setListener(() -> {
                    final String userConnectionId = userConnection.getUserConnectionId();
                    UserConnectionsResource.getInstance().removeUserConnection(userConnectionId, () -> {
                        userConnections.remove(userConnectionId);
                        UserRequestsResource.getInstance().removeUserRequest(userConnectionId, () -> {
                            UserInvitationsResource.getInstance().removeUserInvitation(userConnectionId, this::update);
                        });
                    });
                });
            });
            userConnectionHolder.editUserConnectionIcon.setOnClickListener(v -> FragmentListeners.getInstance().getFragmentChangeListener().goToEditUserConnectionFragment(userConnection));

            userConnectionsContainer.addView(cv);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_settings, container, false);
        editTeamButton = v.findViewById(R.id.editTeamButton);
        addUserConnectionButton = v.findViewById(R.id.addUserConnectionButton);
        userRequestsTitle = v.findViewById(R.id.userRequestsTitle);
        userJoinRequestsContainer = v.findViewById(R.id.userJoinRequestsContainer);
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
        UserInvitationsResource.getInstance().removeUserInvitations(AppRes.getInstance().getUserConnections().keySet(), () ->
                UserConnectionsResource.getInstance().removeUserConnections(team.getTeamId(), () ->
                        LinesResource.getInstance().removeAllLines(() ->
                                GameLinesResource.getInstance().removeAllLines(() ->
                                        GoalsResource.getInstance().removeAllGoals(() ->
                                                GamesResource.getInstance().removeAllGames(() ->
                                                        TeamsResource.getInstance().removeTeam(team, () ->
                                                                SeasonsResource.getInstance().removeAllSeasons(() -> {
                                                                    UserRequestsResource.getInstance().removeUserRequests(AppRes.getInstance().getUserJoinRequests().keySet(), () -> {
                                                                        User user = AppRes.getInstance().getUser();
                                                                        user.getTeamIds().remove(team.getTeamId());
                                                                        UsersResource.getInstance().editUser(user, () -> {
                                                                            Logger.toast(R.string.team_settings_remove_team_successful);
                                                                            FragmentActivity activity = AppRes.getActivity();
                                                                            Intent i = new Intent(activity, MainActivity.class);
                                                                            activity.finish();
                                                                            activity.overridePendingTransition(0, 0);
                                                                            activity.startActivity(i);
                                                                            activity.overridePendingTransition(0, 0);
                                                                        });
                                                                    });
                                                                })
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

    private class UserJoinRequestHolder {
        TextView joinEmailText;
        IconView removeIcon;
        IconView acceptIcon;
    }
}
