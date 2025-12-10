package com.example.datttph39843_ass.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.datttph39843_ass.DAO.TaskDAO;
import com.example.datttph39843_ass.DTO.Task;
import com.example.datttph39843_ass.R;
import com.example.datttph39843_ass.Screen.TaskActivity;

import java.util.List;

public class TaskCheckReceiver extends BroadcastReceiver {

    public static final String REPEATING_CHANNEL_ID = "REPEATING_TASK_CHECK_CHANNEL";
    private static final String REPEATING_CHANNEL_NAME = "Unfinished Task Reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        // The alarm triggers this method
        performCheckAndNotify(context);
    }

    /**
     * Performs the actual check for unfinished tasks and sends a separate notification for each.
     * @param context The application context.
     */
    public static void performCheckAndNotify(Context context) {
        TaskDAO taskDAO = new TaskDAO(context);
        List<Task> tasks = taskDAO.getAllTasks();

        for (Task task : tasks) {
            if (task.getStatus() == 0 || task.getStatus() == 1) { // 0=Todo, 1=In Progress
                String message = "Công việc '" + task.getName() + "' của bạn đang ở trạng thái '" + (task.getStatus() == 0 ? "Cần làm" : "Đang làm") + "'.";
                sendNotification(context, task, message);
            }
        }
    }

    private static void sendNotification(Context context, Task task, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = task.getId(); // Use a unique ID for each notification

        Intent taskIntent = new Intent(context, TaskActivity.class);
        // Pass task information to the activity
        taskIntent.putExtra("TASK_ID", task.getId());
        taskIntent.putExtra("SHOW_TASK_DIALOG", true);
        taskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                taskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(REPEATING_CHANNEL_ID, REPEATING_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REPEATING_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nhắc nhở công việc")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }
}
