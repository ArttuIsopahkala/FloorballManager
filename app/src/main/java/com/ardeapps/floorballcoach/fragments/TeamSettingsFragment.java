package com.ardeapps.floorballcoach.fragments;

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

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballcoach.handlers.GetUserConnectionsHandler;
import com.ardeapps.floorballcoach.handlers.GetUserHandler;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.resources.GameLinesResource;
import com.ardeapps.floorballcoach.resources.GoalsResource;
import com.ardeapps.floorballcoach.resources.GamesResource;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.resources.SeasonsResource;
import com.ardeapps.floorballcoach.resources.TeamsResource;
import com.ardeapps.floorballcoach.resources.UserConnectionsResource;
import com.ardeapps.floorballcoach.resources.UserInvitationsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private class UserConnectionHolder {
        TextView emailText;
        TextView roleText;
        IconView statusUserConnectionIcon;
        IconView removeUserConnectionIcon;
        IconView editUserConnectionIcon;
    }

    public void update() {
        final UserConnectionHolder holder = new UserConnectionHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        userConnectionsContainer.removeAllViews();


        ArrayList<UserConnection> userConnectionsList = new ArrayList<>(userConnections.values());
        Collections.sort(userConnectionsList, new Comparator<UserConnection>() {
            @Override
            public int compare(UserConnection o1, UserConnection o2) {
                UserConnection.Role role1 = UserConnection.Role.fromDatabaseName(o1.getRole());
                UserConnection.Role role2 = UserConnection.Role.fromDatabaseName(o2.getRole());
                if(role1 == role2) {
                    return 0;
                } else if(role1 == UserConnection.Role.ADMIN) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        for(final UserConnection userConnection : userConnectionsList) {
            View cv = inf.inflate(R.layout.list_item_user_connection, userConnectionsContainer, false);
            holder.emailText = cv.findViewById(R.id.emailText);
            holder.roleText = cv.findViewById(R.id.roleText);
            holder.statusUserConnectionIcon = cv.findViewById(R.id.statusUserConnectionIcon);
            holder.removeUserConnectionIcon = cv.findViewById(R.id.removeUserConnectionIcon);
            holder.editUserConnectionIcon = cv.findViewById(R.id.editUserConnectionIcon);

            holder.emailText.setText(userConnection.getEmail());

            String roleText;
            UserConnection.Role role = UserConnection.Role.fromDatabaseName(userConnection.getRole());
            roleText = getString(role == UserConnection.Role.PLAYER ? R.string.player : R.string.admin);
            holder.roleText.setText(roleText);

            final UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnection.getStatus());
            if(status == UserConnection.Status.DENY) {
                holder.statusUserConnectionIcon.setText(getString(R.string.icon_close));
                holder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_red_light));
            } else if(status == UserConnection.Status.PENDING) {
                holder.statusUserConnectionIcon.setText(getString(R.string.icon_clock));
                holder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_yellow_light));
            } else {
                holder.statusUserConnectionIcon.setText(getString(R.string.icon_check));
                holder.statusUserConnectionIcon.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_green_light));
            }

            if(userConnection.getEmail().equalsIgnoreCase(AppRes.getInstance().getUser().getEmail())) {
                holder.removeUserConnectionIcon.setVisibility(View.GONE);
                holder.editUserConnectionIcon.setVisibility(View.GONE);
            } else {
                holder.removeUserConnectionIcon.setVisibility(View.VISIBLE);
                holder.editUserConnectionIcon.setVisibility(View.VISIBLE);
            }

            holder.removeUserConnectionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.team_settings_remove_confirmation));
                    dialogFragment.show(getChildFragmentManager(), "Poistetaanko linkitys käyttäjään?");
                    dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                        @Override
                        public void onDialogYesButtonClick() {
                            final String userConnectionId = userConnection.getUserConnectionId();
                            UserConnectionsResource.getInstance().removeUserConnection(userConnectionId, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                @Override
                                public void onDeleteDataSuccess() {
                                    userConnections.remove(userConnectionId);
                                    UserInvitationsResource.getInstance().removeUserInvitation(userConnectionId, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                        @Override
                                        public void onDeleteDataSuccess() {
                                            // Remove connection from user
                                            if(userConnection.getUserId() != null) {
                                                UsersResource.getInstance().getUser(userConnection.getUserId(), new GetUserHandler() {
                                                    @Override
                                                    public void onUserLoaded(User user) {
                                                        user.getTeamIds().remove(AppRes.getInstance().getSelectedTeam().getTeamId());
                                                        UsersResource.getInstance().editUser(user);
                                                        update();
                                                    }
                                                });
                                            } else {
                                                update();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
            holder.editUserConnectionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToEditUserConnectionFragment(userConnection);
                }
            });

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

        removeTeamButton.setVisibility(AppRes.getInstance().getUser().isAdmin() ? View.VISIBLE : View.GONE);

        update();

        editTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team team = AppRes.getInstance().getSelectedTeam();
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(team);
            }
        });

        addUserConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditUserConnectionFragment(null);
            }
        });

        inactivePlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToInactivePlayersFragment();
            }
        });

        removeTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppRes.getInstance().getPlayers().isEmpty()) {
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.team_settings_remove_players_first));
                    dialog.show(getChildFragmentManager(), "Poista ensin pelaajat");
                    return;
                }

                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.team_settings_remove_team_confirmation));
                dialogFragment.show(getChildFragmentManager(), "Poistetaanko joukkue ja data?");
                dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        deleteAllTeamData();
                    }
                });

            }
        });

        return v;
    }

    private void deleteAllTeamData() {
        // Delete all team data
        final Team team = AppRes.getInstance().getSelectedTeam();
        UserConnectionsResource.getInstance().getUserConnections(team.getTeamId(), new GetUserConnectionsHandler() {
            @Override
            public void onUserConnectionsLoaded(final Map<String, UserConnection> userConnections) {
                UserInvitationsResource.getInstance().removeUserInvitations(userConnections.keySet(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                    @Override
                    public void onDeleteDataSuccess() {
                        UserConnectionsResource.getInstance().removeUserConnections(team.getTeamId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                            @Override
                            public void onDeleteDataSuccess() {
                                LinesResource.getInstance().removeAllLines(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                    @Override
                                    public void onDeleteDataSuccess() {
                                        GameLinesResource.getInstance().removeAllLines(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                            @Override
                                            public void onDeleteDataSuccess() {
                                                GoalsResource.getInstance().removeAllGoals(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                    @Override
                                                    public void onDeleteDataSuccess() {
                                                        GamesResource.getInstance().removeAllGames(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                            @Override
                                                            public void onDeleteDataSuccess() {
                                                                TeamsResource.getInstance().removeTeam(team, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                                    @Override
                                                                    public void onDeleteDataSuccess() {
                                                                        SeasonsResource.getInstance().removeAllSeasons(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                                            @Override
                                                                            public void onDeleteDataSuccess() {
                                                                                User user = AppRes.getInstance().getUser();
                                                                                user.getTeamIds().remove(team.getTeamId());
                                                                                UsersResource.getInstance().editUser(user, new FirebaseDatabaseService.EditDataSuccessListener() {
                                                                                    @Override
                                                                                    public void onEditDataSuccess() {
                                                                                        AppRes.getInstance().setSeasons(null);
                                                                                        AppRes.getInstance().setUserConnections(null);
                                                                                        AppRes.getInstance().setLines(null);
                                                                                        AppRes.getInstance().setLinesByGame(null);
                                                                                        AppRes.getInstance().setGoalsByGame(null);
                                                                                        AppRes.getInstance().setGames(null);
                                                                                        AppRes.getInstance().setTeam(team.getTeamId(), null);
                                                                                        AppRes.getInstance().setSelectedTeam(null);

                                                                                        // Reset common user data
                                                                                        for(String userConnectionId : userConnections.keySet()) {
                                                                                            AppRes.getInstance().setUserInvitation(userConnectionId, null);
                                                                                        }
                                                                                        Logger.toast(R.string.team_settings_remove_team_successful);
                                                                                        AppRes.getActivity().finish();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
