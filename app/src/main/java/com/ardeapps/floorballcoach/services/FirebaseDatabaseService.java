package com.ardeapps.floorballcoach.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.BuildConfig;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.GetPlayersHandler;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.resources.PlayersResource;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.Loader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Arttu on 4.5.2017.
 */
public class FirebaseDatabaseService {

    protected static final String APP_DATA = "appData";
    protected static final String TEAMS = "teams";
    protected static final String PLAYERS_SEASONS_GAMES_STATS = "players_seasons_games_stats";
    protected static final String PLAYERS_SEASONS_GAMES = "players_seasons_games";
    protected static final String TEAMS_SEASONS_GAMES = "teams_seasons_games";
    protected static final String TEAMS_SEASONS_GAMES_GOALS = "teams_seasons_games_goals";
    protected static final String TEAMS_SEASONS_GAMES_LINES = "teams_seasons_games_lines";
    protected static final String TEAMS_SEASONS = "teams_seasons";
    protected static final String TEAMS_LINES = "teams_lines";
    protected static final String TEAMS_PLAYERS = "teams_players";
    protected static final String TEAMS_USER_CONNECTIONS = "teams_userConnections";
    protected static final String USERS = "users";
    protected static final String USER_INVITATIONS = "userInvitations";

    protected static final String DEBUG = "DEBUG";
    protected static final String RELEASE = "RELEASE";

    private static boolean databaseCallInterrupted = false;

    public static void isDatabaseCallInterrupted(boolean value) {
        databaseCallInterrupted = value;
    }

    protected static DatabaseReference getDatabase() {
        if (BuildConfig.DEBUG) {
            return FirebaseDatabase.getInstance().getReference().child(DEBUG);
        } else {
            return FirebaseDatabase.getInstance().getReference().child(RELEASE);
        }
    }

    public static void updateDatabase() {
        final String teamId = AppRes.getInstance().getSelectedTeam().getTeamId();
        PlayersResource.getInstance().getPlayers(new GetPlayersHandler() {
            @Override
            public void onPlayersLoaded(Map<String, Player> players) {
                for(Player player : players.values()) {
                    player.setActive(true);
                    PlayersResource.getInstance().editPlayer(player);
                }
            }
        });
        /*GamesResource.getInstance().getAllGames(new GetGamesHandler() {
            @Override
            public void onGamesLoaded(final Map<String, Game> games) {
                getData(getDatabase().child(PLAYERS_GAMES_STATS), new GetDataSuccessListener() {
                    @Override
                    public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot player : dataSnapshot.getChildren()) {
                            final String playerId = player.getKey();
                            for(DataSnapshot gameShot : player.getChildren()) {
                                final String gameId = gameShot.getKey();
                                Game game = games.get(gameId);
                                if(game != null) {
                                    for(DataSnapshot goalShot : gameShot.getChildren()) {
                                        final Goal goal = goalShot.getValue(Goal.class);
                                        goal.setSeasonId(game.getSeasonId());
                                        editData(getDatabase().child(PLAYERS_SEASONS_GAMES_STATS).child(playerId).child(game.getSeasonId()).child(gameId).child(goal.getGoalId()), goal);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
        GamesResource.getInstance().getGames(new GetGamesHandler() {
            @Override
            public void onGamesLoaded(final Map<String, Game> gamesMap) {
                // Add seasonId to goal and move to new location
                GoalsResource.getInstance().getGoals(new GetTeamGoalsHandler() {
                    @Override
                    public void onTeamGoalsLoaded(Map<String, ArrayList<Goal>> goalsMap) {
                        for(Map.Entry<String, ArrayList<Goal>> entry : goalsMap.entrySet()) {
                            String gameId = entry.getKey();
                            ArrayList<Goal> goals = entry.getValue();

                            Game game = gamesMap.get(gameId);
                            for(Goal goal : goals) {
                                goal.setSeasonId(game.getSeasonId());
                                editData(getDatabase().child(TEAMS_SEASONS_GAMES_GOALS).child(teamId).child(game.getSeasonId()).child(game.getGameId()).child(goal.getGoalId()), goal);
                            }

                        }
                    }
                });

                LinesInGameResource.getInstance().getLines(new GetTeamLinesHandler() {
                    @Override
                    public void onTeamLinesLoaded(Map<String, ArrayList<Line>> linesMap) {
                        for(Map.Entry<String, ArrayList<Line>> entry : linesMap.entrySet()) {
                            String gameId = entry.getKey();
                            ArrayList<Line> lines = entry.getValue();

                            Game game = gamesMap.get(gameId);
                            for(Line line : lines) {
                                line.setGameId(game.getGameId());
                                line.setSeasonId(game.getSeasonId());
                                editData(getDatabase().child(TEAMS_SEASONS_GAMES_LINES).child(teamId).child(game.getSeasonId()).child(game.getGameId()).child(line.getLineId()), line);
                            }

                        }
                    }
                });

                // Move teams_games to teams_seasons_games
                for(Map.Entry<String, Game> entry : gamesMap.entrySet()) {
                    Game game = entry.getValue();
                    editData(getDatabase().child(TEAMS_SEASONS_GAMES).child(teamId).child(game.getSeasonId()).child(game.getGameId()), game);
                }
            }
        });*/


        /*
        // Move teams_games to teams_seasons_games
        getData(getDatabase().child(TEAMS_GAMES), new GetDataSuccessListener() {
            @Override
            public void onGetDataSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot team : dataSnapshot.getChildren()) {
                    for(DataSnapshot game : team.getChildren()) {
                        String seasonId = (String)game.child("seasonId").getValue();
                        editData(getDatabase().child(TEAMS_SEASONS_GAMES).child(team.getKey()).child(seasonId).child(game.getKey()), game);
                        editData(getDatabase().child(TEAMS_GAMES).child(team.getKey()).child(game.getKey()), null);
                    }
                }
            }
        });

        /*final Season season = new Season();
        season.setPeriodInMinutes(20);
        season.setName("4. divari 2017-2018");
        SeasonsResource.getInstance().addSeason(season, new AddDataSuccessListener() {
            @Override
            public void onAddDataSuccess(String id) {
                season.setSeasonId(id);

                GamesResource.getInstance().getGames(new GetGamesHandler() {
                    @Override
                    public void onGamesLoaded(Map<String, Game> games) {
                        for(Game game : games.values()) {
                            game.setSeasonId(season.getSeasonId());
                            GamesResource.getInstance().editGame(game, new EditDataSuccessListener() {
                                @Override
                                public void onEditDataSuccess() {

                                }
                            });
                        }
                    }
                });
            }
        });*/
    }

