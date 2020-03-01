package com.example.bioauthentication.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;
import com.example.bioauthentication.user.NewUserActivity;
import com.example.bioauthentication.pattern.InputPasswordActivity;
import com.example.bioauthentication.pin.LockActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeScreenActivity extends AppCompatActivity {

    private AutoCompleteTextView usersDropDown, testTypesDropDown;
    private Button pinBtn, patternBtn ,addUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        usersDropDown = findViewById(R.id.filled_dropdown_users);
        testTypesDropDown = findViewById(R.id.filled_dropdown_test_types);
        pinBtn = findViewById(R.id.pin_button);
        patternBtn = findViewById(R.id.pattern_button);
        addUserBtn = findViewById(R.id.add_user_button);

        ArrayAdapter<CharSequence> usersAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.users_array, R.layout.dropdown_menu_popup_item);
        ArrayAdapter<CharSequence> testTypesAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.test_types_array, R.layout.dropdown_menu_popup_item);

        usersDropDown.setAdapter(usersAdapter);
        testTypesDropDown.setAdapter(testTypesAdapter);

        patternBtn.setOnClickListener(utils.setClickListener(getApplicationContext(),InputPasswordActivity.class, usersDropDown, testTypesDropDown));
        pinBtn.setOnClickListener(utils.setClickListener(getApplicationContext(), LockActivity.class, usersDropDown, testTypesDropDown));
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(intent);
            }
        });

    }

}