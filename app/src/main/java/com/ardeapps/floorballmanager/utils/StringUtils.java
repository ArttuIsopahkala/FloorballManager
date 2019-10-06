package com.ardeapps.floorballmanager.utils;

import android.text.Html;
import android.text.Spanned;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;

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

    public static String getDateText(long milliseconds, boolean showTime) {
        String result = simpleDateFormat.format(new Date(milliseconds));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        // Show time if different than 00:00
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        if(showTime && hours != 0 || minutes != 0) {
            String minutesString = (minutes < 10 ? "0" : "") + minutes;
            result += " " + AppRes.getContext().getString(R.string.klo) + " " + hours + "." + minutesString;
        }
        return result;
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
