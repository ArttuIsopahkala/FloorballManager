package com.ardeapps.floorballmanager.utils;

import android.text.Html;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Arttu on 4.5.2017.
 */
public class StringUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy", Locale.getDefault());

    public static boolean isEmptyString(String text) {
        return text == null || text.trim().equals("");
    }

    public static boolean areSame(String value1, String value2) {
        return value1 != null && value2 != null && value1.equals(value2);
    }

    public static String getMinSecTimeText(long milliseconds) {
        return String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public static String getTimeText(long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        int minutes = c.get(Calendar.MINUTE);
        String minutesString = minutes < 10 ? "0" + minutes : minutes + "";

        int hours = c.get(Calendar.HOUR_OF_DAY);
        String hoursString = hours < 10 ? "0" + hours : hours + "";

        return hoursString + ":" + minutesString;
    }

    public static String getDateText(long milliseconds) {
        return simpleDateFormat.format(new Date(milliseconds));
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}