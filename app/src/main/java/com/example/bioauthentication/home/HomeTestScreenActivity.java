package com.example.bioauthentication.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;
import com.example.bioauthentication.pattern.PatternActivity;
import com.example.bioauthentication.pattern.PatternTestActivity;
import com.example.bioauthentication.pin.LockActivity;
import com.example.bioauthentication.pin.LockTestActivity;
import com.example.bioauthentication.user.NewUserActivity;
import com.example.bioauthentication.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeTestScreenActivity extends AppCompatActivity {

    private AutoCompleteTextView usersDropDown, testTypesDropDown;
    private Button pinBtn, patternBtn ,addUserBtn;
    private FirebaseDatabase db;
    private  ArrayList<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_home);

        usersDropDown = findViewById(R.id.filled_dropdown_users);
        pinBtn = findViewById(R.id.pin_button);
        patternBtn = findViewById(R.id.pattern_button);
        db = FirebaseDatabase.getInstance();
        mUsers = new ArrayList<>();
        final DatabaseReference users = db.getReference("users");
        final ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> aUsers = new ArrayList<String>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User us = ds.getValue(User.class);
                    assert us != null;
                    aUsers.add(us.getName());
                    mUsers.add(us);
                }
                usersAdapter.clear();
                usersAdapter.addAll(aUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ArrayAdapter<CharSequence> testTypesAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.test_types_array, R.layout.dropdown_menu_popup_item);

        usersDropDown.setAdapter(usersAdapter);

        patternBtn.setOnClickListener(utils.launchTestActivity(this, PatternTestActivity.class, usersDropDown, mUsers));
        pinBtn.setOnClickListener(utils.launchTestActivity(this, LockTestActivity.class, usersDropDown, mUsers));

    }

}