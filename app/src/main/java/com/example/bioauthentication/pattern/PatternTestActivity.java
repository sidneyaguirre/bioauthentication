package com.example.bioauthentication.pattern;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;
import com.example.bioauthentication.pattern.entity.LockPattern;
import com.example.bioauthentication.pattern.utils.PatternView;
import com.example.bioauthentication.services.PredictML;
import com.example.bioauthentication.services.entities.responseCallback;
import com.example.bioauthentication.user.User;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternTestActivity extends AppCompatActivity implements responseCallback {

    private PatternView patternView;
    private static String MY_PREFS_NAME = "PatternLock";
    private User currentUser;
    private String currentPass;
    private ProgressBar loading;
    TextView answer;
    TextView currentPassword;
    SharedPreferences prefs;
    responseCallback responseCB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_test_activity);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        loading = findViewById(R.id.loading_data);
        loading.setVisibility(View.INVISIBLE);
        answer = findViewById(R.id.textCounter);
        answer.setText("");
        currentPassword = (TextView) findViewById(R.id.textPassword2);
        responseCB = this;


        Bundle b = getIntent().getExtras();
        if (b != null) {
            currentUser = (User) b.get("user");
            currentPass = currentUser.getPattern8();
            currentPassword.setText(currentPass);
        }


        patternView = (PatternView) findViewById(R.id.patternView);
        patternView.setCallBack(new PatternView.CallBack() {
            @Override
            public void onFinish(String password, ArrayList<LockPattern> nodes) {
                pushTouchToFirebase(password, nodes);
            }
        });

    }

    private void callBack(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    loading.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                    loading.setVisibility(View.INVISIBLE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void pushTouchToFirebase(final String password, final ArrayList<LockPattern> nodes) {
        if (password.equalsIgnoreCase(currentPass)) {
            AsyncTaskExtractData asyncTask = new AsyncTaskExtractData(currentUser.getUid());
            asyncTask.execute(nodes);

        } else {
            Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateView(int response) {
        callBack(false);
        if(response == 1) {
            answer.setText(R.string.granted_access);
        }else{
            answer.setText(R.string.denied_access);
        }

    }

    @Override
    public void errorInRequest(int message) {
        callBack(false);
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        answer.setText("");
    }

    private class AsyncTaskExtractData extends AsyncTask<ArrayList<LockPattern>,Void, Void> {
        private int userID;

        public AsyncTaskExtractData(int uid) {
            userID = uid;
        }

        private Double getMean(ArrayList<Double> vector) {
            double sum = 0;
            for (int i = 0; i< vector.size(); i++) {
                sum+= vector.get(i);
            }
            return vector.size()  > 0 ? sum/vector.size() : 0;
        }

        private Double getStandardDeviation(ArrayList<Double> vector, double mean) {
            double sum = 0;
            for (int i = 0; i< vector.size(); i++) {
                sum+=Math.pow(vector.get(i) - mean, 2);
            }
            return vector.size()  > 0 ? Math.sqrt(sum/(vector.size() - 1)) : 0;
        }

        private  double[] getAccelerations(ArrayList<Double> vector, ArrayList<Long> times) {
            List accelerations = new ArrayList<Double>();
            for (int i = 0; i< vector.size(); i++) {
                if(i == 0) {
                    accelerations.add(((vector.get(i) - 0)/times.get(i)));
                    continue;
                }
                accelerations.add(((vector.get(i) - vector.get(i-1))/times.get(i)));
            }
            double mean = getMean((ArrayList<Double>) accelerations);
            double sd = getStandardDeviation((ArrayList<Double>) accelerations, mean);
            return new double[]{mean, sd};
        }

        private  ArrayList<Double> getData(ArrayList<LockPattern> data) {
            List velocitiesX = new ArrayList<Double>();
            List velocitiesY = new ArrayList<Double>();
            List pressure = new ArrayList<Double>();
            ArrayList<Long> timesVector = new ArrayList<>();
            ArrayList<Double> dataDigits = new ArrayList<>();
            int index = 0;
            String lastDigit = "";
            for (LockPattern value:  data) {
                if( index > 0) {
                    velocitiesX.add((double)(value.getX() - data.get(index-1).getX())/(value.getTimeOnSelected() - data.get(index-1).getTimeOnSelected()));
                    velocitiesY.add((double)(value.getY() - data.get(index-1).getY())/(value.getTimeOnSelected() - data.get(index-1).getTimeOnSelected()));
                    pressure.add((double)(value.getPress() - data.get(index-1).getPress())/(value.getTimeOnSelected() - data.get(index-1).getTimeOnSelected()));
                    timesVector.add((long)(value.getTimeOnSelected() - data.get(index-1).getTimeOnSelected()));
                }
                if ((!lastDigit.equals("") && value.getDigit() != null && lastDigit != value.getDigit()) || (index + 1) == data.size()) {
                    double xMean = getMean((ArrayList<Double>) velocitiesX);
                    double yMean = getMean((ArrayList<Double>) velocitiesX);
                    double pressMean = getMean((ArrayList<Double>) velocitiesX);
                    double xSD = getStandardDeviation((ArrayList<Double>) velocitiesX, xMean);
                    double ySD = getStandardDeviation((ArrayList<Double>) velocitiesY, yMean);
                    double pressSD = getStandardDeviation((ArrayList<Double>) pressure, pressMean);
                    double[] xAccData = getAccelerations((ArrayList<Double>) velocitiesX, timesVector);
                    double[] yAccData = getAccelerations((ArrayList<Double>) velocitiesY, timesVector);
                    dataDigits.addAll(Arrays.asList(xMean,xSD,yMean,ySD,xAccData[0],xAccData[1], yAccData[0],yAccData[1],pressMean,pressSD));
                    velocitiesX = new ArrayList<Double>();
                    velocitiesY = new ArrayList<Double>();
                    timesVector = new ArrayList<>();
                }
                if(value.getDigit() != null) {
                    lastDigit = value.getDigit();
                }
                index ++;

            }

            return dataDigits;
        }

        @Override
        protected Void doInBackground(ArrayList<LockPattern>... arrayLists) {
            callBack(true);
            try {
                final ArrayList<LockPattern> nodes = arrayLists[0];
                ArrayList<Double> userData=getData(nodes);
                PredictML prediction = new PredictML(responseCB);
                prediction.PostPredictPatter(userID, userData);
            } catch (Error e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
