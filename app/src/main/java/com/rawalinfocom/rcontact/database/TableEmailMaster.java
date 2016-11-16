package com.rawalinfocom.rcontact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

/**
 * Created by Monal on 14/11/16.
 */

public class TableEmailMaster {

    Context context;
    DatabaseHandler databaseHandler;

    public TableEmailMaster(Context context, DatabaseHandler databaseHandler) {
        this.context = context;
        this.databaseHandler = databaseHandler;
    }

    // Table Names
    private static final String TABLE_RC_EMAIL_MASTER = "rc_email_master";

    // Column Names
    private static final String COLUMN_EM_ID = "em_id";
    private static final String COLUMN_EM_EMAIL_ADDRESS = "em_email_address";
    private static final String COLUMN_EM_EMAIL_TYPE = "em_email_type";
    private static final String COLUMN_EM_CUSTOM_TYPE = "em_custom_type";
    private static final String COLUMN_EM_IS_PRIMARY = "em_is_primary";
    private static final String COLUMN_EM_EMAIL_PRIVACY = "em_email_privacy";
    private static final String COLUMN_EM_IS_DEFAULT = "em_is_default";
    private static final String COLUMN_EM_IS_VERIFIED = "em_is_verified";
    private static final String COLUMN_EM_UPDATED_AT = "em_updated_at";
    private static final String COLUMN_EM_UPDATED_DATA = "em_updated_data";
    private static final String COLUMN_EM_DELETED_AT = "em_deleted_at";
    private static final String COLUMN_RC_PROFILE_MASTER_PM_ID = "rc_profile_master_pm_id";


    // Table Create Statements
    public static final String CREATE_TABLE_RC_EMAIL_MASTER = "CREATE TABLE " +
            TABLE_RC_EMAIL_MASTER + " (" +
            " " + COLUMN_EM_ID + " integer NOT NULL CONSTRAINT rc_email_master_pk PRIMARY KEY," +
            " " + COLUMN_EM_EMAIL_ADDRESS + " text NOT NULL," +
            " " + COLUMN_EM_EMAIL_TYPE + " text NOT NULL," +
            " " + COLUMN_EM_CUSTOM_TYPE + " text," +
            " " + COLUMN_EM_IS_PRIMARY + " integer," +
            " " + COLUMN_EM_EMAIL_PRIVACY + " integer," +
            " " + COLUMN_EM_IS_DEFAULT + " integer," +
            " " + COLUMN_EM_IS_VERIFIED + " integer," +
            " " + COLUMN_EM_UPDATED_AT + " datetime," +
            " " + COLUMN_EM_UPDATED_DATA + " integer," +
            " " + COLUMN_EM_DELETED_AT + " datetime," +
            " " + COLUMN_RC_PROFILE_MASTER_PM_ID + " integer" +
            ");";

