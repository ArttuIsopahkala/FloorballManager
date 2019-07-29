package com.ardeapps.floorballcoach.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.BuildConfig;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballcoach.services.AppInviteService;
import com.ardeapps.floorballcoach.services.FirebaseAuthService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    Button inviteButton;
    Button changePasswordButton;
    Button logOutButton;
    TextView rateText;
    TextView moreText;
    TextView versionText;
    TextView privacyPolicyText;
    TextView emailText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        inviteButton = v.findViewById(R.id.inviteButton);
        changePasswordButton = v.findViewById(R.id.changePasswordButton);
        logOutButton = v.findViewById(R.id.logOutButton);
        rateText = v.findViewById(R.id.rateText);
        moreText = v.findViewById(R.id.moreText);
        versionText = v.findViewById(R.id.versionText);
        privacyPolicyText = v.findViewById(R.id.privacyPolicyText);
        emailText = v.findViewById(R.id.emailText);

        emailText.setText(AppRes.getInstance().getUser().getEmail());
        versionText.setText(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        rateText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_rate) + "</u>"));
        moreText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_more) + "</u>"));
        privacyPolicyText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_privacy) + "</u>"));

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInviteService.openChooser();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.settings_change_password_confirm));
                dialogFragment.show(getChildFragmentManager(), "Lähetetäänkö salasananvaihtolinkki?");
                dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        FirebaseAuthService.getInstance().sendPasswordResetEmail(AppRes.getInstance().getUser().getEmail(), new FirebaseAuthService.ResetPasswordHandler() {
                            @Override
                            public void onEmailSentSuccess() {
                                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.login_forgot_password_sent));
                                dialog.show(getChildFragmentManager(), "Salasanan vaihtolinkki lähetettiin");
                            }
                        });
                    }
                });
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                FragmentListeners.getInstance().getFragmentChangeListener().goToLoginFragment();
            }
        });

        rateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getString(R.string.google_play_app_url));
            }
        });

        moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getString(R.string.google_play_developer_url));
            }
        });

        privacyPolicyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(AppRes.getInstance().getAppData().getPrivacyPolicyUrl());
            }
        });

        return v;
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
