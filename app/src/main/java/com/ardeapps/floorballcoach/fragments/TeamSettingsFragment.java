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
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.resources.TeamsGamesGoalsResource;
import com.ardeapps.floorballcoach.resources.TeamsGamesLinesResource;
import com.ardeapps.floorballcoach.resources.TeamsGamesResource;
import com.ardeapps.floorballcoach.resources.TeamsLinesResource;
import com.ardeapps.floorballcoach.resources.TeamsResource;
import com.ardeapps.floorballcoach.resources.TeamsSeasonsResource;
import com.ardeapps.floorballcoach.resources.TeamsUserConnectionsResource;
import com.ardeapps.floorballcoach.resources.UserInvitationsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.HashMap;
import java.util.Map;


public class TeamSettingsFragment extends Fragment {

    Button editTeamButton;
    Button addUserConnectionButton;
    Button removeTeamButton;
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

        for(final UserConnection userConnection : userConnections.values()) {
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
                            TeamsUserConnectionsResource.getInstance().removeUserConnection(userConnectionId, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                @Override
                                public void onDeleteDataSuccess() {
                                    userConnections.remove(userConnectionId);
                                    UserInvitationsResource.getInstance().removeUserInvitation(userConnectionId, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                        @Override
                                        public void onDeleteDataSuccess() {
                                            update();
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
        TeamsUserConnectionsResource.getInstance().getUserConnections(team.getTeamId(), new GetUserConnectionsHandler() {
            @Override
            public void onUserConnectionsLoaded(final Map<String, UserConnection> userConnections) {
                UserInvitationsResource.getInstance().removeUserInvitations(userConnections.keySet(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                    @Override
                    public void onDeleteDataSuccess() {
                        TeamsUserConnectionsResource.getInstance().removeUserConnections(team.getTeamId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                            @Override
                            public void onDeleteDataSuccess() {
                                TeamsLinesResource.getInstance().removeLines(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                    @Override
                                    public void onDeleteDataSuccess() {
                                        TeamsGamesLinesResource.getInstance().removeLines(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                            @Override
                                            public void onDeleteDataSuccess() {
                                                TeamsGamesGoalsResource.getInstance().removeGoals(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                    @Override
                                                    public void onDeleteDataSuccess() {
                                                        TeamsGamesResource.getInstance().removeGames(new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                            @Override
                                                            public void onDeleteDataSuccess() {
                                                                TeamsResource.getInstance().removeTeam(team, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                                                    @Override
                                                                    public void onDeleteDataSuccess() {
                                                                        TeamsSeasonsResource.getInstance().removeSeasons(new FirebaseDatabaseService.DeleteDataSuccessListener() {
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
