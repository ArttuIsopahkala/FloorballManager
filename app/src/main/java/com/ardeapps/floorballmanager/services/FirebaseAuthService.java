package com.ardeapps.floorballmanager.services;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.PrefRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.Loader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import static com.ardeapps.floorballmanager.PrefRes.EMAIL;
import static com.ardeapps.floorballmanager.utils.Helper.isNetworkAvailable;

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

    private static void onEmailNotSentError() {
        if (Loader.isVisible()) {
            Loader.hide();
        }
        Logger.toast(R.string.error_email_not_sent);
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

    public void sendPasswordResetEmail(final String email, final ResetPasswordHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                Loader.hide();
                handler.onResetPasswordEmailSent();
            }).addOnFailureListener(e -> {
                Loader.hide();
                if (e instanceof FirebaseAuthInvalidUserException) {
                    Logger.toast(AppRes.getContext().getString(R.string.login_error_user_not_found));
                } else {
                    onEmailNotSentError();
                }
            });
        } else onNetworkError();
    }

    public void sendEmailVerification(final EmailVerificationHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                user.sendEmailVerification().addOnSuccessListener(aVoid -> {
                    Loader.hide();
                    handler.onEmailVerificationSent();
                }).addOnFailureListener(e -> {
                    Loader.hide();
                    onEmailNotSentError();
                });
            } else onAuthenticationError();
        } else onNetworkError();
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
                    // Save credentials
                    PrefRes.putString(EMAIL, email);
                    if(user.isEmailVerified()) {
                        handler.onEmailPasswordLoginSuccess(user.getUid());
                    } else {
                        handler.onEmailNotVerified();
                    }
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

    public void registerByEmailPassword(final String email, final String password, final EmailPasswordRegisterHandler handler) {
        logAction();
        if (isNetworkAvailable()) {
            Loader.show();
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Loader.hide();
                final FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // Save credentials
                    PrefRes.putString(EMAIL, email);
                    handler.onEmailPasswordRegisterSuccess(user.getUid());
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

    public interface EmailPasswordRegisterHandler {
        void onEmailPasswordRegisterSuccess(String userId);
    }

    public interface EmailPasswordLoginHandler {
        void onEmailPasswordLoginSuccess(String userId);
        void onEmailNotVerified();
    }

    public interface ResetPasswordHandler {
        void onResetPasswordEmailSent();
    }

    public interface EmailVerificationHandler {
        void onEmailVerificationSent();
    }
}
