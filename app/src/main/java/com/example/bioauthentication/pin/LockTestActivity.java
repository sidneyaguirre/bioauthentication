package com.example.bioauthentication.pin;

import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.example.bioauthentication.services.PredictML;
import com.example.bioauthentication.services.entities.request;
import com.example.bioauthentication.services.entities.responseCallback;
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

public class LockTestActivity extends AppCompatActivity implements responseCallback {

    private IndicatorDots mIndicatorDots;
    private final static String TAG = LockTestActivity.class.getSimpleName();
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
    responseCallback responseCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lockPins = new ArrayList<LockPin>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lock);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        counterS = (TextView) findViewById(R.id.textCounter);
        counterS.setText("");
        loading = findViewById(R.id.loading_data_pin);
        loading.setVisibility(View.INVISIBLE);
        currentPassword = (TextView) findViewById(R.id.textPassword);
        responseCB = this;

        Bundle b = getIntent().getExtras();
        if (b != null) {
            currentUser = (User) b.get("user");
            currentPass = currentUser.getPin8();
            pinLength=8;
            currentPassword.setText(currentPass);
        }

        Button resetLastSample = findViewById(R.id.reset_sample_btn);
        resetLastSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockPins = new ArrayList<>();
                mIndicatorDots.updateDot(lockPins.size());
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
        if (checkSizeLockPins(pinLength)) {
            String pass = "";
            for (int i = 0; (i) < lockPins.size(); i++) {
                pass = pass + lockPins.get(i).getDigit();
            }
            if (pass.equalsIgnoreCase(currentPass)) {
                AsyncTaskExample asyncTask = new AsyncTaskExample();
                asyncTask.execute();
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

    @Override
    public void updateView(int response) {
        System.out.println(response);
        callBack(false);
        lockPins = new ArrayList<>();
        mIndicatorDots.updateDot(lockPins.size());
        if(response == 1) {
            counterS.setText(R.string.granted_access);
        }else{
            counterS.setText(R.string.denied_access);
        }
    }

    @Override
    public void errorInRequest(int message) {
        callBack(false);
        lockPins = new ArrayList<>();
        mIndicatorDots.updateDot(lockPins.size());
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        counterS.setText("");
    }

    private  class AsyncTaskExample extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            lockPins.size();
            callBack(true);
            ArrayList<Double> finalData = new ArrayList<Double>();
            int userID = currentUser.getUid();
            for (int i = 0; i < lockPins.size(); i++) {
                finalData.add((double)lockPins.get(i).getPress());
                if(lockPins.get(i).getTimeBetweenTouch() != null){
                    finalData.add((double)lockPins.get(i).getTimeBetweenTouch());
                }
                finalData.add((double)lockPins.get(i).getTimeEventDown());
                finalData.add((double)lockPins.get(i).getTimeEventUp());
                finalData.add((double)lockPins.get(i).getTimeLapsePress());
                finalData.add((double)lockPins.get(i).getX());
                finalData.add((double)lockPins.get(i).getY());
            }
            PredictML prediction = new PredictML(responseCB);
            prediction.PostPredictPin(userID, finalData);
            return null;
        }
    }
}
