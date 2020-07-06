package com.example.bioauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.bioauthentication.home.HomeScreenActivity;
import com.example.bioauthentication.home.HomeTestScreenActivity;
import com.example.bioauthentication.home.utils;
import com.example.bioauthentication.pattern.PatternActivity;

public class SelectionActivity extends AppCompatActivity {

    private Button trainsBtn, testBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        trainsBtn = findViewById(R.id.train_button);
        testBtn = findViewById(R.id.test_button);

        trainsBtn.setOnClickListener(utils.launchNewActivity(this, HomeScreenActivity.class));
        testBtn.setOnClickListener(utils.launchNewActivity(this, HomeTestScreenActivity.class));

    }
}