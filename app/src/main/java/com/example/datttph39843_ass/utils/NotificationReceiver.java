package com.example.datttph39843_ass.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.datttph39843_ass.R;
import com.example.datttph39843_ass.Screen.TaskActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";
    private static final String CHANNEL_NAME = "Task Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for task reminder notifications");
            notificationManager.createNotificationChannel(channel);
        }

        String taskName = intent.getStringExtra("TASK_NAME");
        int notificationId = intent.getIntExtra("TASK_ID", 0);

        // Create an Intent to open the app when the notification is tapped
        Intent openAppIntent = new Intent(context, TaskActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, openAppIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nhắc nhở công việc")
                .setContentText("Đừng quên công việc: " + taskName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // Set the PendingIntent on the notification
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }
}
