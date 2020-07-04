package com.example.bioauthentication.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bioauthentication.R;
import com.example.bioauthentication.pin.entity.LockPin;
import com.example.bioauthentication.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;

public class utils {

    public static String TYPE_DOWN = "down";
    public static String TYPE_UP = "up";

    public static View.OnClickListener setClickListener(final Context appContext, final Class activityToLaunch, final AutoCompleteTextView usersDropDown, final AutoCompleteTextView testTypesDropDown, final ArrayList<User> users) {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersValue = usersDropDown.getText().toString();
                String testTypeValue = testTypesDropDown.getText().toString();
                if(!usersValue.isEmpty() && !testTypeValue.isEmpty()){
                    Intent intent = new Intent(appContext, activityToLaunch);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    User selectedUser = null;
                    for(int i=0; i < users.size(); i++){
                        if (users.get(i).getName().equalsIgnoreCase(usersValue)){
                            selectedUser = users.get(i);
                        }
                    }
                    Bundle b = new Bundle();
                    b.putSerializable("user", selectedUser);
                    b.putString("testType",testTypeValue);
                    intent.putExtras(b);
                    appContext.startActivity(intent);
                } else {
                    Toast.makeText(appContext, R.string.empty_values,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static View.OnClickListener launchNewActivity(final Context appContext, final Class activityToLaunch) {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(appContext, activityToLaunch);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    /*Bundle b = new Bundle();
                    b.putSerializable("user", selectedUser);
                    b.putString("testType",testTypeValue);
                    intent.putExtras(b);*/
                    appContext.startActivity(intent);
            }
        });
    }
}
