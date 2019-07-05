package com.ardeapps.floorballcoach.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.BuildConfig;
import com.ardeapps.floorballcoach.R;
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

/**
 * Created by Arttu on 4.5.2017.
 */
public class FirebaseDatabaseService {

    // TODO 1. päätä nimet
    protected static final String USERS = "users";
    protected static final String USER_TEAMS = "userTeams";
    protected static final String TEAMS = "teams";
    protected static final String GAMES = "games";
    protected static final String PLAYERS = "players";
    protected static final String LINES = "lines";
    protected static final String LINES_TEAM_GAME = "linesTeamGame";
    protected static final String GOALS_TEAM_GAME = "goalsTeamGame";
    protected static final String STATS_PLAYER_GAME = "statsPlayerGame";

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
