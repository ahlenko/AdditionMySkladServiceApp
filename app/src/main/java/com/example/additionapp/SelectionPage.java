package com.example.additionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.additionapp.checkers.InputChecker;
import com.example.additionapp.database.MS_SQLConnector;
import com.example.additionapp.database.MS_SQLError;
import com.example.additionapp.database.MS_SQLSelect;
import com.example.additionapp.exception.SmallException;
import com.example.additionapp.shpreference.SharedData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectionPage extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.selection_page);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AppCompatActivity activity = this; final boolean[] isEmployee = {false};
        SharedData data = new SharedData(this); Context context = this;
        ImageButton toOrderingButton = findViewById(R.id.button_toOrdering);

        TextView setToClientText = findViewById(R.id.button_chengeToClientTitle);
        ImageButton setToClient =  findViewById(R.id.button_chengeToClient);
        EditText enter_cl_phNumber = findViewById(R.id.enterClientPhNumber);
        TextView text_cl_phNumber = findViewById(R.id.titleClientPhNumber);
        ConstraintLayout clientLayout = findViewById(R.id.clientLayout);
        EditText enter_cl_email = findViewById(R.id.enterClientEmail);
        TextView text_cl_email = findViewById(R.id.titleClientEmail);
        EditText enter_cl_pib = findViewById(R.id.enterClientPIB);
        TextView text_cl_pib = findViewById(R.id.titleClientPIB);

        TextView setToEmployeeText = findViewById(R.id.button_changeToEmployeetitle);
        ImageButton setToEmployee = findViewById(R.id.button_changeToEmployee);
        EditText enter_emp_password = findViewById(R.id.enterEmployeePassword);
        TextView text_emp_password = findViewById(R.id.titleEmployeePassword);
        ConstraintLayout employeeLayout = findViewById(R.id.employeeLayout);
        EditText enter_emp_login = findViewById(R.id.enterEmployeeLogin);
        TextView text_emp_login = findViewById(R.id.titleEmployeeLogin);
        EditText enter_co_email = findViewById(R.id.enterCompanyEmail);
        TextView text_co_email = findViewById(R.id.titleCompanyEmail);

        View.OnClickListener ClientListener = new View.OnClickListener() {
            @Override public void onClick(View view) { int enter_err = 0;
                text_cl_phNumber.setTextColor(getColor(R.color.fonts_color_blc));
                text_cl_email.setTextColor(getColor(R.color.fonts_color_blc));
                text_cl_pib.setTextColor(getColor(R.color.fonts_color_blc));

                String phNumber = enter_cl_phNumber.getText().toString().trim();
                String email = enter_cl_email.getText().toString().trim();
                String pib = enter_cl_pib.getText().toString().trim();

                text_cl_phNumber.setText(R.string.phonenumber);
                text_cl_email.setText(R.string.email);
                text_cl_pib.setText(R.string.pib);

                if (InputChecker.isNotPhone(phNumber, text_cl_phNumber, activity)) {
                    text_cl_phNumber.setText(R.string.inc_phone); enter_err++; }
                if (InputChecker.isNotEmail(email, text_cl_email, 35, activity)) {
                    text_cl_email.setText(R.string.non_format_email); enter_err++; }
                if (InputChecker.isNotPIB(pib, text_cl_pib, 70, activity)) {
                    text_cl_pib.setText(R.string.inc_pib); enter_err++; }

                if (enter_err == 0) { data.isClient(email, pib, phNumber);
                    Intent intent = new Intent(SelectionPage.this, CheckingPosition.class);
                    vibrator.vibrate(50); startActivity(intent);
                }
            }
        };

        View.OnClickListener EmployeeListener = new View.OnClickListener() {
            @Override public void onClick(View view) { int enter_err = 0;
                text_emp_password.setTextColor(getColor(R.color.fonts_color_blc));
                text_emp_login.setTextColor(getColor(R.color.fonts_color_blc));
                text_co_email.setTextColor(getColor(R.color.fonts_color_blc));

                String pass = enter_emp_password.getText().toString().trim();
                String login = enter_emp_login.getText().toString().trim();
                String email = enter_co_email.getText().toString().trim();

                text_emp_password.setText(R.string.password);
                text_emp_login.setText(R.string.login);
                text_co_email.setText(R.string.email);

                if (InputChecker.isNotEmail(email, text_co_email, 35, activity)) {
                    text_co_email.setText(R.string.non_format_email); enter_err++; }
                if (InputChecker.isNotEmail(login, text_emp_login, 35, activity)) {
                    text_emp_login.setText(R.string.non_format_login); enter_err++; }
                if (InputChecker.isNotPassword(pass, text_emp_password, activity)) {
                    text_emp_password.setText(R.string.non_current_password); enter_err++; }

                if (enter_err == 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                MS_SQLConnector msc = MS_SQLConnector.getConect();
                                Connection mssqlConnection = msc.connection;
                                ResultSet resultSet = MS_SQLSelect.IsCorrectLoginOP(
                                        mssqlConnection, email, login);
                                if (!resultSet.isBeforeFirst())
                                    throw new SmallException(0, String.valueOf(R.string.non_reg_email));
                                resultSet.next(); if (resultSet.getString("login") == null)
                                    throw new SmallException(1, getString(R.string.non_current_login));
                                if (!resultSet.getString("password").equals(pass))
                                    throw new SmallException(2, getString(R.string.non_current_password));
                                data.isEmployee(resultSet.getInt("c_id"), resultSet.getInt("id"));

                                msc.disconnect(); runOnUiThread(new Runnable() { public void run() {
                                        Intent intent = new Intent(SelectionPage.this, CheckingPosition.class);
                                        vibrator.vibrate(50); startActivity(intent); }
                                });
                            } catch (SQLException e) {
                                MS_SQLError.ErrorOnUIThread(context,  activity);
                            } catch (SmallException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        switch (e.getErrorCode()) {
                                            case 0: text_co_email.setText(e.getErrorMessage());
                                                text_co_email.setTextColor(getColor(R.color.red_note)); break;
                                            case 1: text_emp_login.setText(e.getErrorMessage());
                                                text_emp_login.setTextColor(getColor(R.color.red_note)); break;
                                            case 2: text_emp_password.setText(e.getErrorMessage());
                                                text_emp_password.setTextColor(getColor(R.color.red_note)); break;
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        };

        View.OnClickListener ChoseUserType = new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (isEmployee[0]) { isEmployee[0] = false;
                    toOrderingButton.setOnClickListener(ClientListener);
                    clientLayout.setVisibility(View.VISIBLE);
                    employeeLayout.setVisibility(View.GONE);
                    setToClientText.setVisibility(View.GONE);
                    setToClient.setVisibility(View.GONE);
                    setToEmployeeText.setVisibility(View.VISIBLE);
                    setToEmployee.setVisibility(View.VISIBLE);
                } else { isEmployee[0] = true;
                    toOrderingButton.setOnClickListener(EmployeeListener);
                    clientLayout.setVisibility(View.GONE);
                    employeeLayout.setVisibility(View.VISIBLE);
                    setToClientText.setVisibility(View.VISIBLE);
                    setToClient.setVisibility(View.VISIBLE);
                    setToEmployeeText.setVisibility(View.GONE);
                    setToEmployee.setVisibility(View.GONE);
                } vibrator.vibrate(100);
            }
        };

        toOrderingButton.setOnClickListener(ClientListener);
        setToEmployee.setOnClickListener(ChoseUserType);
        setToClient.setOnClickListener(ChoseUserType);
    }
}