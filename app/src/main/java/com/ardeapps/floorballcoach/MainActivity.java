package com.ardeapps.floorballcoach;

import android.content.pm.PackageManager;
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

import com.ardeapps.floorballcoach.fragments.BluetoothFragment;
import com.ardeapps.floorballcoach.fragments.EditPlayerFragment;
import com.ardeapps.floorballcoach.fragments.EditTeamFragment;
import com.ardeapps.floorballcoach.fragments.GameFragment;
import com.ardeapps.floorballcoach.fragments.GameSettingsFragment;
import com.ardeapps.floorballcoach.fragments.GamesFragment;
import com.ardeapps.floorballcoach.fragments.LinesFragment;
import com.ardeapps.floorballcoach.fragments.LoginFragment;
import com.ardeapps.floorballcoach.fragments.MainSelectionFragment;
import com.ardeapps.floorballcoach.fragments.PlayersFragment;
import com.ardeapps.floorballcoach.fragments.SettingsFragment;
import com.ardeapps.floorballcoach.fragments.TeamDashboardFragment;
import com.ardeapps.floorballcoach.handlers.GetGamesHandler;
import com.ardeapps.floorballcoach.handlers.GetGoalsHandler;
import com.ardeapps.floorballcoach.handlers.GetLinesHandler;
import com.ardeapps.floorballcoach.handlers.GetPlayersHandler;
import com.ardeapps.floorballcoach.handlers.GetTeamsHandler;
import com.ardeapps.floorballcoach.handlers.GetUserHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.resources.GamesResource;
import com.ardeapps.floorballcoach.resources.GoalsByTeamResource;
import com.ardeapps.floorballcoach.resources.LinesResource;
import com.ardeapps.floorballcoach.resources.LinesTeamGameResource;
import com.ardeapps.floorballcoach.resources.PlayersResource;
import com.ardeapps.floorballcoach.resources.TeamsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
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

        Loader.create(loader, loaderSpinner);

        Helper.installShortcutIfNeeded();

        setListeners();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onUserLoggedIn(user.getUid());
        } else {
            FragmentListeners.getInstance().getFragmentChangeListener().goToLoginFragment();
        }
    }

    private void onUserLoggedIn(String userId) {
        UsersResource.getInstance().getUser(userId, new GetUserHandler() {
            @Override
            public void onUserLoaded(User user) {
                // Update lastLoginTime silently
                user.setLastLoginTime(System.currentTimeMillis());
                UsersResource.getInstance().editUser(user);
                AppRes.getInstance().setUser(user);

                // TODO of first time -> go main selection -> otherwise go dashboard
                if(!user.getTeamIds().isEmpty()) {
                    TeamsResource.getInstance().getTeams(user.getTeamIds(), new GetTeamsHandler() {
                        @Override
                        public void onTeamsLoaded(final Map<String, Team> teams) {
                            AppRes.getInstance().setTeams(teams);

                            FragmentListeners.getInstance().getFragmentChangeListener().goToMainSelectionFragment();
                        }
                    });
                } else {
                    Logger.log("EI TIIMEJÄ");
                    Logger.toast("EI TIIMEJÄ");
                    FragmentListeners.getInstance().getFragmentChangeListener().goToMainSelectionFragment();
                }
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
            public void goToTeamDashboardFragment(final Team team) {
                // Store selected team and load team data
                AppRes.getInstance().setSelectedTeam(team);
                LinesResource.getInstance().getLines(new GetLinesHandler() {
                    @Override
                    public void onLinesLoaded(Map<Integer, Line> lines) {
                        AppRes.getInstance().setLines(lines);
                        PlayersResource.getInstance().getPlayers(new GetPlayersHandler() {
                            @Override
                            public void onPlayersLoaded(Map<String, Player> players) {
                                AppRes.getInstance().setPlayers(players);
                                GamesResource.getInstance().getGames(new GetGamesHandler() {
                                    @Override
                                    public void onGamesLoaded(Map<String, Game> games) {
                                        AppRes.getInstance().setGames(games);
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

            @Override
            public void goToMainSelectionFragment() {
                if(mainSelectionFragment.isAdded()) {
                    switchToFragment(mainSelectionFragment);
                } else {
                    addFragment(mainSelectionFragment);
                }
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
            public void goToPlayerDashboardFragment(Player player) {
                // TODO
            }

            @Override
            public void goToLinesFragment() {
                linesFragment.refreshData();
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
                LinesTeamGameResource.getInstance().getLines(game.getGameId(), new GetLinesHandler() {
                    @Override
                    public void onLinesLoaded(Map<Integer, Line> lines) {
                        fragmentData.setLines(lines);
                        GoalsByTeamResource.getInstance().getGoals(game.getGameId(), new GetGoalsHandler() {
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
            public void goToPlayerMonitorFragment() {

            }

            @Override
            public void goToGamesFragment() {
                switchToFragment(gamesFragment);
            }

            @Override
            public void goToGameSettingsFragment(GameSettingsFragmentData gameSettingsFragmentData) {
                gameSettingsFragment.setData(gameSettingsFragmentData);
                switchToFragment(gameSettingsFragment);
            }

            @Override
            public void goToBluetoothFragment() {
                switchToFragment(bluetoothFragment);
            }

        });
        editPlayerFragment.setListener(new EditPlayerFragment.Listener() {
            @Override
            public void onPlayerEdited(Player player) {
                AppRes.getInstance().setPlayer(player.getPlayerId(), player);
                // Back pressed to pop back stack
                onBackPressed();
            }
        });

        editTeamFragment.setListener(new EditTeamFragment.Listener() {
            @Override
            public void onTeamEdited(Team team) {
                AppRes.getInstance().setTeam(team.getTeamId(), team);
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
        FragmentListeners.getInstance().setFragmentRefreshListener(new FragmentListeners.FragmentRefreshListener() {
            @Override
            public void refreshMainSelectionFragment() {
                mainSelectionFragment.update();
            }

            @Override
            public void refreshPlayersFragment() {
                playersFragment.update();
            }

            @Override
            public void refreshLinesFragment() {
                linesFragment.refreshData();
            }
        });

        loginFragment.setListener(new LoginFragment.Listener() {
            @Override
            public void onLogIn(String userId) {
                onUserLoggedIn(userId);
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
        if(f instanceof LoginFragment) {
            menuTop.setVisibility(View.GONE);
        } else if(f instanceof SettingsFragment) {
            titleText.setText(R.string.title_settings);
            settingsIcon.setVisibility(View.GONE);
        } else if(f instanceof TeamDashboardFragment) {
            titleText.setText(AppRes.getInstance().getSelectedTeam().getName());
        } else if(f instanceof MainSelectionFragment) {
            backIcon.setVisibility(View.GONE);
            titleText.setText(R.string.title_add_monitored);
        } else if(f instanceof EditTeamFragment) {
            titleText.setText(R.string.title_settings);
        } else if(f instanceof EditPlayerFragment) {
            titleText.setText(R.string.title_settings);
        } else if(f instanceof PlayersFragment) {
            titleText.setText(R.string.title_manage_players);
        } else if(f instanceof LinesFragment) {
            titleText.setText(R.string.title_lines_default);
        } else if(f instanceof GameFragment) {
            titleText.setText(R.string.title_game);
        } else if(f instanceof GamesFragment) {
            titleText.setText(R.string.title_games);
        } else if(f instanceof GameSettingsFragment) {
            titleText.setText(R.string.title_settings);
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
        if(!Loader.isVisible()) {
            int backStack = getSupportFragmentManager().getBackStackEntryCount();

            // Do not allow user go back to login screen
            if (backStack == 1 && isLoginUsed) {
                finishAffinity();
            } else {
                Helper.hideKeyBoard(getWindow().getDecorView());
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOnDestroyCalled = true;
    }
}
