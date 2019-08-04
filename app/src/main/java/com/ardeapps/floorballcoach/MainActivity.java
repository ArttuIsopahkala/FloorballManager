package com.ardeapps.floorballcoach;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.fragments.BluetoothFragment;
import com.ardeapps.floorballcoach.fragments.EditPlayerFragment;
import com.ardeapps.floorballcoach.fragments.EditTeamFragment;
import com.ardeapps.floorballcoach.fragments.EditUserConnectionFragment;
import com.ardeapps.floorballcoach.fragments.GameFragment;
import com.ardeapps.floorballcoach.fragments.GameSettingsFragment;
import com.ardeapps.floorballcoach.fragments.GamesFragment;
import com.ardeapps.floorballcoach.fragments.InactivePlayersFragment;
import com.ardeapps.floorballcoach.fragments.LinesFragment;
import com.ardeapps.floorballcoach.fragments.LoginFragment;
import com.ardeapps.floorballcoach.fragments.MainSelectionFragment;
import com.ardeapps.floorballcoach.fragments.PlayerStatsFragment;
import com.ardeapps.floorballcoach.fragments.PlayersFragment;
import com.ardeapps.floorballcoach.fragments.SettingsFragment;
import com.ardeapps.floorballcoach.fragments.TeamDashboardFragment;
import com.ardeapps.floorballcoach.fragments.TeamSettingsFragment;
import com.ardeapps.floorballcoach.fragments.TeamStatsFragment;
import com.ardeapps.floorballcoach.handlers.GetAppDataHandler;
import com.ardeapps.floorballcoach.handlers.GetGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetLinesHandler;
import com.ardeapps.floorballcoach.handlers.GetPlayersHandler;
import com.ardeapps.floorballcoach.handlers.GetSeasonsHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamsHandler;
import com.ardeapps.floorballcoach.handlers.GetUserConnectionsHandler;
import com.ardeapps.floorballcoach.handlers.GetUserHandler;
import com.ardeapps.floorballcoach.handlers.GetUserInvitationsHandler;
import com.ardeapps.floorballcoach.objects.AppData;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.objects.UserInvitation;
import com.ardeapps.floorballcoach.resources.AppDataResource;
import com.ardeapps.floorballcoach.resources.GoalsResource;
import com.ardeapps.floorballcoach.resources.LinesInGameResource;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.resources.PlayersResource;
import com.ardeapps.floorballcoach.resources.SeasonsResource;
import com.ardeapps.floorballcoach.resources.TeamsResource;
import com.ardeapps.floorballcoach.resources.UserConnectionsResource;
import com.ardeapps.floorballcoach.resources.UserInvitationsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
import com.ardeapps.floorballcoach.services.AppInviteService;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.GameFragmentData;
import com.ardeapps.floorballcoach.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballcoach.views.IconView;
import com.ardeapps.floorballcoach.views.Loader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    FrameLayout fragmentContainer;
    LoginFragment loginFragment;
    SettingsFragment settingsFragment;
    TeamDashboardFragment teamDashboardFragment;
    MainSelectionFragment mainSelectionFragment;
    EditTeamFragment editTeamFragment;
    EditPlayerFragment editPlayerFragment;
    PlayersFragment playersFragment;
    LinesFragment linesFragment;
    GameFragment gameFragment;
    GamesFragment gamesFragment;
    GameSettingsFragment gameSettingsFragment;
    BluetoothFragment bluetoothFragment;
    TeamSettingsFragment teamSettingsFragment;
    PlayerStatsFragment playerStatsFragment;
    EditUserConnectionFragment editUserConnectionFragment;
    TeamStatsFragment teamStatsFragment;
    InactivePlayersFragment inactivePlayersFragment;

    RelativeLayout loader;
    ImageView loaderSpinner;
    RelativeLayout menuTop;
    IconView backIcon;
    IconView settingsIcon;
    TextView titleText;

    private boolean isLoginUsed = false;
    private volatile boolean isOnDestroyCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = findViewById(R.id.fragmentContainer);
        loader = findViewById(R.id.loader);
        loaderSpinner = findViewById(R.id.loaderSpinner);
        backIcon = findViewById(R.id.backIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        titleText = findViewById(R.id.titleText);
        menuTop = findViewById(R.id.menuTop);

        loginFragment = new LoginFragment();
        settingsFragment = new SettingsFragment();
        mainSelectionFragment = new MainSelectionFragment();
        teamDashboardFragment = new TeamDashboardFragment();
        editTeamFragment = new EditTeamFragment();
        editPlayerFragment = new EditPlayerFragment();
        playersFragment = new PlayersFragment();
        linesFragment = new LinesFragment();
        gameFragment = new GameFragment();
        gamesFragment = new GamesFragment();
        gameSettingsFragment = new GameSettingsFragment();
        bluetoothFragment = new BluetoothFragment();
        teamSettingsFragment = new TeamSettingsFragment();
        playerStatsFragment = new PlayerStatsFragment();
        editUserConnectionFragment = new EditUserConnectionFragment();
        teamStatsFragment = new TeamStatsFragment();
        inactivePlayersFragment = new InactivePlayersFragment();

        AppRes.getInstance().setActivity(this);
        Loader.create(loader, loaderSpinner);

        Helper.installShortcutIfNeeded();
        formatMenuBar(null);
        setListeners();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onUserLoggedIn(user.getUid());
        } else {
            FragmentListeners.getInstance().getFragmentChangeListener().goToLoginFragment();
        }
    }

    private void onUserLoggedIn(String userId) {
        AppRes.getInstance().resetData();
        UsersResource.getInstance().getUser(userId, new GetUserHandler() {
            @Override
            public void onUserLoaded(final User user) {
                // If user is removed from database, remove auth also
                if(user == null) {
                    Logger.toast(R.string.login_error_user_not_found);
                    FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(authUser != null) {
                        authUser.delete();
                    }
                    FirebaseAuth.getInstance().signOut();
                    FragmentListeners.getInstance().getFragmentChangeListener().goToLoginFragment();
                    return;
                }
                // Update lastLoginTime silently
                user.setLastLoginTime(System.currentTimeMillis());
                UsersResource.getInstance().editUser(user);
                AppRes.getInstance().setUser(user);

                AppDataResource.getInstance().getAppData(new GetAppDataHandler() {
                    @Override
                    public void onAppDataLoaded() {
                        if (BuildConfig.VERSION_CODE < AppData.NEWEST_VERSION_CODE) {
                            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.update_new_version));
                            dialogFragment.show(getSupportFragmentManager(), "Päivitä uusin versio");
                            dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                                @Override
                                public void onDialogYesButtonClick() {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(getString(R.string.google_play_app_url)));
                                    startActivity(i);
                                }
                            });
                        }

                        UserInvitationsResource.getInstance().getUserInvitations(new GetUserInvitationsHandler() {
                            @Override
                            public void onUserInvitationsLoaded(Map<String, UserInvitation> userInvitations) {
                                AppRes.getInstance().setUserInvitations(userInvitations);
                                if(!user.getTeamIds().isEmpty()) {
                                    TeamsResource.getInstance().getTeams(user.getTeamIds(), new GetTeamsHandler() {
                                        @Override
                                        public void onTeamsLoaded(final Map<String, Team> teams) {
                                            AppRes.getInstance().setTeams(teams);
                                            FragmentListeners.getInstance().getFragmentChangeListener().goToMainSelectionFragment();
                                        }
                                    });
                                } else {
                                    FragmentListeners.getInstance().getFragmentChangeListener().goToMainSelectionFragment();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * SET FRAGMENT LISTENERS
     */
    private void setListeners() {
        FragmentListeners.getInstance().setFragmentChangeListener(new FragmentListeners.FragmentChangeListener() {
            @Override
            public void goToLoginFragment() {
                // Pop back stack to prevent user navigate back to app
                FragmentManager fm = getSupportFragmentManager();
                int count = fm.getBackStackEntryCount();
                for(int i = 0; i < count; ++i) {
                    fm.popBackStack();
                }

                // This or TeamDashboardFragment is added at first
                isLoginUsed = true;
                if(loginFragment.isAdded()) {
                    switchToFragment(loginFragment);
                } else {
                    addFragment(loginFragment);
                }
            }

            @Override
            public void goToMainSelectionFragment() {
                if(mainSelectionFragment.isAdded()) {
                    switchToFragment(mainSelectionFragment);
                } else {
                    addFragment(mainSelectionFragment);
                }
            }

            @Override
            public void goToTeamDashboardFragment(final Team team) {
                // Check if team has connection to this user
                UserConnectionsResource.getInstance().getUserConnections(team.getTeamId(), new GetUserConnectionsHandler() {
                    @Override
                    public void onUserConnectionsLoaded(Map<String, UserConnection> userConnections) {
                        final User user = AppRes.getInstance().getUser();
                        boolean connectionFound = false;
                        for(UserConnection userConnection : userConnections.values()) {
                            if(user.getUserId().equals(userConnection.getUserId())) {
                                connectionFound = true;
                                UserConnection.Role role = UserConnection.Role.fromDatabaseName(userConnection.getRole());
                                AppRes.getInstance().setSelectedRole(role);
                                break;
                            }
                        }

                        if(!connectionFound) {
                            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.main_selection_connection_not_found));
                            dialogFragment.show(getSupportFragmentManager(), "Ei oikeutta joukkueeseen. Poistetaanko?");
                            dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                                @Override
                                public void onDialogYesButtonClick() {
                                    user.getTeamIds().remove(team.getTeamId());
                                    AppRes.getInstance().setUser(user);
                                    UsersResource.getInstance().editUser(user, new FirebaseDatabaseService.EditDataSuccessListener() {
                                        @Override
                                        public void onEditDataSuccess() {
                                            AppRes.getInstance().setTeam(team.getTeamId(), null);
                                            mainSelectionFragment.update();
                                        }
                                    });
                                }
                            });
                            return;
                        }

                        // Store selected team and load team data
                        AppRes.getInstance().setUserConnections(userConnections);
                        AppRes.getInstance().setSelectedTeam(team);
                        LinesResource.getInstance().getLines(new GetLinesHandler() {
                            @Override
                            public void onLinesLoaded(Map<Integer, Line> lines) {
                                AppRes.getInstance().setLines(lines);
                                PlayersResource.getInstance().getPlayers(new GetPlayersHandler() {
                                    @Override
                                    public void onPlayersLoaded(Map<String, Player> players) {
                                        AppRes.getInstance().setPlayers(players);
                                        SeasonsResource.getInstance().getSeasons(new GetSeasonsHandler() {
                                            @Override
                                            public void onSeasonsLoaded(Map<String, Season> seasons) {
                                                AppRes.getInstance().setSeasons(seasons);
                                                // Set selected season from preferences
                                                String seasonId = PrefRes.getSelectedSeasonId(team.getTeamId());
                                                Season selectedSeason = seasons.get(seasonId);
                                                AppRes.getInstance().setSelectedSeason(selectedSeason);

                                                // This or LoginFragment is added at first
                                                if(teamDashboardFragment.isAdded()) {
                                                    switchToFragment(teamDashboardFragment);
                                                } else {
                                                    addFragment(teamDashboardFragment);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void goToEditTeamFragment(Team team) {
                editTeamFragment.setData(team);
                switchToFragment(editTeamFragment);
            }

            @Override
            public void goToEditPlayerFragment(Player player) {
                editPlayerFragment.setData(player);
                switchToFragment(editPlayerFragment);
            }

            @Override
            public void goToPlayersFragment() {
                switchToFragment(playersFragment);
            }

            @Override
            public void goToLinesFragment() {
                switchToFragment(linesFragment);
            }

            @Override
            public void goToSettingsFragment() {
                switchToFragment(settingsFragment);
            }

            @Override
            public void goToGameFragment(final Game game) {
                final GameFragmentData fragmentData = new GameFragmentData();
                fragmentData.setGame(game);
                LinesInGameResource.getInstance().getLines(game.getGameId(), new GetLinesHandler() {
                    @Override
                    public void onLinesLoaded(Map<Integer, Line> lines) {
                        fragmentData.setLines(lines);

                        GoalsResource.getInstance().getGoals(game.getGameId(), new GetGoalsHandler() {
                            @Override
                            public void onGoalsLoaded(Map<String, Goal> goals) {
                                fragmentData.setGoals(goals);
                                gameFragment.setData(fragmentData);
                                switchToFragment(gameFragment);
                            }
                        });
                    }
                });
            }

            @Override
            public void goToGameSettingsFragment(GameSettingsFragmentData gameSettingsFragmentData) {
                gameSettingsFragment.setData(gameSettingsFragmentData);
                switchToFragment(gameSettingsFragment);
            }

            @Override
            public void goToGamesFragment() {
                switchToFragment(gamesFragment);
            }

            @Override
            public void goToBluetoothFragment() {
                switchToFragment(bluetoothFragment);
            }

            @Override
            public void goToPlayerStatsFragment(final Player player) {
                playerStatsFragment.setData(player);
                switchToFragment(playerStatsFragment);
            }

            @Override
            public void goToTeamStatsFragment() {
                switchToFragment(teamStatsFragment);
            }

            @Override
            public void goToEditUserConnectionFragment(UserConnection userConnection) {
                editUserConnectionFragment.setData(userConnection);
                switchToFragment(editUserConnectionFragment);
            }

            @Override
            public void goToTeamSettingsFragment() {
                teamSettingsFragment.refreshData();
                switchToFragment(teamSettingsFragment);
            }

            @Override
            public void goToInactivePlayersFragment() {
                switchToFragment(inactivePlayersFragment);
            }
        });

        editPlayerFragment.setListener(new EditPlayerFragment.Listener() {
            @Override
            public void onPlayerEdited(Player player) {
                AppRes.getInstance().setPlayer(player.getPlayerId(), player);
                playerStatsFragment.setData(player);
                // Back pressed to pop back stack
                onBackPressed();
            }
        });

        editTeamFragment.setListener(new EditTeamFragment.Listener() {
            @Override
            public void onTeamEdited(Team team) {
                AppRes.getInstance().setTeam(team.getTeamId(), team);
                AppRes.getInstance().setSelectedTeam(team);
                // Back pressed to pop back stack
                onBackPressed();
            }
        });

        gameSettingsFragment.setListener(new GameSettingsFragment.Listener() {
            @Override
            public void onGameEdited(Game game, Map<Integer, Line> lines) {
                GameFragmentData gameFragmentData = gameFragment.getData();
                gameFragmentData.setGame(game);
                gameFragmentData.setLines(lines);
                gameFragment.setData(gameFragmentData);
                gameFragment.update();
                // Back pressed to pop back stack
                onBackPressed();
            }

            @Override
            public void onGameCreated(Game game, Map<Integer, Line> lines) {
                // Back pressed to pop back stack
                getSupportFragmentManager().popBackStack();
                FragmentListeners.getInstance().getFragmentChangeListener().goToGameFragment(game);
            }
        });
        loginFragment.setListener(new LoginFragment.Listener() {
            @Override
            public void onLogIn(String userId) {
                onUserLoggedIn(userId);
            }
        });
        editUserConnectionFragment.setListener(new EditUserConnectionFragment.Listener() {
            @Override
            public void onUserConnectionEdited(UserConnection userConnection) {
                teamSettingsFragment.refreshData();
                teamSettingsFragment.update();
                // Back pressed to pop back stack
                onBackPressed();

                // Ask to send invitation message if user not found
                if(userConnection.getUserId() == null) {
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_user_connection_player_not_exists));
                    dialogFragment.show(getSupportFragmentManager(), "Lähetetäänkö kutsu joukkueeseen?");
                    dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                        @Override
                        public void onDialogYesButtonClick() {
                            AppInviteService.openChooser();
                        }
                    });
                }
            }
        });
    }

    /**
     * MAIN ACTIVITY STATIC METHODS
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            FragmentListeners.getInstance().getPermissionHandledListener().onPermissionGranted(requestCode);
        } else {
            Logger.toast(R.string.error_permission_not_granted);
        }
    }

    public void openFragment(Fragment newFrag) {
        if (!isOnDestroyCalled) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, newFrag)
                    .commitAllowingStateLoss();

            formatMenuBar(newFrag);
        }
    }

    public void addFragment(Fragment newFrag) {
        if (!isOnDestroyCalled) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, newFrag)
                    .commitAllowingStateLoss();

            formatMenuBar(newFrag);
        }
    }

    public void switchToFragment(Fragment newFrag) {
        if (!isOnDestroyCalled) {
            // Replace fragmentCotainer with your container id
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            // Return if the class are the same
            if(currentFragment.getClass().equals(newFrag.getClass())) return;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, newFrag)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();

            formatMenuBar(newFrag);
        }
    }

    private void formatMenuBar(Fragment f) {
        menuTop.setVisibility(View.VISIBLE);
        backIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);
        if(f instanceof LoginFragment) {
            menuTop.setVisibility(View.GONE);
        } else if(f instanceof SettingsFragment) {
            titleText.setText(R.string.title_settings);
            settingsIcon.setVisibility(View.GONE);
        } else if(f instanceof TeamDashboardFragment) {
            backIcon.setVisibility(View.GONE);
            titleText.setText(AppRes.getInstance().getSelectedTeam().getName());
        } else if(f instanceof MainSelectionFragment) {
            backIcon.setVisibility(View.GONE);
            titleText.setText(R.string.title_teams);
        } else if(f instanceof EditTeamFragment) {
            titleText.setVisibility(View.GONE);
        } else if(f instanceof EditPlayerFragment) {
            titleText.setVisibility(View.GONE);
        } else if(f instanceof PlayersFragment) {
            titleText.setText(R.string.title_manage_players);
        } else if(f instanceof LinesFragment) {
            titleText.setText(R.string.title_lines_default);
        } else if(f instanceof GameFragment) {
            titleText.setText(R.string.title_game);
        } else if(f instanceof GamesFragment) {
            titleText.setText(R.string.title_games);
        } else if(f instanceof GameSettingsFragment) {
            titleText.setText(R.string.title_game_settings);
        } else if(f instanceof TeamSettingsFragment) {
            titleText.setText(R.string.title_team_settings);
        } else if(f instanceof PlayerStatsFragment) {
            titleText.setText(R.string.title_player_stats);
        } else if(f instanceof InactivePlayersFragment) {
            titleText.setText(R.string.title_inactive_players);
        } else if(f instanceof EditUserConnectionFragment) {
            titleText.setVisibility(View.GONE);
        } else if(f instanceof TeamStatsFragment) {
            titleText.setText(R.string.title_team_stats);
        } else {
            menuTop.setVisibility(View.GONE);
        }

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToSettingsFragment();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Set values for database call
        FirebaseDatabaseService.isDatabaseCallInterrupted(true);
        if (Loader.isVisible()) {
            Loader.hide();
        }

        int backStack = getSupportFragmentManager().getBackStackEntryCount();

        // Do not allow user go back to login screen
        if (backStack == 1 && isLoginUsed) {
            finishAffinity();
        } else {
            Helper.hideKeyBoard(getWindow().getDecorView());
            super.onBackPressed();

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            formatMenuBar(f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOnDestroyCalled = true;
    }
}
