package com.ardeapps.floorballmanager.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arttu on 21.7.2019.
 */

public class AppInviteService {
    public static void openChooser() {
        Resources resources = AppRes.getContext().getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, StringUtils.fromHtml(resources.getString(R.string.settings_invite_message)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.settings_invite_email_subject));
        emailIntent.setType("message/rfc822");

        PackageManager pm = AppRes.getContext().getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.settings_invite_title));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("whatsapp") || packageName.contains("facebook")
                    || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.settings_invite_message));
                if (packageName.contains("android.gm")) {
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.settings_invite_email_subject));
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        AppRes.getActivity().startActivity(openInChooser);
    }
}
