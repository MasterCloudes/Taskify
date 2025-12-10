package com.example.datttph39843_ass.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datttph39843_ass.DAO.AccountDAO;
import com.example.datttph39843_ass.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etUsername, etEmail;
    private TextView tvUsernameError, tvEmailError;
    private Button btnForgotPassword;
    private TextView tvBackToLogin;
    private AccountDAO accountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Bind views
        etUsername = findViewById(R.id.et_username_forgot);
        etEmail = findViewById(R.id.et_email_forgot);
        tvUsernameError = findViewById(R.id.tv_username_error_forgot);
        tvEmailError = findViewById(R.id.tv_email_error_forgot);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        tvBackToLogin = findViewById(R.id.tv_back_to_login_forgot);

        accountDAO = new AccountDAO(this);

        // Clear error when typing
        addTextWatcher(etUsername, tvUsernameError);
        addTextWatcher(etEmail, tvEmailError);

        // Submit
        btnForgotPassword.setOnClickListener(v -> {
            if (validateFields()) {
                verifyAccount();
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void clearAllErrors() {
        tvUsernameError.setVisibility(View.GONE);
        tvEmailError.setVisibility(View.GONE);
    }

    private boolean validateFields() {
        clearAllErrors();
        boolean isValid = true;

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // --- USERNAME ---
        if (username.isEmpty()) {
            tvUsernameError.setText("Tên đăng nhập không được để trống");
            tvUsernameError.setVisibility(View.VISIBLE);
            Log.d("VALIDATE_INPUT", "USERNAME_EMPTY");
            isValid = false;
        } else if (username.length() < 4) {
            tvUsernameError.setText("Tên đăng nhập phải từ 4 ký tự");
            tvUsernameError.setVisibility(View.VISIBLE);
            Log.d("VALIDATE_INPUT", "USERNAME_TOO_SHORT: " + username);
            isValid = false;
        }

        // --- EMAIL ---
        if (email.isEmpty()) {
            tvEmailError.setText("Email không được để trống");
            tvEmailError.setVisibility(View.VISIBLE);
            Log.d("VALIDATE_INPUT", "EMAIL_EMPTY");
            isValid = false;
        }

        // Focus vào lỗi đầu tiên
        if (!isValid) {
            if (tvUsernameError.getVisibility() == View.VISIBLE) {
                etUsername.requestFocus();
            } else {
                etEmail.requestFocus();
            }
        }

        return isValid;
    }


    private void verifyAccount() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (accountDAO.checkAccountExists(username, email)) {
            Log.d("FORGOT_CHECK", "MATCH_BOTH: Username + Email hợp lệ");
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        } else {
            showGeneralError();
        }
    }

    private void showGeneralError() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        boolean userExists = accountDAO.checkUsernameExists(username);
        boolean emailExists = accountDAO.checkEmailExists(email);
        boolean matchBoth = accountDAO.checkAccountExists(username, email);

        clearAllErrors();

        // 1. USERNAME SAI
        if (!userExists) {
            tvUsernameError.setText("Tên đăng nhập không tồn tại");
            tvUsernameError.setVisibility(View.VISIBLE);
            Log.d("FORGOT_DB", "USERNAME_NOT_EXIST: " + username);
        }

        // 2. EMAIL SAI
        if (!emailExists) {
            tvEmailError.setText("Email không tồn tại");
            tvEmailError.setVisibility(View.VISIBLE);
            Log.d("FORGOT_DB", "EMAIL_NOT_EXIST: " + email);
        }

        // 3. CÓ CẢ 2 NHƯNG KHÔNG KHỚP
        if (userExists && emailExists && !matchBoth) {
            tvUsernameError.setText("Tên đăng nhập và email không khớp");
            tvEmailError.setText("Tên đăng nhập và email không khớp");

            tvUsernameError.setVisibility(View.VISIBLE);
            tvEmailError.setVisibility(View.VISIBLE);

            Log.d("FORGOT_DB", "USERNAME_EMAIL_NOT_MATCH");
        }

        // Focus lỗi đầu tiên
        if (tvUsernameError.getVisibility() == View.VISIBLE) {
            etUsername.requestFocus();
        } else if (tvEmailError.getVisibility() == View.VISIBLE) {
            etEmail.requestFocus();
        }
    }


    private void addTextWatcher(EditText editText, final TextView errorTextView) {
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
