package com.ardeapps.floorballmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballmanager.fragments.AcceptUserRequestFragment;
import com.ardeapps.floorballmanager.fragments.BluetoothFragment;
import com.ardeapps.floorballmanager.fragments.EditPlayerFragment;
import com.ardeapps.floorballmanager.fragments.EditTeamFragment;
import com.ardeapps.floorballmanager.fragments.EditUserConnectionFragment;
import com.ardeapps.floorballmanager.fragments.GameFragment;
import com.ardeapps.floorballmanager.fragments.GameSettingsFragment;
import com.ardeapps.floorballmanager.fragments.GamesFragment;
import com.ardeapps.floorballmanager.fragments.InactivePlayersFragment;
import com.ardeapps.floorballmanager.fragments.LinesFragment;
import com.ardeapps.floorballmanager.fragments.LoginFragment;
import com.ardeapps.floorballmanager.fragments.PlayerStatsFragment;
import com.ardeapps.floorballmanager.fragments.PlayersFragment;
import com.ardeapps.floorballmanager.fragments.SearchTeamFragment;
import com.ardeapps.floorballmanager.fragments.SettingsFragment;
import com.ardeapps.floorballmanager.fragments.TeamDashboardFragment;
import com.ardeapps.floorballmanager.fragments.TeamSelectionFragment;
import com.ardeapps.floorballmanager.fragments.TeamSettingsFragment;
import com.ardeapps.floorballmanager.fragments.TeamStatsFragment;
import com.ardeapps.floorballmanager.handlers.GetGameGoalsHandler;
import com.ardeapps.floorballmanager.handlers.GetGamePenaltiesHandler;
import com.ardeapps.floorballmanager.handlers.PermissionDenyHandler;
import com.ardeapps.floorballmanager.objects.AppData;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserInvitation;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.resources.AppDataResource;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.LinesResource;
import com.ardeapps.floorballmanager.resources.PenaltiesResource;
import com.ardeapps.floorballmanager.resources.PlayersResource;
import com.ardeapps.floorballmanager.resources.SeasonsResource;
import com.ardeapps.floorballmanager.resources.TeamsResource;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UserInvitationsResource;
import com.ardeapps.floorballmanager.resources.UserRequestsResource;
import com.ardeapps.floorballmanager.resources.UsersResource;
import com.ardeapps.floorballmanager.services.AppInviteService;
import com.ardeapps.floorballmanager.services.FirebaseDatabaseService;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.tacticBoard.TacticBoardFragment;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.GameFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballmanager.views.IconView;
import com.ardeapps.floorballmanager.views.Loader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    FrameLayout fragmentContainer;
    LoginFragment loginFragment;
    SettingsFragment settingsFragment;
    TeamDashboardFragment teamDashboardFragment;
    TeamSelectionFragment teamSelectionFragment;
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
    SearchTeamFragment searchTeamFragment;
    AcceptUserRequestFragment acceptUserRequestFragment;
    TacticBoardFragment tacticBoardFragment;

    RelativeLayout loader;
    ImageView loaderSpinner;
    RelativeLayout menuTop;
    IconView backIcon;
    IconView settingsIcon;
    ImageView boardIcon;
    TextView titleText;
    ImageView newInvitationMark;

    private boolean isFirstViewAdded = false;
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
        boardIcon = findViewById(R.id.boardIcon);
        titleText = findViewById(R.id.titleText);
        menuTop = findViewById(R.id.menuTop);
        newInvitationMark = findViewById(R.id.newInvitationMark);

        loginFragment = new LoginFragment();
        settingsFragment = new SettingsFragment();
        teamSelectionFragment = new TeamSelectionFragment();
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
        searchTeamFragment = new SearchTeamFragment();
        acceptUserRequestFragment = new AcceptUserRequestFragment();
        tacticBoardFragment = new TacticBoardFragment();

        AppRes.getInstance().setActivity(this);
        Loader.create(loader, loaderSpinner);

        Helper.installShortcutIfNeeded();
        formatMenuBar(null);
        setListeners();

        // TODO remove
        FragmentListeners.getInstance().getFragmentChangeListener().goToTacticBoardFragment();

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            onUserLoggedIn(user.getUid());
        } else {
            FragmentListeners.getInstance().getFragmentChangeListener().goToLoginFragment();
        }*/
    }

    private void onUserLoggedIn(String userId) {
        AppRes.getInstance().resetData();
        AppDataResource.getInstance().getAppData(() -> {
            if (BuildConfig.VERSION_CODE < AppData.NEWEST_VERSION_CODE) {
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.update_new_version));
                dialogFragment.show(getSupportFragmentManager(), "Päivitä uusin versio");
                dialogFragment.setCancelable(false);
                dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogYesNoListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(AppData.GOOGLE_PLAY_APP_URL));
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onDialogNoButtonClick() {
                        loadUserData(userId);
                    }
                });
            } else {
                loadUserData(userId);
            }
        });
    }

    private void loadUserData(String userId) {
        UsersResource.getInstance().getUser(userId, user -> {
            // If user is removed from database, remove auth also
            if (user == null) {
                Logger.toast(R.string.login_error_user_not_found);
                FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
                if (authUser != null) {
                    authUser.delete();
                }
                FirebaseAuth.getInstance().signOut();
                FragmentActivity activity = AppRes.getActivity();
                Intent i = new Intent(activity, MainActivity.class);
                activity.finish();
                activity.overridePendingTransition(0, 0);
                activity.startActivity(i);
                activity.overridePendingTransition(0, 0);
                return;
            }
            // Update lastLoginTime silently
            user.setLastLoginTime(System.currentTimeMillis());
            UsersResource.getInstance().editUser(user);
            AppRes.getInstance().setUser(user);

            UserInvitationsResource.getInstance().getUserInvitations(userInvitations -> {
                AppRes.getInstance().setUserInvitations(userInvitations);
                UserRequestsResource.getInstance().getUserRequestsAsUser(AppRes.getInstance().getUser().getUserId(), userRequests -> {
                    final Map<String, UserRequest> filteredUserRequests = new HashMap<>();
                    final Map<String, UserRequest> acceptedUserRequests = new HashMap<>();
                    // Check if user request is accepted and add to user teams
                    for(UserRequest userRequest : userRequests.values()) {
                        if(UserRequest.Status.fromDatabaseName(userRequest.getStatus()) == UserRequest.Status.ACCEPTED) {
                            acceptedUserRequests.put(userRequest.getUserConnectionId(), userRequest);
                        } else {
                            filteredUserRequests.put(userRequest.getUserConnectionId(), userRequest);
                        }
                    }
                    AppRes.getInstance().setUserRequests(filteredUserRequests);

                    if(!acceptedUserRequests.isEmpty()) {
                        for(UserRequest userRequest : acceptedUserRequests.values()) {
                            user.getTeamIds().add(userRequest.getTeamId());
                        }
                        // Add new teams
                        UsersResource.getInstance().editUser(user, () -> {
                            // Remove accepted user requests
                            UserRequestsResource.getInstance().removeUserRequests(acceptedUserRequests.keySet(), this::openFirstView);
                        });
                    } else {
                        openFirstView();
                    }
                });
            });
        });
    }

    private void openFirstView() {
        // Go straight to team dashboard if there is already selected team
        User user = AppRes.getInstance().getUser();
        String selectedTeamId = PrefRes.getSelectedTeamId();
        if(user.getTeamIds().isEmpty() || selectedTeamId == null) {
            FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSelectionFragment();
        } else {
            TeamsResource.getInstance().getTeam(selectedTeamId, true, team -> {
                if(team != null) {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToTeamDashboardFragment(team, () -> {
                        FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSelectionFragment();
                    });
                } else {
                    PrefRes.setSelectedTeamId(null);
                    FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSelectionFragment();
                }
            });
        }
    }

    /**
     * SET FRAGMENT LISTENERS
     */
    private void setListeners() {
        FragmentListeners.getInstance().setApplicationListener(() -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            formatMenuBar(f);
        });

        FragmentListeners.getInstance().setFragmentChangeListener(new FragmentListeners.FragmentChangeListener() {
            @Override
            public void goToLoginFragment() {
                isLoginUsed = true;
                isFirstViewAdded = true;
                addFragment(loginFragment);
            }

            @Override
            public void goToTeamSelectionFragment() {
                User user = AppRes.getInstance().getUser();
                TeamsResource.getInstance().getTeams(user.getTeamIds(), teams -> {
                    AppRes.getInstance().setTeams(teams);
                    if (!isFirstViewAdded) {
                        addFragment(teamSelectionFragment);
                    } else {
                        switchToFragment(teamSelectionFragment);
                    }
                    isFirstViewAdded = true;
                });
            }

            @Override
            public void goToTeamDashboardFragment(final Team team, PermissionDenyHandler handler) {
                UserConnectionsResource.getInstance().getUserConnections(team.getTeamId(), userConnections -> {
                    User user = AppRes.getInstance().getUser();
                    boolean connectionFound = false;
                    for (UserConnection userConnection : userConnections.values()) {
                        if (user.getUserId().equals(userConnection.getUserId())) {
                            connectionFound = true;
                            UserConnection.Role role = UserConnection.Role.fromDatabaseName(userConnection.getRole());
                            AppRes.getInstance().setSelectedRole(role);
                            AppRes.getInstance().setSelectedPlayerId(userConnection.getPlayerId());
                            break;
                        }
                    }

                    if (!connectionFound) {
                        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.team_selection_connection_not_found));
                        dialog.show(getSupportFragmentManager(), "Ei oikeuksia joukkueeseen.");

                        if(team.getTeamId().equals(PrefRes.getSelectedTeamId())) {
                            PrefRes.setSelectedTeamId(null);
                        }
                        user.getTeamIds().remove(team.getTeamId());
                        AppRes.getInstance().setUser(user);
                        AppRes.getInstance().setTeam(team.getTeamId(), null);
                        UsersResource.getInstance().editUser(user, handler::onPermissionDenied);
                    } else {
                        // Empty back stack and open new team
                        FragmentManager fm = getSupportFragmentManager();
                        int count = fm.getBackStackEntryCount();
                        for (int i = 0; i < count; ++i) {
                            fm.popBackStack();
                        }

                        // Store selected team and load team data
                        AppRes.getInstance().setUserConnections(userConnections);
                        AppRes.getInstance().setSelectedTeam(team);
                        PrefRes.setSelectedTeamId(team.getTeamId());

                        UserRequestsResource.getInstance().getUserRequests(team.getTeamId(), userRequests -> {
                            AppRes.getInstance().setUserJoinRequests(userRequests);
                            LinesResource.getInstance().getLines(lines -> {
                                AppRes.getInstance().setLines(lines);
                                PlayersResource.getInstance().getPlayers(players -> {
                                    AppRes.getInstance().setPlayers(players);
                                    SeasonsResource.getInstance().getSeasons(seasons -> {
                                        AppRes.getInstance().setSeasons(seasons);
                                        // Set selected season from preferences
                                        String seasonId = PrefRes.getSelectedSeasonId(team.getTeamId());
                                        if (StringUtils.isEmptyString(seasonId) && !seasons.isEmpty()) {
                                            seasonId = seasons.values().iterator().next().getSeasonId();
                                        }
                                        Season selectedSeason = seasons.get(seasonId);
                                        AppRes.getInstance().setSelectedSeason(selectedSeason);

                                        if (!isFirstViewAdded) {
                                            addFragment(teamDashboardFragment);
                                        } else {
                                            switchToFragment(teamDashboardFragment);
                                        }
                                        isFirstViewAdded = true;
                                    });
                                });
                            });
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
                GameLinesResource.getInstance().getLines(game.getGameId(), lines -> {
                    fragmentData.setLines(lines);

                    GoalsResource.getInstance().getGoals(game.getGameId(), (GetGameGoalsHandler) goals -> {
                        fragmentData.setGoals(goals);
                        PenaltiesResource.getInstance().getPenalties(game.getGameId(), (GetGamePenaltiesHandler) penalties -> {
                            fragmentData.setPenalties(penalties);
                            gameFragment.setData(fragmentData);
                            switchToFragment(gameFragment);
                        });
                    });
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
            public void goToSearchTeamFragment() {
                switchToFragment(searchTeamFragment);
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
            public void goToAcceptUserRequestFragment(UserRequest userRequest) {
                acceptUserRequestFragment.setData(userRequest);
                switchToFragment(acceptUserRequestFragment);
            }

            @Override
            public void goToTeamSettingsFragment() {
                switchToFragment(teamSettingsFragment);
            }

            @Override
            public void goToInactivePlayersFragment() {
                switchToFragment(inactivePlayersFragment);
            }

            @Override
            public void goToTacticBoardFragment() {
                switchToFragment(tacticBoardFragment);
            }
        });

        editPlayerFragment.setListener(player -> {
            AppRes.getInstance().setPlayer(player.getPlayerId(), player);
            playerStatsFragment.setData(player);
            // Back pressed to pop back stack
            onBackPressed();
        });

        editTeamFragment.setListener(team -> {
            AppRes.getInstance().setTeam(team.getTeamId(), team);
            Team currentTeam = AppRes.getInstance().getSelectedTeam();
            if(currentTeam != null && currentTeam.getTeamId().equals(team.getTeamId())) {
                AppRes.getInstance().setSelectedTeam(team);
            }
            // Back pressed to pop back stack
            onBackPressed();
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
        loginFragment.setListener(this::onUserLoggedIn);
        editUserConnectionFragment.setListener(userConnection -> {
            // Back pressed to pop back stack
            onBackPressed();

            UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnection.getStatus());
            if (status != UserConnection.Status.CONNECTED) {
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_user_connection_ask_invitation));
                dialogFragment.show(getSupportFragmentManager(), "Lähetetäänkö kutsu joukkueeseen?");
                dialogFragment.setListener(AppInviteService::openChooser);
            }
        });
    }

    /**
     * MAIN ACTIVITY STATIC METHODS
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            FragmentListeners.getInstance().getPermissionHandledListener().onPermissionGranted(requestCode);
        } else {
            Logger.toast(R.string.error_permission_not_granted);
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
            // Replace fragmentContainer with your container id
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            // Return if the class are the same
            if (currentFragment != null && currentFragment.getClass().equals(newFrag.getClass())) return;
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
        boardIcon.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.VISIBLE);
        Map<String, UserInvitation> userInvitations = AppRes.getInstance().getUserInvitations();
        if(userInvitations.isEmpty()) {
            newInvitationMark.setVisibility(View.GONE);
        } else {
            newInvitationMark.setVisibility(View.VISIBLE);
        }

        if (f instanceof LoginFragment) {
            menuTop.setVisibility(View.GONE);
        } else if (f instanceof SettingsFragment) {
            titleText.setText(R.string.title_settings);
            settingsIcon.setVisibility(View.GONE);
            boardIcon.setVisibility(View.GONE);
            newInvitationMark.setVisibility(View.GONE);
        } else if (f instanceof TeamDashboardFragment) {
            backIcon.setVisibility(View.GONE);
            titleText.setText(AppRes.getInstance().getSelectedTeam().getName());
        } else if (f instanceof TeamSelectionFragment) {
            backIcon.setVisibility(AppRes.getInstance().getSelectedTeam() != null ? View.VISIBLE : View.GONE);
            titleText.setText(R.string.title_teams);
        } else if (f instanceof EditTeamFragment) {
            titleText.setText(R.string.title_edit_team);
        } else if (f instanceof EditPlayerFragment) {
            titleText.setText(R.string.title_edit_player);
        } else if (f instanceof PlayersFragment) {
            titleText.setText(R.string.title_manage_players);
        } else if (f instanceof LinesFragment) {
            titleText.setText(R.string.title_lines_default);
        } else if (f instanceof GameFragment) {
            titleText.setText(R.string.title_game);
        } else if (f instanceof GamesFragment) {
            titleText.setText(R.string.title_games);
        } else if (f instanceof GameSettingsFragment) {
            titleText.setText(R.string.title_game_settings);
        } else if (f instanceof TeamSettingsFragment) {
            titleText.setText(R.string.title_team_settings);
        } else if (f instanceof PlayerStatsFragment) {
            titleText.setText(R.string.title_player_stats);
        } else if (f instanceof InactivePlayersFragment) {
            titleText.setText(R.string.title_inactive_players);
        } else if (f instanceof EditUserConnectionFragment) {
            titleText.setText(R.string.title_edit_user_connection);
        } else if (f instanceof TeamStatsFragment) {
            titleText.setText(R.string.title_team_stats);
        } else if (f instanceof SearchTeamFragment) {
            titleText.setText(R.string.title_search_team);
        } else if (f instanceof AcceptUserRequestFragment) {
            titleText.setText(R.string.title_accept_user_request);
        } else if (f instanceof TacticBoardFragment) {
            titleText.setVisibility(View.GONE);
            settingsIcon.setVisibility(View.GONE);
            boardIcon.setVisibility(View.GONE);
            newInvitationMark.setVisibility(View.GONE);
        }  else {
            menuTop.setVisibility(View.GONE);
        }

        backIcon.setOnClickListener(v -> onBackPressed());
        settingsIcon.setOnClickListener(v -> FragmentListeners.getInstance().getFragmentChangeListener().goToSettingsFragment());
        boardIcon.setOnClickListener(v -> FragmentListeners.getInstance().getFragmentChangeListener().goToTacticBoardFragment());
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
