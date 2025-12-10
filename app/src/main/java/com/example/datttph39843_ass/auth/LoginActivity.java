package com.example.datttph39843_ass.auth;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datttph39843_ass.DAO.AccountDAO;
import com.example.datttph39843_ass.DTO.Account;
import com.example.datttph39843_ass.R;
import com.example.datttph39843_ass.Screen.TaskActivity;
import com.example.datttph39843_ass.utils.AlarmScheduler;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvUsernameError, tvPasswordError;
    private Button btnLogin;
    private TextView tvRegisterNow, tvForgotPassword;
    private AccountDAO accountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupTextWatchers();

        accountDAO = new AccountDAO(this);

        btnLogin.setOnClickListener(v -> {
            if (validateFields()) {
                loginUser();
            }
        });

        tvRegisterNow.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username_login);
        etPassword = findViewById(R.id.et_password_login);
        tvUsernameError = findViewById(R.id.tv_username_error_login);
        tvPasswordError = findViewById(R.id.tv_password_error_login);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterNow = findViewById(R.id.tv_register_now);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void setupTextWatchers() {
        addTextWatcher(etUsername, tvUsernameError);
        addTextWatcher(etPassword, tvPasswordError);
    }

    private boolean validateFields() {
        clearAllErrors();
        boolean isValid = true;

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            tvUsernameError.setText("Tên đăng nhập không được để trống");
            tvUsernameError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (password.isEmpty()) {
            tvPasswordError.setText("Mật khẩu không được để trống");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!isValid) {
            if (tvUsernameError.getVisibility() == View.VISIBLE) etUsername.requestFocus();
            else etPassword.requestFocus();
        }

        return isValid;
    }


    private void loginUser() {
        final String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (!accountDAO.checkUsernameExists(username)) {
            tvUsernameError.setText("Tên đăng nhập không tồn tại");
            tvUsernameError.setVisibility(View.VISIBLE);
            etUsername.requestFocus();
            return;
        }

        if (!accountDAO.checkLogin(username, password)) {
            tvPasswordError.setText("Mật khẩu không đúng");
            tvPasswordError.setVisibility(View.VISIBLE);
            etPassword.requestFocus();
            return;
        }

        Runnable proceed = () -> {
            SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("USERNAME", username);
            editor.apply();

            AlarmScheduler.scheduleSingleTaskCheck(this, 25000);

            Account account = accountDAO.getAccountByUsername(username);
            Intent intent = new Intent(LoginActivity.this, TaskActivity.class);
            intent.putExtra("USER_FULLNAME", account.getFullname());
            intent.putExtra("USER_EMAIL", account.getEmail());
            intent.putExtra("USER_USERNAME", account.getUsername());

            startActivity(intent);
            finish();
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("Yêu cầu quyền")
                        .setMessage("Ứng dụng cần quyền đặt báo thức chính xác để có thể gửi thông báo nhắc nhở công việc đúng giờ. Vui lòng cấp quyền trong cài đặt.")
                        .setPositiveButton("Đi đến cài đặt", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            Toast.makeText(this, "Thông báo có thể không chính xác do thiếu quyền.", Toast.LENGTH_LONG).show();
                            proceed.run();
                        })
                        .show();
            } else {
                proceed.run();
            }
        } else {
            proceed.run();
        }
    }


    private void clearAllErrors() {
        tvUsernameError.setVisibility(View.GONE);
        tvPasswordError.setVisibility(View.GONE);
    }

    private void addTextWatcher(EditText editText, TextView errorTextView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (errorTextView.getVisibility() == View.VISIBLE) {
                    errorTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
