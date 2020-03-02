package com.example.bioauthentication.pattern;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.bioauthentication.ProgramActivity;
import com.example.bioauthentication.R;

import java.util.List;

public class InputPasswordActivity extends AppCompatActivity {

    PatternLockView mPatternLockView;

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        password = preferences.getString("password", "0");

        mPatternLockView = findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (password.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                    Intent intent = new Intent(getApplicationContext(), ProgramActivity.class);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onCleared() {

            }
        });
    }
}
