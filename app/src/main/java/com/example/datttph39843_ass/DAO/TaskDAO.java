package com.example.datttph39843_ass.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.datttph39843_ass.DbHelper.DatabaseHelper;
import com.example.datttph39843_ass.DTO.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    private final DatabaseHelper dbHelper;

    public TaskDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_TASKS, null, null, null, null, null, DatabaseHelper.COLUMN_TASK_ID + " DESC")) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    taskList.add(cursorToTask(cursor));
                } while (cursor.moveToNext());
            }
        }
        return taskList;
    }

    public Task getTaskById(int taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Task task = null;
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_TASKS, null,
                DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                task = cursorToTask(cursor);
            }
        }
        return task;
    }

    public long addTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(task);
        return db.insert(DatabaseHelper.TABLE_TASKS, null, values);
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(task);
        return db.update(DatabaseHelper.TABLE_TASKS, values, DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    public int updateTaskStatus(int taskId, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_STATUS, status);
        return db.update(DatabaseHelper.TABLE_TASKS, values, DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public int deleteTask(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_TASKS, DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }

    private Task cursorToTask(Cursor cursor) {
        return new Task(
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_CONTENT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_STATUS)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_START_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_END_DATE))
        );
    }

    private ContentValues getContentValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_NAME, task.getName());
        values.put(DatabaseHelper.COLUMN_TASK_CONTENT, task.getContent());
        values.put(DatabaseHelper.COLUMN_TASK_STATUS, task.getStatus());
        values.put(DatabaseHelper.COLUMN_TASK_START_DATE, task.getStartDate());
        values.put(DatabaseHelper.COLUMN_TASK_END_DATE, task.getEndDate());
        return values;
    }
}
