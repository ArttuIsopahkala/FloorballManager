package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.TeamListAdapter;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.objects.UserInvitation;
import com.ardeapps.floorballcoach.resources.TeamsUserConnectionsResource;
import com.ardeapps.floorballcoach.resources.UserInvitationsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Map;


public class MainSelectionFragment extends Fragment implements TeamListAdapter.Listener {

    Button bluetoothButton;
    Button addTeamButton;
    ListView teamList;
    TextView userInvitationInfoText;
    LinearLayout userInvitationsContainer;
    TeamListAdapter adapter;

    private class UserInvitationHolder {
        TextView roleText;
        TextView nameText;
        IconView removeIcon;
        IconView acceptIcon;
    }

    public void update() {
        //bluetooth.refreshData();
        adapter.setTeams(new ArrayList<>(AppRes.getInstance().getTeams().values()));
        adapter.notifyDataSetChanged();

        Map<String, UserInvitation> userInvitations = AppRes.getInstance().getUserInvitations();
        if(userInvitations.isEmpty()) {
            userInvitationInfoText.setVisibility(View.GONE);
            userInvitationsContainer.setVisibility(View.GONE);
        } else {
            userInvitationInfoText.setVisibility(View.VISIBLE);
            userInvitationsContainer.setVisibility(View.VISIBLE);

            final UserInvitationHolder holder = new UserInvitationHolder();
            LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
            userInvitationsContainer.removeAllViews();

            for(final UserInvitation userInvitation : userInvitations.values()) {
                View cv = inf.inflate(R.layout.list_item_user_invitation, userInvitationsContainer, false);
                holder.roleText = cv.findViewById(R.id.roleText);
                holder.nameText = cv.findViewById(R.id.nameText);
                holder.removeIcon = cv.findViewById(R.id.removeIcon);
                holder.acceptIcon = cv.findViewById(R.id.acceptIcon);

                String roleText;
                UserConnection.Role role = UserConnection.Role.fromDatabaseName(userInvitation.getRole());
                if(role == UserConnection.Role.PLAYER) {
                    roleText = getString(R.string.main_selection_user_invitation_player);
                } else {
                    roleText = getString(R.string.main_selection_user_invitation_admin);
                }
                holder.roleText.setText(roleText);
                holder.nameText.setText(userInvitation.getTeam().getName());

                holder.removeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TeamsUserConnectionsResource.getInstance().editUserConnectionAsInvited(userInvitation, UserConnection.Status.DENY, null, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                UserInvitationsResource.getInstance().removeUserInvitation(userInvitation.getUserConnectionId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                    @Override
                                    public void onDeleteDataSuccess() {
                                        AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                                        update();
                                    }
                                });
                            }
                        });
                    }
                });

                holder.acceptIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Add team to user
                        final User userToSave = AppRes.getInstance().getUser();
                        userToSave.getTeamIds().add(userInvitation.getTeamId());
                        UsersResource.getInstance().editUser(userToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                AppRes.getInstance().setUser(userToSave);
                                // Add team to AppRes. Team is loaded when invitations are shown.
                                AppRes.getInstance().getTeams().put(userInvitation.getTeamId(), userInvitation.getTeam());
                                // Remove invitation
                                UserInvitationsResource.getInstance().removeUserInvitation(userInvitation.getUserConnectionId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                    @Override
                                    public void onDeleteDataSuccess() {
                                        AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                                        // Set user connection as connected
                                        TeamsUserConnectionsResource.getInstance().editUserConnectionAsInvited(userInvitation, UserConnection.Status.CONNECTED, userToSave.getUserId(), new FirebaseDatabaseService.EditDataSuccessListener() {
                                            @Override
                                            public void onEditDataSuccess() {
                                                update();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

                userInvitationsContainer.addView(cv);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeamListAdapter(AppRes.getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_selection, container, false);

        bluetoothButton = v.findViewById(R.id.bluetoothButton);
        addTeamButton = v.findViewById(R.id.addTeamButton);
        teamList = v.findViewById(R.id.teamList);
        userInvitationsContainer = v.findViewById(R.id.userInvitationsContainer);
        userInvitationInfoText = v.findViewById(R.id.userInvitationInfoText);

        teamList.setAdapter(adapter);

        update();

        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(null);
            }
        });
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToBluetoothFragment();
            }
        });
        return v;
    }

    @Override
    public void onTeamSelected(Team team) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToTeamDashboardFragment(team);
    }

}
