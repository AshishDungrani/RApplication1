package com.rawalinfocom.rcontact;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.SmsDataType;
import com.rawalinfocom.rcontact.model.SpamDataType;

import java.util.ArrayList;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Monal on 20/10/16.
 * <p>
 * Base class for maintaining global application state.
 */

public class RContactApplication extends Application {


    private static RContactApplication mInstance;

    public static final int FAVOURITE_UNMODIFIED = 0;
    public static final int FAVOURITE_ADDED = 1;
    public static final int FAVOURITE_REMOVED = 2;


    ArrayList<Object> arrayListAllPhoneBookContacts;
    ArrayList<String> arrayListAllContactHeaders;

    ArrayList<Object> arrayListFavPhoneBookContacts;
    ArrayList<String> arrayListFavContactHeaders;

    int favouriteStatus;

    ArrayList<Object> arrayListObjectCallLogs;
    //    ArrayList<String> arrayListcallLogsHeaders;
    ArrayList<CallLogType> arrayListCallLogType;

    ArrayList<SmsDataType> arrayListSmsLogType;
    ArrayList<Object> arrayListObjectSmsLogs;
    ArrayList<SpamDataType> arrayListSpamDataType;
//    ArrayList<String> arrayListSmsLogsHeaders;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

//         Fabric Initialization
        Fabric.with(this, new Crashlytics());

        arrayListAllPhoneBookContacts = new ArrayList<>();
//        arrayListAllContactHeaders = new ArrayList<>();
        arrayListFavPhoneBookContacts = new ArrayList<>();

//        arrayListcallLogsHeaders = new ArrayList<>();
        arrayListObjectCallLogs = new ArrayList<>();
        arrayListCallLogType = new ArrayList<>();

        arrayListObjectSmsLogs = new ArrayList<>();
        arrayListSpamDataType = new ArrayList<>();
//        arrayListSmsLogsHeaders = new ArrayList<>();

        setLanguage();

    }

    public void setLanguage() {

        String languageToLoad = "en"; // your language

        switch (Utils.getStringPreference(mInstance, AppConstants.PREF_APP_LANGUAGE, "0")) {

            case "0":
                languageToLoad = "en";
                break;

            case "1":
                languageToLoad = "hi";
                break;

            case "2":
                languageToLoad = "gu";
                break;


        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public static synchronized RContactApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ArrayList<Object> getArrayListAllPhoneBookContacts() {
        return arrayListAllPhoneBookContacts;
    }

    public void setArrayListAllPhoneBookContacts(ArrayList<Object> arrayListAllPhoneBookContacts) {
        this.arrayListAllPhoneBookContacts = arrayListAllPhoneBookContacts;
    }

//    public ArrayList<String> getArrayListAllContactHeaders() {
//        return arrayListAllContactHeaders;
//    }

    public void setArrayListAllContactHeaders(ArrayList<String> arrayListAllContactHeaders) {
        this.arrayListAllContactHeaders = arrayListAllContactHeaders;
    }

    public ArrayList<Object> getArrayListFavPhoneBookContacts() {
        return arrayListFavPhoneBookContacts;
    }

//    public void setArrayListFavPhoneBookContacts(ArrayList<Object>
// arrayListFavPhoneBookContacts) {
//        this.arrayListFavPhoneBookContacts = arrayListFavPhoneBookContacts;
//    }

    public ArrayList<String> getArrayListFavContactHeaders() {
        return arrayListFavContactHeaders;
    }

//    public void setArrayListFavContactHeaders(ArrayList<String> arrayListFavContactHeaders) {
//        this.arrayListFavContactHeaders = arrayListFavContactHeaders;
//    }

 /*   public boolean isFavouriteModified() {
        return isFavouriteModified;
    }

    public void setFavouriteModified(boolean favouriteModified) {
        isFavouriteModified = favouriteModified;
    }*/

    public int getFavouriteStatus() {
        return favouriteStatus;
    }

    public void setFavouriteStatus(int favouriteStatus) {
        this.favouriteStatus = favouriteStatus;
    }

//    public ArrayList<Object> getArrayListObjectCallLogs() {
//        return arrayListObjectCallLogs;
//    }
//
//    public void setArrayListObjectCallLogs(ArrayList<Object> arrayListObjectCallLogs) {
//        this.arrayListObjectCallLogs = arrayListObjectCallLogs;
//    }

//    public ArrayList<String> getArrayListcallLogsHeaders() {
//        return arrayListcallLogsHeaders;
//    }
//
//    public void setArrayListcallLogsHeaders(ArrayList<String> arrayListcallLogsHeaders) {
//        this.arrayListcallLogsHeaders = arrayListcallLogsHeaders;
//    }

    public ArrayList<CallLogType> getArrayListCallLogType() {
        return arrayListCallLogType;
    }

    public void setArrayListCallLogType(ArrayList<CallLogType> arrayListCallLogType) {
        this.arrayListCallLogType = arrayListCallLogType;
    }

    public ArrayList<SmsDataType> getArrayListSmsLogType() {
        return arrayListSmsLogType;
    }

    public void setArrayListSmsLogType(ArrayList<SmsDataType> arrayListSmsLogType) {
        this.arrayListSmsLogType = arrayListSmsLogType;
    }

//    public ArrayList<Object> getArrayListObjectSmsLogs() {
//        return arrayListObjectSmsLogs;
//    }

    public void setArrayListObjectSmsLogs(ArrayList<Object> arrayListObjectSmsLogs) {
        this.arrayListObjectSmsLogs = arrayListObjectSmsLogs;
    }

    public ArrayList<SpamDataType> getArrayListSpamDataType() {
        return arrayListSpamDataType;
    }

    public void setArrayListSpamDataType(ArrayList<SpamDataType> arrayListSpamDataType) {
        this.arrayListSpamDataType = arrayListSpamDataType;
    }

    //    public ArrayList<String> getArrayListSmsLogsHeaders() {
//        return arrayListSmsLogsHeaders;
//    }
//
//    public void setArrayListSmsLogsHeaders(ArrayList<String> arrayListSmsLogsHeaders) {
//        this.arrayListSmsLogsHeaders = arrayListSmsLogsHeaders;
//    }
}
