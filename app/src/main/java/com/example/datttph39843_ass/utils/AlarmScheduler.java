package com.example.datttph39843_ass.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.example.datttph39843_ass.DTO.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmScheduler {

    private static final int ONE_TIME_ALARM_REQUEST_CODE = 1002;
    private static final int REPEATING_ALARM_REQUEST_CODE = 1001;

    public static void scheduleTaskReminder(Context context, Task task) {
        // This method remains the same for individual task reminders
        if (task.getEndDate() == null || task.getEndDate().isEmpty()) {
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date endDate = sdf.parse(task.getEndDate());

            if (endDate != null) {
                Calendar triggerCalendar = Calendar.getInstance();
                triggerCalendar.setTime(endDate);
                triggerCalendar.set(Calendar.HOUR_OF_DAY, 9);
                triggerCalendar.set(Calendar.MINUTE, 0);
                triggerCalendar.set(Calendar.SECOND, 0);
                long triggerTime = triggerCalendar.getTimeInMillis();
                long now = System.currentTimeMillis();

                if (triggerTime <= now) {
                    if (DateUtils.isToday(triggerTime)) {
                        triggerTime = now + 60 * 1000;
                    } else {
                        return;
                    }
                }

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("TASK_ID", task.getId());
                intent.putExtra("TASK_NAME", task.getName());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        task.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    // No toast here as it's handled in LoginActivity
                    return;
                }
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void cancelTaskReminder(Context context, Task task) {
        // This method also remains the same
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getId(),
                intent,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Schedules a one-time alarm to check for unfinished tasks after a specified delay.
     * @param context The application context.
     * @param delayInMillis The delay in milliseconds before the check is performed.
     */
    public static void scheduleSingleTaskCheck(Context context, long delayInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskCheckReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ONE_TIME_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        long triggerAtMillis = SystemClock.elapsedRealtime() + delayInMillis;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback for when exact alarms are not allowed
             alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
            return;
        }

        // Use setExact for more precise timing
        alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }

    /**
     * Schedules a repeating alarm that triggers periodically to check for unfinished tasks.
     * This is typically used to reschedule checks after a device reboot.
     */
    public static void scheduleRepeatingTaskCheck(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskCheckReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REPEATING_ALARM_REQUEST_CODE, 
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Set the alarm to trigger every hour
        long interval = AlarmManager.INTERVAL_HOUR;
        long triggerAtMillis = SystemClock.elapsedRealtime() + interval;

        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, 
                triggerAtMillis, 
                interval, 
                pendingIntent
        );
    }
}