    private static void onNetworkError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_network);
    }

    private static void onDatabaseError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_database);
    }

    private static void logAction() {
        String callingClass = Thread.currentThread().getStackTrace()[4].getFileName();
        int lineNumber = Thread.currentThread().getStackTrace()[4].getLineNumber();
        String callingMethod = Thread.currentThread().getStackTrace()[4].getMethodName();
        Logger.log(callingClass + ":" + lineNumber + " - " + callingMethod);
    }

    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppRes.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * This method is used to add data to database without showing any loaders or callbacks
     *
     * @param database reference
     * @param object   value to add
     */
    protected static void addData(final DatabaseReference database, Object object) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            database.setValue(object).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(!databaseCallInterrupted) {
                        Logger.log(e.getMessage() + AppRes.getContext().getString(R.string.error_service_action));
                    }
                }
            });
        } else onNetworkError();
    }

    protected static void addData(final DatabaseReference database, Object object, final AddDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            Loader.show();
            database.setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!databaseCallInterrupted) {
                        Loader.hide();
                        if (task.isSuccessful()) {
                            handler.onAddDataSuccess(database.getKey());
                        } else {
                            Logger.toast(R.string.error_service_action);
                        }
                    }
                }
            });
        } else onNetworkError();
    }

    /**
     * This method is used to set data to database without showing any loaders or callbacks
     *
     * @param database reference
     * @param object   value to set
     */
    protected static void editData(DatabaseReference database, Object object) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            database.setValue(object).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(!databaseCallInterrupted) {
                        Logger.log(e.getMessage() + AppRes.getContext().getString(R.string.error_service_action));
                    }
                }
            });
        } else onNetworkError();
    }

    protected static void editData(DatabaseReference database, Object object, final EditDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            Loader.show();
            database.setValue(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!databaseCallInterrupted) {
                        Loader.hide();
                        if (task.isSuccessful()) {
                            handler.onEditDataSuccess();
                        } else {
                            if(task.getException() != null) {
                                Logger.log(task.getException().getMessage());
                            }
                            Logger.toast(R.string.error_service_action);
                        }
                    }
                }
            });
        } else onNetworkError();
    }

    protected static void deleteData(DatabaseReference database, final DeleteDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            Loader.show();
            database.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!databaseCallInterrupted) {
                        Loader.hide();
                        if (task.isSuccessful()) {
                            handler.onDeleteDataSuccess();
                        } else {
                            Logger.toast(R.string.error_service_action);
                        }
                    }
                }
            });
        } else onNetworkError();
    }

    /**
     * Käytetään update metodeissa. Ei näytetä loaderia tai virheviestejä.
     */
    protected static void getDataAnonymously(DatabaseReference database, final GetDataSuccessListener handler) {
        isDatabaseCallInterrupted(false);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!databaseCallInterrupted) {
                    handler.onGetDataSuccess(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    protected static void getData(DatabaseReference database, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            Loader.show();
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!databaseCallInterrupted) {
                        Loader.hide();
                        handler.onGetDataSuccess(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(!databaseCallInterrupted) {
                        onDatabaseError();
                    }
                }
            });
        } else onNetworkError();
    }

    protected static void getData(Query query, final GetDataSuccessListener handler) {
        logAction();
        if (isNetworkAvailable()) {
            isDatabaseCallInterrupted(false);
            Loader.show();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!databaseCallInterrupted) {
                        Loader.hide();
                        handler.onGetDataSuccess(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(!databaseCallInterrupted) {
                        onDatabaseError();
                    }
                }
            });
        } else onNetworkError();
    }

    public interface GetDataSuccessListener {
        void onGetDataSuccess(DataSnapshot dataSnapshot);
    }

    public interface EditDataSuccessListener {
        void onEditDataSuccess();
    }

    public interface AddDataSuccessListener {
        void onAddDataSuccess(String id);
    }

    public interface DeleteDataSuccessListener {
        void onDeleteDataSuccess();
    }
}
