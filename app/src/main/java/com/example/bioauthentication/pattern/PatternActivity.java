package com.example.bioauthentication.pattern;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.v7.app.ActionBarActivity;
import com.example.bioauthentication.R;
import com.example.bioauthentication.home.HomeScreenActivity;
import com.example.bioauthentication.pattern.entity.LockPattern;
import com.example.bioauthentication.pattern.utils.PatternView;
import com.example.bioauthentication.pin.entity.LockPin;
import com.example.bioauthentication.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PatternActivity extends AppCompatActivity {

    private PatternView patternView;
    private static String MY_PREFS_NAME = "PatternLock";
    private static String PATTERN_KEY;
    private User currentUser;
    private int pinLength;
    private String testType;
    private String currentPass;
    private String path;
    private ProgressBar loading;
    private int sampleNumber;
    private long countSamples;
    private FirebaseDatabase db;
    TextView counterS;
    TextView currentPassword;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_activity);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        loading = findViewById(R.id.loading_data);
        loading.setVisibility(View.VISIBLE);
        counterS = findViewById(R.id.textCounter);
        currentPassword = (TextView) findViewById(R.id.textPassword2);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            currentUser = (User) b.get("user");
            testType = (String) b.get("testType");
//            LockPin currentP = (LockPin) b.get("pin");
        }

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
                    if (pinLength == 4) {
                        currentPass = currentUser.getPattern4();
                    }
                    if (pinLength == 6) {
                        currentPass = currentUser.getPattern6();
                    }
                    if (pinLength == 8) {
                        currentPass = currentUser.getPattern8();
                    }
                    currentPassword.setText(currentPass);

                    String uid = Integer.toString(currentUser.getUid());
                    String lenPattern = "pattern-length-";
                    String numb = Integer.toString((pinLength));
                    lenPattern = lenPattern + numb;
                    Log.d("TAG", "len= " + lenPattern);
                    DatabaseReference patternRef = db.getReference().child("patterns").child(uid).child(testType).child(lenPattern);

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            countSamples = dataSnapshot.getChildrenCount();
                            Log.d("TAG", "count= " + countSamples);
                            sampleNumber = (int)countSamples;
                            counterS.setText(String.valueOf(sampleNumber).concat("/20"));
                            callBack(false);
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

        db = FirebaseDatabase.getInstance();


        DatabaseReference patternRef = db.getReference().child("patterns");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.d("TAG", "count= " + count);
            }@Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        patternRef.addListenerForSingleValueEvent(valueEventListener);

        patternView = (PatternView)findViewById(R.id.patternView);
        patternView.setCallBack(new PatternView.CallBack() {
            @Override
            public void onFinish(String password, ArrayList<LockPattern> nodes) {
                pushTouchToFirebase(password, nodes);
                /*
                PATTERN_KEY = prefs.getString("Pattern", "invalid");

                if(PATTERN_KEY.equals("invalid")) {
                    Toast.makeText(PatternActivity.this, "Options --> Create new Pattern", Toast.LENGTH_LONG).show();
                }else {
                    if(password.equals(PATTERN_KEY)) {
                        Intent intent = new Intent(PatternActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(PatternActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PatternActivity.this, "Pattern incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }
                 */
            }
        });

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

    private void pushTouchToFirebase(final String password, final ArrayList<LockPattern> nodes ) {
        if(sampleNumber > 20) {
            Toast.makeText(getApplicationContext(), R.string.full_samples, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equalsIgnoreCase(currentPass)) {
            DatabaseReference pins = db.getReference("patterns");
            AsyncTask();
            pins.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date resultdate = new Date(System.currentTimeMillis());
                    Log.d("FirebaseTx: ", sdf.format(resultdate) );
                    nodes.size();
                    callBack(true);
                    MutableData root = mutableData.child(currentUser.getUid() + "").child(testType).child("pattern-length-" + pinLength).child("sample-" + sampleNumber);
                    for (int i = 0; i < nodes.size(); i++) {
                        LockPattern currentPin = nodes.get(i);
                        root.child("" + (i + 1)).setValue(currentPin);
                        Date resultMutable = new Date(System.currentTimeMillis());
                        Log.d("FirebaseTxMutable: ", sdf.format(resultMutable) );
                    }
                    resultdate = new Date(System.currentTimeMillis());
                    Log.d("FirebaseTxEnd: ", sdf.format(resultdate) );
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date resultdate = new Date(System.currentTimeMillis());
                    Log.d("FirebaseTxOnComplete: ", sdf.format(resultdate) );
                    if (b) {
                        counterS.setText(String.valueOf(sampleNumber).concat("/20"));
                        sampleNumber += 1;
                        Toast.makeText(getApplicationContext(), R.string.new_sample_added, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), R.string.without_add, Toast.LENGTH_SHORT).show();
                    }
                    callBack(false);
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
        }
    }

/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_new_pattern:
                Intent intent = new Intent(MainActivity.this, ChangeActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
