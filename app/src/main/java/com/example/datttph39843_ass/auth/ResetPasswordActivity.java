package com.example.datttph39843_ass.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datttph39843_ass.DAO.AccountDAO;
import com.example.datttph39843_ass.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private TextView tvNewPasswordError, tvConfirmPasswordError;
    private Button btnResetPassword;
    private AccountDAO accountDAO;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        bindViews();
        accountDAO = new AccountDAO(this);
        addTextWatchers();

        btnResetPassword.setOnClickListener(v -> attemptPasswordReset());
    }

    private void bindViews() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tvNewPasswordError = findViewById(R.id.tv_new_password_error);
        tvConfirmPasswordError = findViewById(R.id.tv_confirm_password_error);
        btnResetPassword = findViewById(R.id.btn_reset_password);
    }

    private void addTextWatchers() {
        etNewPassword.addTextChangedListener(createTextWatcher(tvNewPasswordError));
        etConfirmPassword.addTextChangedListener(createTextWatcher(tvConfirmPasswordError));
    }

    private TextWatcher createTextWatcher(final TextView errorTextView) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorTextView.setVisibility(View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private void attemptPasswordReset() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        tvNewPasswordError.setVisibility(View.GONE);
        tvConfirmPasswordError.setVisibility(View.GONE);

        boolean isValid = true;

        // Validate password
        if (newPassword.isEmpty()) {
            tvNewPasswordError.setText("Mật khẩu không được để trống");
            tvNewPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (newPassword.length() < 6) {
            tvNewPasswordError.setText("Mật khẩu phải từ 6 ký tự");
            tvNewPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!confirmPassword.equals(newPassword)) {
            tvConfirmPasswordError.setText("Mật khẩu nhập lại không khớp");
            tvConfirmPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!isValid) return;

        int updatedRows = accountDAO.updatePassword(username, newPassword);

        if (updatedRows > 0) {
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
