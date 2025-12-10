package com.example.datttph39843_ass.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datttph39843_ass.DAO.AccountDAO;
import com.example.datttph39843_ass.DTO.Account;
import com.example.datttph39843_ass.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword, tilFullname;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword, etFullname;
    private Button btnRegister;
    private AccountDAO accountDAO;
    private View tv_login_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupAutoClearError();
        accountDAO = new AccountDAO(this);

        btnRegister.setOnClickListener(v -> validateAndRegister());
        tv_login_now.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void initViews() {
        // TextInputLayouts
        tilUsername = findViewById(R.id.til_username_register);
        tilEmail = findViewById(R.id.til_email_register);
        tilPassword = findViewById(R.id.til_password_register);
        tilConfirmPassword = findViewById(R.id.til_confirm_password_register);
        tilFullname = findViewById(R.id.til_fullname_register);
        tv_login_now = findViewById(R.id.tv_login_now);

        // EditTexts
        etUsername = findViewById(R.id.et_username_register);
        etEmail = findViewById(R.id.et_email_register);
        etPassword = findViewById(R.id.et_password_register);
        etConfirmPassword = findViewById(R.id.et_confirm_password_register);
        etFullname = findViewById(R.id.et_fullname_register);

        // Button
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupAutoClearError() {
        addClearErrorWatcher(etUsername, tilUsername);
        addClearErrorWatcher(etEmail, tilEmail);
        addClearErrorWatcher(etPassword, tilPassword);
        addClearErrorWatcher(etConfirmPassword, tilConfirmPassword);
        addClearErrorWatcher(etFullname, tilFullname);
    }

    private void addClearErrorWatcher(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
                layout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilFullname.setError(null);
    }

    private void validateAndRegister() {
        clearErrors();

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String fullname = etFullname.getText().toString().trim();

        boolean valid = true;

        // Username
        if (username.isEmpty()) {
            tilUsername.setError("Tên đăng nhập không được để trống");
            valid = false;
        } else if (accountDAO.checkUsernameExists(username)) {
            tilUsername.setError("Tên đăng nhập đã tồn tại");
            valid = false;
        }

        // Email
        if (email.isEmpty()) {
            tilEmail.setError("Email không được để trống");
            valid = false;
        } else if (!email.endsWith("@gmail.com")) {
            tilEmail.setError("Email phải là @gmail.com");
            valid = false;
        } else if (accountDAO.checkEmailExists(email)) {
            tilEmail.setError("Email đã được sử dụng");
            valid = false;
        }

        // Password
        if (password.isEmpty()) {
            tilPassword.setError("Mật khẩu không được để trống");
            valid = false;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Nhập lại mật khẩu");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            valid = false;
        }

        // Full name
        if (fullname.isEmpty()) {
            tilFullname.setError("Họ tên không được để trống");
            valid = false;
        }

        if (!valid) return;

        // Create account with null for msv and lop
        Account account = new Account(0, username, email, password, fullname, null, null);
        long result = accountDAO.addAccount(account);

        if (result > 0) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
        }
    }
}
