package com.example.bioauthentication.pin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.bioauthentication.home.utils.TYPE_UP;

public class LockActivity extends AppCompatActivity {

    private IndicatorDots mIndicatorDots;
    private final static String TAG = LockActivity.class.getSimpleName();
    private List<LockPin> lockPins;
    private int sampleNumber;
    private int pinLength;
    private long countSamples;
    private String currentPass;
    private User currentUser;
    private String testType;
    private ProgressBar loading;
    private FirebaseDatabase db;
    TextView counterS;
    TextView currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lockPins = new ArrayList<LockPin>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        counterS = (TextView) findViewById(R.id.textCounter);
        loading = findViewById(R.id.loading_data_pin);
        loading.setVisibility(View.INVISIBLE);
        currentPassword = (TextView) findViewById(R.id.textPassword);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            currentUser = (User) b.get("user");
            testType = (String) b.get("testType");
            //LockPin currentP = (LockPin) b.get("pin");
        }

        db = FirebaseDatabase.getInstance();
        Button resetLastSample = findViewById(R.id.reset_sample_btn);
        resetLastSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockPins = new ArrayList<>();
                mIndicatorDots.updateDot(lockPins.size());
            }
        });

//        Button newSampleBtn = findViewById(R.id.new_sample_btn);
//        newSampleBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

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
                        lockPins = new ArrayList<>();
                        mIndicatorDots.updateDot(lockPins.size());
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
                    currentPassword.setText(currentPass);

                    String uid = Integer.toString(currentUser.getUid());
                    String lenPin = "pin-length-";
                    String numb = Integer.toString((pinLength));
                    lenPin = lenPin + numb;
                    DatabaseReference patternRef = db.getReference().child("pins").child(uid).child(testType).child(lenPin);

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            countSamples = dataSnapshot.getChildrenCount();
                            sampleNumber = (int)countSamples;
                            counterS.setText(String.valueOf(sampleNumber).concat("/20"));
                            sampleNumber+=1;
                        }@Override
                        public void onCancelled(DatabaseError databaseError) {}
                    };
                    patternRef.addListenerForSingleValueEvent(valueEventListener);
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
    }

    private void callBack(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show){
                    loading.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else {
                    loading.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void pushTouchToFirebase() {
        if(sampleNumber > 20) {
            Toast.makeText(getApplicationContext(), R.string.full_samples, Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkSizeLockPins(pinLength)) {
            String pass = "";
            for (int i = 0; (i) < lockPins.size(); i++) {
                pass = pass + lockPins.get(i).getDigit();
            }
            if (pass.equalsIgnoreCase(currentPass)) {
                DatabaseReference pins = db.getReference("pins");
                pins.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        lockPins.size();
                        callBack(true);
                        for (int i = 0; i < lockPins.size(); i++) {
                            LockPin currentPin = lockPins.get(i);
                            mutableData.child(currentUser.getUid() + "").child(testType).child("pin-length-" + pinLength).child("sample-" + sampleNumber).child("" + (i + 1)).setValue(currentPin);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (b && checkSizeLockPins(pinLength)) {
                            counterS.setText(String.valueOf(sampleNumber).concat("/20"));
                            sampleNumber += 1;
                            lockPins = new ArrayList<>();
                            mIndicatorDots.updateDot(lockPins.size());
                            Toast.makeText(getApplicationContext(), R.string.new_sample_added, Toast.LENGTH_SHORT).show();
                        }
                        callBack(false);
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