    // Adding new Email
    public void addEmail(Email email) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EM_ID, email.getEmId());
        values.put(COLUMN_EM_EMAIL_ADDRESS, email.getEmEmailAddress());
        values.put(COLUMN_EM_EMAIL_TYPE, email.getEmEmailType());
        values.put(COLUMN_EM_CUSTOM_TYPE, email.getEmCustomType());
        values.put(COLUMN_EM_IS_PRIMARY, email.getEmIsPrimary());
        values.put(COLUMN_EM_EMAIL_PRIVACY, email.getEmEmailPrivacy());
        values.put(COLUMN_EM_IS_DEFAULT, email.getEmIsDefault());
        values.put(COLUMN_EM_IS_VERIFIED, email.getEmIsVerified());
        values.put(COLUMN_EM_UPDATED_AT, email.getEmUpdatedAt());
        values.put(COLUMN_EM_UPDATED_DATA, email.getEmUpdatedData());
        values.put(COLUMN_EM_DELETED_AT, email.getEmDeletedAt());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, email.getRcProfileMasterPmId());


        // Inserting Row
        db.insert(TABLE_RC_EMAIL_MASTER, null, values);
        // insertWithOnConflict
        db.close(); // Closing database connection
    }

    // Getting single Email
    public Email getEmail(int emId) {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RC_EMAIL_MASTER, new String[]{COLUMN_EM_ID,
                COLUMN_EM_EMAIL_ADDRESS, COLUMN_EM_EMAIL_TYPE, COLUMN_EM_CUSTOM_TYPE,
                COLUMN_EM_IS_PRIMARY,
                COLUMN_EM_EMAIL_PRIVACY, COLUMN_EM_IS_DEFAULT, COLUMN_EM_IS_VERIFIED,
                COLUMN_EM_UPDATED_AT, COLUMN_EM_UPDATED_DATA,
                COLUMN_EM_DELETED_AT, COLUMN_RC_PROFILE_MASTER_PM_ID}, COLUMN_EM_ID + "=?", new
                String[]{String.valueOf
                (emId)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Email email = new Email();
        if (cursor != null) {
            email.setEmId(cursor.getString(0));
            email.setEmEmailAddress(cursor.getString(1));
            email.setEmEmailType(cursor.getString(2));
            email.setEmCustomType(cursor.getString(3));
            email.setEmIsPrimary(cursor.getString(4));
            email.setEmEmailPrivacy(cursor.getString(5));
            email.setEmIsDefault(cursor.getString(6));
            email.setEmIsVerified(cursor.getString(7));
            email.setEmUpdatedAt(cursor.getString(8));
            email.setEmUpdatedData(cursor.getString(9));
            email.setEmDeletedAt(cursor.getString(10));
            email.setRcProfileMasterPmId(cursor.getString(11));
        }
        // return Email
        return email;
    }

    // Getting All Emails
    public ArrayList<Email> getAllEmails() {
        ArrayList<Email> arrayListEmail = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RC_EMAIL_MASTER;

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Email email = new Email();
                email.setEmId(cursor.getString(0));
                email.setEmEmailAddress(cursor.getString(1));
                email.setEmEmailType(cursor.getString(2));
                email.setEmCustomType(cursor.getString(3));
                email.setEmIsPrimary(cursor.getString(4));
                email.setEmEmailPrivacy(cursor.getString(5));
                email.setEmIsDefault(cursor.getString(6));
                email.setEmIsVerified(cursor.getString(7));
                email.setEmUpdatedAt(cursor.getString(8));
                email.setEmUpdatedData(cursor.getString(9));
                email.setEmDeletedAt(cursor.getString(10));
                email.setRcProfileMasterPmId(cursor.getString(11));
                // Adding email to list
                arrayListEmail.add(email);
            } while (cursor.moveToNext());
        }

        // return Email list
        return arrayListEmail;
    }

    // Getting Email Count
    public int getEmailCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RC_EMAIL_MASTER;
        SQLiteDatabase db = databaseHandler.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Updating single Email
    public int updateEmail(Email email) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EM_ID, email.getEmId());
        values.put(COLUMN_EM_EMAIL_ADDRESS, email.getEmEmailAddress());
        values.put(COLUMN_EM_EMAIL_TYPE, email.getEmEmailType());
        values.put(COLUMN_EM_CUSTOM_TYPE, email.getEmCustomType());
        values.put(COLUMN_EM_IS_PRIMARY, email.getEmIsPrimary());
        values.put(COLUMN_EM_EMAIL_PRIVACY, email.getEmEmailPrivacy());
        values.put(COLUMN_EM_IS_DEFAULT, email.getEmIsDefault());
        values.put(COLUMN_EM_IS_VERIFIED, email.getEmIsVerified());
        values.put(COLUMN_EM_UPDATED_AT, email.getEmUpdatedAt());
        values.put(COLUMN_EM_UPDATED_DATA, email.getEmUpdatedData());
        values.put(COLUMN_EM_DELETED_AT, email.getEmDeletedAt());
        values.put(COLUMN_RC_PROFILE_MASTER_PM_ID, email.getRcProfileMasterPmId());

        // updating row
        return db.update(TABLE_RC_EMAIL_MASTER, values, COLUMN_EM_ID + " = ?",
                new String[]{String.valueOf(email.getEmId())});
    }

    // Deleting single email
    public void deleteEmail(Email email) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete(TABLE_RC_EMAIL_MASTER, COLUMN_EM_ID + " = ?",
                new String[]{String.valueOf(email.getEmId())});
        db.close();
    }
}