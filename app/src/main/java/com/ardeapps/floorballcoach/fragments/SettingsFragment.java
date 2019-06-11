package com.ardeapps.floorballcoach.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.ardeapps.floorballcoach.services.FirebaseAuthService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    Button inviteButton;
    Button changePasswordButton;
    Button logOutButton;
    TextView rateText;
    TextView moreText;
    TextView versionText;
    TextView privacyPolicyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        inviteButton = v.findViewById(R.id.inviteButton);
        changePasswordButton = v.findViewById(R.id.changePasswordButton);
        logOutButton = v.findViewById(R.id.logOutButton);
        rateText = v.findViewById(R.id.rateText);
        moreText = v.findViewById(R.id.moreText);
        versionText = v.findViewById(R.id.versionText);
        privacyPolicyText = v.findViewById(R.id.privacyPolicyText);

        versionText.setText(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        rateText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_rate) + "</u>"));
        moreText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_more) + "</u>"));
        privacyPolicyText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_privacy) + "</u>"));

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChooser();
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
                openUrl(getString(R.string.settings_link_rate_url));
            }
        });

        moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getString(R.string.settings_link_more_url));
            }
        });

        privacyPolicyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getString(R.string.settings_link_privacy_url));
            }
        });

        return v;
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void openChooser() {
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, StringUtils.fromHtml(getString(R.string.settings_invite_message)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_invite_email_subject));
        emailIntent.setType("message/rfc822");

        PackageManager pm = AppRes.getContext().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, getString(R.string.settings_invite_title));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("twitter") || packageName.contains("whatsapp") || packageName.contains("facebook")
                    || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_invite_message));
                if(packageName.contains("android.gm")) {
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_invite_email_subject));
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }
}
