package com.ardeapps.floorballmanager.fragments;

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

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.BuildConfig;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballmanager.objects.AppData;
import com.ardeapps.floorballmanager.services.AppInviteService;
import com.ardeapps.floorballmanager.services.FirebaseAuthService;
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

        inviteButton.setOnClickListener(v16 -> AppInviteService.openChooser());

        changePasswordButton.setOnClickListener(v15 -> {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.settings_change_password_confirm));
            dialogFragment.show(getChildFragmentManager(), "Lähetetäänkö salasananvaihtolinkki?");
            dialogFragment.setListener(() -> FirebaseAuthService.getInstance().sendPasswordResetEmail(AppRes.getInstance().getUser().getEmail(), () -> {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.login_forgot_password_sent));
                dialog.show(getChildFragmentManager(), "Salasanan vaihtolinkki lähetettiin");
            }));
        });

        logOutButton.setOnClickListener(v14 -> {
            FirebaseAuth.getInstance().signOut();
            AppRes.getActivity().finish();
        });

        rateText.setOnClickListener(v13 -> openUrl(getString(R.string.google_play_app_url)));

        moreText.setOnClickListener(v12 -> openUrl(getString(R.string.google_play_developer_url)));

        privacyPolicyText.setOnClickListener(v1 -> openUrl(AppData.PRIVACY_POLICY_URL));

        return v;
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
