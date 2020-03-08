package com.example.bioauthentication.pin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockAdapter;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.bioauthentication.R;
import com.example.bioauthentication.home.HomeScreenActivity;
import com.example.bioauthentication.pin.adapters.LockPinAdapter;
import com.example.bioauthentication.pin.entity.LockPin;
import com.example.bioauthentication.pin.events.OnItemClickListenerLockPin;
import com.example.bioauthentication.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class LockActivity extends AppCompatActivity {

    private float x;
    private float y;
    private float press;
    private float toucharea;
    private String message;
    private PinLockAdapter mPinLockAdapter;

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private final static String TAG = LockActivity.class.getSimpleName();
    private final static String TRUE_CODE = "1234";
    private List<LockPin> lockPins;
    private int sampleNumber;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            lockPins = new ArrayList<LockPin>();

//        class Mlp implements PinLockAdapter.OnNumberClickListener{
//
//            @Override
//            public void onNumberClicked(int keyValue) {
//                Log.d(TAG, "I WANT THIS" + keyValue);
//                Log.d(TAG, "onNumberClicked:" + Mlp.class);
//            }
//        }


        //mPinLockAdapter = new PinLockAdapter(getContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        db = FirebaseDatabase.getInstance();
        sampleNumber = 1;

        Button newSampleBtn = findViewById(R.id.new_sample_btn);
        newSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference pins = db.getReference("pins");
                pins.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        lockPins.size();
                        for (int i=0; i< lockPins.size(); i++) {
                            LockPin currentPin = lockPins.get(i);
                            mutableData.child("jaime").child(""+sampleNumber).child(""+i).setValue(currentPin);
                        }
                        sampleNumber += 1;
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onComplete: "+b +" error: "+ databaseError);
                    }
                });
            }
        });

        LockPinAdapter adapter = new LockPinAdapter();
        LockPinAdapter.OnNumberClickListener onNumberClickListener =
                new LockPinAdapter.OnNumberClickListener() {
                    @Override
                    public void onNumberClicked(LockPin lockPin) {
                        if(!checkSizeLockPins(4)){
                            lockPins.add(lockPin);
                            return;
                        }
                        Toast.makeText(getApplicationContext(),R.string.max_length_added,Toast.LENGTH_SHORT).show();
                    }
                };
        LockPinAdapter.OnDeleteClickListener onDeleteClickListener =
                new LockPinAdapter.OnDeleteClickListener() {
                    @Override
                    public void onDeleteClicked() {
                        if(lockPins.size()>0){
                            lockPins.remove(lockPins.size()-1);
                        }
                    }
        };
        adapter.setOnNumberClickListener(onNumberClickListener);
        adapter.setOnDeleteClickListener(onDeleteClickListener);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_lock_pin);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //recyclerView.addOnItemTouchListener(new OnItemClickListenerLockPin());



        /*mPinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);

        //attach lock_shape view with dot indicator
        mPinLockView.attachIndicatorDots(mIndicatorDots);

        //set lock_shape code length
        mPinLockView.setPinLength(4);

        //set listener for lock_shape code change
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d(TAG, "lock_shape code: " + pin);

                //User input true code
                if (pin.equals(TRUE_CODE)) {
                    Intent intent = new Intent(LockActivity.this, HomeScreenActivity.class);
                    intent.putExtra("code", pin);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LockActivity.this, "Failed code, try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEmpty() {
                //Log.d(TAG, "lock_shape code is empty!");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
                *//*Mlp a = new Mlp();
                a.onNumberClicked(Integer.parseInt(intermediatePin));*//*
            }
        });*/




       /* mPinLockView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {

                Log.d(TAG,"keys" + v);
                Log.d(TAG,"event" + ev);
                x = ev.getX();
                y = ev.getY();
                System.out.println("COORDS: x-> " + x + ", y-> " + y );
                press = ev.getPressure(ev.getPointerId(0));
                System.out.println("PRESSURE: " + press);
                toucharea = ((float) Math.pow(10,10))*ev.getSize();
                System.out.println("TOUCH AREA: " + toucharea);
                message = "COORDS: x-> " + x + ", y-> " + y +"\nPressure: " + press + "\nTouch Area: " + toucharea;
                //message = String.valueOf(press);
                Log.d("PRESSURE", ev.toString());
                System.out.println(message);
                return false;
            }
        });*/

    }

    private boolean checkSizeLockPins(int pingSize) {
        if(lockPins.size() == pingSize){
            /*for (LockPin l : lockPins){
                Toast.makeText(this,l.toString(),Toast.LENGTH_SHORT).show();
            }
            lockPins = new ArrayList<>();*/
            return true;
        }
        return  false;
    }
}
