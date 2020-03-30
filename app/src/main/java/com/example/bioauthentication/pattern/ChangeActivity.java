package com.example.bioauthentication.pattern;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;
import com.example.bioauthentication.pattern.entity.LockPattern;
import com.example.bioauthentication.pattern.utils.PatternView;

import java.util.ArrayList;

public class ChangeActivity extends AppCompatActivity {

    private FrameLayout enterPatternContainer, confirmPatternContainer;
    private PatternView lockViewFirstTry, lockViewConfirm;
    private static String MY_PREFS_NAME = "PatternLock";
    private static String PATTERN_KEY;
    private SharedPreferences prefs;private Editor editor;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        //tvMsg = (TextView)findViewById(R.id.tvMsg);
        //tvMsg.setText(getResources().getString(R.string.draw_pattern_msg));

        enterPatternContainer = (FrameLayout) findViewById(R.id.enterPattern);
        confirmPatternContainer = (FrameLayout) findViewById(R.id.confirmPattern);

        //lockViewFirstTry = (PatternView) findViewById(R.id.lock_viewFirstTry);
        lockViewConfirm =  (PatternView) findViewById(R.id.lock_viewConfirm);
        //we can get a call back string when ever user interacts
        //with the pattern lock view
        lockViewFirstTry.setCallBack(new PatternView.CallBack() {

            @Override
        public void onFinish(String password, ArrayList<LockPattern> nodes) {
        PATTERN_KEY = password;
        enterPatternContainer.setVisibility(View.GONE);
        //tvMsg.setText(getResources().getString(R.string.redraw_confirm_pattern_msg));
        confirmPatternContainer.setVisibility(View.VISIBLE);
        }
    });

    lockViewConfirm.setCallBack(new PatternView.CallBack() {
    @Override
    public void onFinish(String password, ArrayList<LockPattern> nodes) {
        if(password.equals(PATTERN_KEY)){
            Toast.makeText(getApplicationContext(),
                    "Pattern created successfully!",
                    Toast.LENGTH_SHORT).show();
            editor.putString("Pattern", password);
            editor.commit();
            Intent intent = new Intent(ChangeActivity.this, PatternActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "You have drawn the wrong Pattern", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }

}
