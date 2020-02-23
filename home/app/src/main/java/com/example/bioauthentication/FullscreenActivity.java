package com.example.bioauthentication;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private AutoCompleteTextView usersDropDown, testTypesDropDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            // Material design implementations
        }else{
            // NO material design implementation
        }
        usersDropDown = findViewById(R.id.filled_dropdown_users);
        testTypesDropDown = findViewById(R.id.filled_dropdown_test_types);

        ArrayAdapter<CharSequence> usersAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.users_array, R.layout.dropdown_menu_popup_item);
        ArrayAdapter<CharSequence> testTypesAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.test_types_array, R.layout.dropdown_menu_popup_item);

        usersDropDown.setAdapter(usersAdapter);
        testTypesDropDown.setAdapter(testTypesAdapter);


    }

}