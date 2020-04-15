package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class LockActivity extends AppCompatActivity {

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private final static String TAG = LockActivity.class.getSimpleName();
    private final static String TRUE_CODE = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lock);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        //attach lock_shape view with dot indicator
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        //set lock_shape code length
        mPinLockView.setPinLength(6);

        //set listener for lock_shape code change
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {

                //User input true code
                if (pin.equals(TRUE_CODE)) {
                    Intent intent = new Intent(LockActivity.this, MainActivity.class);
                    intent.putExtra("code", pin);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LockActivity.this, "Failed code, try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEmpty() {
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
            }
        });

    }
}
