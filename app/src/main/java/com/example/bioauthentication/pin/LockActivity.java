package com.example.bioauthentication.pin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioauthentication.R;
import com.example.bioauthentication.pin.adapters.LockPinAdapter;
import com.example.bioauthentication.pin.dots.IndicatorDots;
import com.example.bioauthentication.pin.entity.LockPin;
import com.example.bioauthentication.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

import static com.example.bioauthentication.home.utils.TYPE_UP;

public class LockActivity extends AppCompatActivity {

    private float x;
    private float y;
    private float press;
    private float toucharea;
    private String message;
    private IndicatorDots mIndicatorDots;
    private final static String TAG = LockActivity.class.getSimpleName();
    private final static String TRUE_CODE = "1234";
    private List<LockPin> lockPins;
    private int sampleNumber;
    private int pinLength;
    private int currentPass;
    private User currentUser;
    private String testType;
    private FirebaseDatabase db;
    TextView counterS;
    TextView currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lockPins = new ArrayList<LockPin>();

        //mPinLockAdapter = new PinLockAdapter(getContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        counterS = (TextView) findViewById(R.id.textCounter);
        currentPassword = (TextView) findViewById(R.id.textPassword);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            currentUser = (User) b.get("user");
            testType = (String) b.get("testType");
        }

        db = FirebaseDatabase.getInstance();
        sampleNumber = 1;
        Button resetLastSample = findViewById(R.id.reset_sample_btn);
        resetLastSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockPins = new ArrayList<>();
            }
        });

        Button newSampleBtn = findViewById(R.id.new_sample_btn);
        newSampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LockPinAdapter adapter = new LockPinAdapter();
        LockPinAdapter.OnNumberClickListener onNumberClickListener =
                new LockPinAdapter.OnNumberClickListener() {
                    @Override
                    public void onNumberClicked(LockPin lockPin, String type) {
                        if (!checkSizeLockPins(pinLength)) {
                            if (type.equals(TYPE_UP)) {
                                lockPins.add(lockPin);
                                mIndicatorDots.updateDot(lockPins.size());
                                pushTouchToFirebase();
                            } else if (lockPins.size() > 0) {
                                LockPin ant = lockPins.get(lockPins.size() - 1);
                                long timeBetween = lockPin.getTimeEventDown() - ant.getTimeEventUp();
                                lockPin.setTimeBetweenTouch(timeBetween);
                            }
                            return;
                        }
                        Toast.makeText(getApplicationContext(), R.string.max_length_added, Toast.LENGTH_SHORT).show();
                    }
                };
        LockPinAdapter.OnDeleteClickListener onDeleteClickListener =
                new LockPinAdapter.OnDeleteClickListener() {
                    @Override
                    public void onDeleteClicked() {
                        if (lockPins.size() > 0) {
                            lockPins.remove(lockPins.size() - 1);
                            mIndicatorDots.updateDot(lockPins.size());
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
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //recyclerView.addOnItemTouchListener(new OnItemClickListenerLockPin());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title);
        final Spinner input = new Spinner(this);
        ArrayAdapter<Integer> options = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item);
        options.addAll(4, 6, 8);
        input.setAdapter(options);
        builder.setView(input);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    pinLength = (int) input.getSelectedItem();
                    mIndicatorDots.setPinLength(pinLength);
                    if (pinLength == 4) {
                        currentPass = currentUser.getPin4();
                    }
                    if (pinLength == 6) {
                        currentPass = currentUser.getPin6();
                    }
                    if (pinLength == 8) {
                        currentPass = currentUser.getPin8();
                    }
                    currentPassword.setText(Integer.toString(currentPass));
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_number, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();


//
//        //attach lock_shape view with dot indicator
//        //mPinLockView.attachIndicatorDots(mIndicatorDots);
//
//        //set lock_shape code length
//        mPinLockView.setPinLength(4);
//
//        //set listener for lock_shape code change
//        mPinLockView.setPinLockListener(new PinLockListener() {
//            @Override
//            public void onComplete(String pin) {
//                Log.d(TAG, "lock_shape code: " + pin);
//
//                //User input true code
//                if (pin.equals(TRUE_CODE)) {
//                    Intent intent = new Intent(LockActivity.this, HomeScreenActivity.class);
//                    intent.putExtra("code", pin);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(LockActivity.this, "Failed code, try again!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onEmpty() {
//                //Log.d(TAG, "lock_shape code is empty!");
//            }
//
//            @Override
//            public void onPinChange(int pinLength, String intermediatePin) {
//                //Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
//                //*Mlp a = new Mlp();
//                //a.onNumberClicked(Integer.parseInt(intermediatePin));*//*
//            }
//        });




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

    private void pushTouchToFirebase() {
        if (checkSizeLockPins(pinLength)) {
            String passC = Integer.toString(currentPass);
            String pass = "";
            for (int i = 0; (i) < lockPins.size(); i++) {
                pass = pass + lockPins.get(i).getDigit();
            }
            if (pass.equalsIgnoreCase(passC)) {
                DatabaseReference pins = db.getReference("pins");
                pins.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        lockPins.size();
                        for (int i = 0; i < lockPins.size(); i++) {
                            LockPin currentPin = lockPins.get(i);
                            mutableData.child(currentUser.getUid() + "").child(testType).child("pin-length-" + pinLength).child("sample-" + sampleNumber).child("" + (i + 1)).setValue(currentPin);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (b && checkSizeLockPins(pinLength)) {
                            counterS.setText(Integer.toString(sampleNumber));
                            sampleNumber += 1;
                            lockPins = new ArrayList<>();
                            mIndicatorDots.updateDot(lockPins.size());
                            Toast.makeText(getApplicationContext(), R.string.new_sample_added, Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "onComplete: " + b + " error: " + databaseError);
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                lockPins = new ArrayList<>();
                mIndicatorDots.updateDot(lockPins.size());
            }
        }
    }

    private boolean checkSizeLockPins(int pingSize) {
        if (lockPins.size() == pingSize) {
            /*for (LockPin l : lockPins){
                Toast.makeText(this,l.toString(),Toast.LENGTH_SHORT).show();
            }*/
            return true;
        }
        return false;
    }
}
