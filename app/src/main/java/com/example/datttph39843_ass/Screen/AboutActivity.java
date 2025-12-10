package com.example.datttph39843_ass.Screen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.datttph39843_ass.DAO.AccountDAO;
import com.example.datttph39843_ass.DTO.Account;
import com.example.datttph39843_ass.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class AboutActivity extends AppCompatActivity {

    private TextView tvFullname, tvEmail, tvMsv, tvClass;
    private FloatingActionButton fabEditInfo;
    private AccountDAO accountDAO;
    private String currentUsername;
    private Account currentUserAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Bind views
        tvFullname = findViewById(R.id.tv_info_fullname);
        tvEmail = findViewById(R.id.tv_info_email);
        tvMsv = findViewById(R.id.tv_info_msv);
        tvClass = findViewById(R.id.tv_info_class);
        fabEditInfo = findViewById(R.id.fab_edit_info);

        accountDAO = new AccountDAO(this);

        loadUserInfo();

        fabEditInfo.setOnClickListener(v -> showEditInfoDialog());
    }

    private void loadUserInfo() {
        currentUsername = getLoggedInUsername();
        if (currentUsername != null && !currentUsername.isEmpty()) {
            currentUserAccount = accountDAO.getAccountByUsername(currentUsername);
            if (currentUserAccount != null) {
                tvFullname.setText(currentUserAccount.getFullname());
                tvEmail.setText(currentUserAccount.getEmail());
                tvMsv.setText(currentUserAccount.getMsv() != null ? currentUserAccount.getMsv() : "Chưa cập nhật");
                tvClass.setText(currentUserAccount.getLop() != null ? currentUserAccount.getLop() : "Chưa cập nhật");
            }
        }
    }

    private void showEditInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_info, null);
        builder.setView(dialogView);

        final TextInputEditText etEditFullname = dialogView.findViewById(R.id.et_edit_fullname);
        final TextInputEditText etEditEmail = dialogView.findViewById(R.id.et_edit_email);
        final TextInputEditText etEditMsv = dialogView.findViewById(R.id.et_edit_msv);
        final TextInputEditText etEditClass = dialogView.findViewById(R.id.et_edit_class);

        // Pre-fill data
        if (currentUserAccount != null) {
            etEditFullname.setText(currentUserAccount.getFullname());
            etEditEmail.setText(currentUserAccount.getEmail());
            etEditMsv.setText(currentUserAccount.getMsv());
            etEditClass.setText(currentUserAccount.getLop());
        }

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newFullname = etEditFullname.getText().toString().trim();
            String newEmail = etEditEmail.getText().toString().trim();
            String newMsv = etEditMsv.getText().toString().trim();
            String newClass = etEditClass.getText().toString().trim();

            if (newFullname.isEmpty() || newEmail.isEmpty() || newMsv.isEmpty() || newClass.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int updatedRows = accountDAO.updateAccountInfo(currentUsername, newFullname, newEmail, newMsv, newClass);
            if (updatedRows > 0) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                loadUserInfo(); // Reload info to update the TextViews
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USERNAME", null);
    }
}
