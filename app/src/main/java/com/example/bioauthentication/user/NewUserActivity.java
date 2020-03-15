package com.example.bioauthentication.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class NewUserActivity extends AppCompatActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        db = FirebaseDatabase.getInstance();

        final EditText fullName = findViewById(R.id.new_user);
        final EditText pin4 = findViewById(R.id.pin_4);
        final EditText pin6 = findViewById(R.id.pin_6);
        final EditText pin8 = findViewById(R.id.pin_8);

        Button addUser = findViewById(R.id.add_user_button);
        addUser.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final String userName = fullName.getText().toString();
//                 final int pin4length = Integer.parseInt(pin4.getText().toString());
//                 final int pin6length = Integer.parseInt(pin6.getText().toString());
//                 final int pin8length = Integer.parseInt(pin8.getText().toString());
                 final String pin4length = pin4.getText().toString();
                 final String pin6length = pin6.getText().toString();
                 final String pin8length = pin8.getText().toString();

                 Log.d("lenght4", String.valueOf(pin4length));
                 Log.d("userName", userName);


                 if(userName.isEmpty() || pin4length.isEmpty() || pin6length.isEmpty() || pin8length.isEmpty()) {
                     Toast.makeText(getApplicationContext(),R.string.required_value,Toast.LENGTH_SHORT).show();
                     return;
                 }

//                 final int pin4len = Integer.parseInt(pin4.getText().toString());
//                 final int pin6len = Integer.parseInt(pin6.getText().toString());
//                 final int pin8len = Integer.parseInt(pin8.getText().toString());

                 DatabaseReference usersRef = db.getReference("users");
                 usersRef.runTransaction(new Transaction.Handler() {
                     @NonNull
                     @Override
                     public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                         int mayor = 0;
                         for (MutableData currentMutable : mutableData.getChildren()) {
                             User user = currentMutable.getValue(User.class);
                             assert user != null;
                             if ((user.getName().trim()).equalsIgnoreCase(userName.trim())) {
                                 return Transaction.abort();
                             }
                             mayor = Math.max(user.getUid(), mayor);
                         }
                         final User newUser = new User();
                         newUser.setName(userName);
                         newUser.setUid(mayor+1);
                         newUser.setPin4(pin4length);
                         newUser.setPin6(pin6length);
                         newUser.setPin8(pin8length);
                         mutableData.child(newUser.getUid()+"").setValue(newUser);
                         return Transaction.success(mutableData);
                     }

                     @Override
                     public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                         if(!b){
                             Toast.makeText(getApplicationContext(),R.string.user_already_exist,Toast.LENGTH_SHORT).show();
                         }else{
                             finish();
                         }
                     }
                 });
             }
         });

    }

}
