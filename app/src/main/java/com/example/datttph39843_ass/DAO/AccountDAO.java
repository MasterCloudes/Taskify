package com.example.datttph39843_ass.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.datttph39843_ass.DbHelper.DatabaseHelper;
import com.example.datttph39843_ass.DTO.Account;

public class AccountDAO {
    private final DatabaseHelper dbHelper;

    public AccountDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.insert(DatabaseHelper.TABLE_ACCOUNTS, null, getContentValues(account));
    }

    public boolean checkLogin(String username, String password) {
        String selection = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=? AND " + DatabaseHelper.COLUMN_ACCOUNT_PASSWORD + "=?";
        return checkIfExists(selection, new String[]{username, password});
    }

    public boolean checkUsernameExists(String username) {
        String selection = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=?";
        return checkIfExists(selection, new String[]{username});
    }

    public boolean checkEmailExists(String email) {
        String selection = DatabaseHelper.COLUMN_ACCOUNT_EMAIL + "=?";
        return checkIfExists(selection, new String[]{email});
    }

    public boolean checkAccountExists(String username, String email) {
        String selection = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=? AND " + DatabaseHelper.COLUMN_ACCOUNT_EMAIL + "=?";
        return checkIfExists(selection, new String[]{username, email});
    }

    private boolean checkIfExists(String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_ACCOUNTS, new String[]{"1"},
                selection, selectionArgs, null, null, null, "1")) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    public Account getAccountByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=?";

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_ACCOUNTS, null, selection, new String[]{username}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursorToAccount(cursor);
            }
        }
        return null;
    }

    public int updatePassword(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACCOUNT_PASSWORD, newPassword);

        String whereClause = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=?";
        return db.update(DatabaseHelper.TABLE_ACCOUNTS, values, whereClause, new String[]{username});
    }

    public int updateAccountInfo(String username, String fullname, String email, String msv, String lop) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACCOUNT_FULLNAME, fullname);
        values.put(DatabaseHelper.COLUMN_ACCOUNT_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_ACCOUNT_MSV, msv);
        values.put(DatabaseHelper.COLUMN_ACCOUNT_LOP, lop);

        String whereClause = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=?";
        return db.update(DatabaseHelper.TABLE_ACCOUNTS, values, whereClause, new String[]{username});
    }

    public int deleteAccount(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.COLUMN_ACCOUNT_USERNAME + "=?";
        return db.delete(DatabaseHelper.TABLE_ACCOUNTS, whereClause, new String[]{username});
    }

    private Account cursorToAccount(Cursor cursor) {
        return new Account(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_FULLNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_MSV)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACCOUNT_LOP))
        );
    }

    private ContentValues getContentValues(Account account) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ACCOUNT_USERNAME, account.getUsername());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_EMAIL, account.getEmail());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_PASSWORD, account.getPassword());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_FULLNAME, account.getFullname());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_MSV, account.getMsv());
        values.put(DatabaseHelper.COLUMN_ACCOUNT_LOP, account.getLop());
        return values;
    }
}
