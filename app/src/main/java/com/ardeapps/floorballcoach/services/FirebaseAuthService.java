package com.ardeapps.floorballcoach.services;

import android.support.annotation.NonNull;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.PrefRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.views.Loader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import static com.ardeapps.floorballcoach.PrefRes.EMAIL;
import static com.ardeapps.floorballcoach.utils.Helper.isNetworkAvailable;

/**
 * Created by Arttu on 4.5.2017.
 */
public class FirebaseAuthService {

    private static FirebaseAuthService instance;

    public static FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    private static void onNetworkError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_network);
    }

    private static void onUserNotFoundError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_profile);
    }

    private static void onAuthenticationError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_authentication);
    }

    private static void logAction() {
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
        Logger.log(FirebaseAuthService.class.getSimpleName() + ":" + lineNumber + " - " + callingMethod);
    }

    public interface EmailPasswordLoginHandler {
        void onEmailPasswordLoginSuccess(String userId);
    }

    public interface ResetPasswordHandler {
        void onEmailSentSuccess();
    }

    public void sendPasswordResetEmail(final String email, final ResetPasswordHandler handler) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> handler.onEmailSentSuccess()).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthInvalidUserException) {
                Logger.toast(AppRes.getContext().getString(R.string.login_error_user_not_found));
            } else {
                onUserNotFoundError();
            }
        });
    }

    /**
     * LOGIN TO FIREBASE DATABASE
     */
    public void logInByEmailPassword(final String email, final String password, final EmailPasswordLoginHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Loader.hide();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Logger.log("Tunnistautuminen Sähköposti/Salasanalla. userId: " + user.getUid());
                    // Save credentials
                    PrefRes.putString(EMAIL, email);
                    handler.onEmailPasswordLoginSuccess(user.getUid());
                } else {
                    onAuthenticationError();
                }
            }).addOnFailureListener(e -> {
                Loader.hide();
                if (e instanceof FirebaseAuthInvalidUserException) {
                    Logger.toast(AppRes.getContext().getString(R.string.login_error_user_not_found));
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Logger.toast(AppRes.getContext().getString(R.string.login_error_invalid_credentials));
                } else {
                    onAuthenticationError();
                }
            });
        } else onNetworkError();
    }

    public void registerByEmailPassword(final String email, final String password, final EmailPasswordLoginHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Loader.hide();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Logger.log("Tunnistautuminen Sähköposti/Salasanalla. userId: " + user.getUid());
                    // Save credentials
                    PrefRes.putString(EMAIL, email);
                    handler.onEmailPasswordLoginSuccess(user.getUid());
                } else {
                    onAuthenticationError();
                }
            }).addOnFailureListener(e -> {
                Loader.hide();
                if (e instanceof FirebaseAuthWeakPasswordException) {
                    Logger.toast(AppRes.getContext().getString(R.string.login_error_password));
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Logger.toast(AppRes.getContext().getString(R.string.login_error_email));
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    Logger.toast(AppRes.getContext().getString(R.string.login_error_email_in_use));
                } else {
                    onAuthenticationError();
                }
            });
        } else onNetworkError();
    }
}
