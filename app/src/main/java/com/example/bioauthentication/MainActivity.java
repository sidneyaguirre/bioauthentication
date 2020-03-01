package com.example.bioauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.bioauthentication.home.HomeScreenActivity;
import com.example.bioauthentication.pattern.CreatePasswordActivity;
import com.example.bioauthentication.pattern.InputPasswordActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                String password = preferences.getString("password", "0");
                if(password.equals("0")){
                    Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    Intent intent = new Intent(getApplicationContext(), InputPasswordActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        }, 2000);
    }
}
