package com.rawalinfocom.rcontact.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Class containing some static utility methods.
 */

public class Utils {

    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    //<editor-fold desc="Check Android OS Version">

    /**
     * Uses static final constants to detect if the device's platform version is Jellybean or
     * later.
     */
    public static boolean hasJellybean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    //</editor-fold>

    //<editor-fold desc="Network Info">

    /**
     * Check Network Availability
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="SnackBar">
    public static void showErrorSnackBar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarNegative));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSuccessSnackbar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarPositive));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    //</editor-fold>

    //<editor-fold desc="Data Type Operations">
    public static boolean isArraylistNullOrEmpty(ArrayList arrayList) {
        return !(arrayList != null && arrayList.size() > 0);
    }
    //</editor-fold>

    //<editor-fold desc="Date Time Functions">

    /**
     * Returns UTC time in Date Format
     */
    public static Date getUtcDateTimeAsDate() {
        return getStringDateToDate(getUtcDateTimeAsString());
    }

    /**
     * Returns UTC time in String Format
     */
    public static String getUtcDateTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());


    }

    /**
     * Converts Date to String
     */
    public static Date getStringDateToDate(String StrDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date dateToReturn = null;
        try {
            dateToReturn = sdf.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }

    /**
     * Returns OTP expiration Time
     */
    public static String getOtpExpirationTime(String startTime) {
        String endTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date d = sdf.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, AppConstants.OTP_VALIDITY_DURATION);
            endTime = sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }
    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    public static void setStringPreference(Context context, String key, @Nullable String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, defaultValue);
    }

    public static void setStringSetPreference(Context context, String key, @Nullable Set<String>
            value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public static Set<String> getStringSetPreference(Context context, String key, Set<String>
            defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getStringSet(key, defaultValue);
    }

    public static void setIntegerPreference(Context context, String key, int value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(key, defaultValue);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(key, defaultValue);
    }

    public static void setObjectPreference(Context context, String key, @Nullable Object object) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    //</editor-fold>

}
