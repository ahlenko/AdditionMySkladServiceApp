package com.example.additionapp.database;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.additionapp.R;

import androidx.appcompat.app.AppCompatActivity;


public class MS_SQLError {
    public static void ErrorOnUIThread(Context context, AppCompatActivity activity){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, R.string.database_error, Toast.LENGTH_LONG).show();
            }
        }); return;
    }
}
