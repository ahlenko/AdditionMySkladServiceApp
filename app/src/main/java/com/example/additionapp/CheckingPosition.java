package com.example.additionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.additionapp.database.MS_SQLConnector;
import com.example.additionapp.database.MS_SQLError;
import com.example.additionapp.database.MS_SQLInsert;
import com.example.additionapp.database.MS_SQLSelect;
import com.example.additionapp.shpreference.SharedData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckingPosition extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ExecutorService executor = Executors.newFixedThreadPool(1);
    ArrayList <Integer> company_id = new ArrayList<>();
    int Company = 0;
    Thread PrintTask;

    private void enableAllImageButtons(ArrayList<View> View_s) {
        for (View view : View_s) {
            ImageButton btn = view.findViewById(R.id.button_checkcur);
            view.setAlpha(0.4f); if (btn != null)  btn.setEnabled(true);}
    }

    private static String generateRandomNumber(int digits) {
        StringBuilder stringBuilder = new StringBuilder(digits);
        Random random = new Random(); for (int i = 0; i < digits; i++) {
            int digit = random.nextInt(10); stringBuilder.append(digit);
        } return stringBuilder.toString();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.checking_position);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        AtomicInteger chesed_id = new AtomicInteger(); chesed_id.set(1);
        AtomicInteger allCounter = new AtomicInteger(0);
        AppCompatActivity activity = this; Context context = this;
        ArrayList <String> company_names = new ArrayList<>();
        ArrayList <Integer> prod_count = new ArrayList<>();
        ArrayList <Integer> prod_ids = new ArrayList<>();
        SharedData data = new SharedData(this);
        ArrayList<View> View_s = new ArrayList<View>();
        boolean isEmployee = data.getUserType();
        Company = data.getCompany_ID();

        TextView enter_performer = findViewById(R.id.enter_performer);
        Spinner chose_performer = findViewById(R.id.select_performer);
        ImageButton btn_confirm = findViewById(R.id.button_confirm);
        TextView ch_counter = activity.findViewById(R.id.text_info);
        LinearLayout table = activity.findViewById(R.id.TableView);
        TextView infoState = activity.findViewById(R.id.infostate);
        ImageButton btn_minus = findViewById(R.id.button_minus);
        ImageButton btn_plus = findViewById(R.id.button_plus);

        View.OnClickListener tableListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) { int id = view.getId();
                enableAllImageButtons(View_s); chesed_id.set(id);
                View temp = View_s.get(id-1); temp.setAlpha(1f);
                TextView count = temp.findViewById(R.id.view_countorig);
                TextView count_fact = temp.findViewById(R.id.view_countfact);
                String str = count_fact.getText() + " / " + count.getText();
                ch_counter.setText(str);
                if (count_fact.getText().toString().trim().equals("--")){
                    btn_minus.setEnabled(false); btn_minus.setAlpha(0.7f);
                } else {btn_minus.setEnabled(true); btn_minus.setAlpha(1f);}
                if (!isEmployee && count_fact.getText().toString().trim().
                        equals(String.valueOf(prod_count.get(chesed_id.get() - 1)))){
                    btn_plus.setEnabled(false); btn_plus.setAlpha(0.7f);}
                vibrator.vibrate(100);
            }
        };

        View.OnClickListener PlusBTNListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) { View temp = View_s.get(chesed_id.get() - 1);
                TextView count = temp.findViewById(R.id.view_countorig);
                TextView count_fact = temp.findViewById(R.id.view_countfact);
                CheckBox state = temp.findViewById(R.id.cur_state);
                allCounter.set(allCounter.get() + 1);
                if (allCounter.get() != 0) {btn_confirm.setEnabled(true); btn_confirm.setAlpha(1f);}
                if (!count_fact.getText().toString().trim().equals("--")){state.setChecked(true);
                    int val_count = Integer.parseInt(count_fact.getText().toString());
                    val_count += 1; String str = val_count + " / " + count.getText();
                    ch_counter.setText(str); count_fact.setText(String.valueOf(val_count));
                } else { String str = "1 / " + count.getText(); state.setChecked(true);
                    btn_minus.setEnabled(true); btn_minus.setAlpha(1f);
                    ch_counter.setText(str); count_fact.setText("1"); }
                if (!isEmployee && count_fact.getText().toString().trim().
                        equals(String.valueOf(prod_count.get(chesed_id.get() - 1)))){
                    btn_plus.setEnabled(false); btn_plus.setAlpha(0.7f);}
            }
        };

        View.OnClickListener MinusBTNListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) { View temp = View_s.get(chesed_id.get()-1);
                TextView count = temp.findViewById(R.id.view_countorig);
                TextView count_fact = temp.findViewById(R.id.view_countfact);
                CheckBox state = temp.findViewById(R.id.cur_state);
                int val_count; allCounter.set(allCounter.get()-1);
                if (allCounter.get() == 0) {btn_confirm.setEnabled(false); btn_confirm.setAlpha(0.7f);}
                if (!count_fact.getText().toString().trim().equals("--")){
                    val_count = Integer.parseInt(count_fact.getText().toString());
                    if (val_count == 1){ String str = "-- / " + count.getText();state.setChecked(false);
                        ch_counter.setText(str); count_fact.setText("--");
                        btn_minus.setEnabled(false); btn_minus.setAlpha(0.7f);
                    } else { val_count--; String str = val_count + " / " + count.getText();
                        ch_counter.setText(str); count_fact.setText(String.valueOf(val_count));}
                } if (!isEmployee && !count_fact.getText().toString().trim().
                        equals(String.valueOf(prod_count.get(chesed_id.get() - 1))))
                    {btn_plus.setEnabled(true); btn_plus.setAlpha(1f);}
            }
        };

        View.OnClickListener ConfirmListener = new View.OnClickListener() {
            @Override public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override public void run() {
                        try {MS_SQLConnector msc = MS_SQLConnector.getConect();
                            Connection mssqlConnection = msc.connection;
                            String orderCode = generateRandomNumber(8);
                            int orderId = isEmployee ?
                                MS_SQLInsert.AddAddition(mssqlConnection, Company,
                                        data.getEmployee_ID(), allCounter.get())
                                : MS_SQLInsert.AddOrder(mssqlConnection,  Company,
                                    data.getCliName(), data.getCliEmail(), data.getCliPhone(),
                                    allCounter.get(), Integer.parseInt(orderCode));
                            if (orderId == -1) throw new SQLException();
                            int i = 0; for (View view : View_s){
                                TextView count_fact = view.findViewById(R.id.view_countfact);
                                String count = count_fact.getText().toString().trim();
                                CheckBox state = view.findViewById(R.id.cur_state);
                                int position_id = prod_ids.get(i); i++; if (state.isChecked()){
                                    if (isEmployee) MS_SQLInsert.AddAdditionDetails(mssqlConnection,
                                            Company, position_id, orderId, Integer.parseInt(count));
                                    else MS_SQLInsert.AddOrderDetails(mssqlConnection, Company,
                                            position_id, orderId, Integer.parseInt(count));
                                } data.ClearData();
                            } runOnUiThread(new Runnable() {
                                @Override public void run() { vibrator.vibrate(100); finish();
                                    Toast.makeText(context, R.string.orderCreate, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (SQLException e){
                            MS_SQLError.ErrorOnUIThread(context, activity);
                        }
                    }
                }).start();
            }
        };

        PrintTask = new Thread(new Runnable() {
            @Override public void run() {
                int countInt = 0; try { prod_count.clear(); prod_ids.clear();
                    chesed_id.set(1); allCounter.set(0); View_s.clear();
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    ResultSet resultSet = MS_SQLSelect.ReadProducts(
                            mssqlConnection, Company);
                    int i = 1; while (resultSet.next()) {
                        View temp = getLayoutInflater().inflate(R.layout.position_view,
                                table, false);
                        ImageButton button = temp.findViewById(R.id.button_checkcur);
                        TextView count = temp.findViewById(R.id.view_countorig);
                        ImageView image = temp.findViewById(R.id.photo_img);
                        TextView name = temp.findViewById(R.id.view_name);
                        TextView code = temp.findViewById(R.id.view_code);
                        temp.setAlpha(0.4F); if (i == 1) {
                            temp.setAlpha(1F); countInt = resultSet.getInt("count");
                        } button.setId(i); i++; button.setOnClickListener(tableListener);
                        name.setText(resultSet.getString("name"));
                        code.setText(resultSet.getString("code"));
                        prod_count.add(resultSet.getInt("count"));
                        count.setText(resultSet.getString("count"));
                        prod_ids.add(resultSet.getInt("product_id"));
                        byte[] imageBytes = resultSet.getBytes("image");
                        boolean isEmpty = true; for (byte b : imageBytes) if (b != 0)
                            {isEmpty = false;break;} if (!isEmpty) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,
                            0, imageBytes.length);image.setImageBitmap(bitmap);
                        } View_s.add(temp);
                    } int finalCountInt = countInt; msc.disconnect();
                    runOnUiThread(new Runnable() {
                        @Override public void run() { infoState.setText("");
                            btn_confirm.setEnabled(false); btn_confirm.setAlpha(0.7f);
                            btn_minus.setEnabled(false); btn_minus.setAlpha(0.7F);
                            btn_plus.setEnabled(true); btn_plus.setAlpha(1f);
                            String str = "-- / " + finalCountInt; ch_counter.setText(str);
                            table.removeAllViews(); if (View_s.isEmpty()) {
                                infoState.setText(R.string.checking_list_empty);
                                btn_plus.setAlpha(0.7f); btn_plus.setEnabled(false);
                                btn_minus.setAlpha(0.7f); btn_minus.setEnabled(false);
                                btn_confirm.setAlpha(0.7f); btn_confirm.setEnabled(false);
                            } else for (View userView : View_s) table.addView(userView);
                        }
                    });
                } catch (SQLException e){
                    MS_SQLError.ErrorOnUIThread(context, activity);
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MS_SQLConnector msc = MS_SQLConnector.getConect();
                    Connection mssqlConnection = msc.connection;
                    String text = ""; if (isEmployee){
                        ResultSet resultSet = MS_SQLSelect.GetCompanyByID(
                                mssqlConnection, data.getCompany_ID());
                        resultSet.next(); text = resultSet.getString("name");
                    } else {
                        ResultSet resultSet = MS_SQLSelect.GetCompanyAll(
                                mssqlConnection); while (resultSet.next()) {
                            company_names.add(resultSet.getString("name"));
                            company_id.add(resultSet.getInt("id"));}
                    } String finalText = text;
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            if (isEmployee) {
                                enter_performer.setText(finalText); executor.execute(PrintTask);
                            } else {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_spinner_item, company_names);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                chose_performer.setAdapter(adapter); chose_performer.setSelection(0);
                                enter_performer.setText(adapter.getItem(0));
                                chose_performer.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
                            }
                        }
                    });
                } catch (SQLException e){
                    MS_SQLError.ErrorOnUIThread(context, activity);
                }
            }
        }).start();

        btn_plus.setOnClickListener(PlusBTNListener);
        btn_minus.setOnClickListener(MinusBTNListener);
        btn_confirm.setOnClickListener(ConfirmListener);
        if (isEmployee) chose_performer.setEnabled(false);
        btn_minus.setEnabled(false); btn_minus.setAlpha(0.7F);
        btn_confirm.setEnabled(false); btn_confirm.setAlpha(0.7f);
    }

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SharedData data = new SharedData(this); Company = company_id.get(i);
        TextView enter_performer = findViewById(R.id.enter_performer);
        enter_performer.setText(adapterView.getItemAtPosition(i).toString());
        executor.execute(PrintTask);
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {}
}