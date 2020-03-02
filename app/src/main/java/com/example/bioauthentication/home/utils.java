package com.example.bioauthentication.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.bioauthentication.R;

public class utils {
    public static View.OnClickListener setClickListener(final Context appContext, final Class activityToLaunch, final AutoCompleteTextView usersDropDown, final AutoCompleteTextView testTypesDropDown) {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersValue = usersDropDown.getText().toString();
                String testTypeValue = testTypesDropDown.getText().toString();
                if(!usersValue.isEmpty() && !testTypeValue.isEmpty()){
                    Intent intent = new Intent(appContext, activityToLaunch);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user",usersValue);
                    intent.putExtra("testType",testTypeValue);
                    appContext.startActivity(intent);
                } else {
                    Toast.makeText(appContext, R.string.empty_values,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
