package com.example.datttph39843_ass.Screen;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datttph39843_ass.Adapter.TaskAdapter;
import com.example.datttph39843_ass.DAO.TaskDAO;
import com.example.datttph39843_ass.DTO.Task;
import com.example.datttph39843_ass.R;
import com.example.datttph39843_ass.auth.LoginActivity;
import com.example.datttph39843_ass.utils.AlarmScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TaskDAO taskDAO;
    private ImageView ivLogout, ivProfile;
    private FloatingActionButton fabAddTask;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            Toast.makeText(this, "Permission for notifications is not granted.", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askNotificationPermission();

        bindViews();
        taskDAO = new TaskDAO(this);
        setupRecyclerView();
        loadTasks();
        setClickListeners();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("SHOW_TASK_DIALOG", false)) {
            int taskId = intent.getIntExtra("TASK_ID", -1);
            if (taskId != -1) {
                Task task = taskDAO.getTaskById(taskId);
                if (task != null) {
                    showEditTaskDialog(task);
                }
            }
        }
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.rv_tasks);
        ivLogout = findViewById(R.id.iv_logout);
        ivProfile = findViewById(R.id.iv_profile);
        fabAddTask = findViewById(R.id.fab_add_task);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(new TaskAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(Task task) {
                if (task.getStatus() == 0) {
                    new AlertDialog.Builder(TaskActivity.this)
                            .setTitle("Xác nhận")
                            .setMessage("Bắt đầu làm công việc này?")
                            .setPositiveButton("OK", (dialog, which) -> updateTaskStatus(task, 1))
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }

            @Override
            public void onEditClick(Task task) {
                showEditTaskDialog(task);
            }

            @Override
            public void onDeleteClick(Task task) {
                new AlertDialog.Builder(TaskActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
                        .setPositiveButton("Xóa", (dialog, which) -> deleteTask(task))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        recyclerView.setAdapter(taskAdapter);
    }

    private void setClickListeners() {
        ivLogout.setOnClickListener(v -> logout());
        ivProfile.setOnClickListener(v -> showProfile());
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void loadTasks() {
        List<Task> freshTasks = taskDAO.getAllTasks();
        taskAdapter.submitList(freshTasks);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText etTaskTitle = dialogView.findViewById(R.id.et_task_title);
        final EditText etTaskDescription = dialogView.findViewById(R.id.et_task_description);
        final EditText etEndDate = dialogView.findViewById(R.id.et_task_end_date);
        Button btnAddTask = dialogView.findViewById(R.id.btn_add_task);

        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        AlertDialog dialog = builder.create();
        btnAddTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (title.isEmpty()) {
                etTaskTitle.setError("Tiêu đề không được để trống");
                return;
            }

            String description = etTaskDescription.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Task newTask = new Task(0, title, description, 0, startDate, endDate);
            long newId = taskDAO.addTask(newTask);
            if (newId > 0) {
                newTask.setId((int) newId);
                AlarmScheduler.scheduleTaskReminder(this, newTask);
                loadTasks();
                Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showEditTaskDialog(final Task originalTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);
        builder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.et_edit_task_title);
        final EditText etDescription = dialogView.findViewById(R.id.et_edit_task_description);
        final EditText etEndDate = dialogView.findViewById(R.id.et_edit_task_end_date);
        final CheckBox cbIsDone = dialogView.findViewById(R.id.cb_is_done);
        Button btnSave = dialogView.findViewById(R.id.btn_save_task);

        etTitle.setText(originalTask.getName());
        etDescription.setText(originalTask.getContent());
        etEndDate.setText(originalTask.getEndDate());
        cbIsDone.setChecked(originalTask.getStatus() == 2);

        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        AlertDialog dialog = builder.create();
        btnSave.setOnClickListener(v -> {
            String newTitle = etTitle.getText().toString().trim();
            if (newTitle.isEmpty()) {
                etTitle.setError("Tiêu đề không được để trống");
                return;
            }

            int newStatus = cbIsDone.isChecked() ? 2 : (originalTask.getStatus() == 2 ? 0 : originalTask.getStatus());

            Task updatedTask = new Task(
                originalTask.getId(),
                newTitle,
                etDescription.getText().toString().trim(),
                newStatus,
                originalTask.getStartDate(),
                etEndDate.getText().toString().trim()
            );

            if (taskDAO.updateTask(updatedTask) > 0) {
                if (updatedTask.getStatus() == 2) {
                    AlarmScheduler.cancelTaskReminder(this, updatedTask);
                } else {
                    AlarmScheduler.scheduleTaskReminder(this, updatedTask);
                }
                loadTasks();
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void deleteTask(Task taskToDelete) {
        if (taskDAO.deleteTask(taskToDelete.getId()) > 0) {
            AlarmScheduler.cancelTaskReminder(this, taskToDelete);
            loadTasks();
            Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaskStatus(Task task, int newStatus) {
        if (taskDAO.updateTaskStatus(task.getId(), newStatus) > 0) {
            if (newStatus == 2) { // Task is done
                AlarmScheduler.cancelTaskReminder(this, task);
            }
            loadTasks();
            Toast.makeText(this, "Trạng thái đã được cập nhật", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog(final EditText etDateField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate.getTime());
                    etDateField.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showProfile() {
        Intent intent = new Intent(TaskActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void logout() {
        getSharedPreferences("USER_SESSION", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(TaskActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
