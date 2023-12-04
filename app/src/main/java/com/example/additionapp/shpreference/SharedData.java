package com.example.additionapp.shpreference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedData {
    private Context context;
    private boolean isEmployee;
    private int emp_company_id;
    private int emp_user_id;
    private String cli_email;
    private String cli_name;
    private String cli_phone;

    public SharedData(Context context) {
        this.context = context; WriteData();
    }

    public boolean getUserType() {return isEmployee;}
    public int getCompany_ID() {return emp_company_id;}
    public int getEmployee_ID() {return emp_user_id;}
    public String getCliEmail() {return cli_email;}
    public String getCliName() {return cli_name;}
    public String getCliPhone() {return cli_phone;}

    public void setCompany_ID(int id) {
        emp_company_id = id;
        SaveData();
    }

    public void isEmployee(int company_id, int user_id){
        this.emp_company_id = company_id;
        this.emp_user_id = user_id;
        this.isEmployee = true;
        SaveData();
    }

    public void isClient(String email, String name, String phone){
        this.cli_email = email;
        this.cli_phone = phone;
        this.isEmployee = false;
        this.cli_name = name;
        SaveData();
    }

    private void SaveData(){
        SharedPreferences prefs = context.getSharedPreferences("DatSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isEmployee", isEmployee);
        editor.putInt("company_id", emp_company_id);
        editor.putInt("user_id", emp_user_id);
        editor.putString("email", cli_email);
        editor.putString("phone", cli_phone);
        editor.putString("name", cli_name);
        editor.apply();
    }

    private void WriteData(){
        SharedPreferences prefs = context.getSharedPreferences("DatSettings", Context.MODE_PRIVATE);
        isEmployee = prefs.getBoolean("isEmployee",false);
        emp_company_id = prefs.getInt("company_id", 0);
        cli_email = prefs.getString("email", "");
        cli_phone = prefs.getString("phone", "");
        emp_user_id = prefs.getInt("user_id", 0);
        cli_name = prefs.getString("name", "");
    }

    public void ClearData(){
        SharedPreferences prefs = context.getSharedPreferences("DatSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isEmployee",false);
        editor.putString("email", "");
        editor.putString("phone", "");
        editor.putInt("company_id", 0);
        editor.putString("name", "");
        editor.putInt("user_id", 0);
        editor.apply();
    }
}
