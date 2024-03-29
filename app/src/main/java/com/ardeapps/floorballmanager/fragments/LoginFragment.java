package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.PrefRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.resources.UsersResource;
import com.ardeapps.floorballmanager.services.FirebaseAuthService;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;

import static com.ardeapps.floorballmanager.PrefRes.EMAIL;
import static com.ardeapps.floorballmanager.utils.Helper.isValidEmail;


public class LoginFragment extends Fragment {

    Listener mListener = null;
    Button loginButton;
    TextView emailText;
    TextView passwordText;
    TextView infoText;
    TextView changeLoginTypeText;
    LinearLayout passwordContent;
    TextView forgotPasswordText;

    boolean userRegister = false;
    LoginType type = LoginType.LOGIN;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        passwordContent = v.findViewById(R.id.passwordContent);
        loginButton = v.findViewById(R.id.loginButton);
        emailText = v.findViewById(R.id.emailText);
        passwordText = v.findViewById(R.id.passwordText);
        infoText = v.findViewById(R.id.infoText);
        changeLoginTypeText = v.findViewById(R.id.changeLoginTypeText);
        forgotPasswordText = v.findViewById(R.id.forgotPasswordText);

        setView(LoginType.LOGIN);

        final String email = PrefRes.getString(EMAIL);
        if (!StringUtils.isEmptyString(email)) {
            emailText.setText(email);
        }

        forgotPasswordText.setOnClickListener(v13 -> {
            if (type == LoginType.FORGOT_PASSWORD) {
                setView(LoginType.LOGIN);
            } else {
                setView(LoginType.FORGOT_PASSWORD);
            }
        });

        changeLoginTypeText.setOnClickListener(v12 -> {
            userRegister = !userRegister;
            if (type == LoginType.LOGIN) {
                setView(LoginType.REGISTER);
            } else {
                setView(LoginType.LOGIN);
            }
        });

        loginButton.setOnClickListener(v1 -> {
            Helper.hideKeyBoard(emailText);
            if (type == LoginType.FORGOT_PASSWORD) {
                resetPassword();
            } else {
                validateAndSave();
            }
        });

        return v;
    }

    private void setView(LoginType type) {
        this.type = type;
        if (type == LoginType.LOGIN) {
            loginButton.setText(getString(R.string.login_login));
            infoText.setText(getString(R.string.login_login_info));
            changeLoginTypeText.setText(underline(R.string.login_new_user));
            forgotPasswordText.setText(underline(R.string.login_forgot_password));
            forgotPasswordText.setVisibility(View.VISIBLE);
            changeLoginTypeText.setVisibility(View.VISIBLE);
            passwordContent.setVisibility(View.VISIBLE);
        } else if (type == LoginType.REGISTER) {
            loginButton.setText(getString(R.string.login_register));
            infoText.setText(getString(R.string.login_register_info));
            changeLoginTypeText.setText(underline(R.string.login_old_user));
            forgotPasswordText.setText(underline(R.string.login_forgot_password));
            forgotPasswordText.setVisibility(View.GONE);
            changeLoginTypeText.setVisibility(View.VISIBLE);
            passwordContent.setVisibility(View.VISIBLE);
        } else if (type == LoginType.FORGOT_PASSWORD) {
            loginButton.setText(getString(R.string.login_continue));
            infoText.setText(getString(R.string.login_forgot_password_info));
            forgotPasswordText.setText(underline(R.string.login_back));
            forgotPasswordText.setVisibility(View.VISIBLE);
            changeLoginTypeText.setVisibility(View.GONE);
            passwordContent.setVisibility(View.GONE);
        }
    }

    private Spanned underline(int resource) {
        return Html.fromHtml("<u>" + getString(resource) + "</u>");
    }

    private void resetPassword() {
        final String email = emailText.getText().toString();
        if (StringUtils.isEmptyString(email)) {
            Logger.toast(getString(R.string.error_empty));
            return;
        }

        if (!StringUtils.isEmptyString(email) && !isValidEmail(email)) {
            Logger.toast(getString(R.string.login_error_email));
            return;
        }
        FirebaseAuthService.getInstance().sendPasswordResetEmail(email, () -> {
            setView(LoginType.LOGIN);
            InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.login_forgot_password_sent));
            dialog.show(getChildFragmentManager(), "Salasanan vaihtolinkki lähetettiin");
        });
    }

    private void validateAndSave() {
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        if (StringUtils.isEmptyString(email) || StringUtils.isEmptyString(password)) {
            Logger.toast(getString(R.string.error_empty));
            return;
        }

        if (!StringUtils.isEmptyString(email) && !isValidEmail(email)) {
            Logger.toast(getString(R.string.login_error_email));
            return;
        }

        if (!StringUtils.isEmptyString(password) && password.length() < 6) {
            // Salasanan on oltava vähintään 6 merkkiä.
            Logger.toast(getString(R.string.login_error_password));
            return;
        }

        if (type == LoginType.REGISTER) {
            FirebaseAuthService.getInstance().registerByEmailPassword(email, password, userId -> {
                FirebaseAuthService.getInstance().sendEmailVerification(() -> {
                    InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.login_verification_sent));
                    dialog.show(getChildFragmentManager(), "Vahvistusähköposti lähetetty.");
                });
                setView(LoginType.LOGIN);
            });
        } else {
            FirebaseAuthService.getInstance().logInByEmailPassword(email, password, new FirebaseAuthService.EmailPasswordLoginHandler() {
                @Override
                public void onEmailPasswordLoginSuccess(String userId) {
                    UsersResource.getInstance().getUser(userId, oldUser -> {
                        if(oldUser == null) {
                            long now = System.currentTimeMillis();
                            final User user = new User();
                            user.setUserId(userId);
                            user.setEmail(email);
                            user.setCreationTime(now);
                            user.setLastLoginTime(now);
                            UsersResource.getInstance().editUser(user, () -> mListener.onLogIn(user.getUserId()));
                        } else {
                            mListener.onLogIn(userId);
                        }
                    });
                }

                @Override
                public void onEmailNotVerified() {
                    ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.login_email_not_verified));
                    dialogFragment.show(getChildFragmentManager(), "Lähetetäänkö vahvistussähköposti uudelleen?");
                    dialogFragment.setListener(() -> {
                        FirebaseAuthService.getInstance().sendEmailVerification(() -> {
                            InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.login_verification_sent));
                            dialog.show(getChildFragmentManager(), "Vahvistusähköposti lähetetty.");
                        });
                    });
                }
            });
        }
    }

    private enum LoginType {
        LOGIN,
        REGISTER,
        FORGOT_PASSWORD
    }

    public interface Listener {
        void onLogIn(String userId);
    }
}
