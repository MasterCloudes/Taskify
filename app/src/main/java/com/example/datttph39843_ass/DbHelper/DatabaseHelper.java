package com.example.datttph39843_ass.DbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "TaskManagement.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table Accounts
    public static final String TABLE_ACCOUNTS = "Accounts";
    public static final String COLUMN_ACCOUNT_ID = "id";
    public static final String COLUMN_ACCOUNT_USERNAME = "username";
    public static final String COLUMN_ACCOUNT_EMAIL = "email";
    public static final String COLUMN_ACCOUNT_PASSWORD = "password";
    public static final String COLUMN_ACCOUNT_FULLNAME = "fullname";
    public static final String COLUMN_ACCOUNT_MSV = "msv";
    public static final String COLUMN_ACCOUNT_LOP = "lop";

    // Table Tasks
    public static final String TABLE_TASKS = "Tasks";
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_NAME = "name";
    public static final String COLUMN_TASK_CONTENT = "content";
    public static final String COLUMN_TASK_STATUS = "status";
    public static final String COLUMN_TASK_START_DATE = "start_date";
    public static final String COLUMN_TASK_END_DATE = "end_date";

    // SQL statement to create Accounts table
    private static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE " + TABLE_ACCOUNTS + "(" +
            COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ACCOUNT_USERNAME + " TEXT NOT NULL UNIQUE," +
            COLUMN_ACCOUNT_EMAIL + " TEXT NOT NULL," +
            COLUMN_ACCOUNT_PASSWORD + " TEXT NOT NULL," +
            COLUMN_ACCOUNT_FULLNAME + " TEXT," +
            COLUMN_ACCOUNT_MSV + " TEXT," +
            COLUMN_ACCOUNT_LOP + " TEXT" +
            ");";

    // SQL statement to create Tasks table
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + "(" +
            COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_TASK_NAME + " TEXT NOT NULL," +
            COLUMN_TASK_CONTENT + " TEXT," +
            COLUMN_TASK_STATUS + " INTEGER NOT NULL," +
            COLUMN_TASK_START_DATE + " TEXT," +
            COLUMN_TASK_END_DATE + " TEXT" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating tables for the first time.");
        db.execSQL(CREATE_TABLE_ACCOUNTS);
        db.execSQL(CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            Log.d(TAG, "Upgrading to version 2: Adding MSV and LOP to Accounts table");
            db.execSQL("ALTER TABLE " + TABLE_ACCOUNTS + " ADD COLUMN " + COLUMN_ACCOUNT_MSV + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_ACCOUNTS + " ADD COLUMN " + COLUMN_ACCOUNT_LOP + " TEXT");
        }
    }
}
