package com.example.bioauthentication.new_user;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;

public class NewUserActivity extends AppCompatActivity {

    private String newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        EditText fullName = findViewById(R.id.new_user);
        newUser = fullName.getText().toString();
    }

}
